package com.jcohy.sample.reactive.chapter_03.rxjava_reactivestreams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import rx.Subscriber;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:16:41
 * @since 1.0.0
 */
public class AsyncFileSubscriber extends Subscriber<String> implements Runnable {

    static final AtomicIntegerFieldUpdater<AsyncFileSubscriber> WIP =
            AtomicIntegerFieldUpdater.newUpdater(AsyncFileSubscriber.class, "wip");

    final FileChannel fileChannel;

    final ExecutorService executorService;

    final Queue<String> queue;

    final int prefetch;

    int remaining;

    volatile boolean terminated;

    volatile int wip;

    public AsyncFileSubscriber(String file) throws IOException {
        this(file, Executors.newSingleThreadExecutor());
    }

    public AsyncFileSubscriber(String file, ExecutorService executorService) throws IOException {
        this(file, executorService, 256);
    }

    public AsyncFileSubscriber(String file,
            ExecutorService executorService,
            int prefetch) throws IOException {
        this.fileChannel = FileChannel.open(Paths.get(file), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        this.executorService = executorService;
        this.prefetch = prefetch;
        this.queue = new ArrayBlockingQueue<>(prefetch);
        this.remaining = prefetch;
    }

    @Override
    public void onStart() {
        request(prefetch);
    }

    @Override
    public void onCompleted() {
        terminated = true;
        trySchedule();
    }

    @Override
    public void onError(Throwable e) {
        terminated = true;
        queue.clear();
        try {
            fileChannel.close();
        }
        catch (IOException e1) {
        }
    }

    @Override
    public void onNext(String s) {
        if (terminated) {
            return;
        }

        if (!queue.offer(s)) {
            onError(new IllegalStateException());
            return;
        }

        trySchedule();
    }

    @Override
    public void run() {
        String element;
        int wip = this.wip;

        for (; ; ) {
            while (remaining > 0 && (element = queue.poll()) != null) {
                remaining--;
                try {
                    fileChannel.write(ByteBuffer.wrap(element.getBytes()));
                }
                catch (Exception e) {
                    onError(e);
                    return;
                }
            }

            if (terminated && queue.isEmpty()) {
                queue.clear();
                try {
                    fileChannel.close();
                }
                catch (IOException e1) {
                }
                return;
            }

            if (remaining == 0) {
                remaining = prefetch;
                request(prefetch);
            }

            wip = WIP.addAndGet(this, -wip);

            if (wip == 0) {
                return;
            }
        }
    }

    void trySchedule() {
        if (WIP.getAndIncrement(this) > 0) {
            return;
        }

        executorService.submit(this);
    }
}
