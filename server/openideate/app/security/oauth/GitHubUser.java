package security.oauth;

import com.fasterxml.jackson.databind.JsonNode;

import utils.JsonUtils;

public class GitHubUser {
  
  private String email = null;
  private String name = null;
  private String avatarUrl = null;

  public GitHubUser() {}
  
  public GitHubUser(JsonNode json) {
    setEmail(JsonUtils.getString(json, "email", null));
    setName(JsonUtils.getString(json, "name", null));
    setAvatarUrl(JsonUtils.getString(json, "avatar_url", null));
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

}
