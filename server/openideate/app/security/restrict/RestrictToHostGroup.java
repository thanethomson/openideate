package security.restrict;

import java.lang.annotation.*;

import play.mvc.With;

@With(RestrictToHostGroupAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestrictToHostGroup {
  String value() default "default";
}
