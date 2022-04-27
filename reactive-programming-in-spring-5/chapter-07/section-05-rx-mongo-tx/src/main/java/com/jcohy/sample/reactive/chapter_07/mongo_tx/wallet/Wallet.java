package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:36
 * @since 2022.0.1
 */
@Document(collection = "wallet")
public class Wallet {

    @Id
    private ObjectId id;
    private String owner;
    private int balance;

    // Some statistics
    private int depositOperations;
    private int withdrawOperations;

    public Wallet() {
    }

    public Wallet(ObjectId id, String owner, int balance, int depositOperations, int withdrawOperations) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
        this.depositOperations = depositOperations;
        this.withdrawOperations = withdrawOperations;
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

    public static Wallet wallet(String owner, int balance) {
        return new Wallet(new ObjectId(), owner, balance, 0, 0);
    }

    public ObjectId getId() {
        return this.id;
    }

    public Wallet setId(ObjectId id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return getBalance() == wallet.getBalance() &&
                getDepositOperations() == wallet.getDepositOperations() &&
                getWithdrawOperations() == wallet.getWithdrawOperations() &&
                Objects.equals(getId(), wallet.getId()) &&
                Objects.equals(getOwner(), wallet.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getBalance(), getDepositOperations(), getWithdrawOperations());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Wallet{");
        sb.append("id=").append(this.id);
        sb.append(", owner='").append(this.owner).append('\'');
        sb.append(", balance=").append(this.balance);
        sb.append(", depositOperations=").append(this.depositOperations);
        sb.append(", withdrawOperations=").append(this.withdrawOperations);
        sb.append('}');
        return sb.toString();
    }
}
