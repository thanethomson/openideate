package security;

import java.util.Optional;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUserIdentity;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import models.User;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.Context;
import play.mvc.Result;
import views.json.JsonError;

/**
 * Our internal Deadbolt handler for auth. For now, this does basic HTTP
 * authentication.
 */
public class DefaultDeadboltHandler extends AbstractDeadboltHandler {
  
  private final static Logger.ALogger logger = Logger.of(DefaultDeadboltHandler.class);

  @Override
  public Promise<Optional<Result>> beforeAuthCheck(Context ctx) {
    return Promise.promise(Optional::empty);
  }
  
  /**
   * Implements basic HTTP auth.
   */
  @Override
  public Promise<Optional<Subject>> getSubject(final Context ctx) {
    logger.debug("Attempting to get subject for default (API) Deadbolt handler");
    
    final AuthUserIdentity identity = PlayAuthenticate.getUser(ctx);
    
    if (identity != null) {
      logger.debug(String.format("Got AuthUserIdentity value: id=%s, provider=%s", identity.getId(), identity.getProvider()));
      User user = User.findByAuthUserIdentity(identity);
      if (user != null) {
        logger.debug(String.format("Got user: %s", user.toJson().toString()));
        // keep the user object in the context for use in our controllers
        ctx.args.put("user", user);
        return Promise.pure(Optional.ofNullable((Subject)user));
      }
    } else {
      logger.debug("No user found in context");
    }
    
    // no access
    return Promise.promise(Optional::empty);
  }
  
  @Override
  public Promise<Result> onAuthFailure(final Context ctx, final String content) {
    logger.debug("Auth failure");
    // ask the caller to authenticate
    return Promise.promise(() -> status(401, new JsonError("Not authorized").toJson()));
  }

}
