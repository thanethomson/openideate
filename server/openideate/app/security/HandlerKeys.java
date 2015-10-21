package security;

import be.objectify.deadbolt.java.ConfigKeys;

public enum HandlerKeys {
  DEFAULT(ConfigKeys.DEFAULT_HANDLER_KEY),
  FRONTEND("frontend");
  
  public final String key;
  
  private HandlerKeys(final String key) {
    this.key = key;
  }
}
