package com.github.leifoolsen.jerseyjpa.application;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.ApplicationPath;

// The REST-application
@WebServlet(loadOnStartup = 1)
@ApplicationPath("/test-api/*")
public class JerseyJpaApp extends ResourceConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public JerseyJpaApp() {
        packages("com.github.leifoolsen.jerseyjpa");

        // Enable LoggingFilter & output entity.
        registerInstances(new LoggingFilter(java.util.logging.Logger.getLogger(JerseyJpaApp.class.getName()), true));
        
        logger.debug("Application '{}' initialized", getClass().getName());
    }
}
