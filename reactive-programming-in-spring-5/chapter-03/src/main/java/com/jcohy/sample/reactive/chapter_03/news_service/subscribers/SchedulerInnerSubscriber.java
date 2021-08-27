package com.jcohy.sample.reactive.chapter_03.news_service.subscribers;

import com.jcohy.sample.reactive.chapter_03.news_service.SchedulerMainSubscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:18:26
 * @since 1.0.0
 */
public abstract class SchedulerInnerSubscriber<T> implements Subscriber<T> {

    final SchedulerMainSubscription<T> parent;

    Subscription s;

    public SchedulerInnerSubscriber(SchedulerMainSubscription<T> parent) {
        this.parent = parent;
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (this.s == null ){
            this.s = s;
            s.request(Long.MAX_VALUE);
        } else {
            s.cancel();
        }
    }

    @Override
    public void onError(Throwable t) {
        parent.onError(t);
    }

    @Override
    public void onComplete() {

    }
}
