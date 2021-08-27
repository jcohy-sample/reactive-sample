package com.jcohy.sample.reactive.chapter_03.push_vs_pull.push_pull_model;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * <p> 描述: 实例化 Take 和 Filter 操作符的自定义实现，它接受应该获取的元索数.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/27:16:16
 * @since 1.0.0
 */
public class TakeFilterOperator<T> implements Publisher<T> {

    private final Publisher<T> source;
    private final int          take;
    private final Predicate<T> predicate;

    public TakeFilterOperator(Publisher<T> source, int take, Predicate<T> predicate) {
        this.source = source;
        this.take = take;
        this.predicate = predicate;
    }

    /**
     * 为了向流提供额外的逻辑，我们必须将实际的Subscriber 包装到扩展相同接口的适配器类中。
     * @param s s
     */
    @Override
    public void subscribe(Subscriber s) {
        source.subscribe(new TakeFilterInner<>(s,take,predicate));
    }

    /**
     * 该类实现 Subscriber 接口并发挥最重要的作用， 因为它会作为实际的 Subscriber 被传递给主数据源。一旦在 nNext
     * 中接收到该元素，它就会被过滤并传输到下游 Subscriber.
     *
     * TakeFilterInner 类不仅实现了 Subscriber 接口，同时还实现了 Subscription 接口，因而它可以被传输到下游 Subscriber,
     * 从而控制所有下游需求。请注意，这里的 Queue 是 ArrayBlockingQueue 的实例，其大小与 take 相同。
     *
     * 创建扩展 Subscriber 和 Subscription 接口的内部类技术是实现中间转换阶段的经典方法。
     * @param <T>
     */
    static final class TakeFilterInner<T> implements Subscriber<T>, Subscription {

        // 实际订阅者
        final Subscriber<T> actual;
        // 发出的元素个数
        final int           take;
        // 断言条件
        final Predicate<T>  predicate;
        // 阻塞队列，其大小和 take 相同
        final Queue<T> queue;

        Subscription current;
        int          remaining;
        int          filtered;
        Throwable    throwable;
        boolean      done;

        volatile long requested;
        static final AtomicLongFieldUpdater<TakeFilterInner> REQUESTED =
                AtomicLongFieldUpdater.newUpdater(TakeFilterInner.class, "requested");

        volatile int wip;
        static final AtomicIntegerFieldUpdater<TakeFilterInner> WIP =
                AtomicIntegerFieldUpdater.newUpdater(TakeFilterInner.class, "wip");

        TakeFilterInner(Subscriber<T> actual, int take, Predicate<T> predicate) {
            this.actual = actual;
            this.take = take;
            this.remaining = take;
            this.predicate = predicate;
            this.queue = new ConcurrentLinkedQueue<>();
        }

        @Override
        public void onSubscribe(Subscription current) {
            if (this.current == null) {
                this.current = current;

                this.actual.onSubscribe(this);
                if (take > 0) {
                    this.current.request(take);
                } else {
                    onComplete();
                }
            }
            else {
                current.cancel();
            }
        }

        /**
         * 它包含元素处理声明所需的有用参数列表
         * @param element element
         */
        @Override
        public void onNext(T element) {
            if (done) {
                return;
            }

            long r = requested;
            Subscriber<T> a = actual;
            Subscription s = current;

            /**
             * 这是元素处理流程的声明。该处理流程有 3 个关键点。在应该获取的 remaining 元素数
             * 大于 0 ，实际的 Subscriber 已经请求了数据，元素是有效的旦队列中没有元素的情况下，我们
             * 可以将该元素直接发送到下游。如果尚未进行请求，或者队列中存在某些内容，我们就必须
             * 将该元素放入队列(以保存元素的顺序)并稍后进行发送 。在元素无效的情况下，我们必须
             * 增加 filtered 元素的数量。最后，如果 remaining 元素数为 0 ，那么我们必须 cancel
             * 该 Subscription 并结束流。
             */
            if (remaining > 0) {
                boolean isValid = predicate.test(element);
                boolean isEmpty = queue.isEmpty();

                if (isValid && r > 0 && isEmpty) {
                    a.onNext(element);
                    remaining--;

                    REQUESTED.decrementAndGet(this);
                    if (remaining == 0) {
                        s.cancel();
                        onComplete();
                    }
                }
                else if (isValid && (r == 0 || !isEmpty)) {
                    queue.offer(element);
                    remaining--;

                    if (remaining == 0) {
                        s.cancel();
                        onComplete();
                    }
                    drain(a, r);
                }
                else if (!isValid) {
                    filtered++;
                }
            }
            else {
                s.cancel();
                onComplete();
            }

            /**
             * 这是声明获取额外数据的机制。这里，如果 filtered 元素的数量达到上限，我们会在不阻塞整个处理过程的情况下从数据库请求额外的数据。
             */
            if (filtered > 0 && remaining / filtered < 2) {
                s.request(take);
                filtered = 0;
            }
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                return;
            }

            done = true;

            if (queue.isEmpty()) {
                actual.onError(t);
            }
            else {
                throwable = t;
            }
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }

            done = true;

            if (queue.isEmpty()) {
                actual.onComplete();
            }
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                onError(new IllegalArgumentException(
                        "Spec. Rule 3.9 - Cannot request a non strictly positive number: " + n
                ));
            }

            drain(actual, SubscriptionUtils.request(n, this, REQUESTED));
        }

        @Override
        public void cancel() {
            if (!done) {
                current.cancel();
            }

            queue.clear();
        }

        void drain(Subscriber<T> a, long r) {
            if (queue.isEmpty() || r == 0) {
                return;
            }

            int wip;

            if ((wip = WIP.incrementAndGet(this)) > 1) {
                return;
            }

            int c = 0;
            boolean empty;

            for (;;) {
                T e;
                while (c != r && (e = queue.poll()) != null) {
                    a.onNext(e);
                    c++;
                }


                empty = queue.isEmpty();
                r = REQUESTED.addAndGet(this, -c);
                c = 0;

                if (r == 0 || empty) {
                    if (done && empty) {
                        if (throwable == null) {
                            a.onComplete();
                        }
                        else {
                            a.onError(throwable);
                        }
                        return;
                    }

                    wip = WIP.addAndGet(this, -wip);

                    if (wip == 0) {
                        return;
                    }
                }
                else {
                    wip = this.wip;
                }
            }
        }
    }
}
