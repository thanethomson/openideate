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
 * Represents a single upvote by a user for a single idea.
 */
@Entity
@Table(name="idea_upvotes")
public class IdeaUpvote extends Model {
  
  public static Finder<Long, IdeaUpvote> find = new Finder<>(IdeaUpvote.class);
  
  @Id
  private Long id;
  
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenCreated = new Date();
  
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea idea;
  
  @ManyToOne(cascade=CascadeType.ALL)
  private User user;

  
  public IdeaUpvote() {}

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

  public Idea getIdea() {
    return idea;
  }

  public void setIdea(Idea idea) {
    this.idea = idea;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
  
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    
    obj.put("id", getId());
    obj.put("created", DateTimeFormats.ISO8601_FORMAT.format(getWhenCreated()));
    obj.put("ideaId", getIdea().getId());
    obj.put("userId", getUser().getId());
    
    return obj;
  }

}
