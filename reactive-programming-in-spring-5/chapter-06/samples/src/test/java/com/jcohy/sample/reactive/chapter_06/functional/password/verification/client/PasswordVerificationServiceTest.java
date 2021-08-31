package com.jcohy.sample.reactive.chapter_06.functional.password.verification.client;

import java.time.Duration;

import com.jcohy.sample.reactive.chapter_06.functional.password.verification.server.StandaloneApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:15:37
 * @since 1.0.0
 */
public class PasswordVerificationServiceTest {

    @BeforeEach
    public void setUp() throws InterruptedException {
        new Thread(StandaloneApplication::main).start();
        Thread.sleep(1000);
    }

    @Test
    public void checkApplicationRunning() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(18);

        DefaultPasswordVerificationService service = new DefaultPasswordVerificationService(WebClient.builder());

        StepVerifier.create(service.check("test", encoder.encode("test")))
                .expectSubscription()
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }
}