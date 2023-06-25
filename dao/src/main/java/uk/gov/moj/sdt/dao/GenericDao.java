/* Copyrights and Licenses
 *
 * Copyright (c) 2013 by the Ministry of Justice. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * - All advertising materials mentioning features or use of this software must display the
 * following acknowledgment: "This product includes Money Claims OnLine."
 * - Products derived from this software may not be called "Money Claims OnLine" nor may
 * "Money Claims OnLine" appear in their names without prior written permission of the
 * Ministry of Justice.
 * - Redistributions of any form whatsoever must retain the following acknowledgment: "This
 * product includes Money Claims OnLine."
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */
package uk.gov.moj.sdt.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import uk.gov.moj.sdt.dao.api.IGenericDao;
import uk.gov.moj.sdt.domain.api.IDomainObject;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implements generic DAO functionality based on {@link IGenericDao} allowing a single DAO class to provide access to
 * all database entities. The DAO an object tree of domain objects. Business logic implemented in service layer acts
 * upon domain objects returned by JPA. These are translated into JAXB objects before being returned to the SOAP
 * layer.
 *
 * <p>
 * Subclasses of this class may build upon this to provide additional functionality encapsulating custom queries and
 * custom SQLs.
 * </p>
 *
 * @author Robin Compston
 */
// @Transactional (propagation = Propagation.MANDATORY)
// @Transactional
public class GenericDao<T extends IDomainObject> implements IGenericDao {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDao.class);

    private final CrudRepository<T, Long> crudRepository;

    private EntityManager entityManager;

    public GenericDao(final CrudRepository<T, Long> crudRepository) {
        super();
        this.crudRepository = crudRepository;
    }

    /**
     * Default constructor for {@link GenericDao}.
     */
    public GenericDao(final CrudRepository<T, Long> crudRepository,
                      EntityManager entityManager) {
        super();
        this.crudRepository = crudRepository;
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public <D extends IDomainObject> D fetch(final Class<D> domainType, final long id)
            throws DataAccessException {
        // Record start time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        // Domain object of type asked for by caller.
        D domainObject = null;

        // Get unique result using criteria and assign to domain object. This will be a tree of objects up to lazy
        // boundaries
        final Optional<T> value = this.crudRepository.findById(id);
        if (value.isPresent()) {
            domainObject = domainType.cast(value.get());
        }

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseReadsTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseReadsCount();

        // Validate results.
        if (domainObject == null) {
            return null;
        }

        if (domainObject.getId() != id) {
            throw new IllegalStateException();
        }

        return domainObject;
    }

    public <D extends IDomainObject> D[] query(final Class<D> domainType) throws DataAccessException {
        return query(domainType, () -> createSelectQuery(domainType));
    }

    @Override
    public final <D extends IDomainObject> D[] query(final Class<D> domainType,
                                                     Supplier<CriteriaQuery<D>> criteriaQuerySupplier)
            throws DataAccessException {
        LOGGER.debug("query(): domainType={}", domainType);

        // Get a list of results from JPA.
        final List<?> domainObjects = queryAsList(domainType, criteriaQuerySupplier);

        @SuppressWarnings("unchecked") final D[] results =
                (D[]) Array.newInstance(domainType, domainObjects.size());

        return domainObjects.toArray(results);
    }

    @Override
    public final <D extends IDomainObject> List<D> queryAsList(final Class<D> domainType,
                                                               Supplier<CriteriaQuery<D>> criteriaQuerySupplier)
            throws DataAccessException {
        // Record start time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        LOGGER.debug("queryAsList(): domainType={}", domainType);

        TypedQuery<D> typedQuery = getEntityManager().createQuery(criteriaQuerySupplier.get());

        // Get a list of results from JPA.
        @SuppressWarnings("unchecked") final List<D> domainObjects = typedQuery.getResultList();

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseReadsTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseReadsCount();

        return domainObjects;
    }

    @Override
    public void persist(final Object domainObject) throws DataAccessException {
        // Record start time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        LOGGER.debug("Persist domain object {}", domainObject);

        crudRepository.save((T) domainObject);

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseWritesTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseWritesCount();
    }

    @Override
    public <D extends IDomainObject> void persistBulk(final List<D> domainObjectList)
            throws DataAccessException {
        // Record start time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        LOGGER.debug("Persist domain object list {}", domainObjectList);

        int i = 1;
        final int maxBatchSize = 20;
        for (D domainObject : domainObjectList) {
            crudRepository.save((T) domainObject);
            if (i % maxBatchSize == 0) {
                getEntityManager().flush();
            }
            i++;
        }

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseWritesTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseWritesCount();
    }

    @Override
    public long getNextSequenceValue(final String sequenceName) throws DataAccessException {
        // Record start time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        LOGGER.debug("Sequence generation for {}", sequenceName);

        if (sequenceName == null || sequenceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid sequence name");
        }

        String result = getEntityManager()
                                         .createNativeQuery(String.format("SELECT nextval('%s')", sequenceName))
                                         .getSingleResult().toString();

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseReadsTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseReadsCount();

        return Long.valueOf(result);
    }

    public <D extends IDomainObject> D uniqueResult(final Class<D> domainType,
                                                    Supplier<CriteriaQuery<D>> criteriaQuerySupplier) {

        final long startTime = new GregorianCalendar().getTimeInMillis();
        LOGGER.debug("uniqueResult(): domainType={}", domainType);

        TypedQuery<D> typedQuery = getEntityManager().createQuery(criteriaQuerySupplier.get());
        // Get unique result from JPA.
        final D domainObject = typedQuery.getSingleResult();

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseReadsTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseReadsCount();

        return domainObject;
    }

    @Override
    public <D extends IDomainObject> long queryAsCount(final Class<D> domainType,
                                                       Supplier<CriteriaQuery<D>> criteriaQuerySupplier) {
        // Record start time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        LOGGER.debug("queryAsCount(): domainType={}", domainType);

        TypedQuery<D> query = getEntityManager().createQuery(criteriaQuerySupplier.get());
        // Get unique result from JPA.
        final Number countOfObjects = query.getResultList().size();

        // Calculate time in JPA/database.
        final long endTime = new GregorianCalendar().getTimeInMillis();
        SdtMetricsMBean.getMetrics().addDatabaseReadsTime(endTime - startTime);
        SdtMetricsMBean.getMetrics().upDatabaseReadsCount();

        return countOfObjects != null ? countOfObjects.longValue() : 0;
    }

    private <D extends IDomainObject> CriteriaQuery<D> createSelectQuery(Class<D> domainType) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<D> criteriaQuery = criteriaBuilder.createQuery(domainType);
        Root<D> root = criteriaQuery.from(domainType);
        return criteriaQuery.select(root);
    }

    protected Predicate createDatePredicate(CriteriaBuilder criteriaBuilder, Root<T> root, int dataRetention) {
        Path<LocalDateTime> createdDatePath = root.get("createdDate");
        return criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo((createdDatePath), atStartOfDay(dataRetention)),
                criteriaBuilder.lessThan((createdDatePath), atBeginningOfNextDay()));
    }

    private LocalDateTime atBeginningOfNextDay() {
        return LocalDate.now().plusDays(1).atStartOfDay();
    }

    private LocalDateTime atStartOfDay(int dataRetention) {
        return LocalDate.now().plusDays(dataRetention * -1L).atStartOfDay();
    }
}
