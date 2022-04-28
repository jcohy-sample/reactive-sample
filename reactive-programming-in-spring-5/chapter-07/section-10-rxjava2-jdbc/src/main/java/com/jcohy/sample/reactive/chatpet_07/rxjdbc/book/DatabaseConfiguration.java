package com.jcohy.sample.reactive.chatpet_07.rxjdbc.book;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.davidmoten.rx.jdbc.Database;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:11:58
 * @since 2022.0.1
 */
@Configuration
public class DatabaseConfiguration {
    @Bean
    public Database database(
            @Value("${spring.datasource.url}") String uri,
            @Value("${rxjava2jdbc.pool.size}") Integer poolSize
    ) {
        Database db = Database
                .from(uri, poolSize);

        initializeDatabase(db)
                .block();

        return db;
    }

    private Mono<Void> initializeDatabase(Database database) {
        return Mono.fromCallable(() -> {
            String schema =
                    Resources.toString(Resources.getResource("schema.sql"), Charsets.UTF_8);

            String data =
                    Resources.toString(Resources.getResource("data.sql"), Charsets.UTF_8);

            return database.update(schema)
                    .counts()
                    .ignoreElements()
                    .andThen(database
                            .update(data)
                            .counts())
                    .blockingLast();
        }).then();
    }
}
