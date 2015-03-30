package com.github.leifoolsen.jerseyjpa.rest.dto;

import com.github.leifoolsen.jerseyjpa.util.DateAdapter;

import javax.ws.rs.FormParam;

public class BookDTO {
    @FormParam("id")
    public String id;

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
}
