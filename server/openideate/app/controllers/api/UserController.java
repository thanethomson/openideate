package controllers.api;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import models.User;
import play.Logger;
import play.mvc.*;
import views.json.JsonError;

public class UserController extends Controller {
  
  private static final Logger.ALogger logger = Logger.of(UserController.class);
  
  @SubjectPresent
  public Result getUserById(Long id) {
    logger.debug(String.format("Attempting to find user with ID: %d", id));
    
    User user = User.find.byId(id);
    if (user == null) {
      return notFound(new JsonError("User not found").toJson());
    }
    
    // return the JSON form for the user
    return ok(user.toJson());
  }

}
