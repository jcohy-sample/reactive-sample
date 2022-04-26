package com.jcohy.sample.reactive.chapter_07.mongo_rx;

import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:17:13
 * @since 2022.0.1
 */
@Service
public class RxMongoTemplateQueryService {

    private static final String BOOK_COLLECTION = "book";

    private final ReactiveMongoOperations mongoOperations;

    public RxMongoTemplateQueryService(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public Flux<Book> findBooksByTitle(String title) {
        Query query = Query.query(new Criteria("title")
                        .regex(".*" + title + ".*"))
                .limit(100);
        return mongoOperations.find(query, Book.class, BOOK_COLLECTION);
    }
}
