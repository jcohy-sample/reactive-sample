package com.jcohy.sample.reactive.chatpet_07.rxjdbc.book;

import java.util.UUID;

import org.davidmoten.rx.jdbc.annotations.Column;
import org.davidmoten.rx.jdbc.annotations.Query;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:11:27
 * @since 2022.0.1
 */
@Query("select id, title, publishing_year from book order by publishing_year")
public interface Book {
    @Column
    String id();
    @Column String title();
    @Column Integer publishing_year();

    static Book of(String title, Integer publishingYear) {
        return new Impl(UUID.randomUUID().toString(), title, publishingYear);
    }

    class Impl implements Book {
        private final String id;
        private final String title;
        private final Integer publishingYear;

        public Impl(String id, String title, Integer publishingYear) {
            this.id = id;
            this.title = title;
            this.publishingYear = publishingYear;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String title() {
            return title;
        }

        @Override
        public Integer publishing_year() {
            return publishingYear;
        }
    }
}
