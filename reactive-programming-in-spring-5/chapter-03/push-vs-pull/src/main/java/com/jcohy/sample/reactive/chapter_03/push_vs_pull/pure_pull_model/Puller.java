package com.jcohy.sample.reactive.chapter_03.push_vs_pull.pure_pull_model;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * <p> 描述: 纯推模型.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:16
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
     * 执行查询并异步接收结果。然后，当收到结果时，我们对其进行过滤。如果是有效项，我们就将其聚合到队列中。
     * 另外，我们检查是否已经收集了足够的元素，将它们发送给调用者，然后退出拉操作。如果任何一个所涉及的 if 分支被绕过，就再次递归调用pull
     * 方法。
     * 从上述代码可以看出，我们在服务和数据库之间使用了异步、非阻塞交互。乍一看，这里没
     * 有任何问题.然而，逐个请求下一个元素会导致在从服务传递请求到数据库上花费额外的
     * 时间。从服务的角度来看，整体处理时间大部分浪费在空闲状态上。即使没有使用资源，由于额
     * 外的网络活动，整体处理时间也会是原来的两倍甚至三倍。此外，数据库不知道未来请求的数量，
     * 这意味着数据库不能提前生成数据，并因此处于空闲状态。这意味着数据库正在等待新请求。在
     * 响应被传递给服务、服务处理传人响应然后请求新数据的过程中，其效率会比较低下。
     * 为了优化整体执行过程并将模型维持为一等公民，我们可以将拉取操作与批处理结合起来。请参考 {@link com.jcohy.sample.reactive.chapter_03.push_vs_pull.batched_pull_model.Puller}
     *
     * @param elementId elementId
     * @param queue queue
     * @param resultFuture resultFuture
     * @param count count
     */
    private void pull(String elementId, BlockingQueue<Item> queue,
            CompletableFuture<Queue<Item>> resultFuture, int count) {
        dbClient.getNextAfterId(elementId)
                .thenAccept(item -> {
                    if (isValid(item)) {
                        queue.offer(item);
                        if (queue.size() == count) {
                            resultFuture.complete(queue);
                            return;
                        }
                    }
                    pull(item.getId(), queue, resultFuture, count);
                });
    }

    boolean isValid(Item item) {
        return Integer.parseInt(item.getId()) % 2 == 0;
    }
}
