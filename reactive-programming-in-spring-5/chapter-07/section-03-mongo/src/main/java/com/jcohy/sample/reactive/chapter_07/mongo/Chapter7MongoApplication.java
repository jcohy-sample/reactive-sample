package com.jcohy.sample.reactive.chapter_07.mongo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/26:15:26
 * @since 2022.0.1
 */
@EnableMongoRepositories
@SpringBootApplication
public class Chapter7MongoApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Chapter7MongoApplication.class);

    private final BookSpringDataMongoRepository bookRepo;

    public Chapter7MongoApplication(BookSpringDataMongoRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(Chapter7MongoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        bookRepo.save(new Book("The Martian", 2011, "Andy Weir"));
        bookRepo.save(new Book("Blue Mars", 1996, "Kim Stanley Robinson"));
        bookRepo.save(new Book("The War of the Worlds", 1898, "H. G. Wells"));
        bookRepo.save(new Book("Artemis", 2017, "Andy Weir"));
        bookRepo.save(new Book("The Expanse: Leviathan Wakes", 2011, "Daniel Abraham", "Ty Franck"));
        bookRepo.save(new Book("The Expanse: Caliban's War", 2012, "Daniel Abraham", "Ty Franck"));

        log.info("Books saved in DB");

        List<Book> allBooks = bookRepo.findAll();
        log.info("All books in DB: \n{}", toString(allBooks));

        Iterable<Book> andyWeirBooks = bookRepo.findByAuthorsOrderByPublishingYearDesc("Andy Weir");
        log.info("All books by Andy Weir: \n{}", toString(andyWeirBooks));

        Iterable<Book> booksWithFewAuthors = bookRepo.booksWithFewAuthors();
        log.info("Books with few authors: \n{}", toString(booksWithFewAuthors));

        log.info("Application finished successfully!");
    }

    private String toString(Iterable<Book> books) {
        StringBuilder sb = new StringBuilder();
        books.iterator().forEachRemaining(b ->
                sb.append(" - ").append(b.toString()).append("\n"));
        return sb.toString();
    }
}
