package com.jcohy.sample.reactive.chapter_06.websocket;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:16:46
 * @since 1.0.0
 */
@Configuration
public class WebSocketConfiguration {

    /**
     * 这里是 HandlerMapping bean 的声明和设置。我们创建了 SimpleUrlHandlerMapping
     * 它能设置基于路径的到 WebSocketHandler 的映射。为了在其他 HandlerMapping
     * 实例之前处理 SimpleUrlHandlerMapping ，它应该具有更高的优先级。
     *
     * @return /
     */
    @Bean
    public HandlerMapping handlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap("/ws/echo", new EchoWebSocketHandler()));
        mapping.setOrder(-1);
        return mapping;
    }

    /**
     * 这是名为 WebSocketHandlerAdapter 的 HandlerAdapter bean 声明。在这里， WebSocketHandlerAdapter
     * 扮演着最重要的角色， 因为它将 HTTP 连接升级到 WebSocket ，然后调用了 WebSocketHandler#handle 方法。
     *
     * @return /
     */
    @Bean
    public HandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
