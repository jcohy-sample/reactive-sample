package com.jcohy.sample.reactive.chapter_03.push_vs_pull.pure_pull_model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:18
 * @since 1.0.0
 */
public class DelayedFakeAsyncDatabaseClient implements AsyncDatabaseClient {
    @Override
    public CompletionStage<Item> getNextAfterId(String id) {
        CompletableFuture<Item> future = new CompletableFuture<>();

        Flowable.just(new Item("" + (Integer.parseInt(id) + 1)))
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribe(future::complete);
        return future;
    }
}
