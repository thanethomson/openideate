package services;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.google.inject.Inject;

import models.User;
import play.Application;


public class OpenIdeateUserServicePlugin extends UserServicePlugin {

  @Inject
  public OpenIdeateUserServicePlugin(Application app) {
    super(app);
  }

  @Override
  public Object getLocalIdentity(AuthUserIdentity identity) {
    final User u = User.findByAuthUserIdentity(identity);
    
    if (u != null) {
      return u.getId();
    } else {
      // not found
      return null;
    }
  }

  @Override
  public AuthUser link(AuthUser arg0, AuthUser arg1) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AuthUser merge(AuthUser arg0, AuthUser arg1) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object save(AuthUser authUser) {
    if (User.existsByAuthUserIdentity(authUser)) {
      // the user already exists
      return null;
    } else {
      return User.create(authUser).getId();
    }
  }

}
