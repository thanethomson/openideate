package com.dstvdm.labs.openideate.models;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;

/**
 * For keeping track of when users upvote a particular idea.
 */
@Entity
@Table(name="idea_upvotes")
public class IdeaUpVote extends Model {
  
  @Id
  private Long id;
  
  @CreatedTimestamp
  private Timestamp whenCreated;
  
  /** The idea being upvoted. */
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea idea;
  
  /** The user doing the up-voting. */
  @ManyToOne(cascade=CascadeType.ALL)
  private User user;

  public IdeaUpVote() {}

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

}
