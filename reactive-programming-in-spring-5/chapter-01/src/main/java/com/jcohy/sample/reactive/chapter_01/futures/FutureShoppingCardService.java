package com.jcohy.sample.reactive.chapter_01.futures;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.jcohy.sample.reactive.chapter_01.commons.Input;
import com.jcohy.sample.reactive.chapter_01.commons.Output;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:31
 * @since 1.0.0
 */
public class FutureShoppingCardService implements ShoppingCardService{
    @Override
    public Future<Output> calculate(Input input) {
        FutureTask<Output> futureTask = new FutureTask<>(() -> {
            Thread.sleep(1000);
            return new Output();
        });

        new Thread(futureTask).start();

        return futureTask;
    }
}
