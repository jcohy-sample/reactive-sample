package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_pull_model;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:16:10
 * @since 1.0.0
 */
public class DelayedFakeAsyncDatabaseClient implements AsyncDatabaseClient {

    @Override
    public Publisher<Item> getStreamOfItems() {
        return Flowable.range(1, Integer.MAX_VALUE)
                .map(value -> new Item("" + value))
                .delay(500, TimeUnit.MILLISECONDS)
                .hide()
                .subscribeOn(Schedulers.io())
                .delaySubscription(100, TimeUnit.MILLISECONDS);
    }
}
