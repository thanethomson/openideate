package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.avaje.ebean.Model;

import constants.DateTimeFormats;
import play.data.format.Formats;
import play.data.validation.*;

/**
 * Our primary model: the idea. This encapsulates all of the relevant
 * info relating to a logged idea.
 */
@Entity
public class Idea extends Model {
  
  @Id
  private Long id;
  
  @Version
  private Long version;
  
  /** A short, descriptive title for this idea. */
  @Constraints.Required
  private String title;
  
  /** The full details of this idea. */
  @Constraints.Required
  @Lob
  private String details;
  
  /** Who created this idea? */
  @Constraints.Required
  @ManyToOne
  private User creator;
  
  /** From which other idea did we fork this one? */
  @ManyToOne
  private Idea forkedFrom;
  
  @ManyToMany
  private List<IdeaTag> tags = new ArrayList<>();
  
  /** When this idea was created. */
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenCreated = new Date();
  
  /** When this idea was last updated. */
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenUpdated = new Date();
  
  
  public Idea() {}


  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public Long getVersion() {
    return version;
  }


  public void setVersion(Long version) {
    this.version = version;
  }


  public String getTitle() {
    return title;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public String getDetails() {
    return details;
  }


  public void setDetails(String details) {
    this.details = details;
  }


  public User getCreator() {
    return creator;
  }


  public void setCreator(User creator) {
    this.creator = creator;
  }


  public Idea getForkedFrom() {
    return forkedFrom;
  }


  public void setForkedFrom(Idea forkedFrom) {
    this.forkedFrom = forkedFrom;
  }


  public List<IdeaTag> getTags() {
    return tags;
  }


  public void setTags(List<IdeaTag> tags) {
    this.tags = tags;
  }


  public Date getWhenCreated() {
    return whenCreated;
  }


  public void setWhenCreated(Date whenCreated) {
    this.whenCreated = whenCreated;
  }


  public Date getWhenUpdated() {
    return whenUpdated;
  }


  public void setWhenUpdated(Date whenUpdated) {
    this.whenUpdated = whenUpdated;
  }

}
