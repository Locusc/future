package cn.locusc.cloud.function.configuration;


import cn.locusc.cloud.function.endpoints.ActuatorFunctionEndPoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.cloud.function.actuator.FunctionsEndpoint;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jay
 * 注册functions健康检查端点
 * 2022/9/14
 */
@Configuration(proxyBeanMethods = false)
//@ConditionalOnBean(FunctionCatalog.class)
//@ConditionalOnBean(FunctionsEndpoint.class)
@ConditionalOnClass(
        name = {"org.springframework.boot.actuate.endpoint.annotation.Endpoint"}
)
@AutoConfigureAfter({EndpointAutoConfiguration.class})
public class ActuatorFuncAutoConfiguration {

    @Bean
    @ConditionalOnAvailableEndpoint(endpoint = ActuatorFunctionEndPoint.class)
    public ActuatorFunctionEndPoint actuatorFunctionEndPoint(FunctionCatalog functionCatalog) {
        return new ActuatorFunctionEndPoint(functionCatalog);
    }

}
