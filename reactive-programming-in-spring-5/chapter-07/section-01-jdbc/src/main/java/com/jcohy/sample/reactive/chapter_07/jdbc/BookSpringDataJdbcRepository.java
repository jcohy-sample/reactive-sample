package com.jcohy.sample.reactive.chapter_07.jdbc;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:12:06
 * @since 2022.0.1
 */
@Repository
public interface BookSpringDataJdbcRepository extends CrudRepository<Book,Integer> {

    @Query("SELECT * FROM book WHERE LENGTH(title) = " +
            "(SELECT MAX(LENGTH(title)) FROM book)")
    List<Book> findByLongestTitle();

    /**
     * 返回一个 Stream，当客户端调用此方法时，根据底层实现，API 可以在数据库仍然执行查询时处理第一个元素。
     * @return
     */
    @Query("SELECT * FROM book WHERE LENGTH(title) = " +
            "(SELECT MIN(LENGTH(title)) FROM book)")
    Stream<Book> findByShortestTitle();

    /**
     * 利用 Spring 框架的异步支持，该方法返回 CompletableFuture，因此在等待结果时不会阻塞客户端线程，但是，由于 JDBC的阻塞方式，锁定底层线程仍然是必须的。
     * @param title
     * @return
     */
    @Async
    @Query("SELECT * FROM book b " +
            "WHERE b.title = :title")
    CompletableFuture<Book> findBookByTitleAsync(
            @Param("title") String title);

    /**
     * 将 Stream 和 CompletableFuture 结合，在第一批数据到达之前客户端线程不会阻塞，然后我们可以以块的形式遍历结果集。
     * 遗憾的是，我们必须在执行的第一部分阻塞底层线程，并且在获取下一个数据块之后阻塞客户端线程。
     * 这种行为是 JDBC 能实现的最好的行为，同时不具备响应式支持。
     * @param from
     * @param to
     * @return
     */
    @Async
    @Query("SELECT * FROM book b " +
            "WHERE b.id > :fromId AND b.id < :toId")
    CompletableFuture<Stream<Book>> findBooksByIdBetweenAsync(
            @Param("fromId") Integer from,
            @Param("toId") Integer to);
}
