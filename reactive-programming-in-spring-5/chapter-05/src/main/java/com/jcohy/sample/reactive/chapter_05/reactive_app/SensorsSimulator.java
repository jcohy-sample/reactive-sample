package com.jcohy.sample.reactive.chapter_05.reactive_app;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:11:08
 * @since 1.0.0
 */
@Service
public class SensorsSimulator {

    public static final int MAX_READINGS_DELAY = 5000;

    public static final int COLLECTION_MAX_SIZE = 10_000;

    private static final Logger log = LoggerFactory.getLogger(SensorsSimulator.class);

    private final SensorReadingRepository sensorReadingRepository;

    private final ReactiveMongoOperations mongoOperations;

    private final Random random = new Random();

    private Disposable sensorSimulationSubscription;

    public SensorsSimulator(SensorReadingRepository sensorReadingRepository,
            ReactiveMongoOperations mongoOperations) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.mongoOperations = mongoOperations;
    }

    @PostConstruct
    public void init() {
        initializeDb();
        sensorSimulationSubscription = simulateReadings()
                .doOnSubscribe(s -> log.info("Starting IoT sensor simulation"))
                .doOnTerminate(() -> log.info("IoT sensor simulation stopped"))
                .subscribe();
    }

    @PreDestroy
    public void cleanUp() {
        sensorSimulationSubscription.dispose();
    }

    private void initializeDb() {
        CollectionOptions collectionOptions = CollectionOptions.empty()
                .capped()
                .size(COLLECTION_MAX_SIZE);

        mongoOperations.createCollection(
                SensorsReadings.COLLECTION_NAME,
                collectionOptions
        ).block();
    }

    private Mono<Void> simulateReadings() {
        return Flux.range(0, 200)
                .repeat()
                .concatMap(i -> generateReading(i)
                        .delayElement(randomDelay(MAX_READINGS_DELAY))
                        .flatMap(sensorReadingRepository::save)
                        .doOnNext(m -> log.info("SensorsReadings saved: {}", m)))
                .then();
    }

    private Mono<SensorsReadings> generateReading(Integer i) {
        return Mono.fromCallable(() ->
                new SensorsReadings(new ObjectId(),
                        LocalDateTime.now(),
                        (i % 15) + random.nextDouble() * 15,
                        (i % 45) + random.nextDouble() * 45,
                        (i % 10) * 0.1 + random.nextDouble() * 0.4
                )
        );
    }

    private Duration randomDelay(int maxMillis) {
        return Duration.ofMillis(random.nextInt(maxMillis));
    }
}
