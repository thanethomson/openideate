package com.dstvdm.labs.openideate.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

/**
 * The central model for this application: the idea. An idea, in this context,
 * is pretty much just a description of an idea of some kind.
 */
@Entity
@Table(name="ideas")
public class Idea extends Model {
  
  @Id
  private Long id;
  
  @Version
  private Long version;
  
  @CreatedTimestamp
  private Timestamp whenCreated;
  
  @UpdatedTimestamp
  private Timestamp whenUpdated;
  
  @Column(length=255)
  private String title;
  
  @Lob
  private String description;
  
  @ManyToMany(cascade=CascadeType.ALL)
  private List<IdeaTag> tags = new ArrayList<>();
  
  /** The user who created this idea. */
  @ManyToOne(cascade=CascadeType.ALL)
  private User owner;
  
  /** The original idea from which this idea was forked. */
  @ManyToOne(cascade=CascadeType.ALL)
  private Idea forkedFrom;
  
  /** The up-votes for this idea. */
  @OneToMany(mappedBy="idea", cascade=CascadeType.ALL)
  private List<IdeaUpVote> upVotes = new ArrayList<>();
  
  /** The down-votes for this idea. */
  @OneToMany(mappedBy="idea", cascade=CascadeType.ALL)
  private List<IdeaDownVote> downVotes = new ArrayList<>();
  
  @OneToMany(mappedBy="idea", cascade=CascadeType.ALL)
  private List<IdeaComment> comments = new ArrayList<>();

  
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

  public Timestamp getWhenCreated() {
    return whenCreated;
  }

  public void setWhenCreated(Timestamp whenCreated) {
    this.whenCreated = whenCreated;
  }

  public Timestamp getWhenUpdated() {
    return whenUpdated;
  }

  public void setWhenUpdated(Timestamp whenUpdated) {
    this.whenUpdated = whenUpdated;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<IdeaTag> getTags() {
    return tags;
  }

  public void setTags(List<IdeaTag> tags) {
    this.tags = tags;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Idea getForkedFrom() {
    return forkedFrom;
  }

  public void setForkedFrom(Idea forkedFrom) {
    this.forkedFrom = forkedFrom;
  }

  public List<IdeaComment> getComments() {
    return comments;
  }

  public void setComments(List<IdeaComment> comments) {
    this.comments = comments;
  }
  
  public void addTag(IdeaTag tag) {
    this.tags.add(tag);
  }
  
  public void addUpVote(IdeaUpVote upVote) {
    this.upVotes.add(upVote);
  }
  
  public void addDownVote(IdeaDownVote downVote) {
    this.downVotes.add(downVote);
  }
  
  public void addComment(IdeaComment comment) {
    this.comments.add(comment);
  }

}
