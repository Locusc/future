package cn.locusc.reactive.x.rxjava3;

import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.util.Arrays;

/**
 * concatMap和flatMap的功能是一样的,
 * 将一个发射数据的Observable变换为多个Observables,
 * 然后将它们发射的数据放进一个单独的Observable.
 * 只不过最后合并Observables flatMap采用的merge,
 * 而concatMap采用的是连接(concat).
 * 总之一句一话,
 * 他们的区别在于:concatMap是有序的,
 * flatMap是无序的, concatMap最终输出的顺序与原序列保持一致,
 * 而flatMap则不一定, 有可能出现交错.
 *
 */
public class RxJava3FluxMap {

    /**
     * 使用Map转换响应式流
     */
    @Test
    public void test1() {
        Observable.just(1, 2, 3, 4, 5)
                .map(m -> {
                    String[] strArr = new String[m];
                    Arrays.fill(strArr, "a");
                    return strArr;
                })
                .forEach(f -> System.out.println(Arrays.toString(f)));


        Observable.just(1, 2, 3, 4, 5)
                .subscribe(System.out::println);
    }

}
