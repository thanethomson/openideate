package utils;

import java.util.Base64;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class PasswordUtils {

  /**
   * Computes the Base64-encoded SHA-256 hash of the given password.
   * @param password
   * @return
   */
  public static String computeHash(String password) {
    return Base64.getEncoder().encodeToString(
        Hashing.sha256().newHasher()
          .putString(password, Charsets.UTF_8)
          .hash()
          .asBytes());
  }

}
