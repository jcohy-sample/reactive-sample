package com.jcohy.sample.reactive.chapter_01.completion_stage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.jcohy.sample.reactive.chapter_01.commons.Input;

/**
 * <p> 描述: 使用 Java 8 提供的 {@link CompletionStage} 以及其实现 {@link CompletableFuture} 调用.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:52
 * @since 1.0.0
 */
public class OrdersService {
    private final ShoppingCardService shoppingCardService;

    public OrdersService(ShoppingCardService shoppingCardService) {
        this.shoppingCardService = shoppingCardService;
    }

    void process() {
        Input input = new Input();

        shoppingCardService.calculate(input)
                .thenAccept(v -> System.out.println(shoppingCardService.getClass().getSimpleName() + " execution completed"));

        System.out.println(shoppingCardService.getClass().getSimpleName() + " calculate called");
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        OrdersService ordersService1 = new OrdersService(new CompletionStageShoppingCardService());

        ordersService1.process();
        ordersService1.process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));

        Thread.sleep(1000);
    }
}
