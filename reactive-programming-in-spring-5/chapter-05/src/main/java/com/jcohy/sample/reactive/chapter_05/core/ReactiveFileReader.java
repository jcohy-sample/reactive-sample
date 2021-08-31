package com.jcohy.sample.reactive.chapter_05.core;

import reactor.core.publisher.Flux;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:10:37
 * @since 1.0.0
 */
public class ReactiveFileReader {

    public Flux<DataBuffer> backpressuredShakespeare() {
        return DataBufferUtils
                .read(
                        new DefaultResourceLoader().getResource("hamlet.txt"),
                        new DefaultDataBufferFactory(),
                        1024
                ).log();
    }
}
