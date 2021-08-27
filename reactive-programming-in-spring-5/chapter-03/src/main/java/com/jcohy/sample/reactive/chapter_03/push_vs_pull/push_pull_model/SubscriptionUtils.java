package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_pull_model;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:16:20
 * @since 1.0.0
 */
public class SubscriptionUtils {
    private SubscriptionUtils() {
    }


    public static long addCap(long current, long requested) {
        long cap = current + requested;

        if (cap < 0L) {
            cap = Long.MAX_VALUE;
        }

        return cap;
    }


    @SuppressWarnings("unchecked")
    public static long request(long n, Object instance, AtomicLongFieldUpdater updater) {
        for (;;) {
            long currentDemand = updater.get(instance);

            if (currentDemand == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }

            long adjustedDemand = addCap(currentDemand, n);

            if (updater.compareAndSet(instance, currentDemand, adjustedDemand)) {
                return currentDemand;
            }
        }
    }
}
