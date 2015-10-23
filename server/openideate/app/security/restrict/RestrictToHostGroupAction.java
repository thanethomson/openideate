package security.restrict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.InetAddresses;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

import static play.Play.application;

/**
 * Assists in restricting access to controllers from specific IP addresses/ranges.
 * 
 * Based on http://feadro.com/simple-ip-access-list-for-play-2-1-with-java/
 */
public class RestrictToHostGroupAction extends Action<RestrictToHostGroup> {

  private static final Logger.ALogger logger = Logger.of(RestrictToHostGroupAction.class);
  public static final String CTX_KEY = "host-restrict";
  public static final String DEFAULT_GROUP = "default";
  public static final String CONFIG_KEY_GROUPS = "host-restrict.groups.";
  public static final String CONFIG_KEY_REDIRECT = "host-restrict.redirect";

  @Override
  public Promise<Result> call(Context ctx) throws Throwable {
    if (!ctx.args.containsKey(CTX_KEY)) {
      ctx.args.put(CTX_KEY, "");
      
      String value = configuration.value();
      String group = (value == null || value.isEmpty()) ? DEFAULT_GROUP : value;
      String remoteAddress = trimRemoteAddress(ctx.request().remoteAddress());
      
      // check the access list
      for (String pattern: getAllowedAddresses(group)) {
        if (addressMatchesPattern(remoteAddress, pattern)) {
          // allowed!
          return delegate.call(ctx);
        }
      }
      
      // denied
      logger.warn(String.format("Access denied to %s as per group \"%s\" restriction", remoteAddress, group));
      
      if (application().configuration().keys().contains(CONFIG_KEY_REDIRECT)) {
        return Promise.pure(redirect(application().configuration().getString(CONFIG_KEY_REDIRECT)));
      }
      
      return Promise.pure(forbidden("Access denied"));
    }
    
    // next!
    return delegate.call(ctx);
  }
  
  private List<String> getAllowedAddresses(String group) {
    List<String> result = new ArrayList<>(); // no access by default
    
    // make sure we have the specified group
    if (application().configuration().keys().contains(CONFIG_KEY_GROUPS + group)) {
      result.addAll(Arrays.asList(application().configuration().getString(CONFIG_KEY_GROUPS + group).split(",")));
    } else {
      logger.warn(String.format("No access group called \"%s\" - defaulting to no access", group));
    }
    
    logger.debug(String.format("Allowed IP address ranges: %s", String.join(", ", result)));
    
    return result;
  }
  
  private boolean addressMatchesPattern(String addr, String pattern) {
    return StringUtils.startsWith(addr, pattern);
  }
  
  private String trimRemoteAddress(String addr) {
    return isIPv6(addr) ? StringUtils.substringBefore(addr, "%") : addr;
  }
  
  private boolean isIPv6(String addr) {
    return StringUtils.contains(addr, ":");
  }

}
