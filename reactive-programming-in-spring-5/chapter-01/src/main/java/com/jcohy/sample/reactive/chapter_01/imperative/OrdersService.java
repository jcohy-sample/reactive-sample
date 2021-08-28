package com.jcohy.sample.reactive.chapter_01.imperative;

import com.jcohy.sample.reactive.chapter_01.commons.Input;
import com.jcohy.sample.reactive.chapter_01.commons.Output;

/**
 * <p> 描述: 最原始，阻塞调用 ShoppingCardService#calculate 方法.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:16:08
 * @since 1.0.0
 */
public class OrdersService {

    private final ShoppingCardService scService;

    public OrdersService(ShoppingCardService scService) {
        this.scService = scService;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new OrdersService(new BlockingShoppingCardService()).process();
        new OrdersService(new BlockingShoppingCardService()).process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));
    }

    void process() {
        Input input = new Input();
        Output output = scService.calculate(input);

        System.out.println(scService.getClass().getSimpleName() + " execution completed");
    }
}
