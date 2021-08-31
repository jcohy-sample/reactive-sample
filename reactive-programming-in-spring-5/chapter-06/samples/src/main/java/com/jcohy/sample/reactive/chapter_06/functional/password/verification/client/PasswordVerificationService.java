package com.jcohy.sample.reactive.chapter_06.functional.password.verification.client;

import reactor.core.publisher.Mono;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:12:58
 * @since 1.0.0
 */
public interface PasswordVerificationService {
    Mono<Void> check(String raw, String encoded);
}
