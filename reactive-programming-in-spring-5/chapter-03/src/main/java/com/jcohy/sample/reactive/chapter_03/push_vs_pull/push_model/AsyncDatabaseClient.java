package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_model;

import rx.Observable;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:48
 * @since 1.0.0
 */
public interface AsyncDatabaseClient {
    Observable<Item> getStreamOfItems();
}
