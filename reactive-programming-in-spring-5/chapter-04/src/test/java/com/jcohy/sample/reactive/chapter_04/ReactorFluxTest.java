package com.jcohy.sample.reactive.chapter_04;

import java.time.Duration;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:17:09
 * @since 1.0.0
 */
public class ReactorFluxTest {
    private static final Logger log = LoggerFactory.getLogger(ReactorFluxTest.class);
    private final Random random = new Random();


    @Test
    @Disabled
    public void endlessStream() {
        Flux.interval(Duration.ofMillis(1))
                .collectList()
                .block();
    }

    @Test
    @Disabled
    public void endlessStream2() {
        Flux.range(1,5)
                .repeat()
                .doOnNext(e -> log.info("E: {}",e))
                .take(100)
                .blockLast();
    }

    /**
     * 尝试收集无限流发出的所有元素可能导致 OutOfMemoryException
     *
     * range 操作符创建从 1 到 100 的整数序列
     * repeat 操作符在源流完成之后一次又一次地订阅源响应式流。
     * collectList 尝试将所有生成的元素收集到一个列表中。
     * block 操作符会触发实际订阅并阻塞正在运行的线程，直到最终结果到达，而在当前场
     * 景下不会发生这种情况，因为响应式流是无限的。
     */
    @Test
    @Disabled
    public void endlessStreamAndCauseAnError() {
        Flux.range(1, 100)
                .repeat()
                .collectList()
                .block();
    }

    /**
     * 创建 Flux 的方法
     */
    @Test
    public void createFlux() {
        Flux<String> stream1 = Flux.just("Hello", "world");
        Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
        Flux<Integer> stream3 = Flux.range(1, 500);

        Flux<String> emptyStream = Flux.empty();
        Flux<String> streamWithError = Flux.error(new RuntimeException("Hi!"));

        Flux<String> empty = Flux.empty();
    }


    /**
     * 们创建一个简单的响应式流并订阅它:
     */
    @Test
    public void simpleSubscribe(){
        Flux.just("A","B","C")
                .subscribe(
                        (data) -> log.info("onNext: {}",data),
                        errorIgnored -> {},
                        () -> log.info("onComplete"));
    }

    /**
     * 创建一个 range 响应式流，并手动订阅
     * 首先请求 4 个数据，然后立即取消订阅
     */
    @Test
    public void simpleRangeSubscribe(){
        Flux.range(1,100)
                .subscribe(
                        (data) -> log.info("onNext: {}",data),
                        errorIgnored -> {},
                        () -> log.info("onComplete"),
                        subscription -> {
                            subscription.request(4);
                            subscription.cancel();
                        });
    }
}
