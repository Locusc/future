package cn.locusc.reactive.practive.pr;


import cn.locusc.reactive.practive.domain.bo.User;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * @author Jay
 * Learn how to adapt from/to RxJava 3 Observable/Single/Flowable and Java 8+ CompletableFuture.
 *
 * Mono and Flux already implements Reactive Streams interfaces so they are natively
 * Reactive Streams compliant + there are {@link Mono#from(Publisher)} and {@link Flux#from(Publisher)}
 * factory methods.
 *
 * For RxJava 3, you should not use Reactor Adapter but only RxJava 3 and Reactor Core.
 *
 * 2022/7/11
 */
public class AdaptFoundation {

    /**
     * You can make RxJava3 and Reactor 3 types interact without a single external library.
     * In the first two examples we will adapt from Flux to Flowable, which implements Publisher, and vice-versa.
     * This is straightforward as both libraries provide a factory method to do that conversion from any Publisher.
     * The checker below runs the two opposite conversions in one go:
     *
     * Adapt Flux to RxJava Flowable
     */
    public Flowable<User> fromFluxToFlowable(Flux<User> flux) {
        return Flowable.fromPublisher(flux);
    }

    /**
     * Adapt RxJava Flowable to Flux
     */
    public Flux<User> fromFlowableToFlux(Flowable<User> flowable) {
        return Flux.from(flowable);
    }

    /**
     * The next two examples are a little trickier:
     * we need to adapt between Flux and Observable, but the later doesn't implement Publisher.
     *
     * In the first case, you can transform any publisher to Observable.
     * In the second case, you have to first transform the Observable into a Flowable,
     * which forces you to define a strategy to deal with backpressure (RxJava 3 Observable doesn't support backpressure).
     *
     * Adapt Flux to RxJava Observable
     */
    public Observable<User> fromFluxToObservable(Flux<User> flux) {
        return Observable.fromPublisher(flux);
    }

    /**
     * Adapt RxJava Observable to Flux
     */
    public Flux<User> fromObservableToFlux(Observable<User> observable) {
        return Flux.from(observable.toFlowable(BackpressureStrategy.BUFFER));
    }

    /**
     * Next, let's try to transform a Mono to a RxJava Single, and vice-versa.
     * You can simply call the firstOrError method from Observable.
     * For the other way around, you'll once again need to transform the Single into a Flowable first.
     *
     * Adapt Mono to RxJava Single
     */
    public Single<User> fromMonoToSingle(Mono<User> mono) {
        return Single.fromPublisher(mono);
    }

    /**
     * Adapt RxJava Single to Mono
     */
    public Mono<User> fromSingleToMono(Single<User> single) {
        return Mono.from(single.toFlowable());
    }

    /**
     * Finally, you can easily transform a Mono to a Java 8 CompletableFuture and vice-versa.
     * Notice how these conversion methods all begin with from (when converting an external type to a Reactor one)
     * and to (when converting a Reactor type to an external one).
     *
     * Adapt Mono to Java 8+ CompletableFuture
     */
    public CompletableFuture<User> fromMonoToCompletableFuture(Mono<User> mono) {
        return mono.toFuture();
    }

    /**
     * Adapt Java 8+ CompletableFuture to Mono
     */
    public Mono<User> fromCompletableFutureToMono(CompletableFuture<User> future) {
        return Mono.fromFuture(future);
    }

}
