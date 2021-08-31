package com.jcohy.sample.reactive.chapter_06.functional;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:16:17
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class DemoApplicationTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    public void shouldBeAbleToPostOrder() {
        Order order = new Order("1");
        webTestClient
                .post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(order))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("location", "/orders/1");
    }

    @Test
    public void shouldNotBeNotFoundInCaseOfWrongContentType() {
        webTestClient
                .post()
                .uri("/orders")
                .contentType(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void shouldBeAbleToFindOrder() {
        shouldBeAbleToPostOrder();
        webTestClient
                .get()
                .uri("/orders/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .consumeWith(e -> Assert.assertThat(e.getResponseBody(), Matchers.hasProperty("id", Matchers.equalTo("1"))));
    }
}
