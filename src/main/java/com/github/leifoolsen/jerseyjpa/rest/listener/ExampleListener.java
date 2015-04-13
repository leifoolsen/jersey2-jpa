package com.github.leifoolsen.jerseyjpa.rest.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ExampleListener implements ServletContextListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Jersey uses java.util.logging. Bridge to slf4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        logger.debug("Servlet Context Listener initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.debug("Servlet Context Listener destroyed");
    }
}
