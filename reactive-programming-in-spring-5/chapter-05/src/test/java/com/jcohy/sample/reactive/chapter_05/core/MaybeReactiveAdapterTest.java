package com.jcohy.sample.reactive.chapter_05.core;


import io.reactivex.Maybe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.core.ReactiveAdapter;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:10:38
 * @since 1.0.0
 */
public class MaybeReactiveAdapterTest {

    final ReactiveAdapter maybeAdapter = new MaybeReactiveAdapter();

    @Test
    public void convertFromMaybeToPublisherTest() {
        Assertions.assertThat(maybeAdapter.toPublisher(Maybe.just(1)))
                .isInstanceOf(Publisher.class);
    }

    @Test
    public void convertFromPublisherToMaybeTest() {
        Assertions.assertThat(maybeAdapter.fromPublisher(Mono.just(1)))
                .isInstanceOf(Maybe.class);
    }
}