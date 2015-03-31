package com.github.leifoolsen.jerseyjpa.util;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Repository {
    EntityManager getEntityManager();

    <T> T persist(T entity);

    <T> Collection<T> persist(Collection<T> entities);

    <T> T merge(T entity);

    <T> Collection<T> merge(Collection<T> entities);

    <T> T createOrUpdate(T entity);

    <T> Collection<T> createOrUpdate(Collection<T> entities);

    <T> void remove(T entity);

    <T> void remove(Class<T> entityClass, Object id);

    <T> void remove(Collection<T> entities);

    <T> T getReference(Class<T> entityClass, Object id);

    <T> T find(Class<T> entityClass, Object id);

    <T> List<T> find(Class<T> entityClass);

    <T> List<T> find(Class<T> entityClass, Integer offset, Integer limit);

    <T> long count(Class<T> entityClass);

    void clear();

    <T> TypedQuery<T> createQuery(
            String jpql, Class<T> resultClass, Map<String, Object> parameters);

    <T> TypedQuery<T> createNamedQuery(
            String queryName, Class<T> resultClass, Map<String, Object> parameters);

    Query createNativeQuery(String sql, Map<String, Object> parameters);

    <T> Query createNativeQuery(
            String sql, Class<T> resultClass, Map<String, Object> parameters);

    Query createNativeQuery(
            String sql, String resultSetMapping, Map<String, Object> parameters);
}
