//package cn.locusc.cloud.function.configuration;
//
//import org.springframework.cloud.function.context.CustomMessageRoutingCallback;
//import org.springframework.cloud.function.context.MessageRoutingCallback;
//import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;
//import org.springframework.cloud.function.json.JsonMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.messaging.Message;
//
///**
// * @author Jay
// * MessageRoutingCallback配置示例, 主要作用是为了转换上游消息给到下游
// * spring cloud function会将请求数据转化为{@link Message}
// * 2022/9/16
// */
//@Configuration
//public class RoutingCallbackFuncAutoConfiguration {
//
//    /**
//     * functionRouter 是一个默认的路由bean
//     * http.path: /func/functionRouter
//     * http.header: (func_name: length,reverse)
//     * http.body: JAY,LOCUSC,CHAN
//     * {@link ContextFunctionCatalogAutoConfiguration#functionRouter(org.springframework.cloud.function.context.FunctionCatalog, org.springframework.cloud.function.context.FunctionProperties, org.springframework.beans.factory.BeanFactory, org.springframework.cloud.function.context.MessageRoutingCallback)}
//     */
//    @Bean
//    // @ConditionalOnBean(name = "customRouterCallback")
//    public MessageRoutingCallback headerNameRouterCallback() {
//        return new MessageRoutingCallback() {
//
//            @Override
//            public FunctionRoutingResult routingResult(Message<?> message) {
//                return new FunctionRoutingResult((String) message.getHeaders().get("func_name"));
//            }
//
//        };
//    }
//
//    @Bean
//    // @ConditionalOnBean(name = "customRouterCallback")
//    public MessageRoutingCallback echoRouterCallback() {
//        return new MessageRoutingCallback() {
//
//            @Override
//            public FunctionRoutingResult routingResult(Message<?> message) {
//                return new FunctionRoutingResult("echo");
//            }
//
//        };
//    }
//
//    /**
//     * http.path: /func/functionRouter
//     * http.body: {"foo":"blah"} or {"bar":"blah"}
//     */
//    @Bean
//    public MessageRoutingCallback customRouterCallback(JsonMapper jsonMapper) {
//        return new CustomMessageRoutingCallback(jsonMapper);
//    }
//
//    /**
//     * 默认Ioc容器中生产一个空的MessageRoutingCallback
//     * 这样在自定义RoutingFunction时不会调用MessageRoutingCallback
//     */
//    @Bean
//    @Primary
//    public MessageRoutingCallback noneRouterCallback() {
//        return null;
//    }
//
//
//}
