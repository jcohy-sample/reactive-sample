package com.jcohy.sample.reactive.chapter_05.reactive_app;

import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:11:02
 * @since 1.0.0
 */
public interface SensorReadingRepository extends ReactiveMongoRepository<SensorsReadings, ObjectId> {

    @Tailable
    Flux<SensorsReadings> findBy();
}
