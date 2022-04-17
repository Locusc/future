package cn.locusc.project.reactor.basic;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.function.Function;

/**
 * @author Jay
 * 组合转换响应式流
 * 2022/3/18
 */
public class ReactorCombineFlux {

    /**
     * 当我们构建复杂的响应式工作流时, 通常需要在几个不同的地方使用相同的操作符序列。
     * transform 操作符, 可以将这些常见的部分提取到单独的对象中，并在需要时重用它们。
     * transform 操作符, 可以增强流结构本身。
     *
     * transform 操作符仅在流生命周期的组装阶段更新一次流行为, 可以在响应式应用程序中实现代码
     * 重用。
     */
    @Test
    public void test5() {
        Function<Flux<String>, Flux<String>> logUserInfo = stream ->
                stream.index().doOnNext(
                        tp -> System.out.println("[" + tp.getT1() + "] User: " + tp.getT2())
                ).map(Tuple2::getT2);
        Flux.range(1000, 3)
                .map(i -> "user-" + i)
                .transform(logUserInfo)
                .subscribe(e -> System.out.println("onNext: " + e));
    }

}
