package com.github.leifoolsen.jerseyjpa.util;

import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import java.util.Properties;

public class JpaDatabaseConnectionManagerEclipselinkTest {

    private static final String PU_NAME = "jpa-example-eclipselink";
    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection;

    @BeforeClass
    public static void beforeClass() {

        Properties properties = new Properties();

        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");

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

        // Add entity classes used in test
        properties.put("eclipselink.metadata-source", "XML");
        properties.put("eclipselink.metadata-source.xml.file", "META-INF/eclipselink-orm.xml");

        //properties.put("eclipselink.logging.logger", "JavaLogger");

        connection = JpaDatabaseConnectionManager.createConnection(PU_NAME, properties);
        connection.start();
    }

    @AfterClass
    public static void afterClass() {
        JpaDatabaseConnectionManager.removeConnection(PU_NAME);
    }

    @Before
    public void before() {
        connection.unitOfWork().begin();
    }

    @After
    public void after() {
        connection.unitOfWork().end();
    }

    @Test
    public void testPersist() throws Exception {
        EntityManager em = connection.provider().get();
        assertTrue(em.isOpen());

        Publisher publisher = new Publisher(DomainPopulator.ALMA_BOOKS, "Alma books");

        em.getTransaction().begin();
        em.persist(publisher);
        em.flush();
        em.getTransaction().commit();

        Publisher persistedPublisher = em.find(Publisher.class, publisher.getId());
        assertNotNull(persistedPublisher);
    }
}
