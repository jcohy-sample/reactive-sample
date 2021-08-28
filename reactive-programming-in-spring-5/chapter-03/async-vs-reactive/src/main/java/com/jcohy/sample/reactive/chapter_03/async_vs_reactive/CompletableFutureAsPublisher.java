package com.jcohy.sample.reactive.chapter_03.async_vs_reactive;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:15:53
 * @since 1.0.0
 */
public class CompletableFutureAsPublisher<T> implements Publisher<T> {

    final CompletableFuture<? extends T> completableFuture;

    CompletableFutureAsPublisher(CompletableFuture<? extends T> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        Objects.requireNonNull(s, "Subscriber must be present");

        CompletableFutureSubscription<T> subscription =
                new CompletableFutureSubscription<>(completableFuture, s);

        s.onSubscribe(subscription);
    }

    static final class CompletableFutureSubscription<T> extends AtomicBoolean
            implements Subscription {

        final CompletableFuture<? extends T> completableFuture;

        final Subscriber<? super T> actual;

        volatile boolean cancelled;

        CompletableFutureSubscription(
                CompletableFuture<? extends T> future,
                Subscriber<? super T> actual
        ) {
            completableFuture = future;
            this.actual = actual;
        }

        @Override
        public void request(long n) {
            if (compareAndSet(false, true)) {
                completableFuture.whenComplete((v, e) -> {
                    if (cancelled) {
                        return;
                    }

                    if (e != null) {
                        actual.onError(e);
                    }
                    else {
                        if (v != null) {
                            actual.onNext(v);
                        }
                        actual.onComplete();
                    }
                });
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}
