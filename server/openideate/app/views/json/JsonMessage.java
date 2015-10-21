package views.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

public class JsonMessage extends JsonBase {
  
  public String message = null;

  public JsonMessage() {}
  
  public JsonMessage(String message) {
    this.message = message;
  }

  @Override
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    obj.put("message", message);
    return obj;
  }

}
