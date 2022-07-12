package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jay
 * Merging sequences is the operation consisting of listening
 * for values from several Publishers and emitting them in a single Flux.
 *
 * Learn how to merge flux.
 * 2022/7/11
 */
public class MergeFoundation {

    /**
     * On this first exercise we will begin by merging elements of two Flux as soon as they arrive.
     * The caveat here is that values from flux1 arrive with a delay,
     * so in the resulting Flux we start seeing values from flux2 first.
     * <p>
     * Merge flux1 and flux2 values with interleave
     */
    public Flux<User> mergeFluxWithInterleave(Flux<User> flux1, Flux<User> flux2) {
        // return Flux.merge(flux1, flux2);
        return flux1.mergeWith(flux2);
    }

    /**
     * But if we want to keep the order of sources, we can use the concat operator.
     * Concat will wait for flux1 to complete before it can subscribe to flux2,
     * ensuring that all the values from flux1 have been emitted,
     * thus preserving an order corresponding to the source.
     *
     * Merge flux1 and flux2 values with no interleave (flux1 values and then flux2 values)
     */
    public Flux<User> mergeFluxWithNoInterleave(Flux<User> flux1, Flux<User> flux2) {
        return flux1.concatWith(flux2);
    }

    /**
     * You can use concat with several Publisher.
     * For example, you can get two Mono and turn them into a same-order Flux:
     * Create a Flux containing the value of mono1 then the value of mono2
     */
    public Flux<User> createFluxFromMultipleMono(Mono<User> mono1, Mono<User> mono2) {
        return Flux.concat(mono1, mono2);
    }

}
