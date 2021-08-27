package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import com.jcohy.sample.reactive.chapter_03.news_service.subscribers.FastSchedulerInnerSubscriber;
import com.jcohy.sample.reactive.chapter_03.news_service.subscribers.SlowSchedulerInnerSubscriber;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:18:27
 * @since 1.0.0
 */
public class SchedulerMainSubscription<T> implements Subscription, Runnable {

    final Subscriber<? super T> actual;
    final Callable<? extends Publisher<T>> publisherCallable;
    ScheduledFuture<?> scheduledFuture;
    volatile long requested;

    static final AtomicLongFieldUpdater<SchedulerMainSubscription> REQUESTED =
            AtomicLongFieldUpdater.newUpdater(SchedulerMainSubscription.class, "requested");

    volatile boolean cancelled;

    public SchedulerMainSubscription(Subscriber<? super T> actual,
            Callable<? extends Publisher<T>> publisherCallable) {
        this.actual = actual;
        this.publisherCallable = publisherCallable;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void run() {
        if ( !cancelled ) {
            try {
                Publisher<T> innerPublisher = Objects.requireNonNull(publisherCallable.call());
                if( requested == Long.MAX_VALUE ){
                    innerPublisher.subscribe(new FastSchedulerInnerSubscriber<>(this));
                } else {
                    innerPublisher.subscribe(new SlowSchedulerInnerSubscriber<>(this));
                }
            }
            catch (Exception e) {
                onError(e);
            }
        }
    }

    @Override
    public void request(long n) {
        if( n<= 0 ){
            onError(new IllegalArgumentException(
                    "Spec. Rule 3.9 - Cannot request a non strictly positive number: " + n
            ));
            return;
        }

        SubscriptionUtils.request(n,this,REQUESTED);
    }

    @Override
    public void cancel() {
        if (cancelled) {
            cancelled = true;

            if ( scheduledFuture != null ) {
                scheduledFuture.cancel(true);
            }
        }
    }

    public void onError(Throwable throwable){
        if(cancelled){
            return;
        }
        cancel();
        actual.onError(throwable);
    }

    public void emit(T e){
        Subscriber<? super T> a = this.actual;

        a.onNext(e);
    }

    public void tryEmit(T e){
        for (;;) {
            long r = this.requested;

            if (r <= 0) {
                onError(new IllegalStateException("Lack of demand"));
                return;
            }

            if (requested == Long.MAX_VALUE || REQUESTED.compareAndSet(this, r, r - 1)) {
                emit(e);
                return;
            }
        }
    }

    void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
