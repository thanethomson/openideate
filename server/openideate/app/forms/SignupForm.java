package forms;

import play.data.validation.Constraints.Required;

public class SignupForm {
  
  @Required
  private String email;
  @Required
  private String name;
  @Required
  private String password;

  public SignupForm() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
