package com.jcohy.sample.reactive.chapter_03.conversion_problem;

import java.util.concurrent.CompletionStage;

/**
 * <p> 描述: 数据库客户端的接口声明，它是支持异步数据库访问的客户端接口的代表性示例.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:14:41
 * @since 1.0.0
 */
public interface AsyncDatabaseClient {

    <T> CompletionStage<T> store(CompletionStage<T> stage);
}
