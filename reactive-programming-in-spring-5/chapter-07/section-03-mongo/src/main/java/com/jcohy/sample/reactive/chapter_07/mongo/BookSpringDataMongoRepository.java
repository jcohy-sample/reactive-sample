package com.jcohy.sample.reactive.chapter_07.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:15:24
 * @since 2022.0.1
 */
@Repository
public interface BookSpringDataMongoRepository extends MongoRepository<Book,Integer> {

    Iterable<Book> findByAuthorsOrderByPublishingYearDesc(String... authors);

    @Query("{ 'authors.1': { $exists: true } }")
    Iterable<Book> booksWithFewAuthors();

}
