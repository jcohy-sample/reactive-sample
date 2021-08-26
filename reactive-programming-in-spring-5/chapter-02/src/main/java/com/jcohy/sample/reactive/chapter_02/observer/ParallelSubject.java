package com.jcohy.sample.reactive.chapter_02.observer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:35
 * @since 1.0.0
 */
public class ParallelSubject implements Subjects<String> {
    private final Set<Observers<String>> observers =
            new CopyOnWriteArraySet<>();

    @Override
    public void registerObserver(Observers<String> observers) {
        this.observers.add(observers);
    }

    @Override
    public void unregisterObserver(Observers<String> observers) {
        this.observers.remove(observers);
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void notifyObservers(String event) {
        observers.forEach(observers ->
                executorService.submit(
                        () -> observers.observe(event)
                )
        );
    }
}
