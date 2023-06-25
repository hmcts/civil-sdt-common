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

package uk.gov.moj.sdt.transformers;

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.RequestItemType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;

import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.RECEIVED;

/**
 * Maps bulk request JAXB object tree to domain object tree and vice versa.
 *
 * @author d130680
 */
@Component("BulkRequestTransformer")
public final class BulkRequestTransformer extends AbstractTransformer implements
        ITransformer<BulkRequestType, BulkResponseType, IBulkSubmission, IBulkSubmission> {

    /**
     * Maps the header to a Bulk Customer object.
     *
     * @param headerType header type
     * @return bulk customer
     */
    private BulkCustomer mapToBulkCustomer(final HeaderType headerType) {
        final BulkCustomer bulkCustomer = new BulkCustomer();

        bulkCustomer.setSdtCustomerId(headerType.getSdtCustomerId());
        return bulkCustomer;

    }

    /**
     * Maps the bulk request to a list of Individual Request object.
     *
     * @param bulkRequestType bulk request
     * @param bulkSubmission  bulk submission
     */
    private void mapToIndividualRequests(final BulkRequestType bulkRequestType, final IBulkSubmission bulkSubmission) {
        IndividualRequest individualRequest = null;
        final List<RequestItemType> requests = bulkRequestType.getRequests().getRequest();

        // Set the individual requests
        int lineNumber = 1;
        for (RequestItemType request : requests) {
            individualRequest = new IndividualRequest();

            individualRequest.setCustomerRequestReference(request.getRequestId());

            individualRequest.setLineNumber(lineNumber++);

            individualRequest.setRequestStatus(RECEIVED.getStatus());

            individualRequest.setRequestType(request.getRequestType());

            individualRequest.setCreatedDate(LocalDateTime.now());

            bulkSubmission.addIndividualRequest(individualRequest);
        }
    }

    @Override
    public IBulkSubmission transformJaxbToDomain(final BulkRequestType bulkRequest) {
        final HeaderType headerType = bulkRequest.getHeader();

        // Create target domain object.
        final IBulkSubmission bulkSubmission = new BulkSubmission();
        final ITargetApplication targetApplication = new TargetApplication();

        // Set bulk submission fields from JAXB object.
        bulkSubmission.setCustomerReference(headerType.getCustomerReference());
        bulkSubmission.setNumberOfRequest(headerType.getRequestCount());

        targetApplication.setTargetApplicationCode(headerType.getTargetApplicationId().toUpperCase());

        bulkSubmission.setTargetApplication(targetApplication);
        bulkSubmission.setSubmissionStatus(IBulkSubmission.BulkRequestStatus.UPLOADED.getStatus());
        bulkSubmission.setCreatedDate(LocalDateTime.now());

        // Set bulk customer
        final BulkCustomer bulkCustomer = mapToBulkCustomer(headerType);
        bulkSubmission.setBulkCustomer(bulkCustomer);

        // Set individual requests
        mapToIndividualRequests(bulkRequest, bulkSubmission);

        return bulkSubmission;
    }

    @Override
    public BulkResponseType transformDomainToJaxb(final IBulkSubmission bulkSubmission) {
        final BulkResponseType jaxb = new BulkResponseType();
        // Populate the generated SDT Bulk Reference in response
        jaxb.setSdtBulkReference(bulkSubmission.getSdtBulkReference());
        jaxb.setCustomerReference(bulkSubmission.getCustomerReference());
        jaxb.setSdtService(AbstractTransformer.SDT_SERVICE);
        jaxb.setRequestCount(bulkSubmission.getNumberOfRequest());

        // Populate submission date in response
        jaxb.setSubmittedDate(Utilities.convertLocalDateTimeToCalendar(bulkSubmission.getCreatedDate()));

        // Initialise the status to OK
        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);

        // Set errors if any
        if (bulkSubmission.hasError()) {
            final ErrorType errorType = new ErrorType();
            errorType.setCode(bulkSubmission.getErrorCode());
            errorType.setDescription(bulkSubmission.getErrorText());

            statusType.setError(errorType);
            statusType.setCode(StatusCodeType.ERROR);

        }

        jaxb.setStatus(statusType);
        return jaxb;
    }
}
