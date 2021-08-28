package com.jcohy.sample.reactive.chapter_03.conversion_problem;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:02
 * @since 1.0.0
 */
@SpringBootTest(
        webEnvironment = WebEnvironment.DEFINED_PORT,
        classes = { ConversionProblemApp.class, MyController.class, TestController.class }
)
@ExtendWith(SpringExtension.class)
public class TestControllerTest {

    @Test
    public void testMyControllerResponse() {
        RestTemplate template = new RestTemplate();

        String object = template.getForObject("http://localhost:8080", String.class);
        MatcherAssert.assertThat(object, Matchers.equalTo("Hello World"));
    }
}