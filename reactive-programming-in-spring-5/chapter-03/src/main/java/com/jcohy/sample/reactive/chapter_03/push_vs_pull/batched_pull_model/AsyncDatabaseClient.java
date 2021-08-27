package com.jcohy.sample.reactive.chapter_03.push_vs_pull.batched_pull_model;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:37
 * @since 1.0.0
 */
public interface AsyncDatabaseClient {

    // 返回结果为 List<Item>
    CompletionStage<List<Item>> getNextBatchAfterId(String id, int count);
}
