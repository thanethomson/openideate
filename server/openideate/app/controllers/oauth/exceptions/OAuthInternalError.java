package controllers.oauth.exceptions;

public class OAuthInternalError extends OAuthError {

  public OAuthInternalError() {
  }

  public OAuthInternalError(String message) {
    super(message);
  }

}
