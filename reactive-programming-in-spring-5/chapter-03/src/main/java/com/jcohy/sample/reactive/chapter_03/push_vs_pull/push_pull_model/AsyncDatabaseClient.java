package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_pull_model;

import org.reactivestreams.Publisher;

/**
 * <p> 描述: 使用 Reactive Stream 规范.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:16:09
 * @since 1.0.0
 */
public interface AsyncDatabaseClient {
    Publisher<Item> getStreamOfItems();
}
