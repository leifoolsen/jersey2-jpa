package com.github.leifoolsen.jerseyjpa.rest.api;

import com.github.leifoolsen.jerseyjpa.constraint.Isbn;
import com.github.leifoolsen.jerseyjpa.constraint.SearchType;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.exception.ApplicationException;
import com.github.leifoolsen.jerseyjpa.repository.BookRepositoryJpa;
import com.github.leifoolsen.jerseyjpa.rest.dto.BookDTO;
import com.github.leifoolsen.jerseyjpa.rest.interceptor.Compress;
import com.github.leifoolsen.jerseyjpa.util.*;
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
        logger.debug("Updated book with ISBN: {} and title: {}", bookToUpdate.getISBN(), bookToUpdate.getTitle());
        responseBuilder = Response.ok(updatedBook).location(uriBuilder.build());
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
    public Response byIsbn(@Isbn @PathParam("isbn") final String isbn) {

        final Book book = repository.findBookByISBN(isbn);
        if (book == null) {
            throw new WebApplicationException("Book with isbn: '"+ isbn + "' was not found",
                    Response.status(Response.Status.NOT_FOUND)
                            .location(uriInfo.getAbsolutePath())
                            .build()
            );
        }

    /*
    {
        "collection": {
            "version": "1.0",
            "href": "http://api.example.org/books",
            "items": [
                {
                    "href": "http://api.example.org/books/9781846883668",
                    "data": [
                          { "name": "id",        "value": "c814760e-7b2a-4623-93c3-48689260599b", "prompt": "Identifier" },
                          { "name": "version",   "value": "1": "Version" },
                          { "name": "isbn",      "value": "9781846883668", "prompt": "ISBN" },
                          { "name": "title",     "value": "title":"Travelling to Infinity: The True Story", "prompt": "Title" },
                          { "name": "author",    "value": "Hawking, Jane", "prompt": "Author" },
                          { "name": "published", "value": ""2014-12-18T00:00:00+01", "prompt": "Date published" },
                          { "name": "summary",   "value": "Soon to be a major motion picture starring...", "prompt": "Summary" }
                    ],
                    "links" : [
                      {"rel" : "publisher",  "href" : "http://examples.org/books/9781846883668/publisher", "prompt" : "Publisher"},
                      {"rel" : "authorship", "href" : "http://examples.org/books/search/author?q=Hawking, Jane", "prompt" : "Books by this author"}
                    ]
                }
            ]
        }
    }
     */

        String resourcePath = uriInfo.getRequestUriBuilder().toString().replace("9781846883668", "");
        CollectionJson collectionJson = CollectionJson.newCollection("1.0", resourcePath);
        CollectionJson.Collection collection = collectionJson.collection();

        CollectionJson.Item item = new CollectionJson.Item(uriInfo.getRequestUriBuilder().toString());
        item.addData("id",         book.getId(), "Id")
            .addData("version",    book.getVersion().toString(), "Version")
            .addData("isbn",       book.getISBN(), "ISBN")
            .addData("title",      book.getTitle(), "Title")
            .addData("author",     book.getAuthor(), "Author")
            .addData("published",  DateTimeAdapter.dateToString(book.getPublished()), "Published")
            .addData("summary", book.getSummary(), "Summary");
        item.addLink("publisher",  uriInfo.getRequestUriBuilder().path("publisher").build().toString(), "Publisher")
            .addLink("authorship", resourcePath + "search/author?q=" + book.getAuthor(), "Books by this author");

        collection.addItem(item);


        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone();
        Response.ResponseBuilder responseBuilder = Response.ok(book).location(uriBuilder.build());
        return responseBuilder.build();

    }

    @GET
    @Path("{isbn}/publisher")
    public Publisher bookPublisher(@Isbn @PathParam("isbn") final String isbn) {
        final Book book = repository.findBookByISBN(isbn);
        return book.getPublisher();
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
