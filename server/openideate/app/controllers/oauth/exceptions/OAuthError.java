package controllers.oauth.exceptions;

public abstract class OAuthError extends Exception {

  public OAuthError() {
  }

  public OAuthError(String message) {
    super(message);
  }

  public OAuthError(Throwable cause) {
    super(cause);
  }

}
