package com.jcohy.sample.reactive.chapter_01;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:01
 * @since 1.0.0
 */
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping("")
    public String root() {
        return "Please go to http://localhost:8080/api/v1/resource/a or to http://localhost:8080/api/v2/resource/a";
    }
}