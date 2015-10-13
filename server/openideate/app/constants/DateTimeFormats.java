package constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateTimeFormats {

  public final static String ISO8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm'Z'";
  public final static DateFormat ISO8601_FORMAT = new SimpleDateFormat(ISO8601_FORMAT_STRING);
  static {
    ISO8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

}
