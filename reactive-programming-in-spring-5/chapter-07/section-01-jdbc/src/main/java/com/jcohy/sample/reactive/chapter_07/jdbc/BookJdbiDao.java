package com.jcohy.sample.reactive.chapter_07.jdbc;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * 描述: DAO implemented with the Jdbi library.
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:14:31
 * @since 2022.0.1
 */
public interface BookJdbiDao {

    @SqlQuery("SELECT * FROM book ORDER BY id DESC")
    @RegisterBeanMapper(Book.class)
    List<Book> listBooks();

    @SqlUpdate("INSERT INTO book(id, title) VALUES (:id, :title)")
    void insertBook(@BindBean Book book);
}
