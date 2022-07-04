package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * @author Jay
 * Learn how to use StepVerifier to test Mono, Flux or any other kind of Reactive Streams Publisher.
 * <p>
 * Until now, your solution for each exercise was checked by passing the Publisher you defined to a test using a StepVerifier.
 * <p>
 * This class from the reactor-test artifact is capable of subscribing to any Publisher (eg. a Flux or an Akka Stream...) and then assert a set of user-defined expectations with regard to the sequence.
 * <p>
 * If any event is triggered that doesn't match the current expectation, the StepVerifier will produce an AssertionError.
 * <p>
 * You can obtain an instance of StepVerifier from the static factory create. It offers a DSL to set up expectations on the data part and finish with a single terminal expectation (completion, error, cancellation...).
 * <p>
 * Note that you must always call the verify() method or one of the shortcuts that combine the terminal expectation and verify, like .verifyErrorMessage(String). Otherwise the StepVerifier won't subscribe to your sequence and nothing will be asserted.
 * <p>
 * StepVerifier.create(T<Publisher>).{expectations...}.verify()
 * There are a lot of possible expectations, see the reference documentation and the javadoc.
 * 2022/7/1
 */
public class StepVerifierFoundation {

    /**
     * Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then completes successfully.
     * <p>
     * In these exercises, the methods get a Flux or Mono as a parameter and you'll need to test its behavior.
     * You should create a StepVerifier that uses said Flux/Mono, describes expectations about it and verifies it.
     * <p>
     * Let's verify the sequence passed to the first test method emits two specific elements, "foo" and "bar", and that the Flux then completes successfully.
     */
    public void expectFooBarComplete(Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .verifyComplete();
        fail();
    }

    /**
     * Now, let's do the same test but verifying that an exception is propagated at the end.
     * Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then a RuntimeException error.
     */
    public void expectFooBarError(Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .expectError(RuntimeException.class)
                .verify();
        fail();
    }


    /**
     * Use StepVerifier to check that the flux parameter emits a User with "swhite"username
     * and another one with "jpinkman" then completes successfully.
     * <p>
     * Let's try to create a StepVerifier with an expectation on a User's getUsername() getter.
     * Some expectations can work by checking a Predicate on the next value, or even by consuming the next value by passing it to an assertion library like Assertions.assertThat(T) from AssertJ.
     * Try these lambda-based versions (for instance StepVerifier#assertNext with a lambda using an AssertJ assertion like assertThat(...).isEqualTo(...)):
     */
    public void expectSkylerJesseComplete(Flux<User> flux) {
        // Predicate<User> p1 = (User u) -> u.getUsername().equals("swhite");
        // Predicate<User> p2 = (User u) -> u.getUsername().equals("jpinkman");
        StepVerifier.create(flux)
                .assertNext(c -> Assertions.assertThat(c.getUsername()).isEqualTo("swhite"))
                .assertNext(c -> c.getUsername().equals("jpinkman"))
                .expectComplete()
                .verify();
        fail();
    }

    /**
     * Expect 10 elements then complete and notice how long the test takes.
     * <p>
     * On this next test we will receive a Flux which takes some time to emit.
     * As you can expect, the test will take some time to run.
     */
    public void expect10Elements(Flux<Long> flux) {
        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete()
                .toMillis();
        fail();
    }

    /**
     * Expect 3600 elements at intervals of 1 second, and verify quicker than 3600s
     * by manipulating virtual time thanks to StepVerifier#withVirtualTime, notice how long the test takes
     * <p>
     * The next one is even worse: it emits 1 element per second, completing only after having emitted 3600 of them!
     * Since we don't want our tests to run for hours, we need a way to speed that up while still being able to assert the data itself (eliminating the time factor).
     * Fortunately, StepVerifier comes with a virtual time option: by using StepVerifier.withVirtualTime(Supplier<Publisher>),
     * the verifier will temporarily replace default core Schedulers (the component that define the execution context in Reactor).
     * All these default Scheduler are replaced by a single instance of a VirtualTimeScheduler, which has a virtual clock that can be manipulated.
     * In order for the operators to pick up that Scheduler, you should lazily build your operator chain inside the lambda passed to withVirtualTime.
     * You must then advance time as part of your test scenario, by calling either thenAwait(Duration) or expectNoEvent(Duration).
     * The former simply advances the clock, while the later additionally fails if any unexpected event triggers during the provided duration (note that almost all the time there will at least be a "subscription" event even though the clock hasn't advanced, so you should usually put a expectSubscription() after .withVirtualTime() if you're going to use expectNoEvent right after).
     * <p>
     * Let's try that by making a fast test of our hour-long publisher:
     * <p>
     * <p>
     * 有些序列的生成是有时间要求的，比如每隔 1 分钟才产生一个新的元素。
     * 在进行测试中，不可能花费实际的时间来等待每个元素的生成。
     * 此时需要用到 StepVerifier 提供的虚拟时间功能。
     * 通过 StepVerifier.withVirtualTime()方法可以创建出使用虚拟时钟的 StepVerifier。
     * 通过 thenAwait(Duration)方法可以让虚拟时钟前进。
     * 在代码清单 22 中，需要验证的流中包含两个产生间隔为一天的元素，并且第一个元素的产生延迟是 4 个小时。
     * 在通过 StepVerifier.withVirtualTime()方法包装流之后，expectNoEvent()方法用来验证在 4 个小时之内没有任何消息产生，
     * 然后验证第一个元素 0 产生；接着 thenAwait()方法来让虚拟时钟前进一天，然后验证第二个元素 1 产生；最后验证流正常结束。
     *         StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(4), Duration.ofDays(1))
     *                 .take(2))
     *                 .expectSubscription()
     *                 .expectNoEvent(Duration.ofHours(4))
     *                 .expectNext(0L)
     *                 .thenAwait(Duration.ofDays(1))
     *                 .expectNext(1L)
     *                 .verifyComplete();
     */
    @Test
    public void expect3600Elements() {
        long l = StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(1)))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(3600)
                .expectComplete()
                .verify()
                .toMillis();
        System.out.println(l);
        // fail();
    }

    private void fail() {
        throw new AssertionError("workshop not implemented");
    }

}
