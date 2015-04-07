package com.github.leifoolsen.jerseyjpa.rest.resource;

import com.github.leifoolsen.jerseyjpa.constraint.Isbn;
import com.github.leifoolsen.jerseyjpa.constraint.SearchType;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.exception.ApplicationException;
import com.github.leifoolsen.jerseyjpa.repository.BookRepositoryJpa;
import com.github.leifoolsen.jerseyjpa.rest.dto.BookDTO;
import com.github.leifoolsen.jerseyjpa.rest.interceptor.Compress;
import com.github.leifoolsen.jerseyjpa.util.DatabaseConnection;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import com.github.leifoolsen.jerseyjpa.util.StringUtil;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

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
        logger.debug(this.getClass().getSimpleName() + " created");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(final Book book) {
        return create(new BookDTO(book));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@BeanParam final BookDTO params) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone().path(params.isbn);
        Response.ResponseBuilder responseBuilder;

        Publisher publisher = lookupPublisher(params.publisherCode);
        Book bookToCreate = Book
                .with(params.isbn)
                .title(params.title)
                .author(params.author)
                .published(params.published != null ? params.published.getDate() : null)
                .translator(params.translator)
                .summary(params.summary)
                .publisher(publisher)
                .build();

        bookToCreate = repository.newBook(bookToCreate);
        responseBuilder = Response.created(uriBuilder.build()).entity(bookToCreate);
        logger.debug("Created book with ISBN: {}. Title: {}", bookToCreate.getISBN(), bookToCreate.getTitle());

        return responseBuilder.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(final Book book) {
        return update(new BookDTO(book));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@BeanParam final BookDTO params) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone().path(params.isbn);
        Response.ResponseBuilder responseBuilder;

        Publisher publisher = lookupPublisher(params.publisherCode);
        Book bookToUpdate = Book
                .with(params.isbn)
                .title(params.title)
                .author(params.author)
                .published(params.published != null ? params.published.getDate() : null)
                .translator(params.translator)
                .summary(params.summary)
                .publisher(publisher)
                .build(params.id, params.version);

        Book updatedBook = repository.updateBook(bookToUpdate);
        responseBuilder = Response.ok(updatedBook).location(uriBuilder.build());
        logger.debug("Updated book with ISBN: {} and title: {}", bookToUpdate.getISBN(), bookToUpdate.getTitle());

        return responseBuilder.build();
    }

    private Publisher lookupPublisher(final String publisherCode) {

        Publisher publisher = null;
        String p = StringUtil.blankToNull(publisherCode);
        if(p != null) {
            publisher = repository.findPublisherByCode(publisherCode);

            if (publisher == null) {
                throw new ApplicationException(Response.Status.BAD_REQUEST.getStatusCode(), null,
                        "Can not create or update book. Publisher with code " + publisherCode + " was not found", null);
            }
        }
        return publisher;
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
    @Path("{isbn}")
    public Book byIsbn(@Isbn @PathParam("isbn") final String isbn) {

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
        // Calling SearchResource directly is a hack, I think, but don't know how to do it otherwise
        // @GET'ing like this: "/books/search" will hit the byIsbn method,
        // so this method is needed to get all items without searchparams.
        return resourceContext.getResource(
                SearchResource.class).allBooks(SearchType.Type.ANY.type(), null, offset, limit);
    }

    @Path("search/{searchType}")
    public SearchResource search() {
        return resourceContext.getResource(SearchResource.class);
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("ping")
    public String ping() {
        return "Pong!"; // --> Response.Status.OK
    }
}
