package modules;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.google.inject.AbstractModule;

import controllers.routes;
import play.Logger;
import play.mvc.Call;

public class AuthRoutingModule extends AbstractModule {
  
  private final static Logger.ALogger logger = Logger.of(AuthRoutingModule.class);
  
  
  public AuthRoutingModule() {
    logger.debug("Attempting to configure PlayAuthenticate resolver");
    // configure our auth routing resolver
    PlayAuthenticate.setResolver(new Resolver() {
      @Override
      public Call afterAuth() {
        return routes.Application.index();
      }

      @Override
      public Call afterLogout() {
        return routes.Application.index();
      }

      @Override
      public Call askLink() {
        return null;
      }

      @Override
      public Call askMerge() {
        return null;
      }

      @Override
      public Call auth(final String provider) {
        return routes.Application.authenticate(provider);
      }

      @Override
      public Call login() {
        return routes.Application.login();
      }
      
    });
  }

  @Override
  protected void configure() {
    // nothing needed here at present
  }

}
