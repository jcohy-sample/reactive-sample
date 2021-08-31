package com.jcohy.sample.reactive.chapter_06.functional.password.verification.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:12:56
 * @since 1.0.0
 */
public class PasswordDTO {

    private String raw;

    private String secured;

    @JsonCreator
    public PasswordDTO(@JsonProperty("raw") String raw,
            @JsonProperty("secured") String secured) {
        this.raw = raw;
        this.secured = secured;
    }


    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getSecured() {
        return secured;
    }

    public void setSecured(String secured) {
        this.secured = secured;
    }
}
