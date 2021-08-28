package com.jcohy.sample.reactive.chapter_03.push_vs_pull.batched_pull_model;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * <p> 描述: 纯推模型批量推送.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:41
 * @since 1.0.0
 */
public class Puller {

    final AsyncDatabaseClient dbClient = new DelayedFakeAsyncDatabaseClient();

    public CompletionStage<Queue<Item>> list(int count) {
        BlockingQueue<Item> storage = new ArrayBlockingQueue<>(count);
        CompletableFuture<Queue<Item>> result = new CompletableFuture<>();

        pull("1", storage, result, count);

        return result;
    }

    /**
     * 通过查询一批元素，我们可以显著提高list 方法执行的性能并显著减少整体处理时间
     * 另一方面，交互模型中仍然存在一些缺陷.
     * 当数据库查询数据时，客户端仍处于空闲状态。同时，发送一批元素比发送一个元素需要更多的时间。
     * 最后，对整批元素的额外请求实际上可能是多余的。
     * 接下来就是最终优化。我们只会请求一次数据，之后当数据变为可用时，该数据源会异步推送数据。
     * {@link com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_model.Puller}
     *
     * @param elementId elementId
     * @param queue queue
     * @param resultFuture resultFuture
     * @param count count
     */
    private void pull(String elementId, BlockingQueue<Item> queue,
            CompletableFuture<Queue<Item>> resultFuture, int count) {

        dbClient.getNextBatchAfterId(elementId, count)
                .thenAccept(items -> {
                    for (Item item : items) {
                        if (isValid(item)) {
                            queue.offer(item);

                            if (queue.size() == count) {
                                resultFuture.complete(queue);
                                return;
                            }
                        }
                    }

                    pull(items.get(items.size() - 1)
                            .getId(), queue, resultFuture, count);
                });
    }

    boolean isValid(Item item) {
        return Integer.parseInt(item.getId()) % 2 == 0;
    }

}
