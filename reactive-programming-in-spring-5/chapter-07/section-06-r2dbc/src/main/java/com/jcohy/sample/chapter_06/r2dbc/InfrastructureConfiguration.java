//package com.jcohy.sample.chapter_06.r2dbc;
//
//import io.r2dbc.postgresql.PostgresqlConnectionFactory;
//import io.r2dbc.spi.ConnectionFactory;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.relational.core.mapping.RelationalMappingContext;
//
///**
// * 描述: .
// * <p>
// * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
// *
// * @author jiac
// * @version 2022.0.1 2022/4/28:11:20
// * @since 2022.0.1
// */
//@Configuration
//public class InfrastructureConfiguration {
//    //@Bean
//    BookRepository customerRepository2(PostgresqlConnectionFactory factory) {
//        TransactionalDatabaseClient txClient =
//                TransactionalDatabaseClient.builder()
//                        .connectionFactory(factory)
//                        .build();
//        RelationalMappingContext context = new RelationalMappingContext();
//        return new R2dbcRepositoryFactory(txClient, context)
//                .getRepository(BookRepository.class);
//    }
//
//    @Bean
//    BookRepository customerRepository(R2dbcRepositoryFactory factory) {
//        return factory.getRepository(BookRepository.class);
//    }
//
//    @Bean
//    R2dbcRepositoryFactory repositoryFactory(DatabaseClient client) {
//        RelationalMappingContext context = new RelationalMappingContext();
//        return new R2dbcRepositoryFactory(client, context);
//    }
//
//    @Bean
//    TransactionalDatabaseClient databaseClient(ConnectionFactory factory) {
//        return TransactionalDatabaseClient.builder()
//                .connectionFactory(factory)
//                .build();
//    }
//
//    @Bean
//    PostgresqlConnectionFactory connectionFactory(DatabaseLocation databaseLocation) {
//
//        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
//                .host(databaseLocation.getHost())
//                .port(databaseLocation.getPort())
//                .database(databaseLocation.getDatabase())
//                .username(databaseLocation.getUser())
//                .password(databaseLocation.getPassword())
//                .build();
//
//        return new PostgresqlConnectionFactory(config);
//    }
//}
