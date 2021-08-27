package com.jcohy.sample.reactive.chapter_03.conversion_problem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:14:42
 * @since 1.0.0
 */
public class AsyncAdapters {

    /**
     * 这是 ListenableFuture 到 CompletionStage 适配器方法的实现。
     * @param future future
     * @param <T> t
     * @return /
     */
    public static <T> CompletionStage<T> toCompletion(ListenableFuture<T> future) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        // 添加回调
        future.addCallback(completableFuture::complete,
                completableFuture::completeExceptionally);
        return completableFuture;
    }

    /**
     * 这是 CompletionStage 到 ListenableFuture 适配器方法的实现
     * @param stage stage
     * @param <T> t
     * @return /
     */
    public static <T> ListenableFuture<T> toListenable(CompletionStage<T> stage){
        SettableListenableFuture<T> future = new SettableListenableFuture<>();
        stage.whenComplete((v,t) -> {
            if( t== null ){
                future.set(v);
            } else {
                future.setException(t);
            }

        });
        return future;
    }
}
