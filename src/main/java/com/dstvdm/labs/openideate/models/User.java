package com.dstvdm.labs.openideate.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;

/**
 * An API user.
 */
@Entity
@Table(name="users")
public class User extends Model {
  
  @Id
  private Long id;
  
  @CreatedTimestamp
  private Timestamp whenCreated;
  
  /** The user's e-mail address (used as username). */
  @Column(unique=true,length=255)
  private String email;
  
  /** We only store password hashes if we store passwords (i.e. not relevant to OAuth). */
  @Column(length=255)
  private String passwordHash;
  
  /** Has this user's e-mail address been confirmed? */
  private Boolean emailConfirmed;
  
  /** When was this user's e-mail address confirmed? */
  private Timestamp whenEmailConfirmed;
  
  @Column(length=255)
  private String firstName;
  
  @Column(length=255)
  private String lastName;
  

  public User() {}


  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public Timestamp getWhenCreated() {
    return whenCreated;
  }


  public void setWhenCreated(Timestamp whenCreated) {
    this.whenCreated = whenCreated;
  }


  public String getEmail() {
    return email;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public String getPasswordHash() {
    return passwordHash;
  }


  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }


  public String getFirstName() {
    return firstName;
  }


  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }


  public String getLastName() {
    return lastName;
  }


  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public Boolean getEmailConfirmed() {
    return emailConfirmed;
  }


  public void setEmailConfirmed(Boolean emailConfirmed) {
    this.emailConfirmed = emailConfirmed;
  }


  public Timestamp getWhenEmailConfirmed() {
    return whenEmailConfirmed;
  }


  public void setWhenEmailConfirmed(Timestamp whenEmailConfirmed) {
    this.whenEmailConfirmed = whenEmailConfirmed;
  }
  
  public static User findById(Long id) {
    return Ebean.find(User.class).where().idEq(id).findUnique();
  }
  
  public static User findByEmail(String email) {
    return Ebean.find(User.class).where().eq("email", email).findUnique();
  }

}
