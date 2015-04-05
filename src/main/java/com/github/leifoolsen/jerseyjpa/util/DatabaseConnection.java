package com.github.leifoolsen.jerseyjpa.util;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;

import java.util.Arrays;
import java.util.Properties;

public class DatabaseConnection {

    private static final String PU_NAME = "jpa-example-eclipselink";
    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection;

    private DatabaseConnection() {}

    public static JpaDatabaseConnectionManager.JpaDatabaseConnection createConnection() {

        // Configure PU //
        Properties properties = PersistenceProperties.createPropertiesForProvider(
                PersistenceProperties.ECLIPSELINK, null, Arrays.asList(Publisher.class, Book.class));

        connection = JpaDatabaseConnectionManager.createConnection(PU_NAME, properties);
        return connection;
    }

    public static JpaDatabaseConnectionManager.JpaDatabaseConnection getConnection() {
        return connection;
    }

}
