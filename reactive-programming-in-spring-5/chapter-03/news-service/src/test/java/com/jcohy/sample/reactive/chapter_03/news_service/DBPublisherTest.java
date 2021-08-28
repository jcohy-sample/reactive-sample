package com.jcohy.sample.reactive.chapter_03.news_service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.News;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;
import io.reactivex.Flowable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:11:48
 * @since 1.0.0
 */
public class DBPublisherTest extends PublisherVerification<News> implements WithEmbeddedMongo {

    private final MongoCollection<News> collection;

    public DBPublisherTest() {
        super(new TestEnvironment(2000, 2000));
        this.collection = mongoClient()
                .getDatabase("news")
                .getCollection("news", News.class);
    }

    @BeforeEach
    static void up() throws IOException {
        WithEmbeddedMongo.setUpMongo();
    }

    @AfterEach
    static void down() {
        WithEmbeddedMongo.tearDownMongo();
    }

    @Override
    public Publisher<News> createPublisher(long elements) {
        prepareRandomData(elements);
        return new DBPublisher(collection, "tech");
    }

    @Override
    public Publisher<News> createFailedPublisher() {
        return null;
    }

    private void prepareRandomData(long elements) {
        if (elements <= 0) {
            return;
        }

        Flowable<Success> successFlowable = Flowable.fromPublisher(collection.drop())
                .ignoreElements()
                .andThen(
                        Flowable.rangeLong(0L, elements)
                                .map(l -> NewsHarness.generate())
                                .buffer(500, TimeUnit.MILLISECONDS)
                                .flatMap(collection::insertMany)
                );

        if (elements == Long.MAX_VALUE || elements == Integer.MAX_VALUE) {
            successFlowable.subscribe();
        }
        else {
            successFlowable.blockingSubscribe();
        }
    }
}