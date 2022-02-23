package cn.locusc.spring.mvc.cus.framework.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CusAutowired {
    String value() default "";
}
