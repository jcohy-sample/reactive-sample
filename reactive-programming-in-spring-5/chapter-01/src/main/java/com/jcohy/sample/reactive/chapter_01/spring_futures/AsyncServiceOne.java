package com.jcohy.sample.reactive.chapter_01.spring_futures;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

import com.jcohy.sample.reactive.chapter_01.commons.ExamplesCollection;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * <p> 描述: Spring 4 MVC 在很长时间内不支持 {@link CompletionStage}
 * 为了弥补这一点，它提供了向己的 {@link ListenableFuture}.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:57
 * @since 1.0.0
 */
@RestController
@RequestMapping("api/v2/resource/a")
public class AsyncServiceOne {
    private static final String PORT = "8080";

    @GetMapping
    public Future<?> process() {
        AsyncRestTemplate template = new AsyncRestTemplate();
        SuccessCallback onSuccess = r -> System.out.println("Success");
        FailureCallback onFailure = e -> System.out.println("Failure");

        ListenableFuture<?> response = template.getForEntity(
                "http://localhost:" + PORT + "/api/v2/resource/b",
                ExamplesCollection.class
        );

        response.addCallback(onSuccess, onFailure);
        return response;
    }
}
