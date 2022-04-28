package com.jcohy.sample.reactive.chatpet_07.rxjdbc;

import java.time.Duration;

import com.jcohy.sample.reactive.chatpet_07.rxjdbc.book.Book;
import com.jcohy.sample.reactive.chatpet_07.rxjdbc.book.RxBookRepository;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:12:03
 * @since 2022.0.1
 */
@SpringBootApplication
public class Chapter7RxJava2JdbcApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Chapter7RxJava2JdbcApplication.class);

    private final RxBookRepository bookRepository;

    public Chapter7RxJava2JdbcApplication(RxBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Chapter7RxJava2JdbcApplication.class, args);
    }

    @Override
    public void run(String... args) {
        reportResults("All books before save:s", bookRepository.findAll());

        bookRepository.save(
                        Flowable.just(
                                Book.of("Artemis", 2017),
                                Book.of("The Expanse: Leviathan Wakes", 2011),
                                Book.of("The Expanse: Caliban's War", 2012)
                        ))
                .blockingLast();

        reportResults("All books after save:", bookRepository.findAll());

        reportResults("Book with title Artemis: ",
                bookRepository
                        .findByTitle(Mono.just("Artemis"))
                        .toFlowable());

        reportResults("Book with id='99999999-1967-47a1-aaaa-8399a29866a0':",
                bookRepository
                        .findById("99999999-1967-47a1-aaaa-8399a29866a0")
                        .toFlowable());

        reportResults("Books from XX century:",
                bookRepository
                        .findByYearBetween(Single.just(1900), Single.just(1999)));

        Mono.delay(Duration.ofSeconds(5))
                .subscribe(e -> log.info("Application finished successfully!"));

    }

    private void reportResults(String message, Publisher<Book> books) {
        Flux
                .from(books)
                .map(Book::toString)
                .reduce(
                        new StringBuffer(),
                        (sb, b) -> sb.append(" - ")
                                .append(b)
                                .append("\n"))
                .doOnNext(sb -> log.info(message + "\n{}", sb))
                .subscribe();
    }
}
