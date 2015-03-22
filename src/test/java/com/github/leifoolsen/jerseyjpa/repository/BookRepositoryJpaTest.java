package com.github.leifoolsen.jerseyjpa.repository;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.util.DomainPopulator;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.util.DatabasePopulator;
import com.github.leifoolsen.jerseyjpa.util.JpaDatabaseConnectionManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class BookRepositoryJpaTest {
    private static final Logger logger = LoggerFactory.getLogger(BookRepositoryJpaTest.class);

    private static final String PU_NAME = "jpa-example-hibernate";
    private static JpaDatabaseConnectionManager.JpaDatabaseConnection connection = JpaDatabaseConnectionManager.createConnection(PU_NAME);
    private static BookRepositoryJpa bookRepository;

    private static final String ISBN_VREDENS_DRUER = "9788253019727";
    private static final String ISBN_GUIDE_TO_MIDDLE_EARTH = "9780752495620";
    private static final String ISBN_TRAVELING_TO_INFINITY = "9781846883668";


    @BeforeClass
    public static void beforeClass() {
        // Configure PU
        Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:mymemdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");

        // Hibernate
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.default_batch_fetch_size", "16");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");

        properties.put("hibernate.connection.provider_class", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
        properties.put("hibernate.c3p0.min_size", "1");
        properties.put("hibernate.c3p0.max_size", "4");
        properties.put("hibernate.c3p0.acquire_increment", "2");
        properties.put("hibernate.c3p0.timeout", "500");
        properties.put("hibernate.c3p0.max_statements", "50");
        properties.put("hibernate.c3p0.idle_test_period", "1000");

        // Add entity classes, Hibernate
        properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, Arrays.asList(Publisher.class, Book.class));

        // Start db
        connection.properties(properties).start();

        // Create repositoty
        bookRepository = new BookRepositoryJpa(connection);

        // Pupulate DB
        DatabasePopulator.pupulateDb(connection);
    }

    @AfterClass
    public static void afterClass() {
        JpaDatabaseConnectionManager.removeConnection(PU_NAME);
    }

    @Before
    public void before() {
        connection.unitOfWork().begin();
    }

    @After
    public void after() {
        connection.unitOfWork().end();
    }

    @Test
    public void newBook() {
        Publisher publisher = bookRepository.findPublisherByCode(DomainPopulator.CAPPELEN_DAMM);
        assertNotNull(publisher);

        Book book = Book
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

        bookRepository.newBook(book);
        logger.debug("Book with Id: {} and ISBN: '{}' persisted", book.getId(), book.formattedISBN());

        Book persistedBook = bookRepository.findBook(book.getId());
        assertEquals(book.getId(), persistedBook.getId());
        assertEquals(publisher, book.getPublisher());
    }

    @Test
    public void updateBook() {
        final Book vredensDuer = bookRepository.findBookByISBN(ISBN_VREDENS_DRUER);
        assertNotNull(vredensDuer);
        assertNotNull(vredensDuer.getPublisher());

        final String wrongTitle = vredensDuer.getTitle();

        Book vredensDruer = Book.with(vredensDuer, true).title("Vredens druer").build();
        vredensDruer = bookRepository.updateBook(vredensDruer);

        // Book is managed! Both instances, 'vredensDuer' and 'vredensDruer',  vill update
        assertEquals(vredensDruer.getTitle(), vredensDuer.getTitle());

        logger.debug("Updated book title from: '{}' to: '{}'", wrongTitle, vredensDruer.getTitle());

        Book theBookWithTheCorrectedTitle=  bookRepository.findBookByISBN(ISBN_VREDENS_DRUER);
        assertNotEquals(wrongTitle, theBookWithTheCorrectedTitle.getTitle());
    }

    @Test
    public void deleteBook() {
        Book gtm = bookRepository.findBookByISBN(ISBN_GUIDE_TO_MIDDLE_EARTH);
        assertNotNull(gtm);
        String id = gtm.getId();

        bookRepository.deleteBook(gtm);
        logger.debug("Book with Id: {} and ISBN: '{}' deleted", id, gtm.formattedISBN());
        assertNull(bookRepository.findBook(id));
    }

    @Test
    public void deleteNonExistingBookShouldNotFail() {
        String id = "a-non-existing-id";
        bookRepository.deleteBook(id);
    }

    @Test
    public void shouldFindMoreThanTwoBooksByAuthorErlendLoe() {
        final String author = "Loe, Erlend";
        List<Book> books = bookRepository.findBooksByAuthor(author);
        logger.debug("Found {} books by author '{}'", books.size(), author);
        assertThat(books, hasSize(greaterThan(2)));
    }

    @Test
    public void shouldFindMoreThanOneBookByPublisherPicador() {
        final Publisher publisher = bookRepository.findPublisherByCode(DomainPopulator.PICADOR);
        assertNotNull(publisher);
        final List<Book> books = bookRepository.findBooksByPublisher(publisher);
        logger.debug("Found {} books by publisher '{}'", books.size(), publisher.getName());
        assertThat(books, hasSize(greaterThan(1)));
    }

    @Test
    public void shouldFindFourBooks() {
        final List<Book> books = bookRepository.findBooks(2, 4);
        assertThat(books, hasSize(4));
    }

    @Test
    public void shouldFindFivePublishers() {
        final List<Publisher> publishers = bookRepository.findPublishers(0, 5);
        assertThat(publishers, hasSize(5));
    }

    @Test
    public void shouldFindPublishersWithNameCappelen() {
        final List<Publisher> publishers = bookRepository.findPublishersByName("Cappelen");
        assertThat(publishers, hasSize(greaterThan(1)));
    }

    @Test
    public void createNewBookAndUpdateExistingBook() {

        final Publisher publisher = bookRepository.findPublisherByCode(DomainPopulator.WEIDENFELD);
        assertNotNull(publisher);

        Book aNewBook = Book.with("9780297871934")
            .title("Accidence Will Happen : The Non-Pedantic Guide to English Usage")
            .author("Kamm, Oliver")
            .publisher(publisher)
            .published(new GregorianCalendar(2015, 2, 12).getTime())
            .summary("Are standards of English alright - or should that be all right? To knowingly " +
                    "split an infinitive or not to? And what about ending a sentence with preposition, or for " +
                    "that matter beginning one with 'and'? We learn language by instinct, but good English, " +
                    "the pedants tell us, requires rules. Yet, as Oliver Kamm demonstrates, many of the purists' " +
                    "prohibitions are bogus and can be cheerfully disregarded. ACCIDENCE WILL HAPPEN is an " +
                    "authoritative and deeply reassuring guide to grammar, style and the linguistic conundrums " +
                    "we all face.")
            .build();

        Book b = bookRepository.createOrUpdateBook(aNewBook);
        assertThat(b.getVersion(), equalTo(0L));


        Book bookForUpdate = bookRepository.findBookByISBN(ISBN_TRAVELING_TO_INFINITY);
        assertNotNull(bookForUpdate);

        Book changedBook = Book
                .with(bookForUpdate, true)
                .published(new GregorianCalendar(2014, 12, 15).getTime())
                .build();

        b = bookRepository.createOrUpdateBook(changedBook);
        assertThat(b.getVersion(), greaterThan(0L));
    }
}