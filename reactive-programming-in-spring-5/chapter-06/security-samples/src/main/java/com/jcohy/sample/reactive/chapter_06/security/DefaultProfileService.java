package com.jcohy.sample.reactive.chapter_06.security;

import reactor.core.publisher.Mono;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:12:33
 * @since 1.0.0
 */
public class DefaultProfileService implements ProfileService {
    @Override
    public Mono<Profile> getByUser(String name) {
        return Mono.empty();
    }
}
