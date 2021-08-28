package com.jcohy.sample.reactive.chapter_03.vertx;

import io.reactivex.Flowable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.streams.Pump;
import io.vertx.ext.reactivestreams.ReactiveReadStream;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:16:50
 * @since 1.0.0
 */
public class VertxDemoApp extends AbstractVerticle {

    public static void main(final String[] args) {
        Launcher.executeCommand("run", VertxDemoApp.class.getName());
    }

    @Override
    public void start() throws Exception {
        LogService logsService = new MockLogService();

        vertx.createHttpServer()
                .requestHandler(request -> {
                    ReactiveReadStream<Buffer> rrs = ReactiveReadStream.readStream();
                    HttpServerResponse response = request.response();

                    Flowable<Buffer> logs = Flowable.fromPublisher(logsService.stream())
                            .map(Buffer::buffer)
                            .doOnTerminate(response::end);

                    logs.subscribe(rrs);

                    response.setStatusCode(200);
                    response.setChunked(true);
                    response.putHeader("Content-Type", "text/plain");
                    response.putHeader("Connection", "keep-alive");

                    Pump.pump(rrs, response)
                            .start();
                })
                .listen(8080);
        System.out.println("HTTP server started on port 8080");
    }
}
