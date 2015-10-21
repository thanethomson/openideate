package views.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class JsonBase {

  /**
   * Must be implemented in derived classes to return a Jackson-compatible
   * object node.
   */
  public abstract ObjectNode toJson();

}
