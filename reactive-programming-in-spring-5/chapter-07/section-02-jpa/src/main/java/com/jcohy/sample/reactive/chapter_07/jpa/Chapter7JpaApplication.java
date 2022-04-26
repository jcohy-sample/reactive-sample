package com.jcohy.sample.reactive.chapter_07.jpa;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static java.util.stream.Collectors.joining;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:15:13
 * @since 2022.0.1
 */
@EnableJpaRepositories
@SpringBootApplication
public class Chapter7JpaApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Chapter7JpaApplication.class);

    private final BookSpringDataJpaRepository bookRepository;

    public Chapter7JpaApplication(BookSpringDataJpaRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Chapter7JpaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("All books in DB: \n{}",
                toString(bookRepository.findAll()));

        log.info("All books with ids in range 12..15: \n{}",
                toString(bookRepository.findByIdBetween(12, 15)));

        log.info("The book with the shortest titles: \n{}",
                toString(bookRepository.findShortestTitle()));

        log.info("--- Pagination requests --------------------------------------");
        PageRequest initialRequest = PageRequest.of(0, 3);
        for (Page<Book> currentPage = bookRepository.findAll(initialRequest);
             currentPage.hasNext();
             currentPage = bookRepository.findAll(currentPage.nextPageable())) {
            log.info("[++++] Page {}: {}", currentPage.getNumber(), toString(currentPage));
        }

        log.info("Application finished successfully!");
    }

    private String toString(Iterable<Book> books) {
        return toString(StreamSupport.stream(books.spliterator(), false));
    }

    private String toString(Stream<Book> books) {
        try {
            return books
                    .map(Book::toString)
                    .collect(joining("\n - ", "\n - ", ""));
        } finally {
            // We have to close the stream when finished to free the resources used by the query.
            books.close();
        }
    }
}
