package cn.locusc.project.reactor.jpa.crud.adapter;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * @author Jay
 * 响应式CrudRepository
 * 2022/3/20
 */
@RequiredArgsConstructor
public abstract class ReactiveCrudRepositoryAdapter<T, ID, I extends CrudRepository<T, ID>>
        implements ReactiveCrudRepository<T, ID> {

    // 具体委派的CrudRepository
    protected final I delegate;

    // 异步边界
    protected final Scheduler scheduler;

    /**
     * from ReactiveCrudRepository.save
     * 通过实体参数保存
     * @return reactor.core.publisher.Mono<S>
     */
    @Override
    public <S extends T> Mono<S> save(S entity) {
        return Mono.fromCallable(() -> delegate.save(entity))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.saveAll
     * 根据可迭代类型创建响应式流
     * @return reactor.core.publisher.Flux<S>
     */
    @Override
    public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
        return Mono.fromCallable(() -> delegate.saveAll(entities))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.saveAll
     * 根据发布者创建响应式流
     * @return reactor.core.publisher.Flux<S>
     */
    @Override
    public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
        return Flux.from(entityStream)
                .flatMap(entity -> Mono.fromCallable(() -> delegate.save(entity)))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.findById
     * @return reactor.core.publisher.Mono<T>
     */
    @Override
    public Mono<T> findById(ID id) {
        return Mono.fromCallable(() -> delegate.findById(id))
                .flatMap(f -> f
                        .map(Mono::just)
                        .orElseGet(Mono::empty))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.findById
     * 根据发布者创建响应式流
     * @return reactor.core.publisher.Mono<T>
     */
    @Override
    public Mono<T> findById(Publisher<ID> id) {
        return Mono.from(id)
                .flatMap(f -> delegate
                        .findById(f)
                        .map(Mono::just)
                        .orElseGet(Mono::empty))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.existsById
     * @return reactor.core.publisher.Mono<java.lang.Boolean>
     */
    @Override
    public Mono<Boolean> existsById(ID id) {
        return Mono.fromCallable(() -> delegate.existsById(id))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.existsById
     * 根据发布者创建响应式流
     * @return reactor.core.publisher.Mono<java.lang.Boolean>
     */
    @Override
    public Mono<Boolean> existsById(Publisher<ID> id) {
        return Mono.from(id)
                .flatMap(f -> Mono.fromCallable(() -> delegate.existsById(f)))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.findAll
     * @return reactor.core.publisher.Flux<T>
     */
    @Override
    public Flux<T> findAll() {
        return Mono.fromCallable(delegate::findAll)
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.findAllById
     * 根据可迭代类型创建响应式流
     * @return reactor.core.publisher.Flux<T>
     */
    @Override
    public Flux<T> findAllById(Iterable<ID> ids) {
        return Mono.fromCallable(() -> delegate.findAllById(ids))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.findAllById
     * 根据发布者创建响应式流
     * @return reactor.core.publisher.Flux<T>
     */
    @Override
    public Flux<T> findAllById(Publisher<ID> idStream) {
        return Flux.from(idStream)
                .buffer()
                .flatMap(f -> Flux.fromIterable(delegate.findAllById(f)))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.count
     * @return reactor.core.publisher.Mono<java.lang.Long>
     */
    @Override
    public Mono<Long> count() {
        return Mono.fromCallable(delegate::count)
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.deleteById
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> deleteById(ID id) {
        return Mono.<Void>fromRunnable(() -> delegate.deleteById(id))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.deleteById
     * 根据发布者创建响应式流
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> deleteById(Publisher<ID> id) {
        return Mono.from(id)
                .flatMap(f ->
                        Mono.<Void>fromRunnable(() -> delegate.deleteById(f))
                                .subscribeOn(scheduler)
                );
    }

    /**
     * from ReactiveCrudRepository.delete
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> delete(T entity) {
        return Mono.<Void>fromRunnable(() -> delegate.delete(entity))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.deleteAll
     * 根据可迭代类型创建响应式流
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> deleteAll(Iterable<? extends T> entities) {
        return Mono.<Void>fromRunnable(() -> delegate.deleteAll(entities))
                .subscribeOn(scheduler);
    }

    /**
     * from ReactiveCrudRepository.deleteAll
     * 根据发布者创建响应式流
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> deleteAll(Publisher<? extends T> entityStream) {
        return Flux.from(entityStream)
                .flatMap(f -> Mono
                        .fromRunnable(() -> delegate.delete(f))
                        .subscribeOn(scheduler))
                .then();
    }

    /**
     * from ReactiveCrudRepository.deleteAll
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> deleteAll() {
        return Mono.<Void>fromRunnable(delegate::deleteAll)
                .subscribeOn(scheduler);
    }
}
