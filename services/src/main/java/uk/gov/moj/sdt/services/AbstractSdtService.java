/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2013 by the Ministry of Justice. All rights reserved.
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
package uk.gov.moj.sdt.services;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.ACCEPTED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

/**
 * Abstract Sdt Service class for any common functionality between the various services.
 *
 * @author Manoj Kulkarni
 */
public abstract class AbstractSdtService {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSdtService.class);

    private static final String CLAIM_NUMBER = "claimNumber";

    /**
     * Individual Request Dao to perform operations on the individual request object.
     */
    private IIndividualRequestDao individualRequestDao;

    /**
     * Parser for individual response.
     */
    private GenericXmlParser individualResponseXmlParser;

    private RequestTypeXmlNodeValidator requestTypeXmlNodeValidator;

    protected AbstractSdtService(@Qualifier("IndividualRequestDao")
                                      IIndividualRequestDao individualRequestDao,
                              @Qualifier("IndividualResponseXmlParser")
                                  GenericXmlParser individualResponseXmlParser,
                                 RequestTypeXmlNodeValidator requestTypeXmlNodeValidator) {
        this.individualRequestDao = individualRequestDao;
        this.individualResponseXmlParser = individualResponseXmlParser;
        this.requestTypeXmlNodeValidator = requestTypeXmlNodeValidator;
    }

    /**
     * Update the request object. This method is to be called to update the request object on
     * completion i.e. successful response is received from the target application.
     *
     * @param individualRequest the individual request to be marked as completed
     */
    protected void updateCompletedRequest(final IIndividualRequest individualRequest) {
        // Call the method with the populate target application response flag as true.
        updateCompletedRequest(individualRequest, true);
    }

    /**
     * Update the request object. This method is to be called to update the request object on
     * completion i.e. successful response is received from the target application.
     *
     * @param individualRequest         the individual request to be marked as completed
     * @param populateTargetAppResponse If this is true then extract the targetAppResponse from
     *                                  the raw input xml.
     */
    protected void updateCompletedRequest(final IIndividualRequest individualRequest,
                                          final boolean populateTargetAppResponse) {
        if (populateTargetAppResponse) {
            final String targetAppResponse = individualResponseXmlParser.parse();
            if (StringUtils.isNotBlank(targetAppResponse)) {
                individualRequest.setTargetApplicationResponse(targetAppResponse.getBytes(StandardCharsets.UTF_8));
            }
        }

        // now persist the request.
        this.getIndividualRequestDao().persist(individualRequest);

        // Check if all the individual request for the bulk submission are either ACCEPTED or REJECTED
        // If all the requests are Accepted or Rejected, mark the bulk submission as Complete.

        final IBulkSubmission bulkSubmission = individualRequest.getBulkSubmission();

        final List<String> completeRequestStatus = Arrays.asList(ACCEPTED.getStatus(), REJECTED.getStatus());


        final long requestsCount = this.getIndividualRequestDao().queryAsCount(
            IndividualRequest.class,
            () -> createCriteria(
                getIndividualRequestDao(),
                bulkSubmission.getSdtBulkReference(),
                completeRequestStatus
            )
        );

        if (requestsCount == 0) {

            LOGGER.debug("All individual requests for bulk submission [{}] have been processed now. Marking the bulk " +
                            "submission as Completed", bulkSubmission.getSdtBulkReference());

            bulkSubmission.markAsCompleted();

            this.getIndividualRequestDao().persist(bulkSubmission);
        }
    }

    /**
     * Fetch the individual request domain object from the database using the unique
     * Sdt request reference.
     *
     * @param sdtRequestReference the unique Sdt Request Reference
     * @return the individual request associated with the unique Sdt Request Reference
     */
    protected IIndividualRequest getIndRequestBySdtReference(final String sdtRequestReference) {
        // Look for the individual request matching this unique request reference.
        return this.getIndividualRequestDao().getRequestBySdtReference(sdtRequestReference);

    }

    /**
     * @return the generic xml parser instance for the individual response
     */
    public GenericXmlParser getIndividualResponseXmlParser() {
        return individualResponseXmlParser;
    }

    /**
     * @param individualResponseXmlParser the generic xml parser instance for the individual response
     */
    public void setIndividualResponseXmlParser(final GenericXmlParser individualResponseXmlParser) {
        this.individualResponseXmlParser = individualResponseXmlParser;
    }

    /**
     * @return the individual request Dao instance
     */
    public IIndividualRequestDao getIndividualRequestDao() {
        return individualRequestDao;
    }

    /**
     * @param individualRequestDao the individual request dao instance.
     */
    public void setIndividualRequestDao(final IIndividualRequestDao individualRequestDao) {
        this.individualRequestDao = individualRequestDao;
    }

    protected boolean isCMCRequestType(IIndividualRequest individualRequest) {
        return isCMCRequestType(individualRequest, false);
    }

    protected boolean isCMCRequestType(IIndividualRequest individualRequest, boolean throwException) {
        String requestType = individualRequest.getRequestType();
        Boolean readyForAlternateSubmission = individualRequest.getBulkSubmission().getBulkCustomer().getReadyForAlternateService();
        return requestTypeXmlNodeValidator.isCMCClaimRequest(requestType, readyForAlternateSubmission)
            || requestTypeXmlNodeValidator.isCMCRequestType(requestType,
                                                            new String(individualRequest.getRequestPayload(), StandardCharsets.UTF_8),
                                                            CLAIM_NUMBER,
                                                            throwException);
    }


    private CriteriaQuery<IndividualRequest> createCriteria(IIndividualRequestDao individualRequestDao, String sdtBulkReference,
                                       List<String> completeRequestStatus) {
        CriteriaBuilder criteriaBuilder = individualRequestDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<IndividualRequest> criteriaQuery = criteriaBuilder.createQuery(IndividualRequest.class);
        Root<IndividualRequest> root = criteriaQuery.from(IndividualRequest.class);
        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteriaBuilder.equal(root.get("sdtBulkReference"), sdtBulkReference);
        Expression<String> requestStatusExpression = root.get("requestStatus");
        predicates[1] = requestStatusExpression.in(completeRequestStatus);
        return criteriaQuery.select(root).where(predicates);
    }
}
