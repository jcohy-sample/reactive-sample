package com.jcohy.sample.reactive.chapter_03.async_vs_reactive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import reactor.test.scheduler.VirtualTimeScheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static reactor.core.publisher.Flux.range;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:15:49
 * @since 1.0.0
 */
public class CompletionPublisherTest {

    @Test
    public void testCompletablePublisherReturnsResultCorrectly()
            throws ExecutionException, InterruptedException {
        CompletableFuture<ArrayList<Integer>> completableFuture = new PublisherAsCompletableFuture<>(
                range(0, 10),
                ArrayList::new,
                (i, list) -> {
                    list.add(i);
                    return list;
                }
        );

        ArrayList<Integer> integers = completableFuture.get();

        assertEquals(integers, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    @Test
    public void testCompletablePublisherCancelledCorrectly() {
        VirtualTimeScheduler timeScheduler = VirtualTimeScheduler.getOrSet();
        PublisherAsCompletableFuture<Integer, ArrayList<Integer>> completableFuture =
                new PublisherAsCompletableFuture<>(
                        range(0, 10).delayElements(Duration.ofMillis(10)),
                        ArrayList::new,
                        (i, list) -> {
                            list.add(i);
                            return list;
                        }
                );

        timeScheduler.advanceTimeBy(Duration.ofMillis(50));

        completableFuture.cancel(true);

        assertTrue(completableFuture.isCancelled());
        assertTrue(completableFuture.isCompletedExceptionally());

        assertEquals(
                completableFuture.innerSubscriber.state,
                Arrays.asList(0, 1, 2, 3, 4)
        );
    }
}
