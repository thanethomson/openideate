package controllers.oauth.exceptions;

public class OAuthProviderError extends OAuthError {

  public OAuthProviderError() {
  }

  public OAuthProviderError(String message) {
    super(message);
  }

}
