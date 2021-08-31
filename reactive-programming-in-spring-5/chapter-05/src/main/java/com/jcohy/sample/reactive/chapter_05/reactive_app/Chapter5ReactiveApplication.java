package com.jcohy.sample.reactive.chapter_05.reactive_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:11:39
 * @since 1.0.0
 */
@SpringBootApplication
public class Chapter5ReactiveApplication {

    private static final Logger log = LoggerFactory.getLogger(Chapter5ReactiveApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(Chapter5ReactiveApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            SensorReadingRepository sensorReadingRepository) {
        return RouterFunctions
                .route(
                        RequestPredicates.GET("/"),
                        serverRequest -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_STREAM_JSON)
                                .body(sensorReadingRepository.findBy(), SensorsReadings.class));
    }
}
