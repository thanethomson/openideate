package controllers.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import models.Idea;
import models.IdeaTag;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.*;
import utils.JsonUtils;
import utils.TagUtils;
import views.json.JsonError;
import views.json.JsonMessage;

/**
 * Our idea management API.
 */
public class IdeaController extends Controller {
  
  private static final Logger.ALogger logger = Logger.of(IdeaController.class);
  
  /**
   * Helps us to create a new idea.
   */
  @SubjectPresent
  @BodyParser.Of(BodyParser.Json.class)
  public Result create() {
    User user = (User)ctx().args.get("user");
    JsonNode body = request().body().asJson();
    
    logger.debug(String.format("Incoming idea creation request: %s", body.toString()));
    
    Optional<Result> missing = JsonUtils.missingFieldsResult(body, "title", "details");
    
    // if we've got missing fields
    if (missing.isPresent())
      return missing.get();
    
    Idea forkedFrom = null;
    
    // check if it's a forked idea
    if (body.hasNonNull("forkedFromId")) {
      Long forkedFromId = JsonUtils.getLong(body, "forkedFromId", null);
      
      if (forkedFromId == null) {
        return badRequest(new JsonError("Invalid ID for forked idea").toJson());
      }
      
      // try to find the idea
      forkedFrom = Idea.find.byId(forkedFromId);
      
      if (forkedFrom == null) {
        return notFound(new JsonError("The idea with the specified ID does not exist").toJson());
      }
      logger.debug(String.format("Forked idea from idea with ID %d", forkedFromId));
    }
    
    String tagString;
    List<IdeaTag> tags = new ArrayList<>();
    
    if (body.hasNonNull("tags")) {      
      List<String> rawTags;
      
      if (!body.get("tags").isArray()) {
        rawTags = Arrays.asList(body.get("tags").asText().split(","));
      } else {
        rawTags = new ArrayList<>();
        
        for (JsonNode el: body.get("tags")) {
          if (!el.isTextual()) {
            return badRequest(new JsonError("Each tag must be a string").toJson());
          }
          rawTags.add(el.asText());
        }
      }
      
      // now run through the tags and check them
      for (String rawTag: rawTags) {
        tagString = TagUtils.toTag(rawTag);
        if (!TagUtils.isValidTag(tagString)) {
          return badRequest(new JsonError("Invalid tag name").toJson());
        }
        
        // find or create the tag
        IdeaTag tag = IdeaTag.find.where().eq("name", tagString).findUnique();
        // create it if we can't find it
        if (tag == null) {
          tag = new IdeaTag();
          tag.setName(tagString);
          tag.save();
        }
        
        tags.add(tag);
      }
    }
    
    Idea idea = new Idea(body, user, forkedFrom, tags);
    
    try {
      idea.save();
    } catch (Throwable e) {
      logger.error("Unable to save new idea", e);
      return internalServerError(new JsonError("Internal server error").toJson());
    }
    
    // give back the idea as JSON
    return ok(idea.toJson());
  }
  
  
  /**
   * The primary interface through which we can list/search for ideas.
   * @param q A query string for searching for ideas.
   * @param pageNo
   * @param pageSize
   * @param order
   * @param dir
   * @param tags
   * @param creatorId
   * @param starredById
   * @return
   */
  @SubjectPresent
  public Result listIdeas(String q, int pageNo, int pageSize, String order,
      String dir, String tags, long creatorId, long starredById) {
    ObjectNode obj = Json.newObject();
    ArrayNode arr = obj.putArray("ideas");
    ExpressionList<Idea> expr = Idea.find.where();
    User user = (User)ctx().args.get("user");
    
    if (creatorId >= 0) {
      expr = expr.eq("creator.id", creatorId);
    }
    
    if (starredById >= 0) {
      expr = expr.eq("starrings.user.id", starredById);
    }
    
    if (q != null && q.length() > 0) {
      expr = expr.icontains("title", q);
    }
    
    if (tags != null && tags.length() > 0) {
      String[] tagArr = tags.split(",");
      Expression tagExpr = null;
      
      for (String tag: tagArr) {
        if (tagExpr == null) {
          tagExpr = Expr.eq("tags.name", tag);
        } else {
          tagExpr = Expr.or(tagExpr, Expr.eq("tags.name", tag));
        }
      }
      
      expr = expr.add(tagExpr);
    }
    
    PagedList<Idea> results = expr
      .orderBy(String.format("%s %s", order, dir))
      .findPagedList(pageNo, pageSize);
    
    for (Idea idea: results.getList()) {
      arr.add(idea.toJson(user));
    }
    
    obj.put("pageNo", pageNo);
    obj.put("pageSize", pageSize);
    obj.put("total", results.getTotalRowCount());
    
    return ok(obj);
  }
  
  
  /**
   * Allows us to list all of the tags in the database.
   * @return
   */
  @SubjectPresent
  public Result listTags() {
    ArrayNode arr = Json.newArray();
    
    // get all of the tags
    for (IdeaTag tag: IdeaTag.find.orderBy().asc("name").findList()) {
      arr.add(tag.toJsonWithCounts());
    }
    
    return ok(arr);
  }
  
  
  @SubjectPresent
  @BodyParser.Of(BodyParser.Json.class)
  public Result createTag() {
    JsonNode body = request().body().asJson();
    Optional<Result> missing = JsonUtils.missingFieldsResult(body, "name");
    
    if (missing.isPresent())
      return missing.get();
    
    String name = TagUtils.toTag(body.get("name").asText());
    
    logger.debug(String.format("Transformed incoming tag from \"%s\" to \"%s\"", body.get("name").asText(), name));
    if (!TagUtils.isValidTag(name)) {
      return badRequest(new JsonError("Invalid tag format").toJson());
    }
    
    // now check if the tag exists
    IdeaTag tag = IdeaTag.find.where().eq("name", name).findUnique();
    
    if (tag == null) {
      tag = new IdeaTag();
      tag.setName(name);
      tag.save();
    }
    
    // show our tag
    return ok(tag.toJson());
  }
  
  
  @SubjectPresent
  public Result delete(Long id) {
    logger.debug(String.format("Attempting to delete idea with ID: %d", id));
    Idea idea = Idea.find.byId(id);
    
    if (idea == null) {
      return notFound(new JsonError("Cannot find the idea with the given ID").toJson());
    }
    
    // first unlink the idea from any other models
    idea.setCreator(null);
    idea.setForkedFrom(null);
    idea.setStarrings(new ArrayList<>());
    idea.setTags(new ArrayList<>());
    idea.save();
    
    // delete the idea
    idea.delete();
    return ok(new JsonMessage("Deleted").toJson());
  }
  
  
  /**
   * Toggles the "starred" status of the idea with the specified ID.
   * @param id
   * @return
   */
  @SubjectPresent
  public Result toggleStar(Long id) {
    logger.debug(String.format("Attempting to toggle starred status of idea with ID: %d", id));
    
    Idea idea = Idea.find.byId(id);
    if (idea == null) {
      return notFound(new JsonError("Cannot find the idea with the given ID").toJson());
    }
    
    idea.toggleStar((User)ctx().args.get("user"));
    
    // return the full idea's details
    idea.refresh();
    return ok(idea.toJson((User)ctx().args.get("user")));
  }
  
  @SubjectPresent
  public Result toggleUpvote(Long id) {
    logger.debug(String.format("Attempting to toggle upvote status of idea with ID: %d", id));
    
    Idea idea = Idea.find.byId(id);
    if (idea == null) {
      return notFound(new JsonError("Cannot find the idea with the given ID").toJson());
    }
    
    idea.toggleUpvote((User)ctx().args.get("user"));
    
    // return the full idea's details
    idea.refresh();
    return ok(idea.toJson((User)ctx().args.get("user")));
  }
  
  
  @SubjectPresent
  public Result toggleDownvote(Long id) {
    logger.debug(String.format("Attempting to toggle downvote status of idea with ID: %d", id));
    
    Idea idea = Idea.find.byId(id);
    if (idea == null) {
      return notFound(new JsonError("Cannot find the idea with the given ID").toJson());
    }
    
    idea.toggleDownvote((User)ctx().args.get("user"));
    
    // return the full idea's details
    idea.refresh();
    return ok(idea.toJson((User)ctx().args.get("user")));
  }
  
  
  @SubjectPresent
  public Result getIdea(Long id) {
    logger.debug(String.format("Attempting to retrieve details of idea with ID: %d", id));
    
    Idea idea = Idea.find.byId(id);
    if (idea == null) {
      return notFound(new JsonError("Cannot find the idea with the given ID").toJson());
    }
    
    return ok(idea.toJson((User)ctx().args.get("user")));
  }

}
