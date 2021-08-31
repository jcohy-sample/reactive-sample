package com.jcohy.sample.reactive.chapter_05.core;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ReactiveTypeDescriptor;

/**
 * <p> 描述: 提供对 RxJava 2 中的 Maybe 响应式类型的转换.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:10:25
 * @since 1.0.0
 */
public class MaybeReactiveAdapter extends ReactiveAdapter {

    /**
     * ReactiveAdapterRegistry 使我们能将 ReactiveAdapter 的实例保存在一个位置并提供对它们的通用访问
     * 有关如何使用，请查看测试类 {@link MaybeReactiveAdapterTest}
     */
    static {
        ReactiveAdapterRegistry
                .getSharedInstance()
                .registerReactiveType(
                        ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty),
                        rawMaybe -> ((Maybe<?>) rawMaybe).toFlowable(),
                        publisher -> Flowable.fromPublisher(publisher).singleElement()
                );
    }

    public MaybeReactiveAdapter() {
        super(
                // ReactiveTypeDescriptor 提供了有关 ReactiveAdapter 中使用的响应式类型的信息。
                ReactiveTypeDescriptor
                        .singleOptionalValue(Maybe.class, Maybe::empty),
                // 数将原始对象(假设为 Maybe) 转换为 Publisher
                rawMaybe -> ((Maybe<?>) rawMaybe).toFlowable(),
                // 并将任何 Publisher 转换回 Maybe
                publisher -> Flowable.fromPublisher(publisher)
                        .singleElement()
        );
    }
}
