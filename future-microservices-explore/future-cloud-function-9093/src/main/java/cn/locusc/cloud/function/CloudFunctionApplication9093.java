package cn.locusc.cloud.function;

import cn.locusc.cloud.function.functional.StringFunctional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import static cn.locusc.cloud.function.functional.StringFunctional.trim;

/**
 * 主类可以直接注册functional
 * the functional need support from reactive
 */
@SpringBootApplication
public class CloudFunctionApplication9093 implements ApplicationContextInitializer<GenericApplicationContext> {

    public static void main(String[] args) {
        // FunctionalSpringApplication.run(CloudFunctionApplication9093.class);
        SpringApplication.run(CloudFunctionApplication9093.class);
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        applicationContext.registerBean("trim", FunctionRegistration.class,
                () -> new FunctionRegistration<>(trim())
                        .type(FunctionType.from(String.class).to(String.class))
        );
        applicationContext.registerBean("echo", FunctionRegistration.class,
                () -> new FunctionRegistration<>(new StringFunctional())
                        .type(FunctionType.supplier(String.class))
        );
    }

}
