package com.github.leifoolsen.jerseyjpa.feedteller;

import com.github.leifoolsen.jerseyjpa.domain.FeedTeller;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import com.github.leifoolsen.jerseyjpa.util.Repository;
import com.github.leifoolsen.jerseyjpa.util.RepositoryJPA;

import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Properties;

public class FeedTellerTest {

    private static final String PU_NAME = "jpa-example-eclipselink";
    //private static final String PU_NAME = "jpa-example-hibernate";

    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection = JpaDatabaseConnectionManager.createConnection(PU_NAME);
    private static Repository repository;


    public static void beforeClass() {
        // Configure PU //
        Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");

        // Eclipse Link
        properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
        properties.put("eclipselink.ddl-generation.output-mode", "database");
        properties.put("eclipselink.logging.level", "OFF");  // OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
        properties.put("eclipselink.logging.level.sql", "INFO");
        properties.put("eclipselink.logging.parameters", "true");
        properties.put("eclipselink.logging.timestamp", "true");
        properties.put("eclipselink.logging.session", "true");
        properties.put("eclipselink.logging.thread", "true");
        properties.put("eclipselink.logging.exceptions", "true");

        // EL optimization, see: http://java-persistence-performance.blogspot.no/2011/06/how-to-improve-jpa-performance-by-1825.html
        properties.put("eclipselink.jdbc.cache-statements", "true");
        properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        properties.put("eclipselink.jdbc.batch-writing.size", "1000");
        properties.put("eclipselink.persistence-context.flush-mode", "commit");
        properties.put("eclipselink.persistence-context.close-on-commit", "true");
        properties.put("eclipselink.persistence-context.persist-on-commit", "false");
        properties.put("eclipselink.flush-clear.cache", "drop");

        // Add entity classes, Eclipselink
        properties.put("eclipselink.metadata-source", "XML");
        properties.put("eclipselink.metadata-source.xml.file", "META-INF/eclipselink-orm.xml");

        /*
        // Hibernate
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.default_batch_fetch_size", "16");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");

        properties.put("hibernate.connection.provider_class", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
        properties.put("hibernate.c3p0.min_size", "1");
        properties.put("hibernate.c3p0.max_size", "4");
        properties.put("hibernate.c3p0.acquire_increment", "2");
        properties.put("hibernate.c3p0.timeout", "500");
        properties.put("hibernate.c3p0.max_statements", "50");
        properties.put("hibernate.c3p0.idle_test_period", "1000");

        // Add entity classes, Hibernate
        properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, Arrays.asList(FeedTeller.class));
        */


        // Start db
        connection.properties(properties).start();

        // Create repositoty
        repository = new RepositoryJPA(connection);

        //
        connection.unitOfWork().begin();
        FeedTeller f = new FeedTeller("101", 10, 10);
        repository.persist(f);
        connection.unitOfWork().end();


    }

    public static void afterClass() {
        JpaDatabaseConnectionManager.removeConnection(PU_NAME);
    }

    public static void main(String[] args) throws Exception {
        beforeClass();
        connection.unitOfWork().begin();

        FeedTeller feedteller = repository.find(FeedTeller.class, "101");
        feedteller.teller = 9;
        repository.merge(feedteller);


        DataInputStream in = new DataInputStream(System.in);
        System.out.println("Initiell verdi: " + feedteller);
        System.out.println("Trykk enter når Feedteller2 er ferdig");
        in.readByte();
        connection.unitOfWork().end();

        connection.unitOfWork().begin();
        FeedTeller f = repository.find(FeedTeller.class, "101");
        System.out.println("Verdi etter at FeedTeller2 har kjørt: " + f);
        connection.unitOfWork().end();

        afterClass();
    }

}
