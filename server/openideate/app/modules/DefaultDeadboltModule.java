package modules;

import com.google.inject.AbstractModule;

import be.objectify.deadbolt.java.cache.HandlerCache;
import security.DefaultHandlerCache;

/**
 * Dependency injection module to configure our auth handler.
 */
public class DefaultDeadboltModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(HandlerCache.class).to(DefaultHandlerCache.class).asEagerSingleton();
  }

}
