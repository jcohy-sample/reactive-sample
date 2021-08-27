package com.jcohy.sample.reactive.chapter_03.push_vs_pull.batched_pull_model;

import java.util.ArrayList;
import java.util.List;
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
 * @version 1.0.0 2021/8/27:15:38
 * @since 1.0.0
 */
public class DelayedFakeAsyncDatabaseClient implements AsyncDatabaseClient {
    @Override
    public CompletionStage<List<Item>> getNextBatchAfterId(String id, int count) {
        CompletableFuture<List<Item>> future = new CompletableFuture<>();

        Flowable.range(Integer.parseInt(id) + 1, count)
                .map(i -> new Item("" + i))
                .collectInto(new ArrayList<Item>(),ArrayList::add)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribe(future::complete);
        return future;
    }
}
