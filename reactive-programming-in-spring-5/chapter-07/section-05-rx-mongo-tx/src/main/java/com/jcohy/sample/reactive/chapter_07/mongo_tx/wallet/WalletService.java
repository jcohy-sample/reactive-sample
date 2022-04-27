package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.util.Objects;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:38
 * @since 2022.0.1
 */
public interface WalletService {

    Flux<String> generateClient(Integer number, Integer defaultBalance);

    /**
     * 将 amount 金额从 fromOwner 转到 toOwner
     * @param fromOwner fromOwner
     * @param toOwner toOwner
     * @param amount amount
     */
    Mono<TxResult> transferMoney(Mono<String> fromOwner, Mono<String> toOwner, Mono<Integer> amount);

    /**
     * 汇总所有已注册的钱包的数据并检索总余额
     */
    Mono<Statistics> reportAllWallets();

    Mono<Void> removeAllClients();

    /**
     * 转账的三种结果
     */
    enum TxResult {
        SUCCESS,
        NOT_ENOUGH_FUNDS,
        TX_CONFLICT
    }
}
