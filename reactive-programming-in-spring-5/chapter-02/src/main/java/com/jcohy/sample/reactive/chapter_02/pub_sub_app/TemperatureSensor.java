package com.jcohy.sample.reactive.chapter_02.pub_sub_app;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <p> 描述: 模拟温度传感器.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:17:16
 * @since 1.0.0
 */
@Component
public class TemperatureSensor {

    private final ApplicationEventPublisher publisher;

    private final Random random = new Random();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public TemperatureSensor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void startProcessing() {
        this.executor.schedule(this::probe, 1, SECONDS);
    }


    private void probe() {
        double temperature = 16 + random.nextGaussian() * 10;
        publisher.publishEvent(new Temperature(temperature));

        // schedule the next read after some random delay (0-5 seconds)
        executor.schedule(this::probe, random.nextInt(5000), MILLISECONDS);
    }
}
