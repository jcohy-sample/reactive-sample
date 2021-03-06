package com.jcohy.sample.reactive.chapter_01.communication;

import com.jcohy.sample.reactive.chapter_01.commons.ExamplesCollection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:02
 * @since 1.0.0
 */
@RestController
@RequestMapping("api/v1/resource/a")
public class ServiceOne {
    private static final String PORT = "8080";

    @GetMapping
    public ExamplesCollection processRequest() {
        RestTemplate template = new RestTemplate();
        ExamplesCollection result = template.getForObject(
                "http://localhost:" + PORT + "/api/v1/resource/b",
                ExamplesCollection.class
        );

        processResultFurther(result);

        return result;
    }

    private void processResultFurther(ExamplesCollection result) {
        // Do some processing
    }
}
