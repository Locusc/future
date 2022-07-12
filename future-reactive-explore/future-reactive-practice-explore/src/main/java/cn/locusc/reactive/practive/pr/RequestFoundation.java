package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import cn.locusc.reactive.practive.pr.repository.ReactiveRepository;
import cn.locusc.reactive.practive.pr.repository.ReactiveUserRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author Jay
 * Learn how to control the demand.
 *
 * There's one aspect to it that we didn't cover: the volume control.
 * In Reactive Streams terms this is called backpressure.
 * It is a feedback mechanism that allows a Subscriber to signal to its Publisher how much data it is prepared to process,
 * limiting the rate at which the Publisher produces data.
 *
 * This control of the demand is done at the Subscription level:
 * a Subscription is created for each subscribe() call and it can be manipulated to either cancel() the flow of data or tune demand with request(long).
 *
 * Making a request(Long.MAX_VALUE) means an unbounded demand, so the Publisher will emit data at its fastest pace.
 * 2022/7/11
 */
public class RequestFoundation {

    ReactiveRepository<User> repository = new ReactiveUserRepository();

    /**
     * The demand can be tuned in the StepVerifier as well,
     * by using the relevant parameter to create and withVirtualTime for the initial request,
     * then chaining in thenRequest(long) in your expectations for further requests.
     *
     * In this first example,
     * create a StepVerifier that produces an initial unbounded demand and verifies 4 values to be received,
     * before completion. This is equivalent to the way you've been using StepVerifier so far.
     *
     * Create a StepVerifier that initially requests all values and expect 4 values to be received
     */
    public StepVerifier requestAllExpectFour(Flux<User> flux) {
        return StepVerifier.withVirtualTime(() -> flux)
                // StepVerifier.create(flux)
                .expectSubscription()
                .thenRequest(Long.MAX_VALUE)
                .expectNextCount(4)
                .expectComplete();
    }


    /**
     * Next we will request values one by one: for that you need an initial request,
     * but also a second single request after you've received and asserted the first element.
     *
     * Without more request, the source will never complete unless you cancel it.
     * This can be done instead of the terminal expectations by using .thenCancel().
     * If you want to also ensure no incoming signal is received over a Duration you can instead use .expectTimeout(Duration).
     *
     * Create a StepVerifier that initially requests 1 value and expects User.
     * SKYLER then requests another value and expects User.JESSE then stops verifying by cancelling the source
     */
    public StepVerifier requestOneExpectSkylerThenRequestOneExpectJesse(Flux<User> flux) {
        return StepVerifier.create(flux)
                .expectNext(User.SKYLER)
                .expectNext(User.JESSE)
                .thenCancel();
    }

    /**
     * How to check that the previous sequence was requested one by one, and that a cancellation happened?
     * It's important to be able to debug reactive APIs,
     * so in the next example we will make use of the log operator to know exactly what happens in terms of signals and events.
     * Use the repository to get a Flux of all users, then apply a log to it.
     * Observe in the console below how the underlying test requests it, and the other events like subscribe, onNext...
     *
     * Return a Flux with all users stored in the repository that prints automatically logs for all Reactive Streams signals
     */
    public Flux<User> fluxWithLog() {
        return repository.findAll().log();
    }

    /**
     * If you want to perform custom actions without really modifying the elements in the sequence,
     * you can use the "side effect" methods that start with do/doOn.
     * For example, if you want to print "Requested" each time the operator receives a request, use doOnRequest.
     * If you want to print "Starting" first, upon subscription before any signal has been received, use doFirst, etc.
     * Each doOn method takes a relevant callback representing the custom action for the corresponding event.
     * Note that you should not block or invoke operations with latency
     * in these callbacks (which is also true of other operator callbacks like map): it's more for quick operations.
     *
     * Return a Flux with all users stored in the repository that
     * prints "Starring:" at first, "firstname lastname" for all values and "The end!" on complete
     *
     * Go ahead and modify the first two methods in this exercise in order to get some insight into their sequences using log and do[On]XXX.
     */
    public Flux<User> fluxWithDoOnPrintln() {
        return repository.findAll()
                // .doOnSubscribe(s -> System.out.println("Starring:"))
                .doFirst(() -> System.out.println("Starring:"))
                .doOnNext(n -> System.out.println(n.getFirstname() + " " + n.getLastname()))
                .doOnComplete(() -> System.out.println("The end!"));
    }

}
