package com.github.leifoolsen.jerseyjpa.rest.resource;

import com.github.leifoolsen.jerseyjpa.constraint.SearchType;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.repository.BookRepositoryJpa;
import com.github.leifoolsen.jerseyjpa.repository.DatabaseConnection;
import com.github.leifoolsen.jerseyjpa.rest.interceptor.Compress;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JpaDatabaseConnectionManager.JpaDatabaseConnection connection = DatabaseConnection.getConnection();
    private BookRepositoryJpa repository = new BookRepositoryJpa(connection);

    private UriInfo uriInfo;

    public SearchResource(@Context @NotNull UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        logger.debug(this.getClass().getSimpleName() + " created");
    }

    @GET
    @Compress
    public Response allBooks(
            @SearchType @PathParam("searchType") final String searchType,
            @QueryParam("q") final String searchValue,
            @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit) {

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone();

        if(searchValue != null) { uriBuilder.queryParam("q", searchValue); }
        if(offset      != null) { uriBuilder.queryParam("offset", offset); }
        if(limit       != null) { uriBuilder.queryParam("limit", limit); }

        List<Book> books = repository.findBooksBySearchType(SearchType.Type.get(searchType), searchValue, offset, limit);

        if(books.size() < 1) {
            return Response
                    .noContent()
                    .location(uriBuilder.build())
                    .build();
        }

        GenericEntity<List<Book>> entities = new GenericEntity<List<Book>>(books){};
        return Response
                .ok(entities)
                .location(uriBuilder.build())
                .build();
    }
}
