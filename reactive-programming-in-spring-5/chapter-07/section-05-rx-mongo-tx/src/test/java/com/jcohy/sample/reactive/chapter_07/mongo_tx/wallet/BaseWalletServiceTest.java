package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static java.time.Duration.between;
import static java.time.Instant.now;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:11:59
 * @since 2022.0.1
 */
@DataMongoTest
public class BaseWalletServiceTest {

    private static final Logger log = LoggerFactory.getLogger(BaseWalletServiceTest.class);

    Tuple2<Long,Long> simulateOperations(WalletService walletService) {
        int accounts = 500;
        int defaultBalance = 1000;
        int iterations = 10000;
        int parallelism = 200;

        // given
        // Clean up just in case
        walletService.removeAllClients()
                .block();

        List<String> clients = walletService.generateClient(accounts, defaultBalance)
                .doOnNext(name -> log.info("Created wallet for: {}", name))
                .collectList()
                .block();

        // when
        Scheduler scheduler = Schedulers.newParallel("MongoOperations", parallelism);

        Instant startTime = Instant.now();

        OperationalSimulation simulation = OperationalSimulation.builder()
                .walletService(walletService)
                .clients(clients)
                .defaultBalance(defaultBalance)
                .iterations(iterations)
                .simulationScheduler(scheduler)
                .build();

        OperationStats operations = simulation
                .runSimulation()
                .block();

        // then
        log.info("--- Results --------------------------------");
        Statistics statistics = walletService.reportAllWallets()
                .block();
        log.info("Expected/actual total balance: {}$ / {}$ | Took: {}",
                accounts * defaultBalance, statistics.getTotalBalance(), between(startTime, now()));
        log.info("{}", statistics);
        log.info("{}", operations);

        log.info("Cleaning up database");
        walletService.removeAllClients()
                .block();

        return Tuples.of((long) accounts * defaultBalance, statistics.getTotalBalance());
    }

}
