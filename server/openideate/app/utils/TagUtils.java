package utils;

public class TagUtils {
  
  private static final String TAG_CHARS = "abcdefghijklmnopqrstuvwxyz -_";
  
  /**
   * Helper function to convert the specified string into a tag string,
   * stripping it of all unnecessary bits and pieces and converting it to
   * lowercase.
   * @param s
   * @return
   */
  public static String toTag(String s) {
    String lower = s.toLowerCase().trim();
    String result = "", last = "", cur = "";
    
    // strip out any unwanted characters
    for (int i=0;i<lower.length();i++) {
      cur = lower.substring(i, i+1);
      
      if (TAG_CHARS.contains(cur)) {
        // we don't want extra whitespace between words
        if (cur.equals(" ") && last.equals(" ")) {
          continue;
        }
        result += cur;
        last = cur;
      }
    }
    
    return result;
  }
  
  /**
   * Checks whether the specified string is a valid tag.
   * @param s
   * @return
   */
  public static boolean isValidTag(String s) {
    // must be at least 2 chars long
    if (s.length() < 2)
      return false;
    
    return true;
  }

}
