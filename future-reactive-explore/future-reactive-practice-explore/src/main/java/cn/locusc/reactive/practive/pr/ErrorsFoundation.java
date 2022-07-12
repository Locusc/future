package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jay
 * Learn how to deal with errors.
 *
 * Reactor ships with several operators that can be used to deal with errors:
 * propagate errors but also recover from it (eg. by falling back to a different sequence or by retrying a new Subscription).
 * 2022/7/11
 */
public class ErrorsFoundation {

    /**
     * In the first example, we will return a Mono containing default user Saul when an error occurs in the original Mono,
     * using the method onErrorReturn. If you want, you can even limit that fallback to the IllegalStateException class.
     * Use the User#SAUL constant.
     *
     * Return a Mono<User> containing User.SAUL when an error occurs in the input Mono,
     * else do not change the input Mono.
     */
    public Mono<User> betterCallSaulForBogusMono(Mono<User> mono) {
        return mono.onErrorReturn(User.SAUL);
    }

    /**
     * Let's try the same thing with Flux.
     * In this case, we don't just want a single fallback value,
     * but a totally separate sequence (think getting stale data from a cache).
     * This can be achieved with onErrorResume, which falls back to a Publisher<T>.
     * Emit bothUser#SAUL and User#JESSE whenever there is an error in the original FLux:
     *
     * Return a Flux<User> containing User.SAUL and User.JESSE when an error occurs in the input Flux,
     * else do not change the input Flux.
     */
    public Flux<User> betterCallSaulAndJesseForBogusFlux(Flux<User> flux) {
        return flux.onErrorResume(r -> Flux.just(User.SAUL, User.JESSE));
    }

    /**
     * Dealing with checked exceptions is a bit more complicated.
     * Whenever some code that throws checked exceptions is used in an operator (eg. the transformation function of a map),
     * you will need to deal with it.
     * The most straightforward way is to make a more complex lambda with a try-catch block
     * that will transform the checked exception into a RuntimeException that can be signalled downstream.
     *
     * There is a Exceptions#propagate utility that will wrap a checked exception into a special runtime exception
     * that can be automatically unwrapped by Reactor subscribers and StepVerifier:
     * this avoids seeing an irrelevant RuntimeException in the stacktrace.
     *
     * Try to use that on the capitalizeMany method within a map: you'll need to catch a GetOutOfHereException,
     * which is checked, but the corresponding test still expects the GetOutOfHereException directly.
     *
     * Implement a method that capitalizes each user of the incoming flux using the
     * #capitalizeUser method and emits an error containing a GetOutOfHereException error
     */
    public Flux<User> capitalizeMany(Flux<User> flux) {
        return flux.map(m -> {
            try {
                return capitalizeUser(m);
            } catch (GetOutOfHereException e) {
               throw Exceptions.propagate(e);
            }
        });
    }

    public User capitalizeUser(User user) throws GetOutOfHereException {
        if (user.equals(User.SAUL)) {
            throw new GetOutOfHereException();
        }
        return new User(user.getUsername(), user.getFirstname(), user.getLastname());
    }

    protected static final class GetOutOfHereException extends Exception {
        private static final long serialVersionUID = 0L;
    }

}
