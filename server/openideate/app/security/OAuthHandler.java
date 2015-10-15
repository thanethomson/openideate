package security;

import java.util.Optional;

import be.objectify.deadbolt.core.models.Subject;
import models.User;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.Context;

public class OAuthHandler {
  
  private static final Logger.ALogger logger = Logger.of(OAuthHandler.class);
  
  
  public static Promise<Optional<Subject>> handle(String authStr, Context ctx) {
    if (authStr.startsWith("token ")) {
      String token = authStr.substring(6);
      logger.debug(String.format("Got incoming access token: %s", token));
      
      // try to find the first instance of this access token
      User user = User.find.where().eq("accessToken", token).findUnique();
      
      // if we've found the relevant user
      if (user != null) {
        logger.debug(String.format("Token %s is associated with user %s", token, user.getEmail()));
        ctx.args.put("user", user);
        return Promise.promise(() -> Optional.of(user));
      }
    }
    
    // no access
    return Promise.promise(Optional::empty);
  }

}
