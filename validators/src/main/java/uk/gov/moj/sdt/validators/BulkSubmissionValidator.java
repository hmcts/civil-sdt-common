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
package uk.gov.moj.sdt.validators;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;
import uk.gov.moj.sdt.utils.concurrent.InFlightMessage;
import uk.gov.moj.sdt.utils.concurrent.api.IInFlightMessage;
import uk.gov.moj.sdt.utils.visitor.api.ITree;
import uk.gov.moj.sdt.validators.api.IBulkSubmissionValidator;
import uk.gov.moj.sdt.validators.exception.InvalidRequestTypeException;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

/**
 * Implementation of {@link IBulkSubmissionValidator}.
 *
 * @author Saurabh Agarwal
 */
@Component("BulkSubmissionValidator")
public class BulkSubmissionValidator extends AbstractSdtValidator implements IBulkSubmissionValidator {

    private static final String CLAIM_NUMBER = "claimNumber";

    /**
     * Bulk submission dao.
     */
    private IBulkSubmissionDao bulkSubmissionDao;

    private RequestTypeXmlNodeValidator requestTypeXmlNodeValidator;

    /**
     * The concurrencyMap to hold in flight message data keyed on sdtCustId + custRef. This is used to prevent the
     * customer sending two requests close together which both get processed at the same time, causing duplicates. The
     * normal check on a duplicate does not work until the first bulk request has been persisted.
     */
    private Map<String, IInFlightMessage> concurrencyMap;

    @Autowired
    public BulkSubmissionValidator(@Qualifier("BulkCustomerDao")
                                            IBulkCustomerDao bulkCustomerDao,
                                        @Qualifier("GlobalParametersCache")
                                            ICacheable globalParameterCache,
                                        @Qualifier("ErrorMessagesCache")
                                            ICacheable errorMessagesCache,
                                        @Qualifier("BulkSubmissionDao")
                                           IBulkSubmissionDao bulkSubmissionDao,
                                   RequestTypeXmlNodeValidator requestTypeXmlNodeValidator,
                                   @Qualifier("concurrentMap")
                                           Map<String, IInFlightMessage> concurrentMap) {
        super(bulkCustomerDao, globalParameterCache, errorMessagesCache);
        this.bulkSubmissionDao = bulkSubmissionDao;
        this.requestTypeXmlNodeValidator = requestTypeXmlNodeValidator;
        this.concurrencyMap = concurrentMap;
    }

    @Override
    public void visit(final IBulkSubmission bulkSubmission, final ITree tree) {
        // Validate SDT Customer ID and target application
        final IBulkCustomer bulkCustomer = bulkSubmission.getBulkCustomer();
        final long sdtCustomerId = bulkCustomer.getSdtCustomerId();

        final ITargetApplication targetApplication = bulkSubmission.getTargetApplication();
        checkCustomerHasAccess(sdtCustomerId, targetApplication.getTargetApplicationCode());

        // Validate customer reference is unique across data retention period for bulk submission
        final String sdtCustomerReference = bulkSubmission.getCustomerReference();
        List<String> replacements = null;

        addInFlightMessage(bulkSubmission);

        // Get the data retention period
        final int dataRetention = super.getDataRetentionPeriod();
        final IBulkSubmission invalidBulkSubmission =
                bulkSubmissionDao.getBulkSubmission(bulkCustomer, sdtCustomerReference, dataRetention);

        // Check that this custom has not already submitted a bulk submission with this customer reference.
        if (invalidBulkSubmission != null) {
            replacements = new ArrayList<>();
            replacements.add(String.valueOf(sdtCustomerReference));
            replacements.add(Utilities.formatDateTimeForMessage(invalidBulkSubmission.getCreatedDate()));
            replacements.add(invalidBulkSubmission.getSdtBulkReference());
            createValidationException(replacements, IErrorMessage.ErrorCode.DUP_CUST_FILEID);
        }

        // Check the request count matches
        if (bulkSubmission.getNumberOfRequest() != bulkSubmission.getIndividualRequests().size()) {
            replacements = new ArrayList<>();
            replacements.add(Integer.valueOf(bulkSubmission.getIndividualRequests().size()).toString());
            replacements.add("" + bulkSubmission.getNumberOfRequest());
            replacements.add(bulkSubmission.getCustomerReference());
            createValidationException(replacements, IErrorMessage.ErrorCode.REQ_COUNT_MISMATCH);
        }

        // Validate customer reference is unique within the list of individual requests
        final Set<String> customerReferenceSet = new HashSet<>();

        for (IIndividualRequest individualRequest : bulkSubmission.getIndividualRequests()) {

            final String customerRequestReference = individualRequest.getCustomerRequestReference();
            final boolean success = customerReferenceSet.add(customerRequestReference);
            // Check that the user file reference is unique within the current list of individual requests
            if (!success) {
                // Set the error in the error log and continue rather than throw an exception
                replacements = new ArrayList<>();
                replacements.add(customerRequestReference);
                final IErrorLog errorLog =
                        new ErrorLog(IErrorMessage.ErrorCode.DUPLD_CUST_REQID.name(), getErrorMessage(replacements,
                                IErrorMessage.ErrorCode.DUPLD_CUST_REQID));

                // Change the status to rejected
                individualRequest.markRequestAsRejected(errorLog);
            }
        }
    }

    /**
     * Set bulk submission dao.
     *
     * @param bulkSubmissionDao bulk submission dao
     */
    public void setBulkSubmissionDao(final IBulkSubmissionDao bulkSubmissionDao) {
        this.bulkSubmissionDao = bulkSubmissionDao;
    }

    @Override
    public void checkIndividualRequests(final IBulkSubmission bulkSubmission) {
        // Keep a count of the rejected individual requests
        final List<IIndividualRequest> individualRequests = bulkSubmission.getIndividualRequests();
        int rejectedRequests = 0;
        for (IIndividualRequest individualRequest : individualRequests) {
            if (individualRequest.getRequestStatus().equals(
                    REJECTED.getStatus())) {
                rejectedRequests++;
            }
        }

        setErrorLog(bulkSubmission, bulkSubmission.getNumberOfRequest(), rejectedRequests);
    }

    public void validateCMCRequests(final IBulkSubmission bulkSubmission) {
        int rejectedRequests = 0;
        for (IIndividualRequest individualRequest : bulkSubmission.getIndividualRequests()) {
            if (requestTypeXmlNodeValidator.isCCDReference(
                new String(individualRequest.getRequestPayload(), StandardCharsets.UTF_8),
                CLAIM_NUMBER)
                && !requestTypeXmlNodeValidator.isValidRequestType(individualRequest.getRequestType())) {
                final IErrorLog errorLog =
                    new ErrorLog(
                        IErrorMessage.ErrorCode.INVALID_CMC_REQUEST.name(),
                        "Invalid Request type for CMC " + individualRequest.getRequestType()
                    );
                individualRequest.markRequestAsRejected(errorLog);
                rejectedRequests++;
            }
        }
        long numberOfRequests = bulkSubmission.getNumberOfRequest();
        setErrorLog(bulkSubmission, numberOfRequests, rejectedRequests);
        if (rejectedRequests >= numberOfRequests) {
            throw new InvalidRequestTypeException(
                IErrorMessage.ErrorCode.INVALID_CMC_REQUEST.name(),
                "Invalid request type for CMC"
            );
        }
    }

    private void setErrorLog(IBulkSubmission bulkSubmission, long numberOfRequests, long rejectedRequests) {
        // If all the individual requests have been rejected then create an error log
        if (rejectedRequests >= numberOfRequests) {
            final List<String> replacements = new ArrayList<>();
            replacements.add(bulkSubmission.getCustomerReference());

            // Set the error continue rather than throw an exception
            bulkSubmission.setErrorCode(IErrorMessage.ErrorCode.NO_VALID_REQS.name());
            bulkSubmission.setErrorText(getErrorMessage(replacements, IErrorMessage.ErrorCode.NO_VALID_REQS));
            bulkSubmission.setSubmissionStatus(IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus());
        }
    }

    /**
     * Add an in flight message entry to the concurrencyMap for this customer/customer reference. This will be used to
     * used to detect concurrent in flight submissions with the same customer and customer reference.
     *
     * @param bulkSubmission the bulk submission being processed.
     * @throws uk.gov.moj.sdt.validators.exception.AbstractBusinessException super class of the exception to be thrown.
     */
    private void addInFlightMessage(final IBulkSubmission bulkSubmission) {
        synchronized (concurrencyMap) {
            final String key =
                    bulkSubmission.getBulkCustomer().getSdtCustomerId() + bulkSubmission.getCustomerReference();

            // Do we already have this message in flight?
            IInFlightMessage inFlightMessage = concurrencyMap.get(key);
            if (inFlightMessage == null) {
                // No - this is the first thread with this message.
                inFlightMessage = new InFlightMessage();
                inFlightMessage.setCompetingThreads(new HashMap<Thread, Thread>());
                concurrencyMap.put(key, inFlightMessage);
            }

            // Add this thread to list of competing threads handling this message.
            inFlightMessage.getCompetingThreads().put(Thread.currentThread(), Thread.currentThread());
        }
    }
}
