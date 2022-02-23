package cn.locusc.spring.mvc.cus.framework.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CusController {
    String value() default "";
}
