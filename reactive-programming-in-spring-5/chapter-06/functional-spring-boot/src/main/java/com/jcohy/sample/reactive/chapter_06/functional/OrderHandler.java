package com.jcohy.sample.reactive.chapter_06.functional;

import java.net.URI;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:15:46
 * @since 1.0.0
 */
@Service
public class OrderHandler {

    final OrderRepository orderRepository;

    public OrderHandler(OrderRepository repository) {
        orderRepository = repository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request
                .bodyToMono(Order.class)
                .flatMap(orderRepository::save)
                .flatMap(o ->
                        ServerResponse.created(URI.create("/orders/" + o.getId()))
                                .build()
                );
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return orderRepository
                .findById(request.pathVariable("id"))
                .flatMap(order ->
                        ServerResponse
                                .ok()
                                .syncBody(order)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return null;
    }
}
