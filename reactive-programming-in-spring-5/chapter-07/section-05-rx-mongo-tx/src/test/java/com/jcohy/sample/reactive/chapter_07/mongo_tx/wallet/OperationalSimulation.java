package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:12:06
 * @since 2022.0.1
 */
public class OperationalSimulation {

    private final WalletService walletService;

    private final List<String> clients;

    private final int defaultBalance;

    private final int iterations;

    private final Scheduler simulationScheduler;

    private final Random rnd = new Random();

    public static OperationalSimulation.OperationalSimulationBuilder builder() {
        return new OperationalSimulation.OperationalSimulationBuilder();
    }

    public OperationalSimulation(WalletService walletService,
            List<String> clients,
            int defaultBalance,
            int iterations,
            Scheduler simulationScheduler) {
        this.walletService = walletService;
        this.clients = clients;
        this.defaultBalance = defaultBalance;
        this.iterations = iterations;
        this.simulationScheduler = simulationScheduler;
    }

    public Mono<OperationStats> runSimulation() {
        return Flux.range(0,iterations)
                .flatMap( i -> Mono
                        .delay(Duration.ofMillis(rnd.nextInt(10)))
                        .publishOn(simulationScheduler)
                        .flatMap(_i -> {
                            String fromOwner = randomOwner();
                            String toOwner = randomOwnerExcept(fromOwner);
                            int amount = randomTransferAmount();
                            return walletService.transferMoney(
                                    Mono.just(fromOwner),
                                    Mono.just(toOwner),
                                    Mono.just(amount));
                        }))
                .reduce(OperationStats.start(),OperationStats::countTxResult);
    }

    /**
     * 随机生成交易金额
     */
    private int randomTransferAmount() {
        return rnd.nextInt(defaultBalance);
    }

    /**
     * 随机生成汇款人
     */
    private String randomOwner() {
        int from = rnd.nextInt(clients.size());
        return clients.get(from);
    }

    /**
     * 随机生成收款人，排除汇款人
     */
    private String randomOwnerExcept(String fromOwner) {
        String toOwner;
        do {
            int to = rnd.nextInt(clients.size());
            toOwner = clients.get(to);
        } while (fromOwner.equals(toOwner));
        return toOwner;
    }

    public static class OperationalSimulationBuilder {

        private WalletService walletService;

        private List<String> clients;

        private int defaultBalance;

        private int iterations;

        private Scheduler simulationScheduler;

        public OperationalSimulationBuilder walletService(WalletService walletService) {
            this.walletService = walletService;
            return this;
        }

        public OperationalSimulationBuilder clients(List<String> clients) {
            this.clients = clients;
            return this;
        }

        public OperationalSimulationBuilder defaultBalance(int defaultBalance) {
            this.defaultBalance = defaultBalance;
            return this;
        }

        public OperationalSimulationBuilder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public OperationalSimulationBuilder simulationScheduler(Scheduler simulationScheduler) {
            this.simulationScheduler = simulationScheduler;
            return this;
        }

        public OperationalSimulation build() {
            return new OperationalSimulation(this.walletService,
                    this.clients,this.defaultBalance,
                    this.iterations,this.simulationScheduler);
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("OperationalSimulationBuilder{");
            sb.append("walletService=").append(this.walletService);
            sb.append(", clients=").append(this.clients);
            sb.append(", defaultBalance=").append(this.defaultBalance);
            sb.append(", iterations=").append(this.iterations);
            sb.append(", simulationScheduler=").append(this.simulationScheduler);
            sb.append('}');
            return sb.toString();
        }
    }
}
