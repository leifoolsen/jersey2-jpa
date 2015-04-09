package com.github.leifoolsen.jerseyjpa.rest.api;


import com.github.leifoolsen.jerseyjpa.embeddedjetty.JettyFactory;
import com.github.leifoolsen.jerseyjpa.rest.application.JerseyJpaApp;
import com.github.leifoolsen.jerseyjpa.rest.interceptor.GZIPReaderInterceptor;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class CollectionJsonResourceTest {

    private static Server server;
    private static WebTarget target;

    @BeforeClass
    public static void startServer() throws Exception {

        // Start the server
        server = new JettyFactory()
                .build();

        JettyFactory.start(server);

        // Create the client
        Client c = ClientBuilder.newClient();

        // Client interceptor to deflate GZIP'ed content on client side
        c.register(GZIPReaderInterceptor.class);

        target = c.target(server.getURI()).path(JerseyJpaApp.APPLICATION_PATH);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        JettyFactory.stop(server);
    }

    @Test
    public void foo() {
        final Response response = target
                .path("cj")
                .request("application/vnd.collection+json")
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

}
