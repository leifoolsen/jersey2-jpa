package com.github.leifoolsen.jerseyjpa.repository;

import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;

import java.util.Properties;

public class DatabaseConnection {

    private static final String PU_NAME = "jpa-example-eclipselink";
    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection;

    private DatabaseConnection() {}

    public static JpaDatabaseConnectionManager.JpaDatabaseConnection createConnection() {
        Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");

        /*
        // Hibernate
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.default_batch_fetch_size", "16");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");

        // Add entity classes, Hibernate
        properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, Arrays.asList(Publisher.class, Book.class));
        */

        properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
        properties.put("eclipselink.ddl-generation.output-mode", "database");
        properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        properties.put("eclipselink.jdbc.batch-writing.size", "1000");
        properties.put("eclipselink.logging.level", "OFF");  // OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
        properties.put("eclipselink.logging.level.sql", "INFO");
        properties.put("eclipselink.logging.parameters", "true");
        properties.put("eclipselink.logging.timestamp", "true");
        properties.put("eclipselink.logging.session", "true");
        properties.put("eclipselink.logging.thread", "true");
        properties.put("eclipselink.logging.exceptions", "true");

        // Add entity classes, Eclipselink
        properties.put("eclipselink.metadata-source", "XML");
        properties.put("eclipselink.metadata-source.xml.file", "META-INF/eclipselink-orm.xml");

        connection = JpaDatabaseConnectionManager.createConnection(PU_NAME, properties);
        return connection;
    }

    public static JpaDatabaseConnectionManager.JpaDatabaseConnection getConnection() {
        return connection;
    }

}
