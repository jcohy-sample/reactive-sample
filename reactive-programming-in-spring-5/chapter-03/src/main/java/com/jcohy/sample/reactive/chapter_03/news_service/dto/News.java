package com.jcohy.sample.reactive.chapter_03.news_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.annotations.Immutable;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:17:28
 * @since 1.0.0
 */
@Document
@Immutable
public class News {

    @Id
    @JsonIgnore
    private ObjectId id;

    private  String title;
    private  String content;
    private  Date publishedOn;
    private  String category;
    private  String author;

    public News(ObjectId id, String title, String content, Date publishedOn, String category, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishedOn = publishedOn;
        this.category = category;
        this.author = author;
    }


    public News() {
    }



    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(Date publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("News{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", publishedOn=").append(publishedOn);
        sb.append(", category='").append(category).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
