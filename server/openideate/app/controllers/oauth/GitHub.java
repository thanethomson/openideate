package controllers.oauth;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import constants.SignupMethod;
import controllers.oauth.exceptions.OAuthError;
import controllers.oauth.exceptions.OAuthInternalError;
import controllers.oauth.exceptions.OAuthInvalidState;
import controllers.oauth.exceptions.OAuthProviderError;
import models.User;
import play.Logger;
import play.Play;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.mvc.Http.HeaderNames;
import play.mvc.Http.MimeTypes;
import security.oauth.GitHubUser;

/**
 * Our GitHub OAuth handler controller.
 */
public class GitHub extends Controller {
  
  private final static Logger.ALogger logger = Logger.of(GitHub.class);
  private final static int CALLBACK_TIMEOUT = 10000;
  
  private final WSClient ws;
  private final String clientId;
  private final String clientSecret;
  private final String redirectUri;

  @Inject
  public GitHub(WSClient ws) {
    this.ws = ws;
    this.clientId = Play.application().configuration().getString("oauth.github.clientid");
    this.clientSecret = Play.application().configuration().getString("oauth.github.clientsecret");
    this.redirectUri = Play.application().configuration().getString("oauth.github.redirecturi");
  }
  
  /**
   * Helps us get the access token for the given request code and state.
   * @param code
   * @param state
   * @return
   * @throws OAuthInvalidState 
   * @throws OAuthProviderError 
   */
  public String getAccessToken(String code, String state) throws OAuthInvalidState, OAuthProviderError, OAuthInternalError {
    // make sure the state's the same as the one we have in the session
    if (!state.equals(session().get("oauthState"))) {
      throw new OAuthInvalidState("Supplied state is invalid");
    }
    
    // ask GitHub
    WSResponse response = ws.url("https://github.com/login/oauth/access_token")
        .setQueryParameter("client_id", clientId)
        .setQueryParameter("client_secret", clientSecret)
        .setQueryParameter("code", code)
        .setQueryParameter("redirect_uri", redirectUri)
        .setQueryParameter("state", state)
        .setHeader(HeaderNames.ACCEPT, MimeTypes.JSON)
        .post("").get(CALLBACK_TIMEOUT);
    
    // if there was an error
    if (response.getStatus() != 200) {
      logger.error(String.format("Got non-OK status back from GitHub: %d", response.getStatus()));
      logger.error(response.getBody());
      throw new OAuthProviderError("Failed to get positive response from GitHub");
    } else {
      logger.debug(String.format("Got response from GitHub: %s", response.getBody()));
    }
    
    // get the JSON
    JsonNode json;
    
    try {
      json = new ObjectMapper().readTree(response.getBody());
    } catch (JsonProcessingException e) {
      logger.error("JSON processing exception", e);
      throw new OAuthProviderError("Invalid JSON response from GitHub");
    } catch (IOException e) {
      logger.error("I/O error", e);
      throw new OAuthInternalError("I/O error");
    }
    
    if (!json.hasNonNull("access_token") || !json.hasNonNull("scope")) {
      logger.error("Missing access token and/or scope in GitHub response");
      throw new OAuthProviderError("Missing access token and/or scope in GitHub response");
    }
    
    String accessToken = json.get("access_token").asText();
    Set<String> scopes = new HashSet<>(Arrays.asList(json.get("scope").asText().split(",")));
    
    if (!scopes.contains("user:email")) {
      logger.error("Cannot access user's e-mail address");
      throw new OAuthProviderError("Cannot access user e-mail address");
    }
    
    // all's well that ends well
    return accessToken;
  }
  
  public GitHubUser getUserDetails(String accessToken) throws OAuthProviderError, OAuthInternalError {
    // get the user's e-mail address
    WSResponse response = ws.url("https://api.github.com/user")
        .setHeader("Authorization", String.format("token %s", accessToken))
        .get().get(CALLBACK_TIMEOUT);
    
    logger.debug(String.format("Received response: %s", response.getBody()));
    
    if (response.getStatus() != 200) {
      logger.error(String.format("Got error response from GitHub for e-mail address request: %d", response.getStatus()));
      throw new OAuthProviderError("Cannot get e-mail address for user from GitHub");
    }
    
    JsonNode json;
    
    try {
      json = new ObjectMapper().readTree(response.getBody());
    } catch (JsonProcessingException e) {
      logger.error("JSON processing exception", e);
      throw new OAuthProviderError("Invalid JSON response from GitHub");
    } catch (IOException e) {
      logger.error("I/O error", e);
      throw new OAuthInternalError("I/O error");
    }
    
    return new GitHubUser(json);
  }
  
  public Result callback(String code, String state) {
    logger.debug(String.format("Got incoming callback request from GitHub for code %s, state %s", code, state));
    
    String accessToken;
    
    try {
      accessToken = getAccessToken(code, state);
    } catch (OAuthError e) {
      return badRequest(e.getMessage());
    }
    
    // save the OAuth token in our session
    session().put("oauthToken", accessToken);
    // it's a GitHub token
    session().put("oauthProvider", SignupMethod.GITHUB.toString());
    
    logger.debug("Attempting to fetch e-mail address(es) for user...");
    GitHubUser userDetails;
    
    try {
      userDetails = getUserDetails(accessToken);
    } catch (OAuthError e) {
      return badRequest(e.getMessage());
    }
    
    logger.debug(String.format("Got primary e-mail address for user: %s", userDetails.getEmail()));
    
    User user = User.find.where().eq("email", userDetails.getEmail()).findUnique();
    
    if (user == null) {
      logger.debug("User does not exist in database - creating...");
      user = new User();
      user.setEmail(userDetails.getEmail());
      user.setName(userDetails.getName());
      user.setAvatarUrl(userDetails.getAvatarUrl());
      user.setSignupMethod(SignupMethod.GITHUB);
      user.setAccessToken(accessToken);
      user.save();
      logger.debug(String.format("Created new user: %s", user.toJson().toString()));
    } else {
      logger.debug("Updating existing user...");
      user.setSignupMethod(SignupMethod.GITHUB);
      user.setAccessToken(accessToken);
      user.save();
    }
    
    // redirect
    return redirect(controllers.oauth.routes.GitHub.success());
  }
  
  @SubjectPresent(handlerKey="frontend")
  public Result success() {
    return ok(views.html.oauthSuccess.render(request(), (User)ctx().args.get("user")));
  }

}
