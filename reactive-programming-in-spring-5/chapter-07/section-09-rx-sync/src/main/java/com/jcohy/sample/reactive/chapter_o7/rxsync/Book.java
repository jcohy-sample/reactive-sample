package com.jcohy.sample.reactive.chapter_o7.rxsync;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:18:32
 * @since 2022.0.1
 */
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer publishingYear;


    public Book() {
    }

    public Book(Integer id, String title, Integer publishingYear) {
        this.id = id;
        this.title = title;
        this.publishingYear = publishingYear;
    }

    public Book(String title, int publishingYear) {
        this(null, title, publishingYear);
    }

    public Integer getId() {
        return this.id;
    }

    public Book setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getPublishingYear() {
        return this.publishingYear;
    }

    public Book setPublishingYear(Integer publishingYear) {
        this.publishingYear = publishingYear;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(getId(), book.getId()) &&
                Objects.equals(getTitle(), book.getTitle()) &&
                Objects.equals(getPublishingYear(), book.getPublishingYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getPublishingYear());
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("id=").append(this.id);
        sb.append(", title='").append(this.title).append('\'');
        sb.append(", publishingYear=").append(this.publishingYear);
        sb.append('}');
        return sb.toString();
    }
}
