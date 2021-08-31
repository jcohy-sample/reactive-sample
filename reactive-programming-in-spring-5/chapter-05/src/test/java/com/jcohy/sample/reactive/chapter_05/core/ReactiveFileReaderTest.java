package com.jcohy.sample.reactive.chapter_05.core;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:10:45
 * @since 1.0.0
 */
public class ReactiveFileReaderTest {

    final ReactiveFileReader reader = new ReactiveFileReader();

    @Test
    public void readShakespeareWithBackpressureTest() {
        StepVerifier.create(reader.backpressuredShakespeare(), 1)
                .assertNext(db -> assertThat(db.capacity()).isEqualTo(1024))
                .expectNoEvent(Duration.ofMillis(2000))
                .thenRequest(1)
                .assertNext(db -> assertThat(db.capacity()).isEqualTo(1024))
                .thenCancel()
                .verify();
    }
}