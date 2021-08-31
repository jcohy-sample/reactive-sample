package com.jcohy.sample.reactive.chapter_06.functional.password.verification.client;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:12:58
 * @since 1.0.0
 */
public class DefaultPasswordVerificationService implements PasswordVerificationService {

    final WebClient webClient;

    public DefaultPasswordVerificationService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Override
    public Mono<Void> check(String raw, String encoded) {
        return webClient
                .post()
                .uri("/password")
                .body(BodyInserters.fromPublisher(
                        Mono.just(new PasswordDTO(raw, encoded)),
                        PasswordDTO.class
                ))
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.empty();
                    }
                    else if (response.statusCode() == HttpStatus.EXPECTATION_FAILED) {
                        return Mono.error(new BadCredentialsException("Invalid credentials"));
                    }
                    return Mono.error(new IllegalStateException());
                });
    }
}
