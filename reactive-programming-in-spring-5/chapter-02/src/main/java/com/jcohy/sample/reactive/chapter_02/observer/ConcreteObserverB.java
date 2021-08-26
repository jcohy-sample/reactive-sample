package com.jcohy.sample.reactive.chapter_02.observer;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:22
 * @since 1.0.0
 */
public class ConcreteObserverB implements Observers<String> {

    @Override
    public void observe(String event) {
        System.out.println("Observer B: " + event);
    }
}
