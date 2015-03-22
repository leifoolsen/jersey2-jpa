package com.github.leifoolsen.jerseyjpa.main;

import com.github.leifoolsen.jerseyjpa.rest.application.JerseyJpaApp;
import com.github.leifoolsen.jerseyjpa.embeddedjetty.JettyFactory;
import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;
import org.eclipse.jetty.server.Server;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class JettyStarter {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {

        int port = args.length >= 1 ? MoreObjects.firstNonNull(Ints.tryParse(args[0]), DEFAULT_PORT) : DEFAULT_PORT;

        Server server = new JettyFactory().port(port).build();
        JettyFactory.start(server);

        URI applicationURI = UriBuilder.fromUri(server.getURI()).path(JerseyJpaApp.APPLICATION_PATH).build();
        System.out.println(String.format("\nServer started with WADL available at "
                + "%s/application.wadl\nExample usage: %s/books\n\nHIT ENTER TO STOP SERVER ...",
                applicationURI.toString(), applicationURI.toString()));

        try {
            System.in.read();
        }
        finally {
            JettyFactory.stop(server);
        }
    }
}
