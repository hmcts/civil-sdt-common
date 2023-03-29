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
package uk.gov.moj.sdt.dao.api;

import org.springframework.dao.DataAccessException;
import uk.gov.moj.sdt.domain.api.IDomainObject;

import java.util.List;
import java.util.function.Supplier;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

/**
 * core interface of GenericDao.
 *
 * @author David Turner
 */
public interface IGenericDao {
    /**
     * Loads a business domain entity model hierarchy as of the given
     * businessTime and using the given id.
     *
     * <p>
     * This method loads the persistent entity representing the given domainType from the database assuming the given
     * businessTime. The resultant persistent entity is then transformed into the business domain entity. The
     * transformation is done deeply meaning that all associations are followed, however it stops at lazy load
     * boundaries, regardless of whether the objects referenced got loaded already.
     * </p>
     *
     * @param <D> of entity to load
     * @param domainType   of entity to load.
     * @param id           of entity to load.
     * @return business domain entity model hierarchy root object.
     * @throws DataAccessException on any I/O related error.
     */
    <D extends IDomainObject> D fetch(final Class<D> domainType, final long id)
            throws DataAccessException;

    /**
     * Loads an array of domain object model hierarchies with a set of
     * restrictions. This constructs a JPA query from the domainType and
     * restrictions given and uses Session.createCriteria() to retrieve the
     * persistent entities. Then the query is executed.
     *
     * <p>
     * This can be used to load a user by name, for example.
     * </p>
     *
     * @param <D> of entity to load.
     * @param domainType   of entity to load.
     * @return business domain entity model hierarchy root object.
     * @throws DataAccessException on any I/O related error.
     */
    <D extends IDomainObject> D[] query(final Class<D> domainType,
                                        Supplier<CriteriaQuery<D>> criteriaQuerySupplier) throws DataAccessException;

    <D extends IDomainObject> D[] query(final Class<D> domainType) throws DataAccessException;

    /**
     * Returns a list of domain object model hierarchies with a set of
     * restrictions. This constructs a JPA query from the domainType and
     * restrictions given and uses Session.createCriteria() to retrieve the
     * persistent entities. Then the query is executed.
     *
     * <p>
     * This can be used to load a user by name, for example.
     * </p>
     *
     * @param <D> of entity to load.
     * @param domainType   of entity to load.
     * @return business domain entity model hierarchy root object.
     * @throws DataAccessException on any I/O related error.
     */
    <D extends IDomainObject> List<D> queryAsList(final Class<D> domainType,
                                                  Supplier<CriteriaQuery<D>> criteriaQuerySupplier);

    /**
     * Returns unique domain object model hierarchy with a set of
     * restrictions. This constructs a JPA query from the domainType and
     * restrictions given and uses Session.createCriteria() to retrieve the
     * persistent entity. Then the query is executed.
     *
     * <p>
     * This can be used to load a user by name, for example.
     * </p>
     *
     * @param <D> of entity to load.
     * @param domainType   of entity to load.
     * @return business domain entity model hierarchy root object.
     * @throws DataAccessException on any I/O related error.
     */
    <D extends IDomainObject> D uniqueResult(final Class<D> domainType,
                                             Supplier<CriteriaQuery<D>> criteriaQuerySupplier);


    /**
     * Stores a domainObject in the database, either doing an insert or an
     * update.
     *
     * <p>
     * This method persists or updates an object depending upon the presence of an id (if there is one it is an update
     * operation).
     * </p>
     *
     * <p>
     * Since the passed in object will be persisted its immediate state after this method finishes will be to have an id
     * either assigned as part of a insert operation or its previous value if this is an update.
     * </p>
     *
     * @param domainObject instance to insert or update.
     * @throws DataAccessException on any I/O related error.
     */
    void persist(final Object domainObject) throws DataAccessException;

    /**
     * Stores a list of domainObject in the database, either doing an bulk insert or an
     * bulk update.
     *
     * <p>
     * This method persists or updates list of object(s) depending upon the presence of an id (if there is one it is an
     * update operation).
     * </p>
     *
     * <p>
     * Since the passed in object will be persisted its immediate state after this method finishes will be to have an id
     * either assigned as part of a insert operation or its previous value if this is an update.
     * </p>
     *
     * @param <D>     of entity to persist.
     * @param domainObjectList the list of instance to insert or update.
     * @throws DataAccessException on any I/O related error.
     */
    <D extends IDomainObject> void persistBulk(final List<D> domainObjectList)
            throws DataAccessException;

    /**
     * This method returns a sequence number associated with the sequence name.
     *
     * @param sequenceName The name of the sequence
     * @return the sequence number
     * @throws DataAccessException on any I/O related error
     */
    long getNextSequenceValue(final String sequenceName) throws DataAccessException;

    /**
     * Returns a count of domain object model hierarchies with a set of
     * restrictions. This constructs a JPA query from the domainType and
     * restrictions given and uses Session.createCriteria() to retrieve the
     * persistent entities. Then the Projection of row count is applied on the criteria
     * to return an unique count of the domain objects.
     *
     * <p>
     * This can be used where there is no use for the actual objects, for example.
     * </p>
     *
     * @param <D> of entity to load.
     * @param domainType   of entity to load.
     * @return the unique count of the domain object heirarchy matching the criteria
     * @throws DataAccessException on any I/O related error.
     */
    <D extends IDomainObject> long queryAsCount(final Class<D> domainType,
                                                Supplier<CriteriaQuery<D>> criteriaQuerySupplier);

    EntityManager getEntityManager();

}
