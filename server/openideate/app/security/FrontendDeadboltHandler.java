package security;

import java.util.Optional;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
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
    return Promise.promise(Optional::empty);
  }
  
  @Override
  public Promise<Optional<Subject>> getSubject(final Context ctx) {
    logger.debug("Attempting to get subject for frontend Deadbolt handler");
    
    String authStr = null;
    
    if (ctx.session() != null && ctx.session().containsKey("Authorization")) {
      authStr = ctx.session().get("Authorization");
    } else if (ctx.request().hasHeader("Authorization")) {
      authStr = ctx.request().getHeader("Authorization");
    }
    
    if (authStr != null) {
      return BasicAuthHandler.handle(authStr, ctx);
    }
    
    // no access otherwise
    return Promise.promise(Optional::empty);
  }
  
  @Override
  public Promise<Result> onAuthFailure(final Context ctx, final String content) {
    logger.debug("Auth failed - redirecting to login page");
    // redirect to the login page
    return Promise.promise(() -> redirect(controllers.routes.Application.login()));
  }

}
