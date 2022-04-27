package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:37
 * @since 2022.0.1
 */
@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, ObjectId> {

    Mono<Wallet> findByOwner(Mono<String> owner);
}
