package com.jcohy.sample.reactive.chapter_06.functional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:15:45
 * @since 1.0.0
 */
@Component
public class InMemoryOrderRepository implements OrderRepository {

    final Map<String, Order> ordersMap;

    public InMemoryOrderRepository() {
        ordersMap = new ConcurrentHashMap<>();
    }

    @Override
    public Mono<Order> findById(String id) {
        return Mono.justOrEmpty(ordersMap.get(id));
    }

    @Override
    public Mono<Order> save(Order order) {
        ordersMap.put(order.getId(), order);

        return Mono.just(order);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return null;
    }
}
