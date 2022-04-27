package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;


import java.time.Duration;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import reactor.util.function.Tuple2;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 描述: 测试需要运行 Docker 引擎！
 *
 * 如果启动失败，请重新开始测试！.
 * <p>
 *
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:50
 * @since 2022.0.1
 */
public class NaiveWalletServiceTest extends BaseWalletServiceTest {

    private static final Logger log = LoggerFactory.getLogger(NaiveWalletServiceTest.class);

    private static GenericContainer mongo;

    @BeforeAll
    public static void init() throws InterruptedException {
        mongo = new FixedHostPortGenericContainer<>("mongo:4.0.1")
                .withFixedExposedPort(27017,27017)
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMillis(10)));

        mongo.start();

        log.info("Giving MongoDB some time for initialization...");
        Thread.sleep(5_000);
        log.info("MongoDB started");
    }

    @AfterAll
    public static void cleanUp() {
        mongo.stop();
    }

    @DisplayName("Naive approach for data transfer")
    @Test
    public void testNaiveApproach(@Autowired WalletRepository walletRepository) {
        WalletService walletService = new NaiveWalletService(walletRepository);
        Tuple2<Long, Long> expectedActual = simulateOperations(walletService);

        // Whe know that balance will differ with the current approach
        Assert.assertNotEquals(expectedActual.getT1(), expectedActual.getT2());
    }

}