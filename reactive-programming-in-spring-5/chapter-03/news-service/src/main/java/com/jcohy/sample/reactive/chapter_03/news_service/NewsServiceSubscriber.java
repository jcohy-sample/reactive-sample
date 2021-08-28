package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:11:45
 * @since 1.0.0
 */
public class NewsServiceSubscriber implements Subscriber<NewsLetter> {

    final Queue<NewsLetter> mailbox = new ConcurrentLinkedQueue<>();

    final AtomicInteger remaining = new AtomicInteger();

    final int take;

    Subscription subscription;

    public NewsServiceSubscriber(int take) {
        this.take = take;
        this.remaining.set(take);
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (subscription == null) {
            subscription = s;
            subscription.request(take);
        }
        else {
            s.cancel();
        }
    }

    @Override
    public void onNext(NewsLetter newsLetter) {
        Objects.requireNonNull(newsLetter);

        mailbox.offer(newsLetter);
    }

    @Override
    public void onError(Throwable t) {
        Objects.requireNonNull(t);

        if (t instanceof SubscribableErrorLetter) {
            subscription = null;
            ((SubscribableErrorLetter) t).resubscribe(this);
        }
    }

    @Override
    public void onComplete() {
        subscription = null;
    }

    public Optional<NewsLetter> eventuallyReadDigest() {
        NewsLetter letter = mailbox.poll();
        if (letter != null) {
            if (remaining.decrementAndGet() == 0) {
                subscription.request(take);
                remaining.set(take);
            }
            return Optional.of(letter);
        }
        return Optional.empty();
    }
}

