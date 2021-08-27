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

    private final String title;
    private final String recipient;
    private final Collection<News> digest;

    public NewsLetter(String title, String recipient, Collection<News> digest) {


        this.title = title;
        this.recipient = recipient;
        this.digest = digest;
    }
}
