package com.jcohy.sample.reactive.chatpet_07.rxjdbc.wallet;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:12:01
 * @since 2022.0.1
 */
public interface WalletService {

    Mono<Void> initializeDatabase();

    Flux<String> generateClients(Integer number, Integer defaultBalance);

    Mono<TxResult> transferMoney(Mono<String> fromOwner, Mono<String> toOwner, Mono<Integer> amount);

    Mono<Statistics> reportAllWallets();

    Mono<Void> removeAllClients();

    enum TxResult {
        SUCCESS,
        NOT_ENOUGH_FUNDS,
        TX_CONFLICT
    }

    class Statistics {
        private long totalAccounts;
        private long totalBalance;
        private long totalDeposits;
        private long totalWithdraws;

        public Statistics() {
        }

        public Statistics(long totalAccounts, long totalBalance, long totalDeposits, long totalWithdraws) {
            this.totalAccounts = totalAccounts;
            this.totalBalance = totalBalance;
            this.totalDeposits = totalDeposits;
            this.totalWithdraws = totalWithdraws;
        }

        public Statistics withWallet(WalletData w) {
            return new Statistics(
                    this.totalAccounts + 1,
                    this.totalBalance + w.balance(),
                    this.totalDeposits + w.deposits(),
                    this.totalWithdraws + w.withdraws());
        }

        public long getTotalAccounts() {
            return this.totalAccounts;
        }

        public Statistics setTotalAccounts(long totalAccounts) {
            this.totalAccounts = totalAccounts;
            return this;
        }

        public long getTotalBalance() {
            return this.totalBalance;
        }

        public Statistics setTotalBalance(long totalBalance) {
            this.totalBalance = totalBalance;
            return this;
        }

        public long getTotalDeposits() {
            return this.totalDeposits;
        }

        public Statistics setTotalDeposits(long totalDeposits) {
            this.totalDeposits = totalDeposits;
            return this;
        }

        public long getTotalWithdraws() {
            return this.totalWithdraws;
        }

        public Statistics setTotalWithdraws(long totalWithdraws) {
            this.totalWithdraws = totalWithdraws;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Statistics that = (Statistics) o;
            return getTotalAccounts() == that.getTotalAccounts() &&
                    getTotalBalance() == that.getTotalBalance() &&
                    getTotalDeposits() == that.getTotalDeposits() &&
                    getTotalWithdraws() == that.getTotalWithdraws();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTotalAccounts(), getTotalBalance(), getTotalDeposits(), getTotalWithdraws());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Statistics{");
            sb.append("totalAccounts=").append(this.totalAccounts);
            sb.append(", totalBalance=").append(this.totalBalance);
            sb.append(", totalDeposits=").append(this.totalDeposits);
            sb.append(", totalWithdraws=").append(this.totalWithdraws);
            sb.append('}');
            return sb.toString();
        }
    }
}
