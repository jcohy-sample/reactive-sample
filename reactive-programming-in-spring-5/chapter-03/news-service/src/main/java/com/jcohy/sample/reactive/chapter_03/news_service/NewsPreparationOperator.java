package com.jcohy.sample.reactive.chapter_03.news_service;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.News;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:10:37
 * @since 1.0.0
 */
public class NewsPreparationOperator implements Publisher<NewsLetter> {

    final Publisher<? extends News> upstream;

    final String title;

    public NewsPreparationOperator(Publisher<? extends News> upstream, String title) {
        this.upstream = upstream;
        this.title = title;
    }

    @Override
    public void subscribe(Subscriber<? super NewsLetter> s) {
        upstream.subscribe(new NewsPreparationInner(s, title));
    }

    final static class NewsPreparationInner implements Subscription, Subscriber<News> {

        static final AtomicIntegerFieldUpdater<NewsPreparationInner> WIP
                = AtomicIntegerFieldUpdater.newUpdater(NewsPreparationInner.class, "wip");

        static final AtomicLongFieldUpdater<NewsPreparationInner> REQUESTED
                = AtomicLongFieldUpdater.newUpdater(NewsPreparationInner.class, "requested");

        final Subscriber<? super NewsLetter> actual;

        final Queue<News> holder;

        final AtomicBoolean terminated = new AtomicBoolean();

        final String title;

        Subscription subscription;

        boolean completed;

        volatile int wip;

        volatile long requested;

        public NewsPreparationInner(Subscriber<? super NewsLetter> actual, String title) {
            this.actual = actual;
            this.holder = new LinkedList<>();
            this.title = title;

        }

        @Override
        public void onSubscribe(Subscription subscription) {
            if (this.subscription == null) {
                this.subscription = subscription;
                actual.onSubscribe(this);
                subscription.request(Long.MAX_VALUE);
            }
            else {
                subscription.cancel();
            }
        }

        @Override
        public void onNext(News letter) {
            holder.offer(letter);
        }

        @Override
        public void onError(Throwable t) {
            if (!terminated.compareAndSet(false, true)) {
                return;
            }
            holder.clear();
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            completed = true;
            if (requested > 0) {
                drain();
            }
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                onError(new IllegalArgumentException("negative subscription request"));
                return;
            }

            if (terminated.get()) {
                return;
            }

            SubscriptionUtils.request(n, this, REQUESTED);
            drain();
        }

        @Override
        public void cancel() {
            if (!terminated.compareAndSet(false, true)) {
                return;
            }
            subscription.cancel();
            holder.clear();
        }

        void drain() {
            int wip;
            if ((wip = WIP.incrementAndGet(this)) > 1) {
                return;
            }

            for (; ; ) {
                if (completed && terminated.compareAndSet(false, true)) {
                    NewsLetter newsLetter = new NewsLetter(title, new ArrayList<>(holder));

                    holder.clear();
                    actual.onNext(newsLetter);
                    actual.onComplete();
                }

                wip = WIP.addAndGet(this, -wip);

                if (wip == 0) {
                    return;
                }
            }
        }
    }
}


