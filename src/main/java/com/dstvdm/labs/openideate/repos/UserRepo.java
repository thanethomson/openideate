package com.dstvdm.labs.openideate.repos;

import java.util.Base64;

import com.dstvdm.labs.openideate.models.User;
import com.dstvdm.labs.openideate.repos.exceptions.AlreadyExists;
import com.dstvdm.labs.openideate.repos.exceptions.DoesNotExist;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * Allows us to manipulate User objects in the database.
 */
public class UserRepo {

  public UserRepo() {}
  
  /**
   * Shortcut to create a user in the database.
   * @param email
   * @param password
   * @param firstName
   * @param lastName
   * @return
   * @throws AlreadyExists 
   */
  public User createUser(String email, String password, String firstName, String lastName) throws AlreadyExists {
    // first make sure that no other user with that e-mail address exists
    if (User.findByEmail(email) != null) {
      throw new AlreadyExists(String.format("User with e-mail address %s already exists", email));
    }
    
    User user = new User();
    
    user.setEmail(email);
    user.setPasswordHash(computePasswordHash(password));
    user.setEmailConfirmed(false);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    
    // save to the database
    user.save();
    return user;
  }
  
  /**
   * Helper function to compute the password hash of a particular password.
   * At present, this returns the Base64-encoded version of the password hash
   * using the SHA-256 hash algorithm.
   * @param password
   * @return
   */
  public static String computePasswordHash(String password) {
    return Base64.getEncoder().encodeToString(Hashing.sha256().hashString(password, Charsets.UTF_8).asBytes());
  }
  
  /**
   * For our basic HTTP authentication mechanism - validates the login.
   * @param email
   * @param password
   * @return
   * @throws DoesNotExist 
   */
  public boolean isValidLogin(String email, String password) throws DoesNotExist {
    // first try to find the user with the given e-mail address
    User user = User.findByEmail(email);
    
    if (user == null) {
      throw new DoesNotExist(String.format("User with e-mail address %s does not exist", email));
    }
    
    // validate the user's password
    return computePasswordHash(password).equals(user.getPasswordHash());
  }

}
