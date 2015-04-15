package com.github.leifoolsen.jerseyjpa.rest.api;

import com.github.leifoolsen.jerseyjpa.constraint.SearchType;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.util.CollectionJson;
import com.github.leifoolsen.jerseyjpa.util.DateAdapter;
import com.google.common.base.Preconditions;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CollectionJsonResourceHelper {

    private CollectionJsonResourceHelper() {}

    public static UriBuilder resourceRootUriBuilder(final UriInfo uriInfo) {
        URI baseUri = uriInfo.getBaseUri(); // e.g. http://example.org/api
        String path = BookResource.class.getAnnotation(Path.class).value();
        UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
        if(path != null) uriBuilder.path(path); // e.g. http://example.org/api/books
        return uriBuilder;
    }

    public static CollectionJson buildCollectionJson(final UriInfo uriInfo, final Book book) {
        CollectionJson collectionJson = buildCollectionJson(uriInfo);
        if(book != null) {
            collectionJson.collection().addItem(bookToCollectionJsonItem(uriInfo, book));
        }
        return collectionJson;
    }

    public static CollectionJson buildCollectionJson(final UriInfo uriInfo, final List<Book> books) {
        CollectionJson collectionJson = buildCollectionJson(uriInfo);

        for (Book book : books) {
            collectionJson.collection().addItem(bookToCollectionJsonItem(uriInfo, book));
        }
        return collectionJson;
    }

    public static CollectionJson buildCollectionJson(final UriInfo uriInfo, final Publisher publisher) {
        CollectionJson collectionJson = buildCollectionJson(uriInfo);
        if(publisher != null) {
            collectionJson.collection().addItem(publisherToCollectionJsonItem(uriInfo, publisher));
        }
        return collectionJson;
    }

    public static CollectionJson buildCollectionJson(final UriInfo uriInfo) {
        CollectionJson collectionJson = CollectionJson.newCollection(uriInfo.getRequestUri().toString());
        collectionJson.collection().addQueries(collectionJsonQueries(uriInfo));
        return collectionJson;
    }

    public static CollectionJson.Item bookToCollectionJsonItem(final UriInfo uriInfo, final Book book) {

        Preconditions.checkArgument(book!=null, "Book: Null value not allowed");

        UriBuilder uriBuilder = resourceRootUriBuilder(uriInfo);

        CollectionJson.Item item = new CollectionJson.Item(uriBuilder.clone().path(book.getISBN()).toString());

        item.addData("id", book.getId(), "Id")
                .addData("version", book.getVersion().toString(), "Version")
                .addData("isbn", book.getISBN(), "ISBN")
                .addData("title", book.getTitle(), "Title")
                .addData("author", book.getAuthor(), "Author")
                .addData("published", DateAdapter.dateToString(book.getPublished()), "Published")
                .addData("summary", book.getSummary(), "Summary")
                .addData("publisher.code", book.getPublisher().getCode(), "Publisher code");

        item.addLink("self", uriBuilder.clone().path(book.getISBN()).toString(), "This book")
                .addLink("publisher", uriBuilder.clone().path(book.getISBN()).path("publisher").build().toString(), "Publisher of book")
                .addLink("authorship", uriBuilder.clone().path("search/author").queryParam("q", book.getAuthor()).toString(), "Books by this author");

        return item;
    }

    public static CollectionJson.Item publisherToCollectionJsonItem(final UriInfo uriInfo, final Publisher publisher) {

        Preconditions.checkArgument(publisher!=null, "Publisher: Null value not allowed");

        UriBuilder builder = resourceRootUriBuilder(uriInfo).path("search");

        CollectionJson.Item item = new CollectionJson.Item(uriInfo.getRequestUriBuilder().toString());
        item.addData("id", publisher.getId(), "Id")
                .addData("version", publisher.getVersion().toString(), "Version")
                .addData("code", publisher.getCode(), "Code")
                .addData("name", publisher.getName(), "Name");

        item.addLink("self", uriInfo.getRequestUri().toString(), "This publisher")
                .addLink("books", builder
                        .clone().path(SearchType.Type.get("publisher.code").type())
                        .queryParam("q", publisher.getCode()).build().toString(), "Books by this publisher");

        return item;
    }


    public static List<CollectionJson.Query> collectionJsonQueries(final UriInfo uriInfo) {
        List<CollectionJson.Query> queryList = new ArrayList<>();

        UriBuilder builder = resourceRootUriBuilder(uriInfo).path("search");
        for (SearchType.Type t : SearchType.Type.values()) {

            CollectionJson.Query q = new CollectionJson.Query(
                    "search", builder.clone().path(t.type()).build().toString(), "Search " + t.type());
            q.addQueryData("q", "").addQueryData("offset", "").addQueryData("limit", "");
            queryList.add(q);
        }

        return queryList;
    }
}
