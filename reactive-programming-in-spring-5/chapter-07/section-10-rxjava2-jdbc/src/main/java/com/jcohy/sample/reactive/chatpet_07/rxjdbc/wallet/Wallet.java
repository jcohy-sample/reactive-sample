package com.jcohy.sample.reactive.chatpet_07.rxjdbc.wallet;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:12:00
 * @since 2022.0.1
 */
public class Wallet {
    private Integer id;
    private String owner;
    private int balance;

    // Some statistics
    private int depositOperations;
    private int withdrawOperations;

    public Wallet() {
    }

    public Wallet(Integer id, String owner, int balance, int depositOperations, int withdrawOperations) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
        this.depositOperations = depositOperations;
        this.withdrawOperations = withdrawOperations;
    }

    public Integer getId() {
        return this.id;
    }

    public Wallet setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getOwner() {
        return this.owner;
    }

    public Wallet setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public int getBalance() {
        return this.balance;
    }

    public Wallet setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public int getDepositOperations() {
        return this.depositOperations;
    }

    public Wallet setDepositOperations(int depositOperations) {
        this.depositOperations = depositOperations;
        return this;
    }

    public int getWithdrawOperations() {
        return this.withdrawOperations;
    }

    public Wallet setWithdrawOperations(int withdrawOperations) {
        this.withdrawOperations = withdrawOperations;
        return this;
    }

    public boolean hasEnoughFunds(int amount) {
        return balance >= amount;
    }

    public void withdraw(int amount) {
        if (!hasEnoughFunds(amount)) {
            throw new RuntimeException("Not enough funds!");
        }
        this.balance = this.balance - amount;
        this.withdrawOperations += 1;
    }

    public void deposit(int amount) {
        this.balance = this.balance + amount;
        this.depositOperations += 1;
    }

    public static Wallet wallet(Integer id, String owner, int balance) {
        return new Wallet(id, owner, balance, 0, 0);
    }
}
