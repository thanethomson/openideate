package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;

import constants.DateTimeFormats;
import play.data.format.Formats;
import play.libs.Json;

/**
 * For when a user stars a particular idea.
 */
@Entity
@Table(name="idea_stars")
public class IdeaStar extends Model {
  
  public static Finder<Long, IdeaStar> find = new Finder<Long, IdeaStar>(IdeaStar.class);
  
  @Id
  private Long id;
  
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenCreated = new Date();
  
  /** The user who starred the idea. */
  @ManyToOne(cascade=CascadeType.ALL)
  private User user;
  
  /** The idea being starred. */
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea idea;
  

  public IdeaStar() {}


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


  public User getUser() {
    return user;
  }


  public void setUser(User user) {
    this.user = user;
  }


  public Idea getIdea() {
    return idea;
  }


  public void setIdea(Idea idea) {
    this.idea = idea;
  }
  
  
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    
    obj.put("id", getId());
    obj.put("userId", getUser().getId());
    obj.put("ideaId", getIdea().getId());
    obj.put("created", DateTimeFormats.ISO8601_FORMAT.format(getWhenCreated()));
    
    return obj;
  }

}
