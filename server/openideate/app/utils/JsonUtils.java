package utils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import views.json.JsonError;

public class JsonUtils {
  
  /**
   * Helps us establish if there are missing fields in a JSON object.
   * @param json
   * @param requiredFields
   * @return
   */
  public static Set<String> missingFields(JsonNode json, String... requiredFields) {
    Set<String> missing = new HashSet<>();
    
    for (String field: requiredFields) {
      if (!json.hasNonNull(field)) {
        missing.add(field);
      }
    }
    
    return missing;
  }
  
  public static Optional<Result> missingFieldsResult(JsonNode json, String... requiredFields) {
    Set<String> missing = missingFields(json, requiredFields);
    
    if (missing.size() > 0) {
      return Optional.of(
          Controller.badRequest(
              new JsonError(
                  String.format("Missing field(s) in request: %s",
                      String.join(", ", missing))).toJson()));
    }
    
    // all's good
    return Optional.empty();
  }
  
  public static String getString(JsonNode json, String key, String defaultValue) {
    if (json.hasNonNull(key)) {
      try {
        return json.get(key).asText();
      } catch (Throwable e) {
        return defaultValue;
      }
    }
    
    return defaultValue;
  }
  
  public static Long getLong(JsonNode json, String key, Long defaultValue) {
    if (json.hasNonNull(key)) {
      try {
        return json.get(key).asLong();
      } catch (Throwable e) {
        return defaultValue;
      }
    }
    
    return defaultValue;
  }
  
  public static Integer getInt(JsonNode json, String key, Integer defaultValue) {
    if (json.hasNonNull(key)) {
      try {
        return json.get(key).asInt();
      } catch (Throwable e) {
        return defaultValue;
      }
    }
    
    return defaultValue;
  }

}
