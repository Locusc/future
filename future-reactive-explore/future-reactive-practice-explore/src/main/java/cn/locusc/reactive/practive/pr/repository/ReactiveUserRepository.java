package cn.locusc.reactive.practive.pr.repository;

import cn.locusc.reactive.practive.domain.bo.User;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

public class ReactiveUserRepository implements ReactiveRepository<User> {

    @Override
    public Mono<Void> save(Publisher<User> publisher) {
        return null;
    }

    @Override
    public Mono<User> findFirst() {
        return null;
    }

    @Override
    public Flux<User> findAll() {
        Stream<User> stream = Stream.of(User.SKYLER, User.SAUL, User.WALTER, User.JESSE);
        return Flux.fromStream(stream);
    }

    @Override
    public Mono<User> findById(String id) {
        return null;
    }

}
