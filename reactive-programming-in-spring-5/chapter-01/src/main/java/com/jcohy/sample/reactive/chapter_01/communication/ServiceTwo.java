package com.jcohy.sample.reactive.chapter_01.communication;

import com.jcohy.sample.reactive.chapter_01.commons.ExamplesCollection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:03
 * @since 1.0.0
 */
@RestController
@RequestMapping("api/v1/resource/b")
public class ServiceTwo {

    @GetMapping
    public ExamplesCollection process() throws InterruptedException {
        Thread.sleep(1000);

        ExamplesCollection ec = new ExamplesCollection();
        ec.setValue("test");

        return ec;
    }
}