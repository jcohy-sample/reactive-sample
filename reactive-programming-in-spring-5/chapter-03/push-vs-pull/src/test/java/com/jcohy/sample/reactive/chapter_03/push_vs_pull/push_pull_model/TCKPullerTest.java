package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_pull_model;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:16:41
 * @since 1.0.0
 */
public class TCKPullerTest extends PublisherVerification<Item> {

    public TCKPullerTest() {
        super(new TestEnvironment(1000, 1000));
    }

    @Override
    public Publisher<Item> createPublisher(long elements) {
        return new Puller().list(elements > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) elements);
    }

    @Override
    public Publisher<Item> createFailedPublisher() {
        return null;
    }
}
