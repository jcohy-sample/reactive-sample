package com.jcohy.sample.reactive.chapter_02.rx_app;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import org.springframework.stereotype.Component;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:18:20
 * @since 1.0.0
 */
@Component
public class TemperatureSensor {
    private static final Logger log = LoggerFactory.getLogger(TemperatureSensor.class);
    private final Random rnd = new Random();

//    Observable.OnSubscribe<String>() {}
}
