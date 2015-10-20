package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import constants.DateTimeFormats;
import play.data.format.Formats;
import play.data.validation.*;
import play.libs.Json;
import utils.JsonUtils;

/**
 * Our primary model: the idea. This encapsulates all of the relevant
 * info relating to a logged idea.
 */
@Entity
@Table(name="ideas")
public class Idea extends Model {
  
  public static Finder<Long, Idea> find = new Finder<Long, Idea>(Idea.class);
  
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
  @ManyToOne(cascade=CascadeType.ALL)
  private User creator;
  
  /** From which other idea did we fork this one? */
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea forkedFrom;
  
  @ManyToMany(cascade=CascadeType.ALL)
  private List<IdeaTag> tags = new ArrayList<>();
  
  /** When this idea was created. */
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenCreated = new Date();
  
  /** When this idea was last updated. */
  @Formats.DateTime(pattern=DateTimeFormats.ISO8601_FORMAT_STRING)
  private Date whenUpdated = new Date();
  
  @OneToMany(cascade=CascadeType.ALL)
  private List<IdeaStar> starrings = new ArrayList<>();
  
  
  public Idea() {}
  
  
  /**
   * Attempts to build this idea object up from the specified JSON node.
   * @param json
   * @param creator The user creating the idea.
   */
  public Idea(JsonNode json, User creator, Idea forkedFrom, List<IdeaTag> tags) {
    if (json.hasNonNull("id"))
      setId(JsonUtils.getLong(json, "id", null));
    if (json.hasNonNull("version"))
      setVersion(JsonUtils.getLong(json, "version", null));
    
    setTitle(JsonUtils.getString(json, "title", null));
    setDetails(JsonUtils.getString(json, "details", null));
    setCreator(creator);
    
    if (forkedFrom != null)
      setForkedFrom(forkedFrom);
    
    if (tags != null)
      setTags(tags);
  }


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
  
  /**
   * Builds up the JSON object representing this instance.
   * @return
   */
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    ArrayNode tags;
    
    obj.put("id", getId());
    obj.put("version", getVersion());
    obj.put("title", getTitle());
    obj.put("details", getDetails());
    obj.put("creatorId", getCreator().getId());
    obj.put("forkedFromId", getForkedFrom() != null ? getForkedFrom().getId() : null);
    obj.put("created", DateTimeFormats.ISO8601_FORMAT.format(getWhenCreated()));
    obj.put("updated", DateTimeFormats.ISO8601_FORMAT.format(getWhenUpdated()));
    tags = obj.putArray("tags");
    for (IdeaTag tag: getTags()) {
      tags.add(tag.getName());
    }
    obj.put("stars", getStarredCount());
    obj.put("upVotes", getUpvoteCount());
    obj.put("downVotes", 0);
    
    return obj;
  }
  
  public ObjectNode toJson(User user) {
    ObjectNode obj = toJson();
    
    // has this idea been starred by this user?
    obj.put("starred", (getIdeaStar(user) != null));
    // has this idea been upvoted by this user?
    obj.put("upvoted", (getIdeaUpvote(user) != null));
    
    return obj;
  }
  
  /**
   * Helps to star this idea by the specified user.
   * @param byUser
   * @return
   */
  public IdeaStar star(User byUser) {
    IdeaStar ideaStar = getIdeaStar(byUser);
    
    if (ideaStar == null) {
      ideaStar = new IdeaStar();
      ideaStar.setIdea(this);
      ideaStar.setUser(byUser);
      starrings.add(ideaStar);
      save();
    }
    
    return ideaStar;
  }
  
  /**
   * Toggles the "starred" status of this idea by the given user.
   * @param byUser
   * @return
   */
  public IdeaStar toggleStar(User byUser) {
    IdeaStar ideaStar = getIdeaStar(byUser);
    
    if (ideaStar == null) {
      ideaStar = new IdeaStar();
      ideaStar.setIdea(this);
      ideaStar.setUser(byUser);
      starrings.add(ideaStar);
      save();
    } else {
      // delete the star
      ideaStar.setIdea(null);
      ideaStar.setUser(null);
      ideaStar.save();
      ideaStar.delete();
      ideaStar = null;
    }
    
    return ideaStar;
  }
  
  /**
   * Toggles the "upvote" status of this idea by the given user. Note that
   * this automatically removes any "downvotes" associated with this idea for
   * the given user.
   * @param byUser
   * @return
   */
  public IdeaUpvote toggleUpvote(final User byUser) {
    IdeaUpvote upvote = getIdeaUpvote(byUser);
    final Idea _this = this;
    
    // if there's no upvote yet
    if (upvote == null) {
      return Ebean.execute(() -> {
        // TODO: first try to delete any downvotes
        // now create the new upvote
        IdeaUpvote _upvote = new IdeaUpvote();
        _upvote.setIdea(_this);
        _upvote.setUser(byUser);
        _upvote.save();
        
        return _upvote;
      });
    } else {
      // delete the upvote
      upvote.setIdea(null);
      upvote.setUser(null);
      upvote.save();
      upvote.delete();
      return null;
    }
  }
  
  private IdeaUpvote getIdeaUpvote(User byUser) {
    return IdeaUpvote.find.where()
        .eq("idea.id", getId())
        .eq("user.id", byUser.getId())
        .findUnique();
  }
  
  /**
   * Attempts to retrieve the number of "upvotes" of this idea.
   * @return
   */
  public long getUpvoteCount() {
    return IdeaUpvote.find.where()
        .eq("idea.id", getId())
        .findRowCount();
  }
  
  /**
   * Attempts to retrieve the number of "starrings" of this idea.
   * @return
   */
  public long getStarredCount() {
    return IdeaStar.find.where()
        .eq("idea.id", getId())
        .findRowCount();
  }
  
  /**
   * Helps to find the ideas starred by the specified user.
   * @param byUser
   * @param pageNo
   * @param pageSize
   * @return
   */
  public static PagedList<Idea> findStarred(User byUser, int pageNo, int pageSize) {
    return Idea.find.where()
        .eq("starrings.user.id", byUser.getId())
        .findPagedList(pageNo, pageSize);
  }
  
  
  /**
   * Helps to check whether the given idea is already starred by the given user.
   * @param user
   * @param idea
   * @return
   */
  public static boolean isStarred(User user, Idea idea) {
    return (idea.getIdeaStar(user) != null);
  }
  
  /**
   * Tries to get the IdeaStar object associated with the starring of this idea
   * by the given user, if any.
   * @param byUser
   * @return
   */
  private IdeaStar getIdeaStar(User byUser) {
    return IdeaStar.find.where()
        .eq("idea.id", getId())
        .eq("user.id", byUser.getId())
        .findUnique();
  }


  public List<IdeaStar> getStarrings() {
    return starrings;
  }


  public void setStarrings(List<IdeaStar> starrings) {
    this.starrings = starrings;
  }

}
