package com.jcohy.sample.reactive.chapter_06.sse;

import reactor.core.publisher.Flux;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:16:57
 * @since 1.0.0
 */
public interface StocksService {

    Flux<StockItem> stream();
}
