package com.jcohy.sample.reactive.chapter_06.websocket;

import reactor.core.publisher.Mono;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:16:43
 * @since 1.0.0
 */
public class EchoWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(tm -> "Echo: " + tm)
                // 这里的 send 方法接受 Publisher<WebSocketMessage> 并返回 Mono<void> 作为结果。
                // 因此，通过使用 Reactor API 中的 as 操作符，我们可以将 Flux 视为 Mono<void> ，并使用 session::send 作为转换函数。
                .map(session::textMessage)
                .as(session::send);
    }
}
