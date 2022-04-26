package com.jcohy.sample.reactive.chapter_07.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:12:06
 * @since 2022.0.1
 */
public class Chapter7JdbcTest {

    private static final Logger log = LoggerFactory.getLogger(Chapter7JdbcTest.class);

    @Test
    public void jdbcTest() throws SQLException  {
        try(Connection conn = DriverManager.getConnection("jdbc:h2:mem:t1")) {
            conn.createStatement()
                    .executeUpdate("CREATE TABLE book (id INTEGER, title VARCHAR(255))");

            try (PreparedStatement insertBook = conn
                    .prepareStatement("INSERT INTO book values(?, ?)")) {

                insertBook.setInt(1, 1);
                insertBook.setString(2, "The Martian");
                insertBook.executeUpdate();

                insertBook.setInt(1, 2);
                insertBook.setString(2, "Blue Martian");
                insertBook.executeUpdate();

                log.info("Schema created, data inserted");
            }
            try(ResultSet rows = conn.createStatement()
                    .executeQuery("SELECT * FROM book")
            ) {
                log.info("Forward traversal");

                while (rows.next()) {
                    log.info("id: {}, title: {}",
                            rows.getInt(1), rows.getString("title"));
                }
            }

            try(ResultSet rows = conn
                    .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                    .executeQuery("SELECT * FROM book")
            ) {
                log.info("Backward traversal");

                rows.last();
                do {
                    log.info("[Reverse] id: {}, title: {}",
                            rows.getInt(1), rows.getString("title"));
                } while (rows.previous());
            }
        } finally {
            log.info("Connection closed");
        }
    }

}
