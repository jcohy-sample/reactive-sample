package com.jcohy.sample.reactive.chapter_o7.rxsync;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:18:48
 * @since 2022.0.1
 */
@Configuration
public class RxPersistenceConfiguration {
    @Bean
    public Scheduler jpaScheduler() {
        return Schedulers.newParallel("JPA", 10);
    }
}
