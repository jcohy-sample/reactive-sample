package com.jcohy.sample.reactive.chatpet_07.rxjdbc.book;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import com.jcohy.sample.reactive.chatpet_07.rxjdbc.Chapter7RxJava2JdbcApplication;
import com.jcohy.sample.reactive.chatpet_07.rxjdbc.wallet.WalletService;
import com.jcohy.sample.reactive.chatpet_07.rxjdbc.wallet.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static java.time.Duration.between;
import static java.time.Instant.now;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:12:24
 * @since 2022.0.1
 */
public class TransactionalWalletServiceTest {

    private static final Logger log = LoggerFactory.getLogger(TransactionalWalletServiceTest.class);

    private final Random rnd = new Random();

    @DisplayName("Reactive transactions for data transfer with rxJava2-jdbc")
    @ParameterizedTest
    @CsvSource({ "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4, 20"})
    public void testReactiveTransactionalApproach(String uri, Integer maxPoolSize) {
        WalletService walletService = new WalletServiceImpl(uri, maxPoolSize);

        // This test intentionally fails due to issues with transactions
        Assertions.assertThrows(Exception.class, () -> {
            simulateOperations(walletService);
        });
    }

    void simulateOperations(WalletService walletService) {
        int accounts = 1000;
        int defaultBalance = 1000;
        int iterations = 10;
        int parallelism = 2;

        walletService.initializeDatabase()
                .block();

        // given
        // Clean up just in case
        walletService.removeAllClients()
                .block();

        List<String> clients = walletService.generateClients(accounts, defaultBalance)
                .doOnNext(name -> log.info("Created wallet for: {}", name))
                .collectList()
                .block();

        // when
        Scheduler mongoScheduler = Schedulers
                .newParallel("MongoOperations", parallelism);

        Instant startTime = now();
        Operations operations = Flux.range(0, iterations)
                .flatMap(i -> Mono
                        .delay(Duration.ofMillis(rnd.nextInt(10)))
                        .publishOn(mongoScheduler)
                        .flatMap(_i -> {
                            int amount = rnd.nextInt(defaultBalance);
                            int from = rnd.nextInt(accounts);
                            int to;
                            do {
                                to = rnd.nextInt(accounts);
                            } while (to == from);

                            return walletService.transferMoney(
                                    Mono.just(clients.get(from)),
                                    Mono.just(clients.get(to)),
                                    Mono.just(amount));
                        }))
                .reduce(Operations.start(), Operations::outcome)
                .block();

        // then
        log.info("--- Results --------------------------------");
        WalletService.Statistics statistics = walletService.reportAllWallets()
                .block();
        log.info("Expected/actual total balance: {}$ / {}$ | Took: {}",
                accounts * defaultBalance, statistics.getTotalBalance(), between(startTime, now()));
        log.info("{}", statistics);
        log.info("{}", operations);

        log.info("Cleaning up database");
        walletService.removeAllClients()
                .block();
    }

    @ToString
    @RequiredArgsConstructor
    public static class Operations {
        private final int successful;
        private final int notEnoughFunds;
        private final int conflict;

        public Operations outcome(WalletService.TxResult result) {
            switch (result){
                case SUCCESS:
                    return new Operations(successful + 1, notEnoughFunds, conflict);
                case NOT_ENOUGH_FUNDS:
                    return new Operations(successful, notEnoughFunds + 1, conflict);
                case TX_CONFLICT:
                    return new Operations(successful, notEnoughFunds, conflict + 1);
                default:
                    throw new RuntimeException("Unexpected status:" + result);
            }
        }

        public static Operations start() {
            return new Operations(0, 0, 0);
        }
    }
}
