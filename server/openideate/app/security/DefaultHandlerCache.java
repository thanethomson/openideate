package security;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;

@Singleton
public class DefaultHandlerCache implements HandlerCache {
  
  private final DeadboltHandler defaultHandler = new DefaultDeadboltHandler();
  private final Map<String, DeadboltHandler> handlers = new HashMap<>();

  public DefaultHandlerCache() {
    handlers.put(HandlerKeys.DEFAULT.key, defaultHandler);
    handlers.put(HandlerKeys.FRONTEND.key, new FrontendDeadboltHandler());
  }

  @Override
  public DeadboltHandler apply(final String key) {
    return handlers.get(key);
  }

  @Override
  public DeadboltHandler get() {
    return defaultHandler;
  }

}
