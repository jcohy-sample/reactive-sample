package com.jcohy.sample.reactive.chapter_03.news_service.processor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:11:29
 * @since 1.0.0
 */
public class SmartMulticastProcessor implements Processor<NewsLetter, NewsLetter> {

    static final AtomicReferenceFieldUpdater<SmartMulticastProcessor, Subscription> UPSTREAM =
            AtomicReferenceFieldUpdater.newUpdater(SmartMulticastProcessor.class, Subscription.class, "upstream");

    static final AtomicReferenceFieldUpdater<SmartMulticastProcessor, InnerSubscription[]> ACTIVE =
            AtomicReferenceFieldUpdater.newUpdater(SmartMulticastProcessor.class, InnerSubscription[].class, "active");

    private static final InnerSubscription[] EMPTY = new InnerSubscription[0];

    private static final InnerSubscription[] TERMINATED = new InnerSubscription[0];

    NewsLetter cache;

    Throwable throwable;

    volatile Subscription upstream;

    volatile InnerSubscription[] active = EMPTY;

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Override
    public void subscribe(Subscriber<? super NewsLetter> actual) {
        Objects.requireNonNull(actual);
        InnerSubscription s = new InnerSubscription(actual, this);

        if (add(s)) {
            actual.onSubscribe(s);
        }
        else {
            actual.onSubscribe(s);

            if (throwable == null) {
                s.onComplete();
            }
            else {
                s.onError(throwable);
            }
        }

    }

    @Override
    public void onSubscribe(Subscription s) {
        Objects.requireNonNull(s);

        if (UPSTREAM.compareAndSet(this, null, s)) {
            s.request(Long.MAX_VALUE);
        }
        else {
            s.cancel();
        }
    }

    @Override
    public void onNext(NewsLetter newsLetterTemplate) {
        Objects.requireNonNull(newsLetterTemplate);

        InnerSubscription[] active = this.active;
        cache = newsLetterTemplate;

        for (InnerSubscription subscription : active) {
            subscription.tryEmit(newsLetterTemplate);
        }
    }

    @Override
    public void onError(Throwable t) {
        Objects.requireNonNull(t);

        InnerSubscription[] active = ACTIVE.getAndSet(this, TERMINATED);
        throwable = t;

        for (InnerSubscription subscription : active) {
            subscription.onError(t);
        }
    }

    @Override
    public void onComplete() {
        InnerSubscription[] active = ACTIVE.getAndSet(this, TERMINATED);

        for (InnerSubscription subscription : active) {
            subscription.onComplete();
        }
    }

    private boolean add(InnerSubscription subscription) {
        for (; ; ) {
            InnerSubscription[] subscriptions = active;

            if (isTerminated()) {
                return false;
            }

            int n = subscriptions.length;
            InnerSubscription[] copied = new InnerSubscription[n + 1];

            if (n > 0) {
                int index = (n - 1) & hash(subscription);

                if (subscriptions[index].equals(subscription)) {
                    return false;
                }

                System.arraycopy(subscriptions, 0, copied, 0, n);
            }

            copied[n] = subscription;

            if (ACTIVE.compareAndSet(this, subscriptions, copied)) {
                return true;
            }
        }
    }

    boolean remove(InnerSubscription subscription) {
        for (; ; ) {
            InnerSubscription[] subscriptions = active;

            if (isTerminated()) {
                return false;
            }

            int n = subscriptions.length;

            if (n == 0) {
                return false;
            }

            InnerSubscription[] copied = new InnerSubscription[n - 1];
            int index = (n - 1) & hash(subscription);

            if (!subscriptions[index].equals(subscription)) {
                return false;
            }

            if (index > 0) {
                System.arraycopy(subscriptions, 0, copied, 0, index);
            }

            if (index + 1 < n) {
                System.arraycopy(subscriptions, index + 1, copied, index, n - index - 1);
            }

            if (ACTIVE.compareAndSet(this, subscriptions, copied)) {
                return true;
            }
        }
    }

    boolean isTerminated() {
        return active == TERMINATED;
    }
}
