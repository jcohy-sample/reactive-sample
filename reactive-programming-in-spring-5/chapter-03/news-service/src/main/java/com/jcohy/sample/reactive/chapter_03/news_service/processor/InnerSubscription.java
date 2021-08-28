package com.jcohy.sample.reactive.chapter_03.news_service.processor;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import com.jcohy.sample.reactive.chapter_03.news_service.NamedSubscriber;
import com.jcohy.sample.reactive.chapter_03.news_service.SubscriptionUtils;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:11:30
 * @since 1.0.0
 */
public class InnerSubscription implements Subscription {

    static final AtomicLongFieldUpdater<InnerSubscription> REQUESTED =
            AtomicLongFieldUpdater.newUpdater(InnerSubscription.class, "requested");

    static final AtomicIntegerFieldUpdater<InnerSubscription> WIP =
            AtomicIntegerFieldUpdater.newUpdater(InnerSubscription.class, "wip");

    final Subscriber<? super NewsLetter> actual;

    final SmartMulticastProcessor parent;

    Throwable throwable;

    boolean done;

    boolean sent;

    volatile long requested;

    volatile int wip;

    public InnerSubscription(Subscriber<? super NewsLetter> actual,
            SmartMulticastProcessor parent) {
        this.actual = actual;
        this.parent = parent;
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            onError(throwable = new IllegalArgumentException("negative subscription request"));
            return;
        }

        SubscriptionUtils.request(n, this, REQUESTED);

        tryDrain();
    }

    @Override
    public void cancel() {
        parent.remove(this);
        done = true;
    }

    String getRecipient() {
        if (actual instanceof NamedSubscriber) {
            return ((NamedSubscriber) actual).getName();
        }

        return null;
    }

    void onError(Throwable t) {
        if (done) {
            return;
        }

        tryDrain();
    }

    void onComplete() {
        if (done) {
            return;
        }

        parent.remove(this);

        tryDrain();
    }

    boolean isTerminated() {
        return parent.throwable != null || parent.isTerminated();
    }

    boolean hasNoMoreEmission() {
        return sent || parent.cache == null || throwable != null;
    }

    void tryEmit(NewsLetter element) {
        if (done) {
            return;
        }

        int wip;

        if ((wip = WIP.incrementAndGet(this)) > 1) {
            sent = false;
            return;
        }

        Subscriber<? super NewsLetter> a = actual;
        long r = requested;

        for (; ; ) {
            if (r > 0) {
                a.onNext(element.setRecipient(getRecipient()));
                sent = true;

                REQUESTED.decrementAndGet(this);
                this.wip = 0;

                return;
            }
            else {
                wip = WIP.addAndGet(this, -wip);

                if (wip == 0) {
                    sent = false;
                    return;
                }
                else {
                    r = requested;
                }
            }
        }
    }

    void tryDrain() {
        if (done) {
            return;
        }

        int wip;

        if ((wip = WIP.incrementAndGet(this)) > 1) {
            return;
        }

        Subscriber<? super NewsLetter> a = actual;
        long r = requested;

        for (; ; ) {
            NewsLetter element = parent.cache;

            if (r > 0 && !sent && element != null) {
                a.onNext(element.setRecipient(getRecipient()));
                sent = true;

                r = REQUESTED.decrementAndGet(this);
            }

            wip = WIP.addAndGet(this, -wip);

            if (wip == 0) {
                if (!done && isTerminated() && hasNoMoreEmission()) {
                    done = true;
                    if (throwable == null && parent.throwable == null) {
                        a.onComplete();
                    }
                    else {
                        throwable = throwable == null ? parent.throwable : throwable;
                        a.onError(throwable);
                    }
                }

                return;
            }
        }
    }
}
