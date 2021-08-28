package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.Date;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.News;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:17:28
 * @since 1.0.0
 */
public class DBPublisher implements Publisher<News> {

    private final MongoCollection<News> collection;

    private final String category;

    public DBPublisher(MongoCollection<News> collection, String category) {
        this.collection = collection;
        this.category = category;
    }

    @Override
    public void subscribe(Subscriber<? super News> subscriber) {
        FindPublisher<News> findPublisher = collection.find(News.class);

        findPublisher.sort(Sorts.descending("publishedOn"))
                .filter(Filters.and(
                        Filters.eq("category", category),
                        Filters.gt("publishedOn", today())
                ))
                .subscribe(subscriber);
    }

    private Date today() {
        Date date = new Date();
        return new Date(date.getYear(), date.getMonth(), date.getDate());
    }
}
