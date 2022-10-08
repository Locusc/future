//package cn.locusc.cloud.function.configuration;
//
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.function.context.FunctionCatalog;
//import org.springframework.cloud.function.context.FunctionProperties;
//import org.springframework.cloud.function.context.MessageRoutingCallback;
//import org.springframework.cloud.function.context.config.RoutingFunction;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.expression.BeanFactoryResolver;
//import org.springframework.lang.Nullable;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Jay
// * 多路由示例
// * 2022/9/16
// */
//@Configuration
//public class MultipleRouterAutoConfiguration {
//
//    /**
//     * 函数只能根据优先级调用, 这里并不会调用routing-expression后调用routingCallback
//     * routingCallback优先级高于routing-expression以及definition
//     */
//    @Bean
//    public RoutingFunction echoRouter(FunctionCatalog functionCatalog, BeanFactory beanFactory,
//                                      @Qualifier("echoRouterCallback") @Nullable MessageRoutingCallback routingCallback) {
//        Map<String, String> propertiesMap = new HashMap<>();
//        propertiesMap.put(FunctionProperties.PREFIX + ".routing-expression", "'uppercase'");
//        return new RoutingFunction(functionCatalog, propertiesMap, new BeanFactoryResolver(beanFactory), routingCallback);
//    }
//
//    /**
//     * routing-expression支持spEL表达式
//     * 使用spEL表达式引用Ioc容器中reverse函数, 在调用返回结果的函数
//     * #root指向根对象
//     */
//    @Bean
//    public RoutingFunction spELRouter(FunctionCatalog functionCatalog, BeanFactory beanFactory,
//                                      @Nullable MessageRoutingCallback routingCallback) {
//        Map<String, String> propertiesMap = new HashMap<>();
//        propertiesMap.put(FunctionProperties.PREFIX + ".routing-expression", "@reverse.apply(#root.getHeaders().get('func'))");
//        return new RoutingFunction(functionCatalog, propertiesMap, new BeanFactoryResolver(beanFactory), routingCallback);
//    }
//
//}
