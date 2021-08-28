package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.News;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import com.jcohy.sample.reactive.chapter_03.news_service.processor.SmartMulticastProcessor;
import com.mongodb.reactivestreams.client.MongoClient;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:11:28
 * @since 1.0.0
 */
public class NewsServicePublisher implements Publisher<NewsLetter> {

    final SmartMulticastProcessor processor;

    public NewsServicePublisher(MongoClient client, String categoryOfInterests) {
        ScheduledPublisher<NewsLetter> scheduler = new ScheduledPublisher<>(
                () -> new NewsPreparationOperator(
                        new DBPublisher(
                                client.getDatabase("news")
                                        .getCollection("news", News.class),
                                categoryOfInterests
                        ),
                        "Some Digest"
                ),
                1, TimeUnit.DAYS
        );

        SmartMulticastProcessor processor = new SmartMulticastProcessor();
        scheduler.subscribe(processor);

        this.processor = processor;
    }

    public NewsServicePublisher(Consumer<SmartMulticastProcessor> setup) {
        this.processor = new SmartMulticastProcessor();

        setup.accept(processor);
    }

    @Override
    public void subscribe(Subscriber<? super NewsLetter> s) {
        processor.subscribe(s);
    }
}
