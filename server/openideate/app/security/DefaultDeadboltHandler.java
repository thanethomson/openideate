package security;

import java.util.Optional;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
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
    logger.debug("Attempting to get subject for default Deadbolt handler");
    // check if we have the auth header
    if (ctx.request().hasHeader("Authorization")) {
      String authStr = ctx.request().getHeader("Authorization");
      
      if (authStr.startsWith("Basic ")) {
        return BasicAuthHandler.handle(authStr, ctx);
      } else if (authStr.startsWith("token ")) {
        return OAuthHandler.handle(authStr, ctx);
      }
    }
    
    // no access
    return Promise.promise(Optional::empty);
  }
  
  @Override
  public Promise<Result> onAuthFailure(final Context ctx, final String content) {
    ctx.response().setHeader("WWW-Authenticate", "Basic realm=\"openideate\"");
    // ask the caller to authenticate
    return Promise.promise(() -> status(401, new JsonError("Not authorized").toJson()));
  }

}
