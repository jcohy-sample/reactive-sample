package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import org.springframework.stereotype.Service;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:42
 * @since 2022.0.1
 */
@Service
public class NaiveWalletService extends BaseWalletService{

    private static final Logger log = LoggerFactory.getLogger(NaiveWalletService.class);

    public NaiveWalletService(WalletRepository walletRepository) {
        super(walletRepository);
    }

    @Override
    public Mono<TxResult> transferMoney(Mono<String> fromOwner, Mono<String> toOwner, Mono<Integer> amount) {
        return Mono.zip(walletRepository.findByOwner(fromOwner), walletRepository.findByOwner(toOwner),amount)
                .flatMap(TupleUtils.function((from,to,transferAmount) -> {
                    if(from.hasEnoughFunds(transferAmount)) {
                        from.withdraw(transferAmount);
                        to.deposit(transferAmount);
                        return Mono.zip(walletRepository.save(from),walletRepository.save(to))
                                .flatMap(_i -> {
                                    log.debug("Transferred from: {}, to: {}, amount: {}",
                                            from.getOwner(), to.getOwner(), transferAmount);
                                    return Mono.just(TxResult.SUCCESS);
                                });
                    } else {
                        log.debug("FAILED to transfer from: {}, to: {}, amount: {}. Insufficient founds!",
                                from.getOwner(), to.getOwner(), transferAmount);
                        return Mono.just(TxResult.NOT_ENOUGH_FUNDS);
                    }
                }));
    }
}
