package com.jcohy.sample.reactive.chapter_03.news_service.dto;

import java.util.Collection;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:17:31
 * @since 1.0.0
 */
public class NewsLetter {

    private String title;

    private Collection<News> digest;

    private String recipient;

    public NewsLetter(String title, String recipient, Collection<News> digest) {
        this.title = title;
        this.digest = digest;
        this.recipient = recipient;
    }

    public NewsLetter(String title, Collection<News> digest) {
        this.title = title;
        this.digest = digest;
    }

    public String getTitle() {
        return title;
    }

    public NewsLetter setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getRecipient() {
        return recipient;
    }

    public NewsLetter setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public Collection<News> getDigest() {
        return digest;
    }

    public NewsLetter setDigest(Collection<News> digest) {
        this.digest = digest;
        return this;
    }
}
