package cn.locusc.cloud.function.configuration;

import cn.locusc.cloud.function.domain.Bar;
import cn.locusc.cloud.function.domain.Foo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Jay
 * 基础函数配置类
 * 2022/9/14
 */
@Slf4j
@Configuration
public class BaseSampleFuncConfiguration {

    public static final Map<String, UUID> createdMessageIds = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    @Bean
    public Function<Message<?>, Message<?>> print() {
        return m -> {
            // assertThat(m.getHeaders().get("key1")).isEqualTo("hello1");
            // assertThat(m.getHeaders().get("key2")).isEqualTo("hello2");
            // assertThat(m.getHeaders().get("foo")).isEqualTo("application/json");
            log.info("ioEcho: {}", gson.toJson(m));
            return m;
        };
    }

    @Bean
    public Function<Message<?>, Message<?>> fail() {
        return m -> {
            // assertThat(m.getHeaders().containsKey("key1")).isFalse();
            // assertThat(m.getHeaders().get("key2")).isEqualTo("hello2");
            // assertThat(m.getHeaders().containsKey("foo")).isFalse();
            log.info("echoFail: {}", gson.toJson(m));
            return m;
        };
    }

    @Bean
    public Function<Message<?>, Message<?>> split() {
        return m -> {
            // assertThat(m.getHeaders().get("key1")).isEqualTo("foo");
            // assertThat(m.getHeaders().get("key2")).isEqualTo("bar");
            // assertThat(m.getHeaders().get("key3")).isEqualTo("foo/bar/baz");
            log.info("split: {}", gson.toJson(m));
            return m;
        };
    }

    @Bean
    public Function<Message<?>, Message<?>> failed() {
        return x -> {
            // assertThat(x.getHeaders().containsKey("keyOut1")).isFalse();
            log.info("ioFoo: {}", gson.toJson(x));
            return x;
        };
    }

    @Bean
    public Function<Message<Foo>, Message<String>> foo() {
        return foo -> {
            Message<String> m = MessageBuilder.withPayload("foo")
                    .setHeader("originalId", foo.getHeaders().getId())
                    .build();

            createdMessageIds.put("foo", foo.getHeaders().getId());

            return m;
        };
    }

    @Bean
    public Function<Message<Bar>, Message<String>> bar() {
        return foo -> {
            Message<String> m = MessageBuilder.withPayload("bar")
                    .setHeader("originalId", foo.getHeaders().getId())
                    .build();

            createdMessageIds.put("bar", foo.getHeaders().getId());

            return m;
        };
    }

    @Bean
    public Function<String, String> reverse() {
        return f -> String.valueOf(new StringBuilder(f).reverse());
    }

    @Bean
    public Function<String, String> lowercase() {
        return String::toLowerCase;
    }

    @Bean
    public Function<String, String> uppercase() {
        return String::toUpperCase;
    }

    @Bean
    public Function<String, Integer> length() {
        return String::length;
    }

    @Bean
    public Function<String, String> echo() { return f -> String.format("echo: %s", f); }

}
