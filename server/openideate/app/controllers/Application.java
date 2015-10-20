package controllers;

import com.feth.play.module.pa.controllers.Authenticate;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.*;
import play.mvc.*;
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
    // if we're already logged in, redirect the user
    if (ctx().args.containsKey("user"))
      return redirect(controllers.routes.Application.index());
    
    return ok(views.html.login.render(request()));
  }
  
  
  public Result logout() {
    return Authenticate.logout();
  }
  
  
  public Result authenticate(String provider) {
    return Authenticate.authenticate(provider);
  }
  
  
  @SubjectPresent(handlerKey="frontend")
  public Result myIdeas() {
    return ok(views.html.myIdeas.render(request(), (User)ctx().args.get("user")));
  }
  
  
  @SubjectPresent(handlerKey="frontend")
  public Result myStarredIdeas() {
    return ok(views.html.myStarredIdeas.render(request(), (User)ctx().args.get("user")));
  }
  
  @SubjectPresent(handlerKey="frontend")
  public Result listIdeasByTag(String tag) {
    return ok(views.html.ideasByTag.render(request(), (User)ctx().args.get("user"), tag));
  }

}
