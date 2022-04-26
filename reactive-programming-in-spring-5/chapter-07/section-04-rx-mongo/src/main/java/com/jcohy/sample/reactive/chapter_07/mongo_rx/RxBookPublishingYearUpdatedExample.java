package com.jcohy.sample.reactive.chapter_07.mongo_rx;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;
import reactor.util.function.Tuple2;

import org.springframework.stereotype.Component;

import static java.time.Duration.between;
import static java.time.Instant.now;
import static reactor.function.TupleUtils.function;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:16:51
 * @since 2022.0.1
 */
@Component
public class RxBookPublishingYearUpdatedExample {

    private static final Logger log = LoggerFactory.getLogger(RxBookPublishingYearUpdatedExample.class);

    private final BookSpringDataMongoRxRepository rxBookRepository;

    public RxBookPublishingYearUpdatedExample(BookSpringDataMongoRxRepository rxBookRepository) {
        this.rxBookRepository = rxBookRepository;
    }

    public void updatedBookYearByTitle() {
        Instant start = Instant.now();

        /**
         * 解析标题并模拟两秒的延迟，值准备就绪后记录
         */
        Mono<String> title = Mono.delay(Duration.ofSeconds(1))
                .thenReturn("Artemis")
                .doOnSubscribe(s -> log.info("Subscribed for title"))
                .doOnNext(t -> log.info("Book title resolved: {}", t));

        /**
         * 解析新的出版年份值并模拟两秒的延迟，到达时进行记录
         */
        Mono<Integer> publishingYear = Mono.delay(Duration.ofSeconds(2))
                .thenReturn(2017)
                .doOnSubscribe(s -> log.info("Subscribed for publishing year"))
                .doOnNext(t -> log.info("New publishing year resolved: {}", t));

        // 调用业务代码，在更新通知到达时进行记录。
        updatedBookYearByTitle(title,publishingYear)
                .doOnNext(b -> log.info("Publishing year updated for the book: {}", b))
                // 检查是否还存在 onNext 事件（意味着实际的图书更新）
                .hasElement()
                // 完成后，记录更新是否成功并报告总执行时间
                .doOnSuccess(status -> log.info("Updated finished {}, took: {}",
                        status ? "successfully" : "unsuccessfully",between(start, now())))
                .subscribe();

    }


    private final Solution solution = Solution.ZIP_FUNCTION;

    public Mono<Book> updatedBookYearByTitle(Mono<String> title, Mono<Integer> newPublishingYear) {
        switch (solution) {
            case NAIVE_1:
                return updatedBookYearByTitle_1(title, newPublishingYear);
            case NAIVE_2:
                return updatedBookYearByTitle_2(title, newPublishingYear);
            case ZIP_TUPLE:
                return updatedBookYearByTitle_3(title, newPublishingYear);
            case ZIP_TUPLE_DOUBLE_SUBSCRIPTION:
                return updatedBookYearByTitle_3_2(title, newPublishingYear);
            case ZIP_FUNCTION:
                return updatedBookYearByTitle_4(title, newPublishingYear);
            case ZIP_FUNCTION_DOUBLE_SUBSCRIPTION:
                return updatedBookYearByTitle_5_error(title, newPublishingYear);
            case CACHED_TITLE:
                return updatedBookYearByTitle_6(title, newPublishingYear);
            case FINAL:
                return updatedBookYearByTitle_7(title, newPublishingYear);
            default:
                throw new RuntimeException("Unexpected solution: " + solution);
        }
    }

    /**
     * 此示例只有在收到标题后才订阅新的出版年份
     */
    public Mono<Book> updatedBookYearByTitle_1(Mono<String> title, Mono<Integer> newPublishingYear) {
        // 对 title 的引用调用响应式存储库
        return rxBookRepository.findOneByTitle(title)
                // 一旦找打实体 book，我们就订阅新的出版年份值
                .flatMap(book -> newPublishingYear
                        // 等新的出版年份值到达，我们就更新 book 实体
                        .flatMap(year -> {
                            book.setPublishingYear(year);
                            return rxBookRepository.save(book);
                        }));
    }

    /**
     * 此示例只有在收到新的出版年份后才订阅标题
     */
    private Mono<Book> updatedBookYearByTitle_2(Mono<String> title, Mono<Integer> newPublishingYear) {
        return newPublishingYear
                .flatMap(newYear -> rxBookRepository.findOneByTitle(title)
                        .flatMap(book -> {
                            book.setPublishingYear(newYear);
                            return rxBookRepository.save(book);
                        }));
    }

    /**
     * 使用 zip 同时订阅 title 和 year。
     */
    public  Mono<Book> updatedBookYearByTitle_3(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.zip(title,newPublishingYear)
                // 一旦两个值准备就绪，我们的流就会收到一个 Tuple2<String,Integer> 容器。包含相关的值
                .flatMap((Tuple2<String,Integer> data) -> {
                    String titleValue = data.getT1();
                    Integer yearValue = data.getT2();
                    return rxBookRepository.findOneByTitle(Mono.just(titleValue))
                            .flatMap(book -> {
                                book.setPublishingYear(yearValue);
                                return rxBookRepository.save(book);
                            });
                });
    }

    /**
     *
     */
    private Mono<Book> updatedBookYearByTitle_3_2(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.zip(title,newPublishingYear)
                .flatMap((Tuple2<String,Integer> data) ->
                        rxBookRepository.findOneByTitle(title)
                                .flatMap(book -> {
                                    Integer yearValue = data.getT2();
                                    book.setPublishingYear(yearValue);
                                    return rxBookRepository.save(book);
                                }));
    }

    /**
     * 为了提高可读性，并删除 getT1(), 和 getT2 的调用。使用 Reactor Addons 模块，
     * 在此迭代中，我们只有在收到标题和新出版值后才开始加载图书实体
     */
    private Mono<Book> updatedBookYearByTitle_4(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.zip(title,newPublishingYear)
                .flatMap(TupleUtils.function((titleValue,yearValue) ->
                    rxBookRepository
                            // 如果这里使用原始的 title 对象，但是会订阅两次 title 流并接收
                            .findOneByTitle(Mono.just(titleValue))
                            .flatMap(book -> {
                                book.setPublishingYear(yearValue);
                               return rxBookRepository.save(book);
                            })));
    }

    /**
     *
     */
    private Mono<Book> updatedBookYearByTitle_5_error(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.zip(title, newPublishingYear)
                .flatMap(
                        function((t, y) -> rxBookRepository
                                .findOneByTitle(title)
                                .flatMap(book -> {
                                    book.setPublishingYear(y);
                                    return rxBookRepository.save(book);
                                })));
    }

    private Mono<Book> updatedBookYearByTitle_6(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.defer(() -> {
            Mono<String> cacheTitle = title.cache();
            return Mono.zip(cacheTitle,newPublishingYear)
                    .flatMap(TupleUtils.function((titleValue,yearValue) -> rxBookRepository
                            .findOneByTitle(cacheTitle)
                            .flatMap((book) -> {
                                   book.setPublishingYear(yearValue);
                                   return rxBookRepository.save(book);
                            })));
        });
    }

    /**
     * 当出版年份请求仍在进行中但标题已经存在时，我们也可以开始加载图书实体
     */
    private Mono<Book> updatedBookYearByTitle_7(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.zip(newPublishingYear,rxBookRepository.findOneByTitle(title))
                .flatMap(TupleUtils.function((yearValue,bookValue) -> {
                    bookValue.setPublishingYear(yearValue);
                    return rxBookRepository.save(bookValue);
                }));
    }

    private enum Solution {
        NAIVE_1,
        NAIVE_2,
        ZIP_TUPLE,
        ZIP_TUPLE_DOUBLE_SUBSCRIPTION,
        ZIP_FUNCTION,
        ZIP_FUNCTION_DOUBLE_SUBSCRIPTION,
        CACHED_TITLE,
        FINAL
    }
}
