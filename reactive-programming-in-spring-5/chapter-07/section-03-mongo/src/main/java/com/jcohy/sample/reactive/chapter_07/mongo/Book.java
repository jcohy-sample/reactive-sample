package com.jcohy.sample.reactive.chapter_07.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:15:21
 * @since 2022.0.1
 */
@Document(collection = "book")
public class Book {

    @Id
    private ObjectId id;

    @Indexed
    private String title;

    @Field("pubYear")
    private int publishingYear;

    @Indexed
    private List<String> authors;

    public Book() {
    }

    public Book(String title, int publishingYear, String... authors) {
        this.title = title;
        this.publishingYear = publishingYear;
        this.authors = Arrays.asList(authors);
    }

    public ObjectId getId() {
        return this.id;
    }

    public Book setId(ObjectId id) {
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

    public int getPublishingYear() {
        return this.publishingYear;
    }

    public Book setPublishingYear(int publishingYear) {
        this.publishingYear = publishingYear;
        return this;
    }

    public List<String> getAuthors() {
        return this.authors;
    }

    public Book setAuthors(List<String> authors) {
        this.authors = authors;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return getPublishingYear() == book.getPublishingYear() &&
                Objects.equals(getId(), book.getId()) &&
                Objects.equals(getTitle(), book.getTitle()) &&
                Objects.equals(getAuthors(), book.getAuthors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getPublishingYear(), getAuthors());
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("id=").append(this.id);
        sb.append(", title='").append(this.title).append('\'');
        sb.append(", publishingYear=").append(this.publishingYear);
        sb.append(", authors=").append(this.authors);
        sb.append('}');
        return sb.toString();
    }
}
