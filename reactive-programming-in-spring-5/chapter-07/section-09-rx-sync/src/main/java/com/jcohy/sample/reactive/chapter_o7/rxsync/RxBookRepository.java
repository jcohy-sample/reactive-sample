package com.jcohy.sample.reactive.chapter_o7.rxsync;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import org.springframework.stereotype.Component;

import static reactor.function.TupleUtils.function;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:18:46
 * @since 2022.0.1
 */
@Component
public class RxBookRepository extends
        ReactiveCrudRepositoryAdapter<Book, Integer, BookJpaRepository> {

    public RxBookRepository(BookJpaRepository delegate, Scheduler scheduler) {
        super(delegate, scheduler);
    }

    public Flux<Book> findByIdBetween(Publisher<Integer> lowerPublisher, Publisher<Integer> upperPublisher) {
        return Mono.zip(
                        Mono.from(lowerPublisher),
                        Mono.from(upperPublisher)
                ).flatMapMany(
                        function((lower, upper) ->
                                Flux
                                        .fromIterable(delegate.findByIdBetween(lower, upper))
                                        .subscribeOn(scheduler)
                        ))
                .subscribeOn(scheduler);
    }

    public Flux<Book> findShortestTitle() {
        return Mono.fromCallable(delegate::findShortestTitle)
                .subscribeOn(scheduler)
                .flatMapMany(Flux::fromIterable);
    }
}
