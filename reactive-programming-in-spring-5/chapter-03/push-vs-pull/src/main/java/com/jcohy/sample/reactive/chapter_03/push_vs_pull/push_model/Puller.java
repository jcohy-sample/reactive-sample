package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_model;

import rx.Observable;

/**
 * <p> 描述: 纯推模型最终优化.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:15:51
 * @since 1.0.0
 */
public class Puller {

    final AsyncDatabaseClient dbClient = new DelayedFakeAsyncDatabaseClient();

    /**
     * 这里的 Observable<Item> 会返回一个类型，该类型串制只正在被推送的元素。
     * 纯推模型会导致两个问题
     * <ul>
     *     <li>慢生产者和快消费者</li>
     *     <li>快生产者和慢消费者：这里的问题是生产者所发送的数据可能远远超出消费者的处理能力， 而这可能导致组件在压力下发生灾难性故障
     *          可通过以下几种方式解决
     *          <ul>
     *              <li>无界队列：回弹性降低</li>
     *              <li>有界丢弃队列：当消息的重要性很低时可以使用</li>
     *              <li>有界阻塞队列：这种技术否定了系统的所有异步行为。</li>
     *          </ul>
     *     </li>
     * </ul>
     * 背压机制：
     *
     * @param count count
     * @return /
     */
    public Observable<Item> list(int count) {
        return dbClient.getStreamOfItems()
                .filter(this::isValid)
                // 根据调用者的请求获取特定数量的数据
                .take(count);
    }

    boolean isValid(Item item) {
        return Integer.parseInt(item.getId()) % 2 == 0;
    }
}
