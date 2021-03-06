package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_model;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:16:39
 * @since 1.0.0
 */
public class PullerTest {

    @Test
    public void pullTest() throws InterruptedException {
        CountDownLatch l = new CountDownLatch(1);
        Puller puller = new Puller();

        puller.list(10)
                .collect(ArrayList::new, ArrayList::add)
                .subscribe(al -> {
                    MatcherAssert.assertThat(al, Matchers.allOf(
                            Matchers.hasSize(10),
                            Matchers.contains(
                                    Matchers.hasProperty("id", Matchers.equalTo("2")),
                                    Matchers.hasProperty("id", Matchers.equalTo("4")),
                                    Matchers.hasProperty("id", Matchers.equalTo("6")),
                                    Matchers.hasProperty("id", Matchers.equalTo("8")),
                                    Matchers.hasProperty("id", Matchers.equalTo("10")),
                                    Matchers.hasProperty("id", Matchers.equalTo("12")),
                                    Matchers.hasProperty("id", Matchers.equalTo("14")),
                                    Matchers.hasProperty("id", Matchers.equalTo("16")),
                                    Matchers.hasProperty("id", Matchers.equalTo("18")),
                                    Matchers.hasProperty("id", Matchers.equalTo("20"))
                            )
                    ));
                    l.countDown();
                });

        l.await();
    }
}