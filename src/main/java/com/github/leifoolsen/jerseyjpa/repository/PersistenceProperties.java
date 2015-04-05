package com.github.leifoolsen.jerseyjpa.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class PersistenceProperties {
    public static final String ECLIPSELINK = "eclipselink";
    public static final String HIBERNATE   = "hibernate";

    private static final Logger logger = LoggerFactory.getLogger(PersistenceProperties.class);

    private PersistenceProperties() {}

    /**
     * Propertes to use when creating an EntityManagerFactory instance
     * @param providerName eclipselink or hibernate
     * @param overridingProperties properties overriding default properties
     * @param entityClasses entity classes to load, not listed in persistence.xml or orm.xml
     * @return persistence properties for the given provider
     */
    public static Properties createPropertiesForProvider(
            final String providerName, final Properties overridingProperties, final List<?> entityClasses) {

        Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");

        if(ECLIPSELINK.equals(providerName)) {
            // Eclipse Link (EL)

            // eclipselink.ddl-generation: "create-tables", "create-or-extend-tables", "drop-and-create-tables", "none"
            //                        See: http://eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_ddl_generation.htm
            properties.put("eclipselink.ddl-generation", "drop-and-create-tables"); //
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

            // Eclipselink can not load entity classes dynamically.
            // Classes must be added to META-INF/eclipselink-orm.xml by hand :-(
            properties.put("eclipselink.metadata-source", "XML");
            properties.put("eclipselink.metadata-source.xml.file", "META-INF/eclipselink-orm.xml");

            if(entityClasses != null && entityClasses.size() > 0) {
                logger.warn("Eclipselink can not load entity classes dynamically. " +
                        "Ensure that all entity classes are contained in persistence.xml (or connected orm.xml)");
            }
        }
        else {
            // Hibernate
            properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

            // hibernate.hbm2ddl.auto: "validate", "update", "create", "create-drop"
            //                    See: http://hibernate.org/orm/documentation/
            properties.put("hibernate.hbm2ddl.auto", "create-drop"); //
            properties.put("hibernate.default_batch_fetch_size", "16");
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.format_sql", "true");

            /*
            p.put("hibernate.connection.autocommit", "false");
            p.put("hibernate.connection.release_mode", "on_close");
            p.put("hibernate.cache.use_second_level_cache", "false");
            p.put("hibernate.cache.use_query_cache", "false");
            p.put("org.hibernate.cacheable", "false");
            */

            /*
            p.put("hibernate.cache.use_second_level_cache", "true");
            p.put("hibernate.cache.provider_class", "net.sf.ehcache.hibernate.SingletonEhCacheProvider");
            p.put("net.sf.ehcache.configurationResourceName", "/ehcache.xml");
            */

            properties.put("hibernate.connection.pool_size", "10");
            properties.put("hibernate.connection.provider_class", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
            properties.put("hibernate.c3p0.min_size", "1");
            properties.put("hibernate.c3p0.max_size", "10");
            properties.put("hibernate.c3p0.acquire_increment", "2");
            properties.put("hibernate.c3p0.timeout", "500");
            properties.put("hibernate.c3p0.max_statements", "50");
            properties.put("hibernate.c3p0.idle_test_period", "1000");

            if(entityClasses != null && entityClasses.size() > 0) {
                // Add entity classes, Hibernate
                properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, entityClasses);
            }
        }

        Properties mergedProperties = new Properties();
        mergedProperties.putAll(properties);
        if(overridingProperties != null) {
            mergedProperties.putAll(overridingProperties);
        }

        return mergedProperties;
    }

}
