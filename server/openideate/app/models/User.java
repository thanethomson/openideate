package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;
import utils.PasswordUtils;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import constants.DateTimeFormats;
import constants.SignupMethod;

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
  
  @Column(length=20)
  private SignupMethod signupMethod = null;
  
  @Lob
  private String avatarUrl = null;
  
  @Column(length=100)
  private String accessToken = null;
  
  
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
    obj.put("name", getName());
    obj.put("avatarUrl", getAvatarUrl());
    
    return obj;
  }

  @Override
  public String getIdentifier() {
    return getEmail();
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

  public SignupMethod getSignupMethod() {
    return signupMethod;
  }

  public void setSignupMethod(SignupMethod signupMethod) {
    this.signupMethod = signupMethod;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

}
