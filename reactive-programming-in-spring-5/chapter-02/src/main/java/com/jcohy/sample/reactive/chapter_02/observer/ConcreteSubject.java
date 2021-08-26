package com.jcohy.sample.reactive.chapter_02.observer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:23
 * @since 1.0.0
 */
public class ConcreteSubject implements Subjects<String> {

    private final Set<Observers> observers = new CopyOnWriteArraySet<>();

    @Override
    public void registerObserver(Observers<String> observers) {
        this.observers.add(observers);
    }

    @Override
    public void unregisterObserver(Observers<String> observers) {
        this.observers.remove(observers);
    }

    @Override
    public void notifyObservers(String event) {
        observers.forEach((observer) -> {observer.observe(event);});
    }
}
