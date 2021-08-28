package com.jcohy.sample.reactive.chapter_03.conversion_problem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:14:52
 * @since 1.0.0
 */
public class FakeAsyncDatabaseClient implements AsyncDatabaseClient {
    @Override
    public <T> CompletionStage<T> store(CompletionStage<T> stage) {
        return stage.thenCompose(e -> CompletableFuture.supplyAsync(() -> e));
    }
}
