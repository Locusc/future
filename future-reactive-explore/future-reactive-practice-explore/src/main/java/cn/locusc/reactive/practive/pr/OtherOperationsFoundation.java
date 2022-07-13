package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Jay
 * Learn how to use various other operators.
 *
 * In this section, we'll have a look at a few more useful operators that don't fall into the broad categories we explored earlier.
 * Reactor 3 contains a lot of operators,
 * so don't hesitate to have a look at the Flux and Mono javadocs as well as the reference guide to learn about more of them.
 * 2022/7/12
 */
public class OtherOperationsFoundation {

    /**
     * Create a Flux of user from Flux of username, firstname and lastname.
     *
     * In the first exercise we'll receive 3 Flux<String>.
     * Their elements could arrive with latency,
     * yet each time the three sequences have all emitted an element,
     * we want to combine these 3 elements and create a new User.
     * This concatenate-and-transform operation is called zip:
     */
    public Flux<User> userFluxFromStringFlux(Flux<String> usernameFlux, Flux<String> firstnameFlux, Flux<String> lastnameFlux) {
        return Flux.zip(usernameFlux, firstnameFlux, lastnameFlux)
                .map(f -> new User(f.getT1(), f.getT2(), f.getT3()));
    }

    /**
     * Return the mono which returns its value faster
     *
     * If you have 3 possible Mono sources and you only want to keep the one that emits its value the fastest,
     * you can use the firstWithValue static method:
     */
    public Mono<User> useFastestMono(Mono<User> mono1, Mono<User> mono2) {
        return Mono.first(mono1, mono2);
    }

    /**
     * Return the flux which returns the first value faster
     *
     * Flux also has the firstWithValue static method.
     * Only the first element emitted by each Flux is considered to select the fastest Flux (which is then mirrored in the output):
     */
    public Flux<User> useFastestFlux(Flux<User> flux1, Flux<User> flux2) {
        return Flux.first(flux1, flux2);
    }

    /**
     * Convert the input Flux<User> to a Mono<Void> that represents the complete signal of the flux
     *
     * Sometimes you're not interested in elements of a Flux<T>. If you want to still keep a Flux<T> type,
     * you can use ignoreElements(). But if you really just want the completion, represented as a Mono<Void>,
     * you can use then() instead:
     */
    public Mono<Void> fluxCompletion(Flux<User> flux) {
        return flux.then();
    }

    /**
     * Return a valid Mono of user for null input and non null input user (hint: Reactive Streams do not accept null values)
     *
     * Reactive Streams does not allow null values in onNext. There's an operator that allow to just emit one value,
     * unless it is null in which case it will revert to an empty Mono. Can you find it?
     */
    public Mono<User> nullAwareUserToMono(User user) {
        return Mono.justOrEmpty(user);
    }

    /**
     * Return the same mono passed as input parameter, expect that it will emit User.SKYLER when empty
     *
     * Similarly, if you want to prevent the empty Mono case by falling back to a different one,
     * you can find an operator that does this switch:
     */
    public Mono<User> emptyToSkyler(Mono<User> mono) {
        return mono.defaultIfEmpty(User.SKYLER);
    }

    /**
     * Convert the input Flux<User> to a Mono<List<User>> containing list of collected flux values
     *
     * Sometimes you want to capture all values emitted by Flux into separate List.
     * In this case you can use collectList operator that would return Mono containing that List.
     */
    public Mono<List<User>> fluxCollection(Flux<User> flux) {
        return flux.collectList();
    }

}
