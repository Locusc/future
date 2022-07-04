package cn.locusc.reactive.practive.pr;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jay
 * Mono基础练习
 *
 * A Mono<T> is a Reactive Streams Publisher, also augmented with a lot of operators that can be used to generate, transform, orchestrate Mono sequences.
 *
 * It is a specialization of Flux that can emit at most 1 <T> element: a Mono is either valued (complete with element), empty (complete without element) or failed (error).
 *
 * A Mono<Void> can be used in cases where only the completion signal is interesting (the Reactive Streams equivalent of a Runnable task completing).
 *
 * Like for Flux, the operators can be used to define an asynchronous pipeline which will be materialized anew for each Subscription.
 *
 * Note that some API that change the sequence's cardinality will return a Flux (and vice-versa, APIs that reduce the cardinality to 1 in a Flux return a Mono).
 * 2022/7/1
 */
public class MonoFoundation {

    private final CountDownLatch countDownLatch = new CountDownLatch(10);

    /**
     * demo by Mono
     * Pick the first Mono to emit any signal (value, empty completion or error) and replay that signal,
     * effectively behaving like the fastest of these competing sources.
     */
    @Test
    public void monoInAction() throws InterruptedException {
        Mono.first(
                Mono.just(1).map(m -> "foo" + m),
                Mono.delay(Duration.ofMillis(100)).thenReturn("bar")
        ).subscribe(s -> {
            System.out.println(s);
            countDownLatch.countDown();
        });

        countDownLatch.await();
    }


    /**
     * Return an empty Mono
     *
     * Create a Mono that completes without emitting any item.
     */
    public Mono<String> emptyFlux() {
        return Mono.empty();
    }

    /**
     * Return a Mono that never emits any signal
     *
     * Now, we will try to create a Mono which never emits anything. Unlike empty(),
     * it won't even emit an onComplete event.
     */
    public Mono<String> monoWithNoSignal() {
        return Mono.never();
    }

    /**
     * Return a Mono that contains a "foo" value
     *
     * Like Flux, you can create a Mono from an available (unique) value.
     */
    public Mono<String> fooMono() {
        return Mono.just("foo");
    }

    /**
     * Create a Mono that emits an IllegalStateException
     *
     * And exactly as we did for the flux, we can propagate exceptions.
     */
    Mono<String> errorMono() {
        return Mono.error(new IllegalStateException());
    }

}
