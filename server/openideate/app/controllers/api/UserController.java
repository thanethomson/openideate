package controllers.api;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import models.User;
import play.Logger;
import play.mvc.*;
import utils.JsonUtils;
import utils.PasswordUtils;
import views.json.JsonError;

/**
 * Controller for managing users and authentication.
 */
public class UserController extends Controller {
  
  private static final Logger.ALogger logger = Logger.of(UserController.class);

  /**
   * Facilitates the back-end portion of the sign-up process.
   * @return
   */
  @SubjectNotPresent
  @BodyParser.Of(BodyParser.Json.class)
  public Result signUp() {
    JsonNode body = request().body().asJson();
    
    logger.debug(String.format("Received user creation request: %s", body.toString()));
    
    Optional<Result> missing = JsonUtils.missingFieldsResult(body, "email", "password", "firstName", "lastName");
    
    if (missing.isPresent())
      return missing.get();
    
    // check that a user with that e-mail address doesn't exist yet
    if (User.find.where().eq("email", body.get("email").asText()).findRowCount() > 0)
      return badRequest(new JsonError("A user with that e-mail address already exists").toJson());
    
    // create the user
    User user = new User();
    
    user.setEmail(JsonUtils.getString(body, "email", null));
    user.setPasswordHash(
        PasswordUtils.computeHash(
            JsonUtils.getString(body, "password", null)));
    user.setFirstName(JsonUtils.getString(body, "firstName", null));
    user.setLastName(JsonUtils.getString(body, "lastName", null));
    user.save();
    
    return ok(user.toJson());
  }
  
  
  @SubjectPresent
  public Result getUser(long id) {
    logger.debug(String.format("Attempting to find user with ID: %d", id));
    User user = User.find.byId(id);
    
    if (user == null)
      return notFound(new JsonError("Cannot find the specified user").toJson());
    
    return ok(user.toJson());
  }
  
}
