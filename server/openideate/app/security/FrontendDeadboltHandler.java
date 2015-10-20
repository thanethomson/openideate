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

/**
 * Login/auth handling for our Deadbolt front-end.
 */
public class FrontendDeadboltHandler extends AbstractDeadboltHandler {
  
  private final static Logger.ALogger logger = Logger.of(FrontendDeadboltHandler.class);

  @Override
  public Promise<Optional<Result>> beforeAuthCheck(Context ctx) {
    if (PlayAuthenticate.isLoggedIn(ctx.session())) {
      // user is logged in
      return Promise.pure(Optional.empty());
    } else {
      // store the URL to which the user wants to go
      PlayAuthenticate.storeOriginalUrl(ctx);
      ctx.flash().put("error", "You must log in first to view the requested page.");
      // redirect to our login page
      return Promise.promise(() -> Optional.ofNullable(redirect(PlayAuthenticate.getResolver().login())));
    }
  }
  
  @Override
  public Promise<Optional<Subject>> getSubject(final Context ctx) {
    logger.debug("Attempting to get subject for frontend Deadbolt handler");
    
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
    return Promise.pure(Optional.empty());
  }
  
  @Override
  public Promise<Result> onAuthFailure(final Context ctx, final String content) {
    logger.debug("Auth failure - redirecting to login page");
    // redirect to login page
    return Promise.promise(() -> redirect(controllers.routes.Application.login()));
  }

}
