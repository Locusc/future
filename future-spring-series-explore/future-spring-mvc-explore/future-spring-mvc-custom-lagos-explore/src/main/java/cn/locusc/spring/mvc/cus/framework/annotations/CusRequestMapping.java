package cn.locusc.spring.mvc.cus.framework.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CusRequestMapping {
    String value() default "";
}
