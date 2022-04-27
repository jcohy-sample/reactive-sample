package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import org.springframework.data.mongodb.core.ReactiveMongoContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static java.time.Instant.now;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:56
 * @since 2022.0.1
 */
@Service
public class TransactionalWalletService extends BaseWalletService {

    private static final Logger log = LoggerFactory.getLogger(TransactionalWalletService.class);

    /**
     * 因为 ReactiveMongoDB 连接器不支持存储库级别的事务，仅支持 MongoDB 模板级别的事务，所有，我们必须使用 ReactiveMongoTemplate
     */
    private final ReactiveMongoTemplate mongoTemplate;

    public TransactionalWalletService(ReactiveMongoTemplate mongoTemplate,WalletRepository walletRepository) {
        super(walletRepository);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<TxResult> transferMoney(Mono<String> fromOwner, Mono<String> toOwner, Mono<Integer> requestAmount) {
        // zip 订阅所有方法参数
        return Mono.zip(fromOwner,toOwner,requestAmount)
                .flatMap(TupleUtils.function((form,to,amount) -> {
                    Instant start = Instant.now();
                    // 实际的资金转账
                    return doTransferMoney(form,to,amount)
                            // 该方法可能返回 onError 信号，可以使用 retryBackoff 重试该操作，该方法需要知道重试次数（20），
                            // 初始重试延迟（1ms），最大重试延迟（50ms），抖动值（0.1）
                            .retryBackoff(20, Duration.ofMillis(1),Duration.ofMillis(50),0.1)
                            // 如果在所有重试后都无法完成任务，我们应该将 TX_CONFLICT 返回给客户端
                            .onErrorReturn(TxResult.TX_CONFLICT)
                            .doOnSuccess(result -> log.info("Transaction result: {}, took: {}",
                                    result, Duration.between(start, now())));
                }));
    }

    private Mono<TxResult> doTransferMoney(String from, String to, Integer amount) {
        // mongoTemplate.inTransaction() 定义了一个新事务的边界
        return mongoTemplate.inTransaction()
                // 返回 ReactiveMongoOperations 类的 session 实例，session 实例被绑定到 MongoDB 事务。
                .execute((session) ->
            session
                    // 先搜索汇款人的钱包
                    .findOne(queryForOwner(from),Wallet.class)
                    .flatMap(fromWallet -> session
                            // 搜索收款着的钱包
                            .findOne(queryForOwner(to),Wallet.class)
                            .flatMap(toWallet -> {
                                    // 在解析了两个钱包后，检查汇款人是否有足够的金额
                                    if(fromWallet.hasEnoughFunds(amount)) {
                                        // 从汇款人提取正确的金额
                                        fromWallet.withdraw(amount);
                                        // 收款者存入正确的金额，此时，更改尚未保存数据库
                                        toWallet.deposit(amount);
                                        // 保存汇款人更新后的钱包，再保存收款人更新后的钱包。
                                        return session.save(fromWallet)
                                                .then(session.save(toWallet))
                                                .then(ReactiveMongoContext.getSession())
                                                .doOnNext(tx -> log.info("Current session: {}", tx))
                                                .then(Mono.just(TxResult.SUCCESS));
                                    } else {
                                        return Mono.just(TxResult.NOT_ENOUGH_FUNDS);
                                    }
                                })))
                .onErrorResume(e -> Mono.error(new RuntimeException("Conflict")))
                .last();
    }

    private Query queryForOwner(String owner) {
        return Query.query(new Criteria("owner").is(owner));
    }
}
