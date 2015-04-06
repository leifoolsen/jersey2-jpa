package com.github.leifoolsen.jerseyjpa.util;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JpaDatabaseConnectionManagerEclipselinkTest {

    private static final String PU_NAME = "jpa-example-eclipselink";
    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection;

    @BeforeClass
    public static void beforeClass() {
        // Configure PU //
        Properties properties = PersistenceProperties.createPropertiesForProvider(
                PersistenceProperties.ECLIPSELINK, null, Arrays.asList(Publisher.class, Book.class));

        // Start db
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
