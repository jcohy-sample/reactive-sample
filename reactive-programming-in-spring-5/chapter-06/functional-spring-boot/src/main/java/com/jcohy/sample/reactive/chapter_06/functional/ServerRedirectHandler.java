package com.jcohy.sample.reactive.chapter_06.functional;

import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:15:47
 * @since 1.0.0
 */
public class ServerRedirectHandler implements HandlerFunction<ServerResponse> {

    final WebClient webClient = WebClient.create();

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return webClient
                .method(request.method())
                .uri(request.headers()
                        .header("Redirect-Traffic")
                        .get(0))
                .headers((h) -> h.addAll(request.headers().asHttpHeaders()))
                .body(BodyInserters.fromDataBuffers(
                        request.bodyToFlux(DataBuffer.class)
                ))
                .cookies(c -> request
                        .cookies()
                        .forEach((key, list) -> list.forEach(cookie -> c.add(key, cookie.getValue())))
                )
                .exchange()
                .flatMap(cr -> ServerResponse
                        .status(cr.statusCode())
                        .cookies(c -> c.addAll(cr.cookies()))
                        .headers(hh -> hh.addAll(cr.headers().asHttpHeaders()))
                        .body(BodyInserters.fromDataBuffers(
                                cr.bodyToFlux(DataBuffer.class)
                        ))
                );
    }
}
