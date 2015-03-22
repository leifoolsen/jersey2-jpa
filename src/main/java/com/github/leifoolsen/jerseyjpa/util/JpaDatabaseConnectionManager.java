package com.github.leifoolsen.jerseyjpa.util;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>
 * A generic facade that provides easy access to a JPA persistence unit using
 * static methods.
 * </p>
 * <p>
 * This static class is designed so that it can be used with any JPA
 * application.
 * </p>
 * <p>
 * See: http://stackoverflow.com/questions/15071238/entitymanager-threadlocal-pattern-with-jpa-in-jse
 * See: http://stackoverflow.com/questions/3711439/how-to-create-a-thread-safe-entitymanagerfactory
 * See: http://naildrivin5.com/blog/2008/05/14/using-threadlocal-and-servlet-filters-to-cleanly-access-jpa-an-entitymanager.html
 * </p>
 */

public class JpaDatabaseConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(JpaDatabaseConnectionManager.class);
    private static final ConcurrentMap<String, JpaDatabaseConnection> connections = new ConcurrentHashMap();

    private JpaDatabaseConnectionManager() {}


    /**
     * <p>Create an EntityManagerProvider for the named persistence unit.</p>
     *
     * @param persistenceUnitName The name of the persistence unit
     * @return The provider
     */
    public static JpaDatabaseConnection createConnection(final String persistenceUnitName) {
        return createConnection(persistenceUnitName, null);
    }

    /**
     * <p>Create an EntityManagerProvider for the named persistence unit.</p>
     *
     * @param persistenceUnitName
     * @param properties
     * @return The provider
     */
    public static JpaDatabaseConnection createConnection(final String persistenceUnitName, final Properties properties) {
        JpaDatabaseConnection connection = connections.get(persistenceUnitName);
        if(connection == null) {
            synchronized (JpaDatabaseConnectionManager.class) {
                connection = new JpaDatabaseConnection(persistenceUnitName).properties(properties);
                JpaDatabaseConnection oldConnection = connections.putIfAbsent(persistenceUnitName, connection);
                if(oldConnection != null) {connection = oldConnection;}
            }
        }
        return connection;
    }

    /**
     * <p>Close an EntityManagerProvider for the named persistence unit.</p>
     *
     * @param persistenceUnitName The name of the persistence unit to remove
     *
     */
    public static void removeConnection(final String persistenceUnitName) {
        JpaDatabaseConnection connection = connections.get(persistenceUnitName);
        if(connection != null) {
            synchronized (JpaDatabaseConnectionManager.class) {
                connection.stop();
                connections.remove(persistenceUnitName);
            }
        }
    }


    public static class JpaDatabaseConnection implements UnitOfWork, Provider<EntityManager> {
        private volatile EntityManagerFactory entityManagerFactory;
        private final String persistenceUnitName;
        private final ThreadLocal<EntityManager> threadLocalEm = new ThreadLocal<EntityManager>();
        private Properties properties;

        private JpaDatabaseConnection(final String persistenceUnitName) {
            this.persistenceUnitName = persistenceUnitName;
        }
        
        public JpaDatabaseConnection properties(final Properties properties) {
            this.properties = properties;
            return this;
        }

        public synchronized void start() {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                logger.debug("Entity Manager Factory created for pu '{}'", persistenceUnitName);
            }
            catch(Throwable t) {
                logger.error("Entity Manager Factory creation failed", t);
                throw new ExceptionInInitializerError(t);
            }
        }

        public synchronized void stop() {
            end();
            if(entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
            logger.debug("Entity Manager Factory closed for pu '{}'", persistenceUnitName);
        }


        public UnitOfWork unitOfWork() {
            return this;
        }

        public Provider<EntityManager> provider() {
            return this;
        }

        /**
         * <p>
         * Provide a per-thread EntityManager "singleton" instance.
         * </p>
         * <p>
         * This method can be called as many times as needed per thread, and it will
         * return the same EntityManager instance, until the manager is closed.
         * </p>
         *
         * @return EntityManager singleton for this thread
         */
        @Override
        public EntityManager begin() {
            try {
                EntityManager em = threadLocalEm.get();
                Preconditions.checkState(em == null, "Entitymanager already created for this thread");
                em = entityManagerFactory.createEntityManager();
                threadLocalEm.set(em);
                logger.debug("Entity Manager created for pu '{}'", persistenceUnitName);
                return em;
            }
            catch(Throwable t) {
                logger.error("Entity Manager creation failed", t);
                throw new ExceptionInInitializerError(t);
            }
        }

        /**
         * <p>Close the EntityManager and set the thread's instance to null.</p>
         */
        @Override
        public void end() {
            EntityManager em = threadLocalEm.get();
            if(em != null) {
                try {
                    if (em.isOpen()) {
                        em.close();
                    }
                    logger.debug("Entity Manager closed for pu '{}'", persistenceUnitName);
                }
                finally {
                    threadLocalEm.remove();
                }
            }
        }

        /**
         * <p>
         * Provide a per-thread EntityManager "singleton" instance.
         * </p>
         * <p>
         * This method can be called as many times as needed per thread, and it will
         * return the same EntityManager instance, until the manager is closed.
         * </p>
         *
         * @return EntityManager singleton for this thread
         */
        @Override
        public EntityManager get() {
            EntityManager em = threadLocalEm.get();
            if (em == null || !em.isOpen()) {
                return begin();
            }
            return em;
        }
    }
}