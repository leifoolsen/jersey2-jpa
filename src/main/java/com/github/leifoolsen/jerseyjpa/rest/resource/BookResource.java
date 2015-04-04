package com.github.leifoolsen.jerseyjpa.rest.resource;

import com.github.leifoolsen.jerseyjpa.constraint.Isbn;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.exception.ApplicationException;
import com.github.leifoolsen.jerseyjpa.repository.BookRepositoryJpa;
import com.github.leifoolsen.jerseyjpa.repository.DatabaseConnection;
import com.github.leifoolsen.jerseyjpa.rest.dto.BookDTO;
import com.github.leifoolsen.jerseyjpa.rest.interceptor.Compress;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Singleton
@Path(BookResource.RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {
    public static final String RESOURCE_PATH = "books";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JpaDatabaseConnectionManager.JpaDatabaseConnection connection = DatabaseConnection.getConnection();
    private BookRepositoryJpa repository = new BookRepositoryJpa(connection);

    private UriInfo uriInfo; // actual uri info provided by parent resource (threadsafe)
    private ResourceContext resourceContext;

    public BookResource(@Context @NotNull UriInfo uriInfo, @Context @NotNull ResourceContext resourceContext) {
        this.uriInfo = uriInfo;
        this.resourceContext = resourceContext;
        logger.debug("Resource created");
    }

    @GET
    @Path("{isbn}")
    public Book byIsbn(
            @NotBlank
            @Isbn
            @PathParam("isbn") final String isbn) {

        final Book result = repository.findBookByISBN(isbn);

        if (result == null) {
            throw new WebApplicationException("Book with isbn: '"+ isbn + "' was not found",
                    Response.status(Response.Status.NOT_FOUND)
                            .location(uriInfo.getAbsolutePath())
                            .build()
            );
        }
        return result; // Response.Status.OK
        // return Response.Status.BAD_REQUEST if Bean validation fails
    }

    @GET
    @Compress
    public Response allBooks(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone();
        if(offset != null) {
            uriBuilder.queryParam("offset", offset);
        }
        if(limit != null) {
            uriBuilder.queryParam("limit", limit);
        }
        final List<Book> books = repository.findBooks(offset, limit);


        if(books.size()< 1) {
            return Response
                    .noContent()
                    .location(uriBuilder.build())
                    .build();
        }

        GenericEntity<List<Book>> entities = new GenericEntity<List<Book>>(books){};
        UriBuilder linkBuilder = uriInfo.getRequestUriBuilder().clone();
        return Response
                .ok(entities)
                .location(uriBuilder.build())
                        //.link(linkBuilder.queryParam("offset", 10).queryParam("limit", limit).build(), "prev")
                        //.link(linkBuilder.queryParam("offset", 20).queryParam("limit", limit).build(), "next")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@BeanParam final BookDTO params) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone().path(params.isbn);
        Response.ResponseBuilder responseBuilder;

        Publisher publisher = null;
        String p = blankToNull(params.publisherCode);
        if(p != null) {
            publisher = repository.findPublisherByCode(params.publisherCode);

            if (publisher == null) {
                throw new ApplicationException(Response.Status.BAD_REQUEST.getStatusCode(), null,
                        "Could not create book. Publisher with code "
                                + params.publisherCode + " was not found", null);
            }
        }

        Book result = repository.findBookByISBN(params.isbn);
        if(result != null) {
            throw new WebApplicationException(
                    "Could not create book with ISBN: '" + params.isbn + "'. Book already in database.",
                    Response.status(Response.Status.CONFLICT)
                            .location(uriInfo.getAbsolutePath())
                            .build());
        }

        result = Book.with(params.isbn)
                .title(params.title)
                .author(params.author)
                .published(params.published != null ? params.published.getDate() : null)
                .translator(params.translator)
                .summary(params.summary)
                .publisher(publisher)
                .build();

        result = repository.newBook(result);
        responseBuilder = Response.created(uriBuilder.build()).entity(result);
        logger.debug("Created book with ISBN: {}. Title: {}", result.getISBN(), result.getTitle());

        return responseBuilder.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdate(final Book book) {
        return createOrUpdate(new BookDTO(book));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createOrUpdate(@BeanParam final BookDTO params) {

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone().path(params.isbn);
        Response.ResponseBuilder responseBuilder;

        Publisher publisher = null;
        String p = blankToNull(params.publisherCode);
        if(p != null) {
            publisher = repository.findPublisherByCode(params.publisherCode);

            if (publisher == null) {
                throw new ApplicationException(Response.Status.BAD_REQUEST.getStatusCode(), null,
                        "Could not create book. Publisher with code "
                                + params.publisherCode + " was not found", null);
            }
        }

        Book.Builder builder = Book.with(params.isbn)
                .title(params.title)
                .author(params.author)
                .published(params.published != null ? params.published.getDate() : null)
                .translator(params.translator)
                .summary(params.summary)
                .publisher(publisher);

        Book result = repository.findBookByISBN(params.isbn);
        if(result != null) {
            result = repository.updateBook(builder.id(result.getId()).version(result.getVersion()).build());
            responseBuilder = Response.ok(result).location(uriBuilder.build());
            logger.debug("Updated book with ISBN: {}. Title: {}", result.getISBN(), result.getTitle());
        }
        else {
            result = repository.newBook(builder.build());
            responseBuilder = Response.created(uriBuilder.build()).entity(result);
            logger.debug("Created book with ISBN: {}. Title: {}", result.getISBN(), result.getTitle());
        }
        return responseBuilder.build();
    }

    @DELETE
    @Path("{isbn}")
    public void delete(@PathParam("isbn") @Isbn final String isbn) {
        Book book = repository.findBookByISBN(isbn);
        if(book != null) {
            repository.deleteBook(book);
            logger.debug("Book with isbn: '{}' deleted", isbn);
        }
        // void method returns Response.Status.NO_CONTENT
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("ping")
    public String ping() {
        return "Pong!"; // --> Response.Status.OK
    }


    private static String blankToNull(final String value) {
        String s = MoreObjects.firstNonNull(value, "").trim();
        return s.length() > 0 ? s : null;
    }

}
