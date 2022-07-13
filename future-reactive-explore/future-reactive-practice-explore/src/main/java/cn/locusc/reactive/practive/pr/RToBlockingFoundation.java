package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jay
 * Learn how to turn Reactive API to blocking one.
 *
 * Sometimes you can only migrate part of your code to be reactive,
 * and you need to reuse reactive sequences in more imperative code.
 *
 * Thus if you need to block until the value from a Mono is available,
 * use Mono#block() method. It will throw an Exception if the onError event is triggered.
 *
 * 2022/7/12
 */
public class RToBlockingFoundation {

    /**
     * Return the user contained in that Mono
     *
     * Note that you should avoid this by favoring having reactive code end-to-end, as much as possible.
     * You MUST avoid this at all cost in the middle of other reactive code,
     * as this has the potential to lock your whole reactive pipeline.
     */
    public User monoToValue(Mono<User> mono) {
        return mono.block();
    }

    /**
     * Return the users contained in that Flux
     *
     * Similarly, you can block for the first or last value in a Flux with blockFirst()/blockLast().
     * You can also transform a Flux to an Iterable with toIterable. Same restrictions as above still apply.
     */
    public Iterable<User> fluxToValues(Flux<User> flux) {
        return flux.toIterable();
    }

}
