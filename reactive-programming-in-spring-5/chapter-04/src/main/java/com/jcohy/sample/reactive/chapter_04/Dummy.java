package com.jcohy.sample.reactive.chapter_04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:17:08
 * @since 1.0.0
 */
public class Dummy {
    private static final Logger log = LoggerFactory.getLogger(Dummy.class);
    public static void main(String[] args) {
        Flux.just("A","B","C")
                .subscribe(
                        (data) -> log.info("onNext: {}",data));
    }
}
