package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.feth.play.module.pa.user.AuthUser;

/**
 * For linking accounts to a single user identity.
 */
@Entity
@Table(name="linked_accounts")
public class LinkedAccount extends Model {

  @Id
  private Long id;
  
  /** The user to whom this linked account belongs. */
  @ManyToOne
  private User user;
  
  private String providerUserId;
  private String providerKey;
  
  
  public LinkedAccount() {}
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }

  public String getProviderUserId() {
    return providerUserId;
  }

  public void setProviderUserId(String providerUserId) {
    this.providerUserId = providerUserId;
  }

  public String getProviderKey() {
    return providerKey;
  }

  public void setProviderKey(String providerKey) {
    this.providerKey = providerKey;
  }

  public static LinkedAccount create(final AuthUser authUser) {
    final LinkedAccount linkedAccount = new LinkedAccount();
    linkedAccount.update(authUser);
    return linkedAccount;
  }
  
  public void update(final AuthUser authUser) {
    setProviderKey(authUser.getProvider());
    setProviderUserId(authUser.getId());
  }
  
}
