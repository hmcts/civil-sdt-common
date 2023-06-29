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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.IndividualStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.RequestItemType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.RequestsType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for BulkRequestTransformer.
 *
 * @author d130680
 */
class BulkRequestTransformerTest extends AbstractSdtUnitTestBase {
    /**
     * Bulk request transformer.
     */
    private BulkRequestTransformer transformer;

    /**
     * Set up variables for the test.
     */
    public void setUpLocalTests() {
        transformer = new BulkRequestTransformer();
    }

    /**
     * Test the transformation from jaxb to domain object.
     */
    @Test
    void testTransformJaxbToDomain() {
        // Set up the jaxb object to transform
        final BulkRequestType jaxb = new BulkRequestType();
        final long sdtCustomerId = 123;
        final String targetApplicationId = "MCOL";
        final long resultCount = 20;
        final String customerReference = "A123546";

        // Set up the header
        final HeaderType header = new HeaderType();
        header.setSdtCustomerId(sdtCustomerId);
        header.setTargetApplicationId(targetApplicationId);
        header.setRequestCount(resultCount);
        header.setCustomerReference(customerReference);
        jaxb.setHeader(header);

        // Set up the individual requests
        final RequestsType requestsType = new RequestsType();

        // Individual request 1
        requestsType.getRequest().add(createRequestItem("request 1", "mcolClaimStatusUpdate"));

        // Individual request 2
        requestsType.getRequest().add(createRequestItem("request 2", "mcolClaim"));

        // Individual request 3
        requestsType.getRequest().add(createRequestItem("request 3", "mcolWarrant"));

        jaxb.setRequests(requestsType);

        final IBulkSubmission domain = transformer.transformJaxbToDomain(jaxb);

        // Test the jaxb object has been transformed to a domain object
        assertEquals(sdtCustomerId, domain.getBulkCustomer().getSdtCustomerId(), "SDT Customer ID does not match");
        assertEquals(targetApplicationId, domain.getTargetApplication().getTargetApplicationCode(), "Target Application ID does not match");
        assertEquals(requestsType.getRequest().size(), domain.getIndividualRequests().size(),
                "Individual request list size does not match");

        int index = 0;
        for (RequestItemType item : requestsType.getRequest()) {
            verify(item, domain.getIndividualRequests().get(index), ++index);
        }

    }

    /**
     * Creates an instance of RequestItemType with given values.
     *
     * @param id   request id
     * @param type request type
     * @return RequestItemType
     */
    private RequestItemType createRequestItem(final String id, final String type) {
        final RequestItemType requestItem = new RequestItemType();
        requestItem.setRequestId(id);
        requestItem.setRequestType(type);
        return requestItem;
    }

    /**
     * Verifies that individual request contains expected values.
     *
     * @param expected request item type
     * @param actual   individual request
     * @param row      record number
     */
    private void verify(final RequestItemType expected, final IIndividualRequest actual, final int row) {
        assertNotNull(actual.getBulkSubmission());
        assertEquals(IIndividualRequest.IndividualRequestStatus.RECEIVED.getStatus(), actual.getRequestStatus(),
                "Customer reference does not match");
        assertEquals(expected.getRequestId(), actual.getCustomerRequestReference(),
                "Request id for individual request " + row + " does not match");
        assertEquals(row, actual.getLineNumber(),
                "Line number for individual request " + row + " does not match");
        assertEquals(expected.getRequestType(), actual.getRequestType(), "Request type mismatch");
    }

    /**
     * Test the error transformation from domain to jaxb
     */
    @Test
    public void testTransformDomainToJaxbHasError() {
        final IBulkSubmission domain = new BulkSubmission();
        final LocalDateTime createdDate = LocalDateTime.now();
        final String errorCode = "MOCK_ERROR_CODE";
        final String errorText = "MOCK ERROR TEXT";

        domain.setCreatedDate(createdDate);
        domain.setErrorCode(errorCode);
        domain.setErrorText(errorText);

        final BulkResponseType jaxb = transformer.transformDomainToJaxb(domain);

        assertEquals(errorCode, jaxb.getStatus().getError().getCode());
        assertEquals(errorText, jaxb.getStatus().getError().getDescription());
        assertEquals(StatusCodeType.ERROR, jaxb.getStatus().getCode());
    }

    /**
     * Test the from transformation domain to jaxb object.
     */
    @Test
    public void testTransformDomainToJaxb() {
        // Set up the domain object to transform
        final IBulkSubmission domain = new BulkSubmission();
        final long numberOfRequest = 8;
        final String sdtBulkReference = "A123456789";
        final String customerRef = "C10000123";
        final LocalDateTime createdDate = LocalDateTime.now();
        final String submissionStatus = "Uploaded";

        domain.setNumberOfRequest(numberOfRequest);
        domain.setSdtBulkReference(sdtBulkReference);
        domain.setCustomerReference(customerRef);
        domain.setCreatedDate(createdDate);
        domain.setSubmissionStatus(submissionStatus);

        // Setup some individual requests
        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        final String customerRequestReference1 = "request 1";
        IndividualRequest ir = new IndividualRequest();
        ir.setCustomerRequestReference(customerRequestReference1);
        ir.setRequestStatus(IndividualStatusCodeType.ACCEPTED.value());
        individualRequests.add(ir);

        ir = new IndividualRequest();
        final String customerRequestReference2 = "request 2";
        ir.setCustomerRequestReference(customerRequestReference2);
        ir.setRequestStatus(IndividualStatusCodeType.RECEIVED.value());
        individualRequests.add(ir);

        // Set one up as rejected so an error log can be created
        ir = new IndividualRequest();
        final String customerRequestReference3 = "request 3";
        ir.setCustomerRequestReference(customerRequestReference3);
        ir.setRequestStatus(IndividualStatusCodeType.REJECTED.value());

        // Set the error log and message
        final String errorCode = "87";
        final String errorText = "Specified claim does not belong to the requesting customer.";
        final ErrorLog errorLog = new ErrorLog(errorCode, errorText);
        ir.setErrorLog(errorLog);
        individualRequests.add(ir);

        domain.setIndividualRequests(individualRequests);

        final BulkResponseType jaxb = transformer.transformDomainToJaxb(domain);

        assertEquals(domain.getSdtBulkReference(),
                jaxb.getSdtBulkReference(), "The Sdt Bulk Reference is as expected");
        assertEquals(domain.getCustomerReference(), jaxb.getCustomerReference(),
                "The customer reference is as expected");
        assertEquals(AbstractTransformer.SDT_SERVICE, jaxb.getSdtService(), "The Sdt Service is as expected");
        assertEquals(jaxb.getRequestCount(), domain.getNumberOfRequest(),
                "The number of requests are as expected");
        assertNotNull(jaxb.getSubmittedDate(), "The submitted date is found");

        assertEquals(jaxb.getStatus().getCode().value(), "Ok", "The status code is as expected");
    }
}
