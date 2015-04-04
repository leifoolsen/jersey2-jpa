package com.github.leifoolsen.jerseyjpa.domain;

import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Book {
    @Id
    @Column(length=36)
    private String id;

    @Version
    private Long version;

    @NotBlank
    @Size(min=13, max=13, message = "{excact.n.digits}")
    @Pattern(regexp = "[0-9]+", message = "{book.isbn.notvalid}")
    @Column(length = 13, unique = true)
    private String isbn;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String author;

    @Temporal(TemporalType.DATE)
    private Date published;

    private String translator;

    @Lob
    private String summary;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="publisher_id", nullable = false)
    private Publisher publisher;

    protected Book() {}
    
    private Book(Builder builder) {
        this.id = builder.id;
        this.version = builder.version;
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.published = builder.published;
        this.translator = builder.translator;
        this.summary = builder.summary;
        this.publisher = builder.publisher;
    }

    public String getId() { return id; }
    public Long getVersion() { return version; }
    public String getISBN() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Date getPublished() { return published; }
    public String getTranslator() { return translator; }
    public String getSummary() { return summary; }
    public Publisher getPublisher() { return publisher; }

    /**
     * <p>An ISBN number is divided into the following number segments:</p>
     * <ul>
     *     <li>EAN: (European article number) product code: the first three digits</li>
     *     <li>Group identifier: a single digit following the EAN product code that specifies the country or language in which the book is published</li>
     *     <li>Publisher prefix: a number that identifies a particular publisher within the preceding group</li>
     *     <li>Title identifier: a number that identifies a particular title or edition of a title issued by the preceding publisher</li>
     *     <li>Check digit: a single digit at the end of the ISBN that validates the accuracy of the ISBN</li>
     * </ul>
     * <p>More details about the ISBN number can be found e.g. here:</p>
     * <ul>
     *      <li>https://www.isbn-international.org/sites/default/files/ISBN%20Manual%202012%20-corr.pdf</li>
     *      <li>http://www.lac-bac.gc.ca/isn/041011-1020-e.html</li>
     *      <li>http://no.wikipedia.org/wiki/ISBN</li>
     *      <li>http://www.nb.no/Om-NB/Standardnummerering/ISBN/Om-ISBN</li>
     * </ul>
     *
     * @return the formatted ISBN number
     */
    public String formattedISBN() {
        return  isbn.substring(0, 3)  + "-" +
                isbn.substring(3, 4)  + "-" +
                isbn.substring(4, 9)  + "-" +
                isbn.substring(9, 12) + "-" +
                isbn.substring(12);
    }

    public String publisherCode() {
        return isbn.substring(3, 8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return isbn == null ? (book.isbn == null) : isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }

    @Override
    public String toString() {
        return isbn + ", " + title;
    }

    public static Builder with(final String isbn) { return new Builder(isbn); }

    public static Builder with(final Book source, final boolean copyId) {
        Builder b = new Builder(source.isbn)
                .title(source.title)
                .author(source.author)
                .published(source.published)
                .summary(source.summary)
                .publisher(source.publisher);

        if(copyId) {
            b.id(source.id).version(source.version);
        }
        return b;
    }

    public static class Builder {
        private String id  = UUID.randomUUID().toString();
        private Long version;
        private String isbn;
        private String title;
        private String author;
        private Date   published;
        private String translator;
        private String summary;
        private Publisher publisher;

        private static String blankToNull(final String value) {
            String s = MoreObjects.firstNonNull(value, "").trim();
            return s.length() > 0 ? s : null;
        }
        private Builder(final String isbn) {
            this.isbn = blankToNull(isbn);
        }
        public Builder id(final String id) {
            this.id = id;
            return this;
        }
        public Builder version(final Long version) {
            this.version = version;
            return this;
        }
        public Builder title(final String title) {
            this.title = blankToNull(title);
            return this;
        }
        public Builder author(final String author) {
            this.author = blankToNull(author);
            return this;
        }
        public Builder published(final Date published) {
            this.published = published;
            return this;
        }
        public Builder translator(final String translator) {
            this.translator = blankToNull(translator);
            return this;
        }
        public Builder summary(final String summary) {
            this.summary = blankToNull(summary);
            return this;
        }
        public Builder publisher(final Publisher publisher) {
            this.publisher = publisher;
            return this;
        }
        public Book build() { return new Book(this); }
    }
}
