package com.jcohy.sample.reactive.chapter_06.sse;

import java.util.Map;

import reactor.core.publisher.Flux;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:16:58
 * @since 1.0.0
 */
@RestController
public class ServerSentController {


    private Map<String, StocksService> stringStocksServiceMap;

    @RequestMapping("/sse/stocks")
    public Flux<ServerSentEvent<?>> streamStocks() {
        return Flux
                .fromIterable(stringStocksServiceMap.values())
                .flatMap(StocksService::stream)
                .<ServerSentEvent<?>>map(stockItem ->
                        ServerSentEvent.builder()
                                .event("StockItem")
                                .id(stockItem.getId())
                                .build()
                )
                .startWith(
                        ServerSentEvent
                                .builder()
                                .event("Stocks")
                                .data(stringStocksServiceMap.keySet())
                                .build()
                );

    }
}
