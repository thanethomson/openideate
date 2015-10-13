package views.json;

import play.libs.Json;

/**
 * The generic base class for JSON objects.
 */
public abstract class JsonBase {
  
  @Override
  public String toString() {
    return Json.toJson(this).toString();
  }

}
