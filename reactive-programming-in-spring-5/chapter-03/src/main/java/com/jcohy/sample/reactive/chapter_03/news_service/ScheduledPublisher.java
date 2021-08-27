package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:18:00
 * @since 1.0.0
 */
public class ScheduledPublisher<T> implements Publisher<T> {

    final ScheduledExecutorService scheduledExecutorService;
    final int period;
    final TimeUnit unit;
    final Callable<? extends Publisher<T>> publisherCallable;

    public ScheduledPublisher(ScheduledExecutorService scheduledExecutorService,
            int period, TimeUnit unit, Callable<? extends Publisher<T>> publisherCallable) {
        this(publisherCallable, period, unit,
                Executors.newSingleThreadScheduledExecutor());
    }

    public ScheduledPublisher(
            Callable<? extends Publisher<T>> publisherCallable,
            int period,
            TimeUnit unit,
            ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.period = period;
        this.unit = unit;
        this.publisherCallable = publisherCallable;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {

    }
}
