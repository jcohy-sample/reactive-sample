package com.jcohy.sample.reactive.chapter_o7.rxsync;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:18:33
 * @since 2022.0.1
 */
@Repository
public interface BookJpaRepository extends CrudRepository<Book, Integer> {

    Iterable<Book> findByIdBetween(int lower, int upper);

    @Query("SELECT b FROM Book b WHERE " +
            "LENGTH(b.title) = (SELECT MIN(LENGTH(b2.title)) FROM Book b2)")
    Iterable<Book> findShortestTitle();
}
