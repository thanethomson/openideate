package com.dstvdm.labs.openideate.repos;

import com.avaje.ebean.Ebean;
import com.dstvdm.labs.openideate.models.Idea;
import com.dstvdm.labs.openideate.models.User;

/**
 * Repository for managing ideas.
 */
public class IdeaRepo {

  public IdeaRepo() {}
  
  /**
   * Helps us to create an idea in the system.
   * @param title
   * @param description
   * @param owner
   * @return
   */
  public Idea createIdea(String title, String description, User owner) {
    Idea idea = new Idea();
    
    idea.setTitle(title);
    idea.setDescription(description);
    idea.setOwner(owner);
    
    idea.save();
    
    return idea;
  }
  
  /**
   * Deletes the idea with the given ID.
   * @param id The ID of the idea to delete.
   */
  public void deleteIdea(Long id) {
    // delete the idea with that ID, if it exists
    Ebean.find(Idea.class).where().idEq(id).delete();
  }

}
