package com.jcohy.sample.reactive.chapter_04;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/30:18:22
 * @since 1.0.0
 */
public class ReactiveContextTest {

    @Test
    public void showcaseContext() {
        printCurrentContext("top")
                .subscriberContext(Context.of("top", "context"))
                .flatMap(__ -> printCurrentContext("middle"))
                .subscriberContext(Context.of("middle", "context"))
                .flatMap(__ -> printCurrentContext("bottom"))
                .subscriberContext(Context.of("bottom", "context"))
                .flatMap(__ -> printCurrentContext("initial"))
                .block();
    }

    void print(String id, Context context) {
        System.out.println(id + " {");
        System.out.print("  ");
        System.out.println(context);
        System.out.println("}");
        System.out.println();
    }

    Mono<Context> printCurrentContext(String id) {
        return Mono.subscriberContext()
                .doOnNext( context -> print(id,context));
    }
}
