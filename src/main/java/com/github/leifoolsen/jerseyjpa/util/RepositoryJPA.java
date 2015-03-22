package com.github.leifoolsen.jerseyjpa.util;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RepositoryJPA {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryJPA.class);

    private final Provider<EntityManager> provider;

    @Inject
    public RepositoryJPA(final Provider<EntityManager> provider) {
        this.provider = provider;
    }

    public EntityManager getEntityManager() {
        return provider.get();
    }

    public <T> T persist(final T entity) {
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        try {
            em.persist(entity);
            em.flush();
        }
        catch (Exception e) {
            if(!transactionAlreadyStarted) {
                em.getTransaction().rollback();
            }
            throw e;
        }
        if(!transactionAlreadyStarted) {
            em.getTransaction().commit();
        }
        return entity;
    }

    public <T> Collection<T> persist(final Collection<T> entities) {
        final Collection<T> result = Lists.newArrayList();
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        for(T entity : entities) {
            try {
                em.persist(entity);
                em.flush();
                result.add(entity);
            }
            catch (Exception e) {
                if(!transactionAlreadyStarted) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        if(!transactionAlreadyStarted) {
            em.getTransaction().commit();
        }
        return result;
    }

    public <T> T merge(final T entity) {
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        try {
            T result = em.merge(entity);
            em.flush();
            return result;
        }
        catch (Exception e) {
            if(!transactionAlreadyStarted) {
                em.getTransaction().rollback();
            }
            transactionAlreadyStarted = true;  // block commit in finally block
            throw e;
        }
        finally {
            if(!transactionAlreadyStarted) {
                em.getTransaction().commit();
            }
        }
    }

    public <T> Collection<T> merge(final Collection<T> entities) {
        final Collection<T> result = Lists.newArrayList();
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        for(T entity : entities) {
            try {
                T e = em.merge(entity);
                em.flush();
                result.add(e);
            }
            catch (Exception e) {
                if(!transactionAlreadyStarted) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        if(!transactionAlreadyStarted) {
            em.getTransaction().commit();
        }
        return result;
    }

    public <T> T createOrUpdate(final T entity) {
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        try {
            T result = entity;
            Member member = getMemberAnnotadedWithId(entity.getClass());
            Object id = getMemberValue(member, entity);
            String logMsg = "Entity '{}'  with id '{}' ";

            if(em.find(entity.getClass(), id) != null) {
                result = em.merge(entity);
                logMsg += "updated";
            }
            else {
                em.persist(entity);
                logMsg += "created";
            }
            em.flush();
            if(logger.isDebugEnabled()) logger.debug(logMsg, entityName(entity.getClass()), id);
            return result;
        }
        catch (Exception e) {
            if(!transactionAlreadyStarted) {
                em.getTransaction().rollback();
            }
            transactionAlreadyStarted = true;  // block commit in finally block
            throw e;
        }
        finally {
            if(!transactionAlreadyStarted) {
                em.getTransaction().commit();
            }
        }
    }

    public <T> Collection<T> createOrUpdate(final Collection<T> entities) {
        final Collection<T> result = Lists.newArrayList();
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        for(T entity : entities) {
            try {
                T e = entity;
                Member member = getMemberAnnotadedWithId(entity.getClass());
                Object id = getMemberValue(member, entity);
                if(em.find(entity.getClass(), id) != null) {
                    e = em.merge(entity);
                }
                else {
                    em.persist(entity);
                }
                em.flush();
                result.add(e);
            }
            catch (Exception e) {
                if(!transactionAlreadyStarted) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        if(!transactionAlreadyStarted) {
            em.getTransaction().commit();
        }
        return result;
    }

    public <T> void remove(final T entity) {
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        try {
            em.remove(entity);
            em.flush();
        }
        catch (Exception e) {
            if(!transactionAlreadyStarted) {
                em.getTransaction().rollback();
            }
            transactionAlreadyStarted = true;  // block commit in finally block
            throw e;
        }
        finally {
            if(!transactionAlreadyStarted) {
                em.getTransaction().commit();
            }
        }
    }

    public <T> void remove(final Class<T> entityClass, final Object id) {
        EntityManager em = getEntityManager();
        final T entity = em.find(entityClass, id);
        if(entity != null) {
            boolean transactionAlreadyStarted = em.isJoinedToTransaction();
            if (!transactionAlreadyStarted) {
                em.getTransaction().begin();
            }
            try {
                em.remove(entity);
                em.flush();
            }
            catch (EntityNotFoundException e) {
            }
            catch (Exception e) {
                if (!transactionAlreadyStarted) {
                    em.getTransaction().rollback();
                }
                transactionAlreadyStarted = true;  // block commit in finally block
                throw e;
            }
            finally {
                if (!transactionAlreadyStarted) {
                    em.getTransaction().commit();
                }
            }
        }
        else {
            logger.debug("Nothing to remove. Entity: '{}' with id: '{}' was not found.", entityClass.getSimpleName(), id);
        }
    }

    public <T> void remove(final Collection<T> entities) {
        EntityManager em = getEntityManager();
        boolean transactionAlreadyStarted = em.isJoinedToTransaction();
        if(!transactionAlreadyStarted) {
            em.getTransaction().begin();
        }
        for(T entity : entities) {
            try {
                em.remove(entity);
                em.flush();
            }
            catch (Exception e) {
                if(!transactionAlreadyStarted) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        if(!transactionAlreadyStarted) {
            em.getTransaction().commit();
        }
    }

    /**
     * Note: getReference returns a PROXY. Use getReference with care!!
     */
    public <T> T getReference(final Class<T> entityClass, final Object id) {

        T entity = getEntityManager().getReference(entityClass, id);

        // EntityNotFoundException is not thrown as expected
        // Looks like the exception is embedded in the proxy!!
        Member idMember = getMemberAnnotadedWithId(entityClass);
        Object idValue = getMemberValue(idMember, entity);

        if(idValue == null) {
            throw new EntityNotFoundException("Entity " + entityClass.getName() + " not found for id: " + id);
        }
        return entity;
    }

    public <T> T find(final Class<T> entityClass, final Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public <T> List<T> find(final Class<T> entityClass) {
        return find(entityClass, null, null);
    }

    public <T> List<T> find(final Class<T> entityClass, final Integer offset, final Integer limit) {
        final String entityName = entityName(entityClass);
        TypedQuery<T> query = getEntityManager()
                .createQuery(String.format("select e from %s e", entityName), entityClass);

        return findWithQuery(query, offset, limit);
    }

    /**
     * WARNIG! Not an efficient count-query. Use this as a template to create your own count.
     *  e.g.; "select count(e.id) from MyEntity e"
     */
    public <T> long count(final Class<T> entityClass) {
        final String entityName = entityName(entityClass);
        final Long result = (Long) getEntityManager()
                .createQuery("select count(*) from " + entityName)
                .getSingleResult();

        return result.intValue();
    }

    public void clear() {
        getEntityManager().clear();
    }

    public <T> TypedQuery<T> createQuery(final String jpql, final Class<T> resultClass, final Map<String, Object> parameters) {
        final TypedQuery<T> query = getEntityManager().createQuery(jpql, resultClass);
        addQueryParameters(query, parameters);
        return query;
    }

    public <T> TypedQuery<T> createNamedQuery(final String queryName, final Class<T> resultClass, final Map<String, Object> parameters) {
        final TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, resultClass);
        addQueryParameters(query, parameters);
        return query;
    }

    public <T> Query createNativeQuery(final String sql, final Class<T> resultClass, final Map<String, Object> parameters) {
        final Query query = getEntityManager().createNativeQuery(sql, resultClass);
        addQueryParameters(query, parameters);
        return query;
    }

    public Query createNativeQuery(final String sql, final String resultSetMapping, final Map<String, Object> parameters) {
        final Query query = getEntityManager().createNativeQuery(sql, resultSetMapping);
        addQueryParameters(query, parameters);
        return query;
    }


    // Helper methods
    public static <T> List<T> findWithQuery(final TypedQuery<T> query, final Integer offset, final Integer limit) {
        if(MoreObjects.firstNonNull(offset, 0) > 0) {
            query.setFirstResult(offset);
        }
        if(MoreObjects.firstNonNull(limit, 0)  > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    public static <T> T findFirstWithQuery(final TypedQuery<T> query) {
        List<T> result = query.setMaxResults(1).getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

    public static <T> List<T> findWithQuery(final Query query, final Integer offset, final Integer limit) {
        if(MoreObjects.firstNonNull(offset, 0) > 0) {
            query.setFirstResult(offset);
        }
        if(MoreObjects.firstNonNull(limit, 0)  > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    public static <T> T findFirstWithQuery(final Query query) {
        List<T> result = query.setMaxResults(1).getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

    public static void addQueryParameters(final Query query, final Map<String, Object> parameters) {
        if (parameters != null) {
            final Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
            for (final Map.Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    public static <T> boolean isEntity(final Class<T> entityClass) {
        return entityClass.isAnnotationPresent(Entity.class);
    }

    public static <T> String entityName(final Class<T> entityClass) {
        final Entity entity = entityClass.getAnnotation(Entity.class);
        final String entityName = entity.name();
        return MoreObjects.firstNonNull(entityName, "").trim().length() > 0 ? entityName : entityClass.getSimpleName();
    }


    private static final ConcurrentMap<String, Member> entityIdCache = new ConcurrentHashMap<String, Member>();

    public static <T> Member getMemberAnnotadedWithId(final Class<T> entityClass) {
        Member member = entityIdCache.get(entityClass.getName());
        if(member == null) {
            List<Member> m = findMembersAnnotatedWith(entityClass, Id.class, EmbeddedId.class);
            if(m.size() > 1) {
                throw new IllegalStateException("Multile @Id annotations are not supported by this repository. " +
                            "Use @EmbeddedId to represent a composite primary key!");
            }
            else if(m.size() < 1) {
                throw new IllegalStateException("Entity without @Id annotation is not supported by this repository.");
            }
            member = m.get(0);
            entityIdCache.putIfAbsent(entityClass.getName(), member);
        }
        return member;
    }


    // TODO: Make fluent
    // e.g: Members.ofType(Field.class).and(Method.class).annotatedWith(Id.class).or(Foo.class).in(MyClass.class).get();
    public static <T> List<Member> findMembersAnnotatedWith(
            final Class<T> target, final Class<? extends Annotation>... annotations ) {

        final List<Member> members = new ArrayList<Member>();
        for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Member[] m = ObjectArrays.concat(clazz.getDeclaredFields(), clazz.getDeclaredMethods(), Member.class);
            for (final Member member : m) {
                for(Class<? extends Annotation> a : annotations) {
                    if (member instanceof Field && ((Field) member).isAnnotationPresent(a)) {
                        members.add(member);
                    }
                    else if (member instanceof Method && ((Method) member).isAnnotationPresent(a)) {
                        members.add(member);
                    }
                }
            }
        }
        return members;
    }

    // TODO: Use e.g. fest-reflect
    public static Object getMemberValue(final Member member, final Object target) {
        return member instanceof Field ? getFieldValue((Field) member, target) : getMethodValue((Method) member, target);
    }

    public static Object getFieldValue(final Field field, final Object target) {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(target);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Could not get field value by reflection: " + fieldToString(field), e);
        }
        finally {
            field.setAccessible(accessible);
        }
    }

    // TODO: Use e.g. fest-reflect
    public static Object getMethodValue(final Method method, final Object target) {
        final boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            return method.invoke(target, (Object[])null);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Not a valid getter method: %s" + methodToString(method), e);
        }
        finally {
            method.setAccessible(accessible);
        }
    }

    public static String fieldToString(final Field field) {
        return field.getDeclaringClass().getName() + '.' + field.getName();
    }

    public static String methodToString(final Method method) {
        return method.getDeclaringClass().getName() + '.' + method.getName() + "(" +
                method.getParameterTypes().toString() + ")";
    }
}
