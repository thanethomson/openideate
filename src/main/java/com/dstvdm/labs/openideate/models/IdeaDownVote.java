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
 * For keeping track of when users downvote a particular idea.
 */
@Entity
@Table(name="idea_downvotes")
public class IdeaDownVote extends Model {
  
  @Id
  private Long id;
  
  @CreatedTimestamp
  private Timestamp whenCreated;
  
  /** The idea being down-voted. */
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea idea;
  
  /** The user doing the down-voting. */
  @ManyToOne(cascade=CascadeType.ALL)
  private User user;
  
  public IdeaDownVote() {}

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
