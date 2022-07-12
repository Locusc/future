package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author
 * Transform
 * Reactor ships with several operators that can be used to transform data.
 * 2022/7/11
 */
public class TransformFoundation {

    /**
     * In the first place,
     * we will capitalize a String. Since this is a simple 1-1 transformation with no expected latency,
     * we can use the map operator with a lambda transforming a T into a U.
     *
     * Capitalize the user username, firstname and lastname
     */
    public Mono<User> capitalizeOne(Mono<User> mono) {
        return mono.map(f -> new User(
                f.getUsername().toUpperCase(),
                f.getFirstname().toUpperCase(),
                f.getLastname().toUpperCase()
        ));
    }

    /**
     * We can use exactly the same code on a Flux, applying the mapping to each element as it becomes available.
     * Capitalize the users username, firstName and lastName
     *
     * Capitalize the users username, firstName and lastName
     */
    public Flux<User> capitalizeMany(Flux<User> flux) {
        return flux.map(f -> new User(
                f.getUsername().toUpperCase(),
                f.getFirstname().toUpperCase(),
                f.getLastname().toUpperCase()
        ));
    }

    /**
     * Now imagine that we have to call a webservice to capitalize our String.
     * This new call can have latency so we cannot use the synchronous map anymore.
     * Instead, we want to represent the asynchronous call as a Flux or Mono, and use a different operator: flatMap.
     * flatMap takes a transformation Function that returns a Publisher<U> instead of a U.
     * This publisher represents the asynchronous transformation to apply to each element.
     * If we were using it with map, we'd obtain a stream of Flux<Publisher<U>>. Not very useful.
     * But flatMap on the other hand knows how to deal with these inner publishers:
     * it will subscribe to them then merge all of them into a single global output, a much more useful Flux<U>.
     * Note that if values from inner publishers arrive at different times, they can interleave in the resulting Flux.
     *
     * Capitalize the users username, firstName and lastName using #asyncCapitalizeUser
     */
    public Flux<User> asyncCapitalizeMany(Flux<User> flux) {
        return flux.flatMap(this::asyncCapitalizeUser);
    }

    public Mono<User> asyncCapitalizeUser(User u) {
        return Mono.just(
                new User(u.getUsername().toUpperCase(),
                        u.getFirstname().toUpperCase(),
                        u.getLastname().toUpperCase()
                )
        );
    }

}
