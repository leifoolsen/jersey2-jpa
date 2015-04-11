package com.github.leifoolsen.jerseyjpa.rest.api;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.embeddedjetty.JettyFactory;
import com.github.leifoolsen.jerseyjpa.rest.application.JerseyJpaApp;
import com.github.leifoolsen.jerseyjpa.rest.exception.ErrorMessage;
import com.github.leifoolsen.jerseyjpa.rest.interceptor.GZIPReaderInterceptor;
import com.github.leifoolsen.jerseyjpa.util.CollectionJson;
import com.github.leifoolsen.jerseyjpa.util.DomainPopulator;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
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
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BookResourceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ISBN_NOT_IN_DATABSE = "1111111111111";
    private static final String ISBN_TRAVELING_TO_INFINITY = "9781846883668";
    private static final String ISBN_VREDENS_DRUER = "9788253019727";
    private static final String ISBN_GUIDE_TO_MIDDLE_EARTH = "9780752495620";
    private static final String ISBN_DUPLICATE = "9781447279402";

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
    public void getBookByIsbnShouldReturn_OK() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_TRAVELING_TO_INFINITY)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));
        Book book = collectionJson.collection().item(0).unMarshalData(Book.class);
        assertEquals(ISBN_TRAVELING_TO_INFINITY, book.getISBN());

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void bookNotFoundShouldReturn_NOT_FOUND() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_NOT_IN_DATABSE)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), errorMessage.getResponseStatusCode());
    }

    @Test
    public void invalidIsbnShouldReturn_BAD_REQUEST() throws Exception {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("123456abc")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), errorMessage.getResponseStatusCode());
    }

    @Test
    public void publisherOfBookShouldReturn_OK() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_TRAVELING_TO_INFINITY)
                .path("publisher")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));
        Publisher publisher = collectionJson.collection().item(0).unMarshalData(Publisher.class);
        assertNotNull(publisher);

        //logger.debug(collectionJson.toString());
    }


    @Test
    public void newBookShouldReturn_CREATED() {

        final Publisher publisher = DomainPopulator.getPublishers().get(DomainPopulator.CAPPELEN_DAMM);
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
                .post(Entity.entity(book, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));
        Book b = collectionJson.collection().item(0).unMarshalData(Book.class);
        assertEquals(book.getISBN(), b.getISBN());

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void newBookWithDuplicateIsbnShouldReturn_CONFLICT() {

        final Book book = Book.with(ISBN_DUPLICATE)
                .title("The Guest Cat: DUPLICATE")
                .author("Hiraide, Takashi")
                .publisher(DomainPopulator.getPublishers().get(DomainPopulator.PICADOR))
                .published(new GregorianCalendar(2014, 8, 25).getTime())
                .translator("Selland, Eric")
                .summary("....")
                .build();

        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void newBookWithFormPostShouldReturn_CREATED() {
        Form form = new Form()
            .param("isbn", "9780297871934")
            .param("title", "Accidence Will Happen : The Non-Pedantic Guide to English Usage")
            .param("author", "Kamm, Oliver")
            .param("published", "2015-02-12")
            .param("translator", null)
            .param("summary", "Are standards of English alright - or should that be all right? To knowingly " +
                    "split an infinitive or not to? And what about ending a sentence with preposition, or for " +
                    "that matter beginning one with 'and'? We learn language by instinct, but good English, " +
                    "the pedants tell us, requires rules. Yet, as Oliver Kamm demonstrates, many of the purists' " +
                    "prohibitions are bogus and can be cheerfully disregarded. ACCIDENCE WILL HAPPEN is an " +
                    "authoritative and deeply reassuring guide to grammar, style and the linguistic conundrums " +
                    "we all face.")
            .param("publisher.code", "02978");

        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));
        Book b = collectionJson.collection().item(0).unMarshalData(Book.class);
        assertEquals(form.asMap().getFirst("isbn"), b.getISBN());

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void newBooktMissingRequiredFieldShouldReturn_BAD_REQUEST() {
        Form form = new Form()
            .param("isbn", "9780857520197")
            .param("author", "Watson, S. J.")
            .param("published", "2015-02-12")
            .param("translator", null)
            .param("summary", "The sensational new psychological thriller from ... ");

        final Response response = target
            .path(BookResource.RESOURCE_PATH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


    @Test
    public void newBookWithUnknownPublisherShouldReturn_BAD_REQUEST() {

        final Publisher nonExistingPublisher = new Publisher("22222", "Does not exist");
        final Book book = Book
                .with("9788202289331")
                .publisher(nonExistingPublisher)
                .title("A title")
                .author("Loe, Erlend")
                .published(new GregorianCalendar(2008, 0, 1).getTime())
                .summary("Lorem ipsum etc")
                .build();

        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateBookShouldReturn_OK() {
        Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_VREDENS_DRUER)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));

        Book bookToUpdate = collectionJson.collection().item(0).unMarshalData(Book.class);
        assertEquals(ISBN_VREDENS_DRUER, bookToUpdate.getISBN());


        Form form = new Form();
        Map<String, String> items = collectionJson.collection().items().get(0).nameValueItems();
        for (Map.Entry<String, String> entry : items.entrySet()) {
            form.param(entry.getKey(), entry.getValue());
        }
        form.asMap().remove("title");
        form.param("title", "Vredens druer");

        response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));
        Book b = collectionJson.collection().item(0).unMarshalData(Book.class);
        assertEquals(form.asMap().getFirst("isbn"), b.getISBN());

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void updateIsbnToDuplicateValueShouldReturn_CONFLICT() {
        Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_VREDENS_DRUER)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));
        assertEquals(ISBN_VREDENS_DRUER, collectionJson.collection().items().get(0).data("isbn").value());

        Form form = new Form();
        Map<String, String> items = collectionJson.collection().items().get(0).nameValueItems();
        for (Map.Entry<String, String> entry : items.entrySet()) {
            form.param(entry.getKey(), entry.getValue());
        }
        form.asMap().remove("isbn");
        form.param("isbn", ISBN_DUPLICATE);

        response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateBookWithNullIdShouldReturn_BAD_REQUEST() {
        Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_VREDENS_DRUER)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(1));

        assertEquals(ISBN_VREDENS_DRUER, collectionJson.collection().items().get(0).data("isbn").value());

        Form form = new Form();
        Map<String, String> items = collectionJson.collection().items().get(0).nameValueItems();
        for (Map.Entry<String, String> entry : items.entrySet()) {
            form.param(entry.getKey(), entry.getValue());
        }
        form.asMap().remove("id");
        form.param("id", null);

        response = target
                .path(BookResource.RESOURCE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


    @Test
    public void deleteBookShouldReturn_NO_CONTENT() {
        Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_GUIDE_TO_MIDDLE_EARTH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_GUIDE_TO_MIDDLE_EARTH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = target
                .path(BookResource.RESOURCE_PATH)
                .path(ISBN_GUIDE_TO_MIDDLE_EARTH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }


    @Test
    public void allBooksLimitToFiveItemsShouldReturn5() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .queryParam("offset", 5)
                .queryParam("limit", 5)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(5));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void searchAnyBookLimitToFiveItemsShouldReturn5() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/any")
                .queryParam("offset", 5)
                .queryParam("limit", 5)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(5));

        //logger.debug(collectionJson.toString());

    }

    @Test
    public void searchByIsbnShouldReturnAtLeastOneItem() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/isbn")
                .queryParam("q", "9788")
                .queryParam("offset", 0)
                .queryParam("limit", 5)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(greaterThan(0)));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void searchByPublisherNameShouldReturnAtLeastOneItem() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/publisher.name")
                .queryParam("q", "gyldendal")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(greaterThan(0)));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void searchByAuthorShouldReturnAtLeastOneItem() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/author")
                .queryParam("q", "Loe")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(greaterThan(0)));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void searchAnyBookWithTextHawkingShouldReturnAtLeastOneItem() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/any")
                .queryParam("q", "hawking")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), hasSize(greaterThan(0)));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void searchWithNotHitsShouldeturnEmptyItems() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/any")
                .queryParam("q", "Abby lane, binke bane, ole dole doff!")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().items(), is(empty()));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void paginate() {
        Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/any")
                .queryParam("offset", 0)
                .queryParam("limit", 5)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CollectionJson collectionJson = response.readEntity(CollectionJson.class);
        assertThat(collectionJson.collection().links(), hasSize(greaterThan(0)));
        assertThat(collectionJson.collection().links("next"), hasSize(greaterThan(0)));

        int count = 1;
        while(collectionJson.collection().links("next").size() > 0) {

            String s1 = target.getUri().toString();
            String s2 = collectionJson.collection().links("next").get(0).href();
            String s3 = Lists.newArrayList(Splitter.on(s1).omitEmptyStrings().trimResults().split(s2)).get(0);

            URI uri = URI.create(s3);
            Map<String, String> map = Splitter.on('&').omitEmptyStrings().withKeyValueSeparator('=').split(uri.getQuery());

            WebTarget t = target.path(uri.getPath());
            for (String key: map.keySet()) {
                t = t.queryParam(key, map.get(key));
            }
            response = t.request(MediaType.APPLICATION_JSON_TYPE).get();

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            collectionJson = response.readEntity(CollectionJson.class);

            count++;
        }

        assertThat(count, greaterThan(1));
        //logger.debug(collectionJson.toString());
    }


    @Test
    public void headerShouldContainContentEncodingGzipAndContentTypeUtf8() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("search/any")
                .queryParam("q", "hawking")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        List<Object> objects = response.getHeaders().get("Content-Encoding");
        assertTrue(objects != null && objects.contains("gzip"));

        objects = response.getHeaders().get("Content-Type");
        assertNotNull(objects);
        String s = objects.toString();
        assertThat(s, containsString("utf-8"));
    }

    @Test
    public void pingShouldReturnPong() {
        final Response response = target
                .path(BookResource.RESOURCE_PATH)
                .path("ping")
                .request(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String ping = response.readEntity(String.class);
        assertEquals(ping, "Pong!");
    }

}
