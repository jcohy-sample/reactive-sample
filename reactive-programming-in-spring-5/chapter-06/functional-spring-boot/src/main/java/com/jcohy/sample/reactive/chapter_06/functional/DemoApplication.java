package com.jcohy.sample.reactive.chapter_06.functional;

import reactor.core.publisher.Hooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:15:54
 * @since 1.0.0
 */
@SpringBootApplication
public class DemoApplication {

    final ServerRedirectHandler serverRedirectHandler = new ServerRedirectHandler();

    public static void main(String[] args) {
        Hooks.onOperatorDebug();
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(OrderHandler orderHandler) {
        return
                nest(path("/orders"),
                        nest(accept(APPLICATION_JSON),
                                route(GET("/{id}"), orderHandler::get)
                                        .andRoute(method(HttpMethod.GET), orderHandler::list)
                        )
                                .andNest(contentType(APPLICATION_JSON),
                                        route(POST("/"), orderHandler::create)
                                )
                                .andNest((serverRequest) -> serverRequest.cookies()
                                                .containsKey("Redirect-Traffic"),
                                        route(all(), serverRedirectHandler)
                                )
                );
    }
}
