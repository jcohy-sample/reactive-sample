package com.jcohy.sample.reactive.chapter_03.vertx;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:16:49
 * @since 1.0.0
 */
public class MockLogService implements LogService {
    @Override
    public Publisher<String> stream() {
        return Flowable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> "[" + System.nanoTime() + "] [LogServiceApplication] " + "[Thread " + Thread.currentThread() + "] Some loge here " + i + "\n");
    }
}
