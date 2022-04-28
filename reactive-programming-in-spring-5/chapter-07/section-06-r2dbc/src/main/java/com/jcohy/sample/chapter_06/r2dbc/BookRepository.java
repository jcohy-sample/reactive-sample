package com.jcohy.sample.chapter_06.r2dbc;

import reactor.core.publisher.Flux;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:11:15
 * @since 2022.0.1
 */
@Repository
public interface BookRepository extends ReactiveCrudRepository<Book, Integer> {

    @Query("SELECT * FROM book WHERE publishing_year = " +
            "(SELECT MAX(publishing_year) FROM book)")
    Flux<Book> findTheLatestBooks();
}
