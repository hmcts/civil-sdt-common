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

import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.IndividualStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.IndividualStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.ResponseType;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for BulkRequestTransformer.
 *
 * @author d130680
 */
class BulkFeedbackTransformerTest extends AbstractSdtUnitTestBase {

    /**
     * Bulk feedback transformer.
     */
    private BulkFeedbackTransformer transformer;

    /**
     * Set up variables for the test.
     */
    public void setUpLocalTests() {
        transformer = new BulkFeedbackTransformer();
    }

    /**
     * Test the transformation from jaxb to domain object.
     */
    @Test
    public void testTransformJaxbToDomain() {
        // Set up the jaxb object to transform
        final BulkFeedbackRequestType jaxb = new BulkFeedbackRequestType();
        final long sdtCustomerId = 123;
        final String sdtBulkReference = "A123456";

        // Set up the header
        final HeaderType header = new HeaderType();
        header.setSdtBulkReference(sdtBulkReference);
        header.setSdtCustomerId(sdtCustomerId);
        jaxb.setHeader(header);

        // Do the transformation
        final IBulkFeedbackRequest domain = transformer.transformJaxbToDomain(jaxb);
        assertEquals(sdtCustomerId, domain.getBulkCustomer().getSdtCustomerId(), "SDT Customer ID does not match");
        assertEquals(sdtBulkReference, domain.getSdtBulkReference(), "SDT Bulk Reference does not match");

    }

    /**
     * Test the from transformation domain to jaxb object.
     */
    @Test
    void testTransformDomainToJaxb() {
        // Set up the domain object to transform
        final IBulkSubmission domain = new BulkSubmission();
        final long numberOfRequest = 8;
        final String sdtBulkReference = "A123456789";
        final String customerRef = "C10000123";
        final LocalDateTime createdDate = LocalDateTime.now();
        final String submissionStatus = "Validated";

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

        final BulkFeedbackResponseType jaxb = transformer.transformDomainToJaxb(domain);

        // Check the domain object has been transformed
        assertEquals(numberOfRequest, domain.getNumberOfRequest(), "The number of request does not match");
        assertEquals(sdtBulkReference, domain.getSdtBulkReference(), "The SDT Bulk Reference does not match");
        assertEquals(customerRef, domain.getCustomerReference(), "The Customer Reference does not match");
        assertEquals(createdDate, domain.getCreatedDate(), "The created date does not match");
        assertEquals(submissionStatus, domain.getSubmissionStatus(), "The submission status does not match");

        final List<ResponseType> responseTypes = jaxb.getResponses().getResponse();
        // Individual request 1
        ResponseType responseType = responseTypes.get(0);
        assertEquals(customerRequestReference1, responseType.getRequestId(),
                "Request ID for individual request 1 does not match");
        assertEquals(IndividualStatusCodeType.ACCEPTED.value(), responseType.getStatus().getCode().value(),
                "Status for individual request 1 does not match");
        assertNotNull(responseType.getResponseDetail(), "ResponseDetail should not be null");

        // Individual request 2
        responseType = responseTypes.get(1);
        assertEquals(customerRequestReference2, responseType.getRequestId(),
                "Request ID for individual request 2 does not match");
        assertEquals(IndividualStatusCodeType.RECEIVED.value(), responseType.getStatus().getCode().value(),
                "Status for individual request 2 does not match");
        assertNotNull(responseType.getResponseDetail(), "ResponseDetail should not be null");

        // Individual request 3
        responseType = responseTypes.get(2);
        assertEquals(customerRequestReference3, responseType.getRequestId(),
                "Request ID for individual request 3 does not match");
        assertEquals(IndividualStatusCodeType.REJECTED.value(), responseType.getStatus().getCode().value(),
                "Status for individual request 3 does not match");
        assertNotNull(responseType.getResponseDetail(), "ResponseDetail should not be null");

        // Check for the errors
        final IndividualStatusType individualStatusType = responseType.getStatus();
        assertEquals(IndividualStatusCodeType.REJECTED.value(), individualStatusType.getCode().value(),
                "Error status for individual request 3 does not match");
        final ErrorType errorType = individualStatusType.getError();
        assertEquals(errorCode, errorType.getCode(), "Error code for individual request 3 does not match");
        assertEquals(errorText, errorType.getDescription(),
                "Error text for individual request 3 does not match");
        assertNotNull(responseType.getResponseDetail(), "ResponseDetail should not be null");
    }
}
