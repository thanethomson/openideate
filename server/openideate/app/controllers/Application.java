package controllers;

import java.util.UUID;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import forms.LoginForm;
import play.*;
import play.data.Form;
import play.mvc.*;
import security.BasicAuthHandler;
import models.User;

/**
 * The primary web interface.
 */
public class Application extends Controller {
  
  private static final Logger.ALogger logger = Logger.of(Application.class);
  
  
  @SubjectPresent(handlerKey="frontend")
  public Result index() {
    return ok(views.html.index.render(request(), (User)ctx().args.get("user")));
  }
  
  
  public Result login() {
    // if we're already logged in, go to the home page
    if (ctx().args.containsKey("user")) {
      return redirect(controllers.routes.Application.index());
    }
    
    if (!session().containsKey("oauthState")) {
      session().put("oauthState", UUID.randomUUID().toString());
    }
    
    // otherwise render our login page
    return ok(views.html.login.render(request(), null, session().get("oauthState")));
  }
  
  
  public Result doLogin() {
    Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest(request());
    
    logger.debug("Got login info");
    
    // check if we have any login errors
    if (loginForm.hasErrors() || loginForm.hasGlobalErrors()) {
      return badRequest(views.html.login.render(request(), loginForm, session().get("oauthState")));
    } else {
      LoginForm login = loginForm.get();
      
      logger.debug(String.format("Login attempt for user: %s", login.getEmail()));
      User user = login.getUser();
      logger.debug(String.format("Found user: %s", user.toJson().toString()));
      session().put("Authorization", BasicAuthHandler.generateAuthHeader(login.getEmail(), login.getPassword()));
    }
    
    // go to the home page otherwise
    return redirect(controllers.routes.Application.index());
  }
  
  
  public Result logout() {
    if (session().containsKey("Authorization")) {
      logger.debug(String.format("Logout request received for auth string: %s", session().get("Authorization")));
      session().remove("Authorization");
    }
    
    if (session().containsKey("oauthToken")) {
      session().remove("oauthToken");
    }
    if (session().containsKey("oauthProvider")) {
      session().remove("oauthProvider");
    }
    
    if (ctx().args.containsKey("user")) {
      User user = (User)ctx().args.get("user");
      
      user.setAccessToken(null);
      user.save();
      logger.debug(String.format("Removed access token for user: %s", user.getEmail()));
    }
    
    return redirect(controllers.routes.Application.index());
  }
  
  
  public Result signup() {
    // if we're already logged in, go to the home page
    if (ctx().args.containsKey("user")) {
      return redirect(controllers.routes.Application.index());
    }
    
    return ok(views.html.signup.render(request(), null));
  }
  
  
  public Result doSignup() {
    return ok("OK");
  }

}
