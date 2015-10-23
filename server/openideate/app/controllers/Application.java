package controllers;

import com.feth.play.module.pa.controllers.Authenticate;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.*;
import play.mvc.*;
import security.restrict.RestrictToHostGroup;
import models.User;

/**
 * The primary web interface.
 */
public class Application extends Controller {
  
  private static final Logger.ALogger logger = Logger.of(Application.class);
  
  
  @RestrictToHostGroup
  @SubjectPresent(handlerKey="frontend")
  public Result index() {
    return ok(views.html.index.render("Home", "home", request(), (User)ctx().args.get("user"), null));
  }
  
  @RestrictToHostGroup
  public Result login() {
    // if we're already logged in, redirect the user
    if (ctx().args.containsKey("user"))
      return redirect(controllers.routes.Application.index());
    
    return ok(views.html.login.render(request()));
  }
  
  
  @RestrictToHostGroup
  public Result logout() {
    return Authenticate.logout();
  }
  
  
  @RestrictToHostGroup
  public Result authenticate(String provider) {
    return Authenticate.authenticate(provider);
  }
  
  
  @RestrictToHostGroup
  @SubjectPresent(handlerKey="frontend")
  public Result myIdeas() {
    return ok(views.html.index.render("- My Ideas", "mine", request(), (User)ctx().args.get("user"), null));
  }
  
  
  @RestrictToHostGroup
  @SubjectPresent(handlerKey="frontend")
  public Result myStarredIdeas() {
    return ok(views.html.index.render("- Starred Ideas", "starred", request(), (User)ctx().args.get("user"), null));
  }
  
  @RestrictToHostGroup
  @SubjectPresent(handlerKey="frontend")
  public Result listIdeasByTag(String tag) {
    return ok(views.html.index.render("- Tagged Ideas", "tagged", request(), (User)ctx().args.get("user"), tag));
  }
  
  public Result restricted() {
    return forbidden("You are not authorised to access this page/site.");
  }

}
