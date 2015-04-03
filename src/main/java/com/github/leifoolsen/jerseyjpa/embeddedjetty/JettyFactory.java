package com.github.leifoolsen.jerseyjpa.embeddedjetty;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyFactory {

    private static final Logger logger = LoggerFactory.getLogger(JettyFactory.class);

    private String extraClasspath = null;
    private int port = 8080;

    public JettyFactory extraClasspath(final String extraClasspath) {
        this.extraClasspath = blankToNull(extraClasspath);
        return this;
    }
    
    public JettyFactory port(final int port) {
        this.port = port;
        return this;
    }

    public Server build() throws Exception {
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(8);
        threadPool.setMaxThreads(100);


        // The Server
        Server server = new Server(threadPool);

        // HTTP connector
        final String host="localhost";
        final int idleTimeoutInMsecs = 30000;

        ServerConnector http = new ServerConnector(server);
        http.setHost(host);
        http.setPort(port);
        http.setIdleTimeout(idleTimeoutInMsecs);

        // Set the connector
        server.addConnector(http);



        // The WebAppContext is the entity that controls the environment in
        // which a web application lives and breathes. In this example the
        // context path is being set to "/" so it is suitable for serving root
        // context requests and then we see it setting the location of the war.
        // A whole host of other configurations are available, ranging from
        // configuring to support annotation scanning in the webapp (through
        // PlusConfiguration) to choosing where the webapp will unpack itself.
        WebAppContext webapp = new WebAppContext();

        // Since we don't package the project as a war, we use our src/main/webapp directory
        // as the resource base directory for the server (will serve static content from the webapp directory)
        final String wardir = "src/main/webapp/";

        webapp.setResourceBase(wardir);
        webapp.setDescriptor(wardir + "WEB-INF/web.xml");
        webapp.setContextPath("/");

        // Configuration classes. This gives support for multiple features.
        // The annotationConfiguration is required to support annotations like @WebServlet
        // See: http://www.eclipse.org/jetty/documentation/current/configuring-webapps.html
        webapp.setConfigurations(new Configuration[]{
                new AnnotationConfiguration()     // Scan container and web app jars looking for @WebServlet, @WebFilter, @WebListener etc
                , new WebInfConfiguration()       // Extracts war, orders jars and defines classpath
                , new WebXmlConfiguration()       // Processes a WEB-INF/web.xml file
                , new MetaInfConfiguration()      // Looks in container and webapp jars for META-INF/resources and META-INF/web-fragment.xml
                , new FragmentConfiguration()     // Processes all discovered META-INF/web-fragment.xml files
                , new EnvConfiguration()          // Creates java:comp/env for the webapp, applies a WEB-INF/jetty-env.xml file
                , new PlusConfiguration()         // Processes JNDI related aspects of WEB-INF/web.xml and hooks up naming entries
                , new JettyWebXmlConfiguration()  // Processes a WEB-INF/jetty-web.xml file
        });

        // Important! make sure Jetty scans all classes under ./classes looking for annotations.
        String p = Joiner.on(";").skipNulls().join("./target/classes", extraClasspath);
        webapp.setExtraClasspath(p);

        // Parent loader priority is a class loader setting that Jetty accepts.
        // By default Jetty will behave like most web containers in that it will
        // allow your application to replace non-server libraries that are part of the
        // container. Setting parent loader priority to true changes this behavior.
        // Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        webapp.setParentLoaderPriority(true);

        // fail if the web app does not deploy correctly
        webapp.setThrowUnavailableOnStartupException(true);

        // disable directory listing
        // context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        // A WebAppContext is a ContextHandler as well so it needs to be set to
        // the server so it is aware of where to send the appropriate requests.
        server.setHandler(webapp);

        return server;
    }

    private static String blankToNull(final String value) {
        String s = MoreObjects.firstNonNull(value, "").trim();
        return s.length() > 0 ? s : null;
    }

    /**
     * Start embedded Jetty server.
     * @throws Exception
     */
    public static void start(final Server server) throws Exception {
        logger.debug("Starting Jetty ...");

        server.start();
        //server.dump(System.err);

        logger.info("Jetty started at: " + server.getURI());
    }

    /**
     * Start embedded Jetty server and wait until the server is done executing.
     * @throws Exception
     */
    public static void startAndWait(final Server server) throws Exception {
        start(server);
        try {
            // The use of server.join() will make the current thread join and
            // wait until the server is done executing.
            // See: http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
            server.join();
        }
        finally {
            stop(server);
        }
    }

    /**
     * Stops embedded Jetty server.
     * @throws Exception
     */
    public static void stop(final Server server) throws Exception {
        logger.info("Stopping Jetty at: " + server.getURI());
        server.stop();
        logger.debug("Jetty stopped!");
    }

}
