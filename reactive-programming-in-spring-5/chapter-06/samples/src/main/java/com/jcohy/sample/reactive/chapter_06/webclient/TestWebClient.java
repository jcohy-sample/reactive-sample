package com.jcohy.sample.reactive.chapter_06.webclient;

import org.springframework.security.core.userdetails.User;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:16:21
 * @since 1.0.0
 */
public class TestWebClient {
    public static void main(String[] args) throws InterruptedException {
        WebClient.create("http://localhost:8080/api")
                .get()
                .uri("/users/{id}", 10)
                .retrieve()
                .bodyToMono(User.class)
                .map(User::getUsername)
                .subscribe();

        Thread.sleep(1000);
    }
}
