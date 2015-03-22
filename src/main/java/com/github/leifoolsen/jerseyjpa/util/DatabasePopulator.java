package com.github.leifoolsen.jerseyjpa.util;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DatabasePopulator {
    private static final Logger logger = LoggerFactory.getLogger(DatabasePopulator.class);

    private DatabasePopulator() {}

    public static void pupulateDb(JpaDatabaseConnectionManager.JpaDatabaseConnection connection) {

        GenericRepositoryJPA repository = new GenericRepositoryJPA(connection);

        // Persist some publishers
        final Map<String, Publisher> publishers = DomainPopulator.getPublishers();

        connection.unitOfWork().begin();
        repository.persist(publishers.values());
        logger.debug("Persisted {} publishers ", publishers.size());
        connection.unitOfWork().end();


        // Persist some books
        final List<Book> books = DomainPopulator.getBooks(publishers);

        connection.unitOfWork().begin();
        repository.persist(books);
        logger.debug("Persisted {} books ", books.size());
        connection.unitOfWork().end();
    }
}
