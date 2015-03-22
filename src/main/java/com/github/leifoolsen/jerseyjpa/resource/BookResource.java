package com.github.leifoolsen.jerseyjpa.resource;

import com.github.leifoolsen.jerseyjpa.application.Compress;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.repository.BookRepositoryJpa;
import com.github.leifoolsen.jerseyjpa.repository.DatabaseConnection;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public BookResource(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        logger.debug("Resource created");
    }


    @GET
    @Path("{isbn}")
    public Book byIsbn(
            @NotNull
            @Size(min = 13, max = 13)
            @Pattern(regexp = "\\d+", message = "ISBN must be a valid number")
            @PathParam("isbn") final String isbn) {

        final Book result;
        try {
            result = repository.findBookByISBN(isbn);
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        if (result == null) {
            logger.debug(("Book with isbn: '{}' not found"), isbn);
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .location(uriInfo.getAbsolutePath())
                            .build()
            );
        }
        return result; // ==>  Response.Status.OK
        // return Response.Status.BAD_REQUEST if Bean validation fails
    }

    @Compress
    @GET
    public Response allBooks(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone();
        if(offset != null) {
            uriBuilder.queryParam("offset", offset);
        }
        if(limit != null) {
            uriBuilder.queryParam("limit", limit);
        }
        final List<Book> books;
        try {
            books = repository.findBooks(offset, limit);
        }
        catch(Exception e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }


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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdate(final Book book) {

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone().path(book.getISBN());
        Response.ResponseBuilder responseBuilder;

        try {
            Book result;
            Publisher publisher = repository.findPublisherByCode(book.getPublisher().getCode());
            if(publisher == null) {
                throw new EntityNotFoundException("Publisher " + book.getPublisher().getCode() + " not found");
            }

            if(repository.findBook(book.getId()) != null) {
                result = repository.updateBook(Book.with(book, true).publisher(publisher).build());
                responseBuilder = Response.ok(result).location(uriBuilder.build());
            }
            else {
                result = repository.newBook(Book.with(book, false).publisher(publisher).build());
                responseBuilder = Response.created(uriBuilder.build()).entity(result);
            }
        }
        catch(Exception e) {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).location(uriBuilder.build());
        }

        return responseBuilder.build();
    }


    /*
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdate(final List<Book>books) {
        return Response.serverError().build();
    }
    */


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("ping")
    public String ping() {
        return "Pong!"; // --> Response.Status.OK
    }



    public static class DateAdapter {
        private Date date;

        public DateAdapter(String date){
            this.date = getDateFromString(date);
        }

        public Date getDate(){
            return this.date;
        }

        public static Date getDateFromString(String dateString) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = df.parse(dateString);
                return date;
            } catch (ParseException e) {
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = df.parse(dateString);
                    return date;
                } catch (ParseException e2) {
                    //TODO: throw WebApplicationException ...
                    return null;
                }
            }
        }
    }


}
