package com.github.leifoolsen.jerseyjpa.resource;

import com.github.leifoolsen.jerseyjpa.embeddedjetty.JettyFactory;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.util.DomainPopulator;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BookResourceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ISBN_NOT_IN_DATABSE = "1111111111111";
    private static final String ISBN_TRAVELING_TO_INFINITY = "9781846883668";
    private static final String ISBN_VREDENS_DRUER = "9788253019727";

    private static Server server;
    private static WebTarget target;

    @BeforeClass
    public static void startServer() throws Exception {

        // Start the server
        server = new JettyFactory()
                .extraClasspath("./target/test-classes/com/github/leifoolsen/jerseyjpa")
                .build();

        JettyFactory.start(server);

        // Create the client
        Client c = ClientBuilder.newClient();
        target = c.target(server.getURI()).path("test-api");
    }

    @AfterClass
    public static void stopServer() throws Exception {
        JettyFactory.stop(server);
    }

    @Test
    public void getBookByIsbnShouldReturn_OK() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_TRAVELING_TO_INFINITY)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Book book = response.readEntity(Book.class);
        assertEquals(ISBN_TRAVELING_TO_INFINITY, book.getISBN());
    }

    @Test
    public void bookNotFoundShouldReturn_NOT_FOUND() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_NOT_IN_DATABSE)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void invalidIsbnShouldReturn_BAD_REQUEST() throws Exception {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("123456abc")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getFiveBooksShouldReturn_OK() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .queryParam("offset", 0)
                .queryParam("limit", 5)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final List<Book> result = response.readEntity(new GenericType<List<Book>>() {});
        assertThat(result, hasSize(5));
    }


    @Test
    public void newBookShouldReturn_CREATED() {

        final Publisher publisher = new Publisher(DomainPopulator.CAPPELEN_DAMM, "Cappelen Damm");
        final Book book = Book
            .with("9788202289331")
            .publisher(publisher)
            .title("Kurtby")
            .author("Loe, Erlend")
            .published(new GregorianCalendar(2008, 1, 1).getTime())
            .summary("Kurt og gjengen er på vei til Mummidalen da Kurt sovner ved rattet og trucken havner " +
                    "i en svensk elv. Et langt stykke nedover elva ligger Kurtby - et lite samfunn hvor en " +
                    "dame som heter Kirsti Brud styrer og steller i samråd med Den hellige ånd. Det går " +
                    "ikke bedre enn at Kurt havner på kjøret, nærmere bestemt på Jesus-kjøret. " +
                    "Så blir han pastor og går bananas.")
            .build();

        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(book, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateBookShouldReturn_OK() {

    }


    @Test
    public void pingShouldReturnPong() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("ping")
                .request(MediaType.TEXT_PLAIN)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String ping = response.readEntity(String.class);
        assertEquals(ping, "Pong!");
    }

    /*
    @Test
    public void getApplicationWadl() throws Exception {
        final Response response = target
                .path("application.wadl")
                .request(MediaType.APPLICATION_XML)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
    */
}
