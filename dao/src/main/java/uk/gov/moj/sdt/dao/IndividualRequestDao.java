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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.dao.repository.IndividualRequestRepository;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IDomainObject;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.FORWARDED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.RECEIVED;

/**
 * Implements specific DAO functionality based on {@link IIndividualRequestDao}. This is a derived DAO extending
 * {@link GenericDao} which provides generic JPA access. This specific DAO exists in order to construct domain
 * specific selections where column matches are needed on columns other than the id. For each domain specific query, it
 * constructs an array of JPA predicates which are passed to the generic method.
 *
 * @author Son Loi
 */
@Component("IndividualRequestDao")
public class IndividualRequestDao extends GenericDao<IndividualRequest> implements IIndividualRequestDao {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualRequestDao.class);
    public static final String REQUEST_STATUS = "requestStatus";
    public static final String UPDATED_DATE = "updatedDate";
    public static final String DEAD_LETTER = "deadLetter";
    public static final String SDT_REQUEST_REFERENCE = "sdtRequestReference";
    public static final String FORWARDING_ATTEMPTS = "forwardingAttempts";

    private final Root<IndividualRequest> root;

    private final CriteriaBuilder criteriaBuilder;

    private final CriteriaQuery<IndividualRequest> criteriaQuery;

    @Autowired
    public IndividualRequestDao(final IndividualRequestRepository crudRepository, EntityManager entityManager) {
        super(crudRepository, entityManager);
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(IndividualRequest.class);
        root = criteriaQuery.from(IndividualRequest.class);
    }

    @Override
    public IIndividualRequest getIndividualRequest(final IBulkCustomer bulkCustomer, final String customerReference,
                                                   final int dataRetention) throws DataAccessException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get individual request matching the bulk customer[" + bulkCustomer + "], " +
                    "customer reference[" + customerReference + "] and the data retention period[" + dataRetention +
                    "]");
        }

        Predicate sdtCustomerPredicate = criteriaBuilder.equal(root.get("bulkSubmission").get("bulkCustomer").get("sdtCustomerId"),
                                                               bulkCustomer.getSdtCustomerId());
        Predicate customerReferencePredicate = criteriaBuilder.equal(criteriaBuilder.lower(root.get("customerRequestReference")),
                                                                     customerReference.toLowerCase());
        TypedQuery<IndividualRequest> typedQuery = getEntityManager().createQuery(criteriaQuery.select(root)
                                                                                   .where(
                                                                                       sdtCustomerPredicate,
                                                                                       customerReferencePredicate,
                                                                                       createDatePredicate(criteriaBuilder, root, dataRetention)
                                                                                   ));
        final List<IndividualRequest> individualRequests = typedQuery.getResultList();

        // Return null or the first individual request
        if (individualRequests == null || individualRequests.size() == 0) {
            return null;
        }

        return individualRequests.get(0);
    }

    @Override
    public IIndividualRequest getRequestBySdtReference(final String sdtReferenceId) throws DataAccessException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get individual request matching the SDT reference id[" + sdtReferenceId + "]");
        }

        // Call the generic dao to do this query.
        final IDomainObject[] individualRequests = this.query(IndividualRequest.class, () -> {
            Predicate[] sdtCustomerPredicate = createSdtRequestReferencePredicate(sdtReferenceId);
            return criteriaQuery.select(root).where(sdtCustomerPredicate);
        });

        // Should only return one or none at all
        if (individualRequests == null || individualRequests.length == 0) {
            return null;
        }

        if (individualRequests.length > 1) {
            throw new IllegalStateException("Multiple Individual Requests found for the Sdt Request Reference " +
                    sdtReferenceId);
        }

        return (IIndividualRequest) individualRequests[0];
    }

    @Override
    public List<IIndividualRequest> getPendingIndividualRequests(final int maxAllowedAttempts)
            throws DataAccessException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get pending individual requests in status FORWARDED that have  "
                    + "reached their maximum forwarding attempts.");
        }
        List<IndividualRequest> queryList = this.queryAsList(IndividualRequest.class, () -> {
            Predicate[] sdtCustomerPredicate =  createIndividualRequestPredicate(FORWARDED.getStatus(),
                                                                                 maxAllowedAttempts, false);
            return criteriaQuery.select(root).where(sdtCustomerPredicate);
        });
        return new ArrayList<>(queryList);
    }

    @Override
    public List<IIndividualRequest> getStaleIndividualRequests(final int minimumAgeInMinutes)
            throws DataAccessException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get unforwarded individual requests created more than " + minimumAgeInMinutes +
                    " minutes ago");
        }

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime latestTime = now.minusMinutes(minimumAgeInMinutes);
        List<IndividualRequest> queryList = this.queryAsList(IndividualRequest.class, () -> {
            Predicate[] sdtCustomerPredicate =   createIndividualRequestPredicate(false, latestTime);
            return criteriaQuery.select(root).where(sdtCustomerPredicate);
        });
        return new ArrayList<>(queryList);
    }

    private Predicate[] createIndividualRequestPredicate(boolean deadLetter, LocalDateTime latestTime) {
        Predicate[] predicates = new Predicate[3];
        predicates[0] = criteriaBuilder.or(criteriaBuilder.equal(root.get(REQUEST_STATUS), RECEIVED.getStatus()),
                                           criteriaBuilder.equal(root.get(REQUEST_STATUS), FORWARDED.getStatus()));
        predicates[1] = criteriaBuilder.equal(root.get(DEAD_LETTER), deadLetter);
        predicates[2] = criteriaBuilder.or(criteriaBuilder.lessThan(root.get(UPDATED_DATE), latestTime), criteriaBuilder.isNull(root.get(UPDATED_DATE)));
        return predicates;
    }

    private Predicate[] createSdtRequestReferencePredicate(String sdtRequestReference) {
        Predicate[] predicates = new Predicate[1];
        predicates[0] = criteriaBuilder.equal(root.get(SDT_REQUEST_REFERENCE), sdtRequestReference);
        return predicates;
    }

    private Predicate[] createIndividualRequestPredicate(String requestStatus, int maxAllowedAttempts, boolean deadLetter) {
        Predicate[] predicates = new Predicate[3];
        predicates[0] = criteriaBuilder.equal(root.get(REQUEST_STATUS), requestStatus);
        predicates[1] = criteriaBuilder.equal(root.get(DEAD_LETTER), deadLetter);
        predicates[2] = criteriaBuilder.ge(root.get(FORWARDING_ATTEMPTS), maxAllowedAttempts);
        return predicates;
    }
}
