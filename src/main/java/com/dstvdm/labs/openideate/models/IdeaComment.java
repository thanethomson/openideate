package com.dstvdm.labs.openideate.models;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;

/**
 * A comment on a particular idea.
 */
@Entity
@Table(name="idea_comments")
public class IdeaComment extends Model {
  
  @Id
  private Long id;
  
  @CreatedTimestamp
  private Timestamp whenCreated;
  
  /** The idea on which this comment was made. */
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea idea;
  
  /** The user who made this comment. */
  @ManyToOne(cascade=CascadeType.ALL)
  private User user;
  
  /** The actual content of the comment. */
  @Lob
  private String content;

  public IdeaComment() {}

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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
