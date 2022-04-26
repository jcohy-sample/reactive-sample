package com.jcohy.sample.reactive.chapter_07.mongo_rx;


import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:15:40
 * @since 2022.0.1
 */
public interface BookSpringDataMongoRxRepository extends ReactiveMongoRepository<Book, ObjectId> {

    Mono<Book> findOneByTitle(Mono<String> title);

    Flux<Book> findManyByTitleRegex(String regexp);

    @Meta(maxScanDocuments = 3)
    Flux<Book> findByAuthorsOrderByPublishingYearDesc(Publisher<String> authors);

    @Query("{ 'authors.1':  {$exists:  true}}")
    Flux<Book> booksWithFewAuthors();

    Flux<Book> findByPublishingYearBetweenOrderByPublishingYear(Integer from, Integer to, Pageable pageable);
}
