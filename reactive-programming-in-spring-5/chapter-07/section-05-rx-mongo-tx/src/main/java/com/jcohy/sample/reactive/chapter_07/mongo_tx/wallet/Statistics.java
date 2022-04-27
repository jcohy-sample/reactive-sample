package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.util.Objects;

/**
 * 描述: 表示系统中所有钱包的聚合状态，这对完整性检查很有用.
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:41
 * @since 2022.0.1
 */
public class Statistics {

    private long totalAccounts;
    private long totalBalance;
    private long totalDeposits;
    private long totalWithdraws;

    public Statistics() {
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


    public Statistics(long totalAccounts, long totalBalance, long totalDeposits, long totalWithdraws) {
        this.totalAccounts = totalAccounts;
        this.totalBalance = totalBalance;
        this.totalDeposits = totalDeposits;
        this.totalWithdraws = totalWithdraws;
    }

    public Statistics withWallet(Wallet w) {
        return new Statistics(
                this.totalAccounts + 1,
                this.totalBalance + w.getBalance(),
                this.totalDeposits + w.getDepositOperations(),
                this.totalWithdraws + w.getWithdrawOperations());
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
