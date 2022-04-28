package com.jcohy.sample.reactive.chatpet_07.rxjdbc.wallet;

import java.util.Arrays;
import java.util.Comparator;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.tuple.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

/**
 * 描述: .
 *  Unfortunately it ides not work.
 *  Transactions are too hard for rxjava2-jdbc :-(
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:12:02
 * @since 2022.0.1
 */
public class WalletServiceImpl implements WalletService{

    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    public static final String SELECT_BY_OWNER =
            "select id, owner, balance, deposits, withdraws from wallet where owner=:owner";
    public static final String UPDATE_WALLET =
            "update wallet set balance=:balance, withdraws=:withdraws, deposits=:deposits where owner=:owner";

    private Database database;

    public WalletServiceImpl(String dbUri, int poolSize) {
        this.database = Database.from(dbUri, poolSize);
    }

    @Override
    public Mono<Void> initializeDatabase() {
        return Mono.fromCallable(() -> {
            String schema =
                    Resources.toString(Resources.getResource("schema.sql"), Charsets.UTF_8);
            return database.update(schema)
                    .counts()
                    .blockingLast();
        }).then();
    }

    @Override
    public Flux<String> generateClients(Integer number, Integer defaultBalance) {
        return Flux.from(
                database.update("insert into wallet values (?, ?, ?, ?, ?)")
                        .parameterListStream(
                                Flowable
                                        .range(0, number)
                                        .map(id -> Arrays.asList(id, ownerName(id), defaultBalance, 0, 0))
                        ).complete()
                        .andThen(Flowable
                                .range(0, number)
                                .map(this::ownerName)));
    }

    @Override
    public Mono<TxResult> transferMoney(
            Mono<String> fromOwnerMono,
            Mono<String> toOwnerMono,
            Mono<Integer> amountMono
    ) {
        Flowable<TxResult> rxJavaResult = Single.zip(
                Single.fromPublisher(fromOwnerMono),
                Single.fromPublisher(toOwnerMono),
                Single.fromPublisher(amountMono),
                Tuple3::new
        ).flatMapPublisher(tuple -> {
            String from = tuple._1();
            String to = tuple._2();
            Integer amount = tuple._3();

            return database
                    .select(SELECT_BY_OWNER)
                    .parameter("owner", from)
                    .transacted()
                    .autoMap(WalletData.class)
                    .flatMap(txFrom ->
                            txFrom
                                    .select(SELECT_BY_OWNER)
                                    .parameter("owner", to)
                                    .autoMap(WalletData.class)
                                    .flatMap(txTo -> {
                                        WalletData fromWallet = txFrom.value();
                                        WalletData toWallet = txTo.value();
                                        if (fromWallet.balance() > amount) {
                                            return txTo
                                                    .update(UPDATE_WALLET)
                                                    .parameter("owner", from)
                                                    .parameter("balance", fromWallet.balance() - amount)
                                                    .parameter("deposits", fromWallet.deposits())
                                                    .parameter("withdraws", fromWallet.withdraws() + 1)
                                                    .counts()
                                                    .flatMap(updateTx ->
                                                            updateTx
                                                                    .update(UPDATE_WALLET)
                                                                    .parameter("owner", to)
                                                                    .parameter("balance", toWallet.balance() + amount)
                                                                    .parameter("deposits", toWallet.deposits() + 1)
                                                                    .parameter("withdraws", toWallet.withdraws())
                                                                    .valuesOnly()
                                                                    .counts()
                                                                    .flatMap(tx -> {
                                                                        log.warn("Transferring from: {}, to {}, amount: {}",
                                                                                fromWallet.owner(),
                                                                                toWallet.owner(),
                                                                                amount);
                                                                        return Flowable.just(TxResult.SUCCESS);
                                                                    })
                                                    ).onErrorReturn(e -> TxResult.TX_CONFLICT);
                                        } else {
                                            return Flowable.just(TxResult.NOT_ENOUGH_FUNDS);
                                        }
                                    })
                    );
        });
        return Mono.from(rxJavaResult);
    }

    @Override
    public Mono<Statistics> reportAllWallets() {
        return Flux.from(database.select(WalletData.class).get())
                .sort(Comparator.comparing(WalletData::owner))
                .doOnNext(w ->
                        log.info(format("%10s: %7d$ (d: %5s | w: %5s)",
                                w.owner(), w.balance(), w.deposits(), w.withdraws())))
                .reduce(new Statistics(), Statistics::withWallet);
    }

    @Override
    public Mono<Void> removeAllClients() {
        return Mono
                .from(database.update("delete from wallet").counts())
                .doOnSuccess(count -> log.info("Deleted {} records", count))
                .then();
    }

    private String ownerName(Integer id) {
        return format("client-%05d", id);
    }
}
