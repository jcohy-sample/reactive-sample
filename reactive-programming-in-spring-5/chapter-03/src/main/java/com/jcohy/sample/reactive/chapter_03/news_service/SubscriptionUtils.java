package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:18:13
 * @since 1.0.0
 */
public class SubscriptionUtils {

    private SubscriptionUtils() {
    }

    /**
     *
     * @param current 当前大小
     * @param requested 请求大小
     * @return 总容量
     */
    public static long addCap(long current,long requested) {
        long cap = current + requested;

        if( cap <0L ){
            cap = Long.MAX_VALUE;
        }

        return cap;
    }

    @SuppressWarnings("unchecked")
    public static long request(long n, Object instant, AtomicLongFieldUpdater updater) {
        for(;;){
            long currentDemand = updater.get(instant);

            if (currentDemand == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }

            long adjustedDemand = addCap(currentDemand,n);

            if ( updater.compareAndSet(instant,currentDemand,adjustedDemand)) {
                return currentDemand;
            }
        }
    }
}
