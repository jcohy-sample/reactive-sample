package com.jcohy.sample.reactive.chapter_03.news_service.subscribers;

import com.jcohy.sample.reactive.chapter_03.news_service.SchedulerMainSubscription;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:18:33
 * @since 1.0.0
 */
public class FastSchedulerInnerSubscriber<T> extends SchedulerInnerSubscriber<T> {

    public FastSchedulerInnerSubscriber(SchedulerMainSubscription<T> parent) {
        super(parent);
    }

    @Override
    public void onNext(T t) {
        if (parent.isCancelled()) {
            s.cancel();
            return;
        }
        parent.emit(t);
    }
}
