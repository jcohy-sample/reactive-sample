package com.jcohy.sample.reactive.chapter_03.push_vs_pull.pure_pull_model;

import java.util.concurrent.CompletionStage;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:18
 * @since 1.0.0
 */
public interface AsyncDatabaseClient {
    // 执行查询并异步接收结果
    CompletionStage<Item> getNextAfterId(String id);
}
