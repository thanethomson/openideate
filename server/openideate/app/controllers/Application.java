package controllers;

import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.*;
import play.mvc.*;

import views.html.*;

/**
 * The primary web interface.
 */
public class Application extends Controller {

  @SubjectPresent(handlerKey="frontend")
  public Result index() {
    return ok(index.render(request()));
  }
  
  @SubjectNotPresent(handlerKey="frontend")
  public Result login() {
    return ok(login.render(request()));
  }

}
