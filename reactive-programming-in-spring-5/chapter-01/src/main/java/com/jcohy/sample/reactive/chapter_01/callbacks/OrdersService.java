package com.jcohy.sample.reactive.chapter_01.callbacks;

import com.jcohy.sample.reactive.chapter_01.commons.Input;

/**
 * <p> 描述: 异步与同步调用比较.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:31
 * @since 1.0.0
 */
public class OrdersService {

    public final ShoppingCardService shoppingCardService;

    public OrdersService(ShoppingCardService shoppingCardService) {
        this.shoppingCardService = shoppingCardService;
    }

    void process() {
        Input input = new Input();
        shoppingCardService.calculate(input,output -> {
            System.out.println(shoppingCardService.getClass().getSimpleName() + " execution completed");
        });
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        OrdersService ordersServiceAsync = new OrdersService(new AsyncShoppingCardService());
        OrdersService ordersServiceSync = new OrdersService(new SyncShoppingCardService());

        ordersServiceAsync.process();
        ordersServiceAsync.process();
        ordersServiceSync.process();

        System.out.println("运行总时间 (单位：毫秒): " + (System.currentTimeMillis() - start));

        Thread.sleep(1000);
    }
}
