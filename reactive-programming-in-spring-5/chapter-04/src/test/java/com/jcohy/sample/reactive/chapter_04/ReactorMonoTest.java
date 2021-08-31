package com.jcohy.sample.reactive.chapter_04;

import java.util.Optional;

import com.jcohy.sample.reactive.chapter_04.dto.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:18:03
 * @since 1.0.0
 */
public class ReactorMonoTest {

    private static final Logger log = LoggerFactory.getLogger(ReactorMonoTest.class);

    @Test
    public void createMono() {
        Mono<String> stream4 = Mono.just("One");
        Mono<Object> stream5 = Mono.justOrEmpty(null);
        Mono<Object> stream6 = Mono.justOrEmpty(Optional.empty());

        Mono<String> stream7 = Mono.fromCallable(() -> httpRequest());
        Mono<String> stream8 = Mono.fromCallable(this::httpRequest);
        Mono<String> error = Mono.error(new RuntimeException("Unknown id"));

        StepVerifier.create(stream8)
                .expectErrorMessage("IO error")
                .verify();

        Mono<Void> noData = Mono.fromRunnable(this::doLongAction);

        StepVerifier.create(noData)
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void simpleSubscribe() {

    }

    /**
     * 此代码可将对 sessionId 的验证推迟至发生实际订阅之后.
     */
    public Mono<User> requestUserDate(String userId) {
        return Mono.defer(() ->
                isValid(userId)
                        ? Mono.fromCallable(() -> requestUser(userId))
                        : Mono.error(new IllegalArgumentException("Invalid user id")));
    }

    /**
     * 以下代码在调用 requestUserData2(. . . ) 方法时就执行验证
     * 而这可能发生在实际订阅之前(也就是说， 可能根本不会发生订阅)
     */
    public Mono<User> requestUserData2(String userId) {
        return isValid(userId)
                ? Mono.fromCallable(() -> requestUser(userId))
                : Mono.error(new IllegalArgumentException("Invalid user id"));
    }

    @Test
    public void shouldCreateDefer() {
        Mono<User> userMono = requestUserDate(null);
        StepVerifier.create(userMono)
                .expectNextCount(0)
                .expectErrorMessage("Invalid user id")
                .verify();
    }

    private User requestUser(String id) {
        return new User();
    }

    private boolean isValid(String userId) {
        return userId != null;
    }

    private String httpRequest() {
        log.info("Making HTTP request");
        throw new RuntimeException("IO error");
    }

    private void doLongAction() {
        log.info("Long action");
    }

}
