package com.jcohy.sample.reactive.chapter_01.completion_stage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.jcohy.sample.reactive.chapter_01.commons.Input;
import com.jcohy.sample.reactive.chapter_01.commons.Output;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:53
 * @since 1.0.0
 */
public class CompletionStageShoppingCardService implements ShoppingCardService{
    @Override
    public CompletionStage<Output> calculate(Input input) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Output();
        });
    }
}
