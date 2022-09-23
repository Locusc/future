package cn.locusc.cloud.function.functional;

import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Jay
 * functional
 * 2022/9/14
 */
@Component
public class StringFunctional implements Supplier<String> {

    public static Function<String, String> trim() {
        return String::trim;
    }

    @Override
    public String get() {
        return "this is spring cloud function demo";
    }

}
