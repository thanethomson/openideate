package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.Model;

import constants.DateTimeFormats;

/**
 * Encapsulates a system user.
 */
@Entity
public class User extends Model {

  @Id
  private Long id;
  
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenCreated = new Date();
  
  /** This user's e-mail address (their unique identifier). */
  @Constraints.Email
  @Column(unique=true)
  private String email;
  
  /** Our user's first name. */
  private String firstName;
  
  /** Our user's last name. */
  private String lastName;
  
  /** When was this user's e-mail address validated? */
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenEmailValidated = null;
  
  
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

  public Date getWhenEmailValidated() {
    return whenEmailValidated;
  }

  public void setWhenEmailValidated(Date whenEmailValidated) {
    this.whenEmailValidated = whenEmailValidated;
  }

}
