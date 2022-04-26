package com.jcohy.sample.reactive.chapter_07.mongo_rx;

import java.util.Collections;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.conversions.Bson;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
public class RxMongoDriverQueryService {

    private static final String BOOK_COLLECTION = "book";

    private final MongoClient mongoClient;
    private final String dbName;

    public RxMongoDriverQueryService(
            MongoClient mongoClient,
            @Value("${spring.data.mongodb.database}") String dbName
    ) {
        this.mongoClient = mongoClient;
        this.dbName = dbName;
    }

    public Flux<Book> findBooksByTitle(String title, boolean negate) {
        return Flux.defer(() -> {
                    // findBooksByTitleRegex 正则匹配
                    Bson query = Filters
                            .regex("title", ".*" + title + ".*");
                    // findBooksByTitle
                    if (negate) {
                        query = Filters.not(query);
                    }
                    // 返回一个新的 Flux 实例，它将执行过程推迟到实际订阅发生的时间。
                    return mongoClient
                            .getDatabase(dbName)
                            .getCollection(BOOK_COLLECTION)
                            .find(query);
                })
                // 领域实体映射
                .map(doc -> new Book(
                        doc.getObjectId("id"),
                        doc.getString("title"),
                        doc.getInteger("pubYear"),
                        Collections.emptyList() // Omit authors deserialization for the sake of simplicity
                ));
    }
}
