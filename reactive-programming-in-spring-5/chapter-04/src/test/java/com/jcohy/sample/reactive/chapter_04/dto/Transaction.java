package com.jcohy.sample.reactive.chapter_04.dto;

import java.time.Duration;
import java.util.Random;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p> 描述: 模拟响应式事务.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/30:15:39
 * @since 1.0.0
 */
public class Transaction {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);

    private static final Random random = new Random();

    private final int id;

    public Transaction(int id) {
        this.id = id;
        log.info("[T: {}] created", id);
    }

    public static Mono<Transaction> beginTransaction() {
        return Mono.defer(() ->
                Mono.just(new Transaction(random.nextInt(1000))));
    }

    public Flux<String> insertRows(Publisher<String> rows) {
        return Flux.from(rows)
                .delayElements(Duration.ofMillis(100))
                .flatMap(row -> {
                    if (random.nextInt(10) < 2) {
                        return Mono.error(new RuntimeException("Error on: " + row));
                    }
                    else {
                        return Mono.just(row);
                    }
                });
    }


    public Mono<Void> commit() {
        return Mono.defer(() -> {
            log.info("[T: {}] commit", id);
            if (random.nextBoolean()) {
                return Mono.empty();
            }
            else {
                return Mono.error(new RuntimeException("Conflict"));
            }
        });
    }

    public Mono<Void> rollback() {
        return Mono.defer(() -> {
            log.info("[T: {}] rollback", id);
            if (random.nextBoolean()) {
                return Mono.empty();
            }
            else {
                return Mono.error(new RuntimeException("Conn error"));
            }
        });
    }

}
