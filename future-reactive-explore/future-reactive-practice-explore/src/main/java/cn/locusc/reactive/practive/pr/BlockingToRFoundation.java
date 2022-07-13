package cn.locusc.reactive.practive.pr;

import cn.locusc.reactive.practive.domain.bo.User;
import cn.locusc.reactive.practive.pr.repository.BlockingUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author Jay
 * Learn how to call blocking code from Reactive one with adapted concurrency strategy for
 * blocking code that produces or receives data.
 *
 * For those who know RxJava:
 *  - RxJava subscribeOn = Reactor subscribeOn
 *  - RxJava observeOn = Reactor publishOn
 *  - RxJava Schedulers.io <==> Reactor Schedulers.elastic
 *
 *
 * The big question is "How to deal with legacy, non reactive code?".
 *
 * Say you have blocking code (eg. a JDBC connection to a database),
 * and you want to integrate that into your reactive pipelines while avoiding too much of an impact on performance.
 *
 * The best course of action is to isolate such intrinsically blocking parts of your code into
 * their own execution context via a Scheduler,
 * keeping the efficiency of the rest of the pipeline high and only creating extra threads when absolutely needed.
 *
 * In the JDBC example you could use the fromIterable factory method.
 * But how do you prevent the call to block the rest of the pipeline?
 *
 * @see Flux#subscribeOn(Scheduler)
 * @see Flux#publishOn(Scheduler)
 * @see Schedulers
 * 2022/7/12
 */
public class BlockingToRFoundation {

    /**
     * Create a Flux for reading all users from the blocking repository deferred until the flux is subscribed,
     * and run it with a bounded elastic scheduler
     *
     * The subscribeOn method allow to isolate a sequence from the start on a provided Scheduler.
     * For example, the Schedulers.boundedElastic() will create a pool of threads that grows on demand,
     * releasing threads that haven't been used in a while automatically.
     * In order to avoid too many threads due to abusing of this easy option,
     * the boundedElastic Scheduler places an upper limit to the number of threads it can create and reuse (unlike the now deprecated elastic() one).
     *
     * Use that trick to slowly read all users from the blocking repository in the first exercise.
     * Note that you will need to wrap the call to the repository inside a Flux.defer lambda.
     */
    public Flux<User> blockingRepositoryToFlux(BlockingUserRepository repository) {
        return Flux.defer(() -> Flux.fromIterable(repository.findAll())
                .subscribeOn(Schedulers.boundedElastic())
        );
    }

    /**
     * Insert users contained in the Flux parameter in the blocking repository using a bounded elastic scheduler
     * and return a Mono<Void> that signal the end of the operation
     *
     * For slow subscribers (eg. saving to a database),
     * you can isolate a smaller section of the sequence with the publishOn operator.
     * Unlike subscribeOn, it only affects the part of the chain below it, switching it to a new Scheduler.
     *
     * As an example, you can use doOnNext to perform a save on the repository,
     * but first use the trick above to isolate that save into its own execution context.
     * You can make it more explicit that you're only interested in knowing if the save succeeded or
     * failed by chaining the then() operator at the end, which returns a Mono<Void>.
     */
    public Mono<Void> fluxToBlockingRepository(Flux<User> flux, BlockingUserRepository repository) {
        return flux.publishOn(Schedulers.boundedElastic())
                .doOnNext(repository::save)
                .then();
    }

}
