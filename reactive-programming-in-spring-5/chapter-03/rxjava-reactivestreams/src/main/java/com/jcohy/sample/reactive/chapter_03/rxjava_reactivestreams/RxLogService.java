package com.jcohy.sample.reactive.chapter_03.rxjava_reactivestreams;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import org.reactivestreams.Publisher;
import rx.Observable;
import rx.RxReactiveStreams;


/**
 * <p> 描述: 该类表示旧的基于 Rx 的实现.
 * ，我们使用 RxNetty HttpClient ，它能使用封装在基于 RxJava API 中的 Netty Client 以异步、
 * 非阻塞方式与外部服务进行交互。
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:16:36
 * @since 1.0.0
 */
public class RxLogService implements LogService {

    final HttpClient<ByteBuf, ByteBuf> rxClient = HttpClient.newClient(new InetSocketAddress(8080));

    @Override
    public Publisher<String> stream() {
        //外部请求执行过程。在此处， 通过使用创建的 HttpClient 实例，我们从外部服务请求日志流，并将传人的元素转换为 String 实例。
        Observable<String> rxStream = rxClient.createGet("/logs")
                .flatMap(HttpClientResponse::getContentAsServerSentEvents)
                .map(ServerSentEvent::contentAsString);
        // 使用 RxReactiveStreams 库对 Publisher 进行的 rxStream 适配。
        return RxReactiveStreams.toPublisher(rxStream);
    }
}
