package com.jcohy.sample.chapter_06.r2dbc;

import java.time.Duration;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:11:16
 * @since 2022.0.1
 */
@Configuration
public class PostgresConfiguration {
    private static final Logger log = LoggerFactory.getLogger(PostgresConfiguration.class);

    private PostgreSQLContainer postgres;

    @Bean
    public DatabaseLocation databaseLocation() throws InterruptedException {
        postgres = new PostgreSQLContainer<>();
        postgres.waitingFor(Wait.forListeningPort()
                .withStartupTimeout(Duration.ofMillis(10)));
        postgres.withLogConsumer((Consumer<OutputFrame>) outputFrame -> log.info("[PgSQL]: {}", outputFrame.getUtf8String().trim()));
        postgres.start();

        // TODO: Use some better wait strategy
        Thread.sleep(10_000);

        DatabaseLocation dbLocation = new DatabaseLocation(
                "localhost",
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName(),
                postgres.getUsername(),
                postgres.getPassword());

        log.info("Database location: {}", dbLocation);

        return dbLocation;
    }

    @PreDestroy
    public void cleanUp() {
        postgres.stop();
    }
}
