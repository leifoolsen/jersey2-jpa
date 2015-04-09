package com.github.leifoolsen.jerseyjpa.rest.dto;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.util.DateAdapter;

import javax.ws.rs.FormParam;
import java.util.Date;

public class BookDTO {
    @FormParam("id")
    public String id;

    @FormParam("version")
    public Long version;

    @FormParam("isbn")
    public String isbn;

    @FormParam("title")
    public String title;

    @FormParam("author")
    public String author;

    @FormParam("published")
    public DateAdapter published;

    @FormParam("translator")
    public String translator;

    @FormParam("summary")
    public String summary;

    @FormParam("publisher-code")
    public String publisherCode;

    public BookDTO() {}
    public BookDTO(final Book book) {
        if(book != null) {
            this.id(book.getId())
                .version(book.getVersion())
                .isbn(book.getISBN())
                .title(book.getTitle())
                .author(book.getAuthor())
                .published(book.getPublished())
                .translator(book.getTranslator())
                .summary(book.getSummary())
                .publisherCode(book.getPublisher() != null ? book.getPublisher().getCode() : null) ;
        }
    }
    public BookDTO id(final String id) { this.id = id; return this; }
    public BookDTO version(final Long version) { this.version = version; return this; }
    public BookDTO isbn(final String isbn) { this.isbn = isbn; return this; }
    public BookDTO title(final String title) { this.title = title; return this; }
    public BookDTO author(final String author) { this.author = author; return this; }
    public BookDTO published(final String published) { this.published = new DateAdapter(published); return this; }
    public BookDTO published(final Date published) { this.published = new DateAdapter(published); return this; }
    public BookDTO translator(final String translator) { this.translator = translator; return this; }
    public BookDTO summary(final String summary) { this.summary = summary; return this; }
    public BookDTO publisherCode(final String publisherCode) { this.publisherCode = publisherCode; return this; }
}
