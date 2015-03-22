package com.github.leifoolsen.jerseyjpa.embeddedjetty;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EmbeddedJettyTest {
    private static Server server;

    @BeforeClass
    public static void startServer() throws Exception {

        // Start the server
        server = new JettyFactory().build();
        JettyFactory.start(server);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        JettyFactory.stop(server);
    }

    @Test
    public void testServer() throws Exception {
        assertTrue(server.isStarted());
        assertTrue(server.isRunning());
    }
}
