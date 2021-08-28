package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_pull_model;

import org.reactivestreams.Publisher;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:56
 * @since 1.0.0
 */
public class Puller {

    final AsyncDatabaseClient dbClient = new DelayedFakeAsyncDatabaseClient();

    public Publisher<Item> list(int count) {
        Publisher<Item> source = dbClient.getStreamOfItems();
        TakeFilterOperator<Item> takeFilter = new TakeFilterOperator<>(source, count, this::isValid);
        return takeFilter;
    }

    boolean isValid(Item item) {
        return Integer.parseInt(item.getId()) % 2 == 0;
    }
}
