package controllers.oauth.exceptions;

public class OAuthInvalidState extends OAuthError {

  public OAuthInvalidState() {
  }

  public OAuthInvalidState(String message) {
    super(message);
  }

}
