package com.jcohy.sample.reactive.chapter_07.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:11:36
 * @since 2022.0.1
 */
@Repository
public class BookJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Book findById(int id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM book WHERE id = ?" ,
                new Object[] {id} ,
                new BeanPropertyRowMapper<>(Book.class));
    }

    public List<Book> findByTitle(String phrase) {
        // 在执行 sql 查询时将值绑定到 sql 里的命名参数，而不是使用 ? 占位符
        NamedParameterJdbcTemplate named =
                new NamedParameterJdbcTemplate(jdbcTemplate);

        // 此将参数值 Map 传递给 NamedParameterJdbcTemplate 类的方法。
        SqlParameterSource nameParameters = new MapSqlParameterSource("search_phrase",phrase);

        String sql = "SELECT * FROM book WHERE title = :search_phrase" ;
        return named.query(sql,nameParameters,new BeanPropertyRowMapper<>(Book.class));
    }

    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM book" ,new BookMapper());
    }

    static class BookMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(rs.getInt("id"),rs.getString("title"));
        }
    }
}
