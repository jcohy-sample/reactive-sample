package com.jcohy.sample.reactive.chapter_03.jdk9;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import com.jcohy.sample.reactive.chapter_03.news_service.NewsServicePublisher;
import com.jcohy.sample.reactive.chapter_03.news_service.NewsServiceSubscriber;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:12:57
 * @since 1.0.0
 */
public class AdapterExample {

    public static void main(String[] args) {
        Flow.Publisher jdkPublisher = FlowAdapters.toFlowPublisher(new NewsServicePublisher(smp ->
                Flowable.intervalRange(0, 10, 0, 10, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .map(e -> new NewsLetter(String.valueOf(e), Collections.emptyList()))
                        .subscribe(smp)
        ));

        Publisher external = FlowAdapters.toPublisher(jdkPublisher);
        Flow.Publisher jdkPublisher2 = FlowAdapters.toFlowPublisher(
                external
        );

        NewsServiceSubscriber newsServiceSubscriber = new NewsServiceSubscriber(2);
        jdkPublisher2.subscribe(FlowAdapters.toFlowSubscriber(newsServiceSubscriber));


        while (true) {
            Optional<NewsLetter> letterOptional = newsServiceSubscriber.eventuallyReadDigest();

            if (letterOptional.isPresent()) {
                NewsLetter letter = letterOptional.get();
                System.out.println(letter);

                if (letter.getTitle().equals("9")) {
                    break;
                }
            }
        }
    }
}
