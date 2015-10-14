package views.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

/**
 * A generic JSON error message.
 */
public class JsonError extends JsonBase {
  
  public String error = null;

  public JsonError() {}
  
  public JsonError(String error) {
    this.error = error;
  }

  @Override
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    obj.put("error", this.error);
    return obj;
  }

}
