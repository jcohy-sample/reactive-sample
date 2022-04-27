package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:43
 * @since 2022.0.1
 */
public abstract class BaseWalletService implements WalletService{

    private static final Logger log = LoggerFactory.getLogger(BaseWalletService.class);

    protected final WalletRepository walletRepository;

    public BaseWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Flux<String> generateClient(Integer number, Integer defaultBalance) {
        return walletRepository.saveAll(
                Flux.range(1,number)
                        .map(id -> String.format("client-%05d", id))
                        .map(owner -> Wallet.wallet(owner,defaultBalance)))
                .map(Wallet::getOwner);
    }

    @Override
    public Mono<Statistics> reportAllWallets() {
        return walletRepository.findAll()
                .sort(Comparator.comparing(Wallet::getOwner))
                .doOnNext( wallet ->
                        log.info(format("%10s: %7d$ (d: %5s | w: %5s)",
                                wallet.getOwner(),
                                wallet.getBalance(),
                                wallet.getDepositOperations(),
                                wallet.getWithdrawOperations())))
                .reduce(new Statistics(),Statistics::withWallet);
    }

    @Override
    public Mono<Void> removeAllClients() {
        return walletRepository.deleteAll();
    }
}
