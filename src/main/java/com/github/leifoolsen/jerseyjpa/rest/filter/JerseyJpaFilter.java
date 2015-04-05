package com.github.leifoolsen.jerseyjpa.rest.filter;

import com.github.leifoolsen.jerseyjpa.util.DatabaseConnection;
import com.github.leifoolsen.jerseyjpa.util.DatabasePopulator;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(
        urlPatterns = "/api/*",
        filterName = "JPA-Filter",
        description = "JPA Session in View Filter"
)
public class JerseyJpaFilter  implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private JpaDatabaseConnectionManager.JpaDatabaseConnection connection;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        connection = DatabaseConnection.createConnection();
        connection.start();

        // Prepopulate db - for demonstration only. TODO: Use e.g. staging
        DatabasePopulator.pupulateDb(connection);

        logger.debug("Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("Filtering request");
        connection.unitOfWork().begin();
        try {
            chain.doFilter(request, response);
        }
        finally {
            connection.unitOfWork().end();
        }
    }

    @Override
    public void destroy() {
        connection.stop();
        logger.debug("Filter destroyed");
    }
}
