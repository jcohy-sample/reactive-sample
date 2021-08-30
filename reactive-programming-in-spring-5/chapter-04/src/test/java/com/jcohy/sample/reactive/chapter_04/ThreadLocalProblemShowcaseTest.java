package com.jcohy.sample.reactive.chapter_04;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/30:18:19
 * @since 1.0.0
 */
public class ThreadLocalProblemShowcaseTest {

    @Test
    public void shouldFailDueToDifferentThread() {
        ThreadLocal<Map<Object,Object>> threadLocal = new ThreadLocal<>();
        threadLocal.set(new HashMap<>());

        Flux.range(0,10)
                .doOnNext( k -> threadLocal.get().put(k,new Random(k).nextGaussian()))
                .publishOn(Schedulers.parallel())
                .map( k -> threadLocal.get().get(k))
                .blockLast();
    }
}
