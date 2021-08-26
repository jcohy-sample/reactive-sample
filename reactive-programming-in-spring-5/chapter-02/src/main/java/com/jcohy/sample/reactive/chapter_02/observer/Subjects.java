package com.jcohy.sample.reactive.chapter_02.observer;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:19
 * @since 1.0.0
 */
public interface Subjects<T> {

    void registerObserver(Observers<T> observers);

    void unregisterObserver(Observers<T> observers);

    void notifyObservers(T event);
}
