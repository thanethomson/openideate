package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.*;

import com.avaje.ebean.Model;

/**
 * A tag relevant to one or more ideas.
 */
@Entity
public class IdeaTag extends Model {
  
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

  public void setName(String name) {
    this.name = name;
  }

}
