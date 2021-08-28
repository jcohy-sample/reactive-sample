package com.jcohy.sample.reactive.chapter_03.rxjava_reactivestreams;

import java.io.IOException;

import org.reactivestreams.Publisher;
import rx.RxReactiveStreams;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:16:41
 * @since 1.0.0
 */
public class RxFileService implements FileService {
    @Override
    public void writeTo(String file, Publisher<String> content) {
        try {
            AsyncFileSubscriber rxSubscriber = new AsyncFileSubscriber(file);
            content.subscribe(RxReactiveStreams.toSubscriber(rxSubscriber));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
