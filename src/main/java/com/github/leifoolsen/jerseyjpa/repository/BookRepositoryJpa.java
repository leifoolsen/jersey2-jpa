package com.github.leifoolsen.jerseyjpa.repository;

import com.github.leifoolsen.jerseyjpa.constraint.SearchType;
import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.github.leifoolsen.jerseyjpa.util.QueryParameter;
import com.github.leifoolsen.jerseyjpa.util.Repository;
import com.github.leifoolsen.jerseyjpa.util.RepositoryJPA;
import com.github.leifoolsen.jerseyjpa.util.StringUtil;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookRepositoryJpa {
    private static final Logger logger = LoggerFactory.getLogger(BookRepositoryJpa.class);

    private final Repository repository;

    public BookRepositoryJpa(final Provider<EntityManager> provider) {
        this.repository = new RepositoryJPA(provider);
    }

    public Book newBook(final Book book) {
        return repository.persist(book);
    }

    public Book updateBook(final Book book) {
        return repository.merge(book);
    }

    public Book createOrUpdateBook(final Book book) {
        return repository.createOrUpdate(book);
    }

    public Book findBook(final String id) {
        return repository.find(Book.class, id);
    }

    public void deleteBook(final Book book) {
        repository.remove(book);
    }

    public void deleteBook(final String id) {
        repository.remove(Book.class, id);
    }

    public List<Book> findBooks(final Integer offset, final Integer limit) {
        return repository.find(Book.class, offset, limit);
    }

    public Book findBookByISBN(final String isbn) {
        final String jpql = "select b from %s b where b.isbn = :isbn";
        final QueryParameter qp = QueryParameter.with("isbn", isbn);
        return RepositoryJPA.findFirstWithQuery(createQuery(jpql, Book.class, qp.parameters()));
    }

    public List<Book> findBooksBySearchType(
            final SearchType.Type searchType, final String searchValue, final Integer offset, final Integer limit) {

        String jpql = "select b from %s b ";
        QueryParameter qp = null;

        final String sv = StringUtil.blankToNull(searchValue);

        if(SearchType.Type.ANY == searchType && sv != null) {
            jpql += "where ";
            List<String> any = new ArrayList<>();
            for (SearchType.Type t : SearchType.Type.values()) {
                if(SearchType.Type.ANY != t ) {
                    any.add("LOWER(b." + t.type() + ") like :searchType ");
                }
            }
            jpql += Joiner.on("or ").join(any);
            qp = QueryParameter.with("searchType", "%" + sv.toLowerCase() + "%");
        }
        else if(sv != null) {
            jpql += "where LOWER(b." + searchType.type() + ") like :searchType ";
            qp = QueryParameter.with("searchType", "%" + sv.toLowerCase() + "%");
        }

        if(SearchType.Type.ANY != searchType) {
            jpql += "order by b." + searchType.type();
        }

        return RepositoryJPA.findWithQuery(
                createQuery(jpql, Book.class, (qp != null ? qp.parameters() : null)), offset, limit);
    }

    public Publisher findPublisherByCode(final String publisherCode) {
        final String jpql = "select p from %s p where p.code = :code";
        final QueryParameter qp = QueryParameter.with("code", publisherCode);
        return RepositoryJPA.findFirstWithQuery(createQuery(jpql, Publisher.class, qp.parameters()));
    }

    public List<Publisher> findPublishersByName(final String publisherName) {
        final String jpql = "select p from %s p where p.name like :name";
        final QueryParameter qp = QueryParameter.with("name", publisherName+"%");
        return RepositoryJPA.findWithQuery(createQuery(jpql, Publisher.class, qp.parameters()), null, null);
    }

    public List<Publisher> findPublishers (final Integer offset, final Integer limit) {
        return repository.find(Publisher.class, offset, limit);
    }

    private <T> TypedQuery<T> createQuery(final String queryString, final Class<T> entityClass, final Map<String, Object> parameters) {
        final String entityName = RepositoryJPA.entityName(entityClass);
        final String jpql = String.format(queryString, entityName);
        return repository.createQuery(jpql, entityClass, parameters);
    }
}
