package com.jcohy.sample.reactive.chapter_06.functional;

import reactor.core.publisher.Mono;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:15:45
 * @since 1.0.0
 */
public interface OrderRepository {

    Mono<Order> findById(String id);

    Mono<Order> save(Order order);

    Mono<Void> deleteById(String id);
}
