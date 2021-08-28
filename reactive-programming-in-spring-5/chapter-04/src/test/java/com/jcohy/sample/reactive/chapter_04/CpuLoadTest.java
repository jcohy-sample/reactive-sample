package com.jcohy.sample.reactive.chapter_04;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static java.lang.String.format;
import static java.time.Instant.now;
/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:17:15
 * @since 1.0.0
 */
public class CpuLoadTest {

    @Test
    public void loadCpuDate() throws InterruptedException {
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

        Flux<Double> loadStream = Flux.interval(Duration.ofMillis(1000

                ))
                .map(ignore -> osMXBean.getSystemLoadAverage());

        System.out.println("Application pid: " + applicationPid());

        loadStream.filter(load -> !load.isNaN())
                .subscribe(load ->
                        System.out.println(format("[%s] System CPU load: %2.2f %%", now(), load * 100.0)));
        Thread.sleep(100_000);
    }

    private int applicationPid() {
        String appName = ManagementFactory.getRuntimeMXBean().getName();
        String pidInString = appName.split("@")[0];
        return Integer.parseInt(pidInString);
    }
}
