package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.validation.*;
import play.libs.Json;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A tag relevant to one or more ideas.
 */
@Entity
@Table(name="idea_tags")
public class IdeaTag extends Model {
  
  public static Finder<Integer, IdeaTag> find = new Finder<Integer, IdeaTag>(IdeaTag.class);
  
  @Id
  private Integer id;
  
  /** The short, descriptive, unique name for this tag. */
  @Column(length=50, unique=true)
  @Constraints.Required
  private String name;

  public IdeaTag() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  
  public long getIdeaCount() {
    return Idea.find.where().eq("tags.id", getId()).findRowCount();
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public ObjectNode toJson() {
    ObjectNode obj = Json.newObject();
    
    obj.put("id", getId());
    obj.put("name", getName());
    
    return obj;
  }
  
  public ObjectNode toJsonWithCounts() {
    ObjectNode obj = toJson();
    
    obj.put("ideaCount", getIdeaCount());
    
    return obj;
  }

}
