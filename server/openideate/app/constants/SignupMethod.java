package constants;

import com.avaje.ebean.annotation.EnumValue;

/**
 * People can sign up/in to OpenIdeate in various ways.
 */
public enum SignupMethod {
  @EnumValue("email")
  EMAIL,
  
  @EnumValue("github")
  GITHUB,
  
  @EnumValue("twitter")
  TWITTER,
  
  @EnumValue("google")
  GOOGLE,
  
  @EnumValue("facebook")
  FACEBOOK
}
