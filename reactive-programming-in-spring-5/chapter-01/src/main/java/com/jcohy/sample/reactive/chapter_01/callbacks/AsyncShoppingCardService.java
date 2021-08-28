package com.jcohy.sample.reactive.chapter_01.callbacks;

import java.util.function.Consumer;

import com.jcohy.sample.reactive.chapter_01.commons.Input;
import com.jcohy.sample.reactive.chapter_01.commons.Output;

/**
 * <p> 描述: 将结果传递给回调函数来立即返回结果.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:24
 * @since 1.0.0
 */
public class AsyncShoppingCardService implements ShoppingCardService {

    @Override
    public void calculate(Input input, Consumer<Output> c) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            c.accept(new Output());
        }).start();
    }
}
