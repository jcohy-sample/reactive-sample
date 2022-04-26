package com.jcohy.sample.reactive.chapter_07.mongo_rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:15:45
 * @since 2022.0.1
 */
@SuppressWarnings("Duplicates")
@EnableMongoRepositories
@SpringBootApplication
public class Chapter7RxMongoApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Chapter7RxMongoApplication.class);

    private final BookSpringDataMongoRxRepository rxBookRepository;

    private final RxMongoTemplateQueryService rxMongoTemplateQueryService;

    private final RxMongoDriverQueryService rxMongoDriverQueryService;

    private final RxBookPublishingYearUpdatedExample yearUpdatedExample;

    public Chapter7RxMongoApplication(BookSpringDataMongoRxRepository rxBookRepository,
            RxMongoTemplateQueryService rxMongoTemplateQueryService,
            RxMongoDriverQueryService rxMongoDriverQueryService,
            RxBookPublishingYearUpdatedExample yearUpdatedExample) {
        this.rxBookRepository = rxBookRepository;
        this.rxMongoTemplateQueryService = rxMongoTemplateQueryService;
        this.rxMongoDriverQueryService = rxMongoDriverQueryService;
        this.yearUpdatedExample = yearUpdatedExample;
    }


    public static void main(String... args) {
        SpringApplication.run(Chapter7RxMongoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 准备要插入到数据库的响应式流
        Flux<Book> books = Flux.just(
                new Book("The Martian", 2011, "Andy Weir"),
                new Book("Blue Mars", 1996, "Kim Stanley Robinson"),
                new Book("The War of the Worlds", 1898, "H. G. Wells"),
                new Book("Artemis", 2016, "Andy Weir"),
                new Book("The Expanse: Leviathan Wakes", 2011, "Daniel Abraham", "Ty Franck"),
                new Book("The Expanse: Caliban's War", 2012, "Daniel Abraham", "Ty Franck")
        );
        rxBookRepository
                // 在实际订阅者订阅之前不会进行保存.
                .saveAll(books)
                // 使用 then 转换流，只传播 onComplete 和 onError 事件
                .then()
                // 当响应式流已完成并保存所有图书时，我们会报告相应的日志信息
                .doOnSuccess(amount -> log.info("books saved in DB"))
                // 只要是响应式流，就应该存在订阅者，此处为了简单起见，我们在订阅时没有提供任何处理程序。但在
                // 实际的应用程序中应该有真正的订阅者。例如来自处理响应的 WebFlux exchange 方法的订阅
                .subscribe();
//        rxBookRepository
//                // 在实际订阅者订阅之前不会进行保存.
//                .saveAll(books)
//                .count()
//                .doOnNext(amount -> log.info("{} books saved in DB", amount))
//                .block();

        Flux<Book> allBooks = rxBookRepository.findAll();
        reportResults("All books in DB：", allBooks);

        Flux<Book> andyWeirBooks = rxBookRepository
                .findByAuthorsOrderByPublishingYearDesc(Mono.just("Andy Weir"));
        reportResults("All books by Andy Weir:", andyWeirBooks);

        reportResults("Search for books with title regexp:",
                rxBookRepository.findManyByTitleRegex("Exp.*"));

        Flux<Book> booksWithFewAuthors = rxBookRepository.booksWithFewAuthors();
        reportResults("Books with few authors:", booksWithFewAuthors);

        log.info("--- Pageable support ------------------------------------------------------------");
        reportResults("The first page of books:",
                rxBookRepository.findByPublishingYearBetweenOrderByPublishingYear(1800, 2020, PageRequest.of(0, 2)));
        reportResults("The second page of books:",
                rxBookRepository.findByPublishingYearBetweenOrderByPublishingYear(1800, 2020, PageRequest.of(1, 2)));
        reportResults("The third page of books:",
                rxBookRepository.findByPublishingYearBetweenOrderByPublishingYear(1800, 2020, PageRequest.of(2, 2)));


        log.info("--- Updating book's publishing year ---------------------------------------------");
        yearUpdatedExample.updatedBookYearByTitle();


        log.info("--- Custom Query Service with ReactiveMongoTemplate -----------------------------");
        reportResults("Search for books with Mars:",
                rxMongoTemplateQueryService.findBooksByTitle("Expanse"));

        log.info("--- Custom Query Service with ReactiveStreams Mongo Driver ----------------------");
        reportResults("Search for books without 'Expanse':",
                rxMongoDriverQueryService.findBooksByTitle("Expanse", true));


    }

    /**
     * 将人类可读的图书列表打印为一条日志消息的方法，该消息具有所需的消息前缀
     * @param message
     * @param books
     */
    private void reportResults(String message, Flux<Book> books) {
        books.map(Book::toString)
                // 将所有图书表示收集到一条消息中，如果图书的数量很大，这种方法可能不起作用，因为每本新书都会增加存储缓冲区的大小，并可能导致高内存消耗。
                // 为了存储中间过程，我们使用 StringBuilder，注意 StringBuilder 不是线程安全的。onNext 方法可能调用不同的线程，但响应式规范保证了
                // 发生前（happens-before）关系。因此，即使不同的线程推送不同的实体，也可以安全的用 StringBuilder 将他们连在一起，因为
                // 内存屏障（memory barrier）可以保证 StringBuilder 对象在一个响应式流内更新时处于最新状态。
                // 一个图书被添加到单个缓冲区
                //
                .reduce(
                        new StringBuilder(),
                        (sb,b) -> sb.append(" - ")
                                .append(b)
                                .append("\n"))
                // 由于 reduce 方法仅在处理了所有传入的 onNext 事件后才发出 onNext 事件，因此我们可以安全的记录所有图书的最终消息。
                .doOnNext(sb -> log.info(message + "\n{}", sb))
                // 要启动处理过程，我们必须执行 subscribe
                .subscribe();
    }}