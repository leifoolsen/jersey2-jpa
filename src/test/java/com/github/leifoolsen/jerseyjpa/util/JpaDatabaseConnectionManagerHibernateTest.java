package com.github.leifoolsen.jerseyjpa.util;

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

public class JpaDatabaseConnectionManagerHibernateTest {

    private static final String PU_NAME = "jpa-example-hibernate";
    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection;

    @BeforeClass
    public static void beforeClass() {

        Properties properties = new Properties();

        /*
        properties.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
        properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        */

        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");

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


        // Add entity classes used in test
        properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, Arrays.asList(Publisher.class));

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
