package com.jcohy.sample.reactive.chapter_03.news_service;

import java.util.Date;
import java.util.Random;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.News;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.News.NewsBuilder;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:11:58
 * @since 1.0.0
 */
public interface NewsHarness {
    Random RANDOM = new Random();

    static News generate() {
        return NewsBuilder.builder()
                .author(String.valueOf(RANDOM.nextGaussian()))
                .category("tech")
                .publishedOn(new Date())
                .content(String.valueOf(RANDOM.nextGaussian()))
                .title(String.valueOf(RANDOM.nextGaussian()))
                .build();
    }
}
