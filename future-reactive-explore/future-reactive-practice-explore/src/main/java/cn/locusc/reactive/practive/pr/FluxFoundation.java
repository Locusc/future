package cn.locusc.reactive.practive.pr;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jay
 * Flux基础练习
 *
 * A Flux<T> is a Reactive Streams Publisher, augmented with a lot of operators that can be used to generate,
 * transform, orchestrate Flux sequences.
 *
 * It can emit 0 to n <T> elements (onNext event) then either completes or errors (onComplete and onError terminal events).
 * If no terminal event is triggered, the Flux is infinite.
 *
 * Static factories on Flux allow to create sources, or generate them from several callbacks types.
 * Instance methods, the operators, let you build an asynchronous processing pipeline that will produce an asynchronous sequence.
 * Each Flux#subscribe() or multicasting operation such as Flux#publish and Flux#publishNext will materialize a dedicated instance of the pipeline and trigger the data flow inside it.
 * 2022/7/1
 */
public class FluxFoundation {

    private final CountDownLatch countDownLatch = new CountDownLatch(10);

    /**
     * demo by flux
     */
    @Test
    public void fluxInAction() throws InterruptedException {
        Flux.range(0, 100)
                .delayElements(Duration.ofMillis(100))
                .map(d -> d * 2)
                .take(3)
                .subscribe(s -> {
                    System.out.println(s);
                    countDownLatch.countDown();
                });

        countDownLatch.await();
    }

    /**
     * Return an empty Flux
     *
     * Create a Flux that completes without emitting any item.
     */
    public Flux<String> emptyFlux() {
        return Flux.empty();
    }

    /**
     *  Return a Flux that contains 2 values "foo" and "bar" without using an array or a collection
     *
     *  Create a new Flux that emits the specified item(s) and then complete.
     */
    public Flux<String> fooBarFluxFromValues() {
        return Flux.just("foo", "bar");
    }

    /**
     * Create a Flux from a List that contains 2 values "foo" and "bar"
     *
     * Create a Flux that emits the items contained in the provided Iterable.
     */
    public Flux<String> fooBarFluxFromList() {
        return Flux.fromIterable(Arrays.asList("foo", "bar"));
    }

    /**
     * Create a Flux that emits an IllegalStateException
     *
     * Create a Flux that completes with the specified error.
     */
    public Flux<String> errorFlux() {
        return Flux.error(new IllegalStateException());
    }

    /**
     * Create a Flux that emits increasing values from 0 to 9 each 100ms
     *
     * Create a new Flux that emits an ever incrementing long starting with 0 every period on the global timer.
     */
    public Flux<Long> counter() {
        return Flux.interval(Duration.ofMillis(100))
                .take(10);
    }

}
