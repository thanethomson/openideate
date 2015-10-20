package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;
import utils.PasswordUtils;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.providers.oauth2.github.GithubAuthUser;
import com.feth.play.module.pa.providers.oauth2.google.GoogleAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import constants.DateTimeFormats;

/**
 * Encapsulates a system user.
 */
@Entity
@Table(name="users")
public class User extends Model implements Subject {
  
  public static Finder<Long, User> find = new Finder<Long, User>(User.class);

  @Id
  private Long id;
  
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenCreated = new Date();
  
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date lastLogin = new Date();
  
  /** This user's e-mail address (their unique identifier). */
  @Constraints.Email
  @Column(unique=true, length=300)
  private String email;
  
  @Constraints.Required
  @Column(length=100)
  private String passwordHash;
  
  private String name;
  
  /** When was this user's e-mail address validated? */
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenEmailValidated = null;
  
  @OneToMany(cascade=CascadeType.ALL)
  private List<LinkedAccount> linkedAccounts = new ArrayList<>();
  
  private Boolean active = true;
  
  /** The URL for this user's profile picture. */
  @Lob
  private String pictureUrl = null;
  
  
  public User() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getWhenCreated() {
    return whenCreated;
  }

  public void setWhenCreated(Date whenCreated) {
    this.whenCreated = whenCreated;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getWhenEmailValidated() {
    return whenEmailValidated;
  }

  public void setWhenEmailValidated(Date whenEmailValidated) {
    this.whenEmailValidated = whenEmailValidated;
  }
  
  public boolean isValidPassword(String password) {
    return getPasswordHash().equals(PasswordUtils.computeHash(password));
  }
  
  /**
   * Converts this user object into its JSON representation.
   * @return
   */
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    
    obj.put("id", getId());
    obj.put("email", getEmail());
    obj.put("created", DateTimeFormats.ISO8601_FORMAT.format(getWhenCreated()));
    obj.put("lastLogin", DateTimeFormats.ISO8601_FORMAT.format(getLastLogin()));
    obj.put("name", getName());
    obj.put("pictureUrl", getPictureUrl());
    
    return obj;
  }

  @Override
  public String getIdentifier() {
    return Long.toString(getId());
  }

  @Override
  public List<? extends Permission> getPermissions() {
    return null;
  }

  @Override
  public List<? extends Role> getRoles() {
    return null;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
  }
  
  public static boolean existsByAuthUserIdentity(final AuthUserIdentity identity) {
    return getAuthUserFind(identity).findRowCount() > 0;
  }
  
  private static ExpressionList<User> getAuthUserFind(final AuthUserIdentity identity) {
    return find.where().eq("active", true)
        .eq("linkedAccounts.providerUserId", identity.getId())
        .eq("linkedAccounts.providerKey", identity.getProvider());
  }
  
  public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
    if (identity == null)
      return null;
    
    return getAuthUserFind(identity).findUnique();
  }

  public List<LinkedAccount> getLinkedAccounts() {
    return linkedAccounts;
  }

  public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
    this.linkedAccounts = linkedAccounts;
  }
  
  public void addLinkedAccount(final LinkedAccount linkedAccount) {
    this.linkedAccounts.add(linkedAccount);
    save();
  }
  
  /**
   * Creates a User model entry from the given AuthUser structure.
   * @param authUser
   * @return
   */
  public static User create(final AuthUser authUser) {
    final User user = new User();
    
    if (authUser.getProvider().equals("google")) {
      GoogleAuthUser googleUser = (GoogleAuthUser)authUser;
      user.setName(googleUser.getName());
      user.setEmail(googleUser.getEmail());
      user.setPictureUrl(googleUser.getPicture());
    } else if (authUser.getProvider().equals("github")) {
      GithubAuthUser githubUser = (GithubAuthUser)authUser;
      user.setName(githubUser.getName());
      user.setEmail(githubUser.getEmail());
      user.setPictureUrl(githubUser.getPicture());
    }
    user.addLinkedAccount(LinkedAccount.create(authUser));
    // create the entry in the database
    user.save();
    
    return user;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }

}
