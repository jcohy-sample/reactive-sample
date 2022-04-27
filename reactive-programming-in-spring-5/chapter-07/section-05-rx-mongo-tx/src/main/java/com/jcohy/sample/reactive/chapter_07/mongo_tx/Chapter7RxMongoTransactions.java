package com.jcohy.sample.reactive.chapter_07.mongo_tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 描述: 将一笔金额从账户A 转到账户 B，我们应该执行以下操作.
 * <ol>
 *     <li>启动新事务</li>
 *     <li>加载账户 A 的钱包</li>
 *     <li>加载账户 B 的钱包</li>
 *     <li>检查账户 A 的钱包中是否有足够的资金</li>
 *     <li>提取转账金额并计算账户 A 的新余额</li>
 *     <li>存入转入金额并计算账户 B 的新余额</li>
 *     <li>保存账户 A 的钱包</li>
 *     <li>保存账户 B 的钱包</li>
 *     <li>提交事务</li>
 * </ol>
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:10:30
 * @since 2022.0.1
 */
@EnableMongoRepositories
@SpringBootApplication
public class Chapter7RxMongoTransactions implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Chapter7RxMongoTransactions.class);


    public static void main(String... args) {
        SpringApplication.run(Chapter7RxMongoTransactions.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("This application is verifiable through unit tests only!");
    }
}
