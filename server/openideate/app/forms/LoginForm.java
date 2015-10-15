package forms;

import java.util.ArrayList;
import java.util.List;

import models.User;
import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

public class LoginForm {
  
  @Required
  private String email;
  @Required
  private String password;
  
  private User user = null;

  public LoginForm() {}
  
  public LoginForm(String email, String password) {
    setEmail(email);
    setPassword(password);
  }
  
  public List<ValidationError> validate() {
    List<ValidationError> errors = new ArrayList<>();
    
    user = User.find.where().eq("email", getEmail()).findUnique();
    
    if (user == null) {
      errors.add(new ValidationError("email", "User does not exist"));
    } else
    // validate the user's password
    if (!user.isValidPassword(getPassword())) {
      user = null;
      errors.add(new ValidationError("password", "Invalid password"));
    }
    
    return errors.isEmpty() ? null : errors;
  }

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
  
  public User getUser() {
    return user;
  }

}
