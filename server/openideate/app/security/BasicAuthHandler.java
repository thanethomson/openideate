package security;

import java.util.Base64;
import java.util.Optional;

import be.objectify.deadbolt.core.models.Subject;
import models.User;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.Context;

public class BasicAuthHandler {
  
  private final static Logger.ALogger logger = Logger.of(BasicAuthHandler.class);
  
  public static Promise<Optional<Subject>> handle(String authStr, Context ctx) {
    if (authStr.startsWith("Basic ")) {
      String encodedCreds = authStr.substring(6);
      logger.debug(String.format("Received encoded credentials: %s", encodedCreds));
      
      String creds;
      
      try {
        creds = new String(Base64.getDecoder().decode(encodedCreds));
      } catch (Throwable e) {
        logger.error("Cannot decode incoming HTTP credentials", e);
        creds = null;
      }
      
      if (creds != null) {
        if (creds.contains(":")) {
          String email = creds.substring(0, creds.indexOf(":"));
          String password = creds.substring(creds.indexOf(":")+1);
          
          // try to log the user in
          User user = User.find.where().eq("email", email).findUnique();
          
          // if we've found the user
          if (user != null) {
            // check the user's password
            if (user.isValidPassword(password)) {
              logger.debug(String.format("Successfully authenticated user: %s", email));
              // store the user's details in the cache
              ctx.args.put("user", user);
              return Promise.promise(() -> Optional.of(user));
            } else {
              logger.debug("Invalid password supplied");
            }
          } else {
            logger.error(String.format("User with e-mail address %s not found", email));
          }
        } else {
          logger.error(String.format("Invalid format for incoming credentials: %s", creds));
        }
      }
    }
    
    // no access
    return Promise.promise(Optional::empty);
  }
  
}
