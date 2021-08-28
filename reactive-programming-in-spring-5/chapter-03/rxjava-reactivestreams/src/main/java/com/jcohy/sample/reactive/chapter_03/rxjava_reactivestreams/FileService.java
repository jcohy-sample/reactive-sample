package com.jcohy.sample.reactive.chapter_03.rxjava_reactivestreams;

import org.reactivestreams.Publisher;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:16:41
 * @since 1.0.0
 */
public interface FileService {

    void writeTo(String file, Publisher<String> content);
}
