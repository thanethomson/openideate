package com.dstvdm.labs.openideate.models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;

/**
 * A simple text tag to allow us to tag ideas.
 */
@Entity
@Table(name="ideas")
public class IdeaTag extends Model {
  
  @Id
  private Long id;
  
  @CreatedTimestamp
  private Timestamp whenCreated;
  
  @Column(length=100)
  private String name;
  
  @ManyToMany(mappedBy="tags", cascade=CascadeType.ALL)
  private List<Idea> ideas;
  

  public IdeaTag() {}


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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public List<Idea> getIdeas() {
    return ideas;
  }


  public void setIdeas(List<Idea> ideas) {
    this.ideas = ideas;
  }

}
