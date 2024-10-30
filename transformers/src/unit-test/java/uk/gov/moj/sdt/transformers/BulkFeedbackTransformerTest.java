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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.IndividualStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.IndividualStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkRequestStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.ResponseType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
    @Override
    protected void setUpLocalTests() {
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

        assertEquals(sdtBulkReference, domain.getSdtBulkReference(), "SDT Bulk Reference does not match");

        IBulkCustomer bulkCustomer = domain.getBulkCustomer();
        assertNotNull(bulkCustomer, "Bulk customer should not be null");
        assertEquals(sdtCustomerId, bulkCustomer.getSdtCustomerId(), "SDT Customer ID does not match");
    }

    /**
     * Test the transformation from domain to jaxb object.
     * @param individualRequestStatus The status of the individual request
     * @param expectedIndividualStatusCodeType The expected status of the individual status type
     */
    @ParameterizedTest
    @MethodSource("domainAndJaxbStatuses")
    void testTransformDomainToJaxb(IIndividualRequest.IndividualRequestStatus individualRequestStatus,
                                   IndividualStatusCodeType expectedIndividualStatusCodeType) {
        // Set up the domain object to transform
        final IBulkSubmission domain = new BulkSubmission();
        final long numberOfRequest = 8;
        final String sdtBulkReference = "A123456789";
        final String customerRef = "C10000123";
        final LocalDateTime createdDate = LocalDateTime.now();
        final String submissionStatus = "Validated";

        final String customerRequestRef = "CustReqRef";
        final String errorCode = "87";
        final String errorText = "Specified claim does not belong to the requesting customer.";

        domain.setNumberOfRequest(numberOfRequest);
        domain.setSdtBulkReference(sdtBulkReference);
        domain.setCustomerReference(customerRef);
        domain.setCreatedDate(createdDate);
        domain.setSubmissionStatus(submissionStatus);

        // Setup individual request
        final List<IIndividualRequest> individualRequests = new ArrayList<>();

        IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setCustomerRequestReference(customerRequestRef);
        individualRequest.setRequestStatus(individualRequestStatus.getStatus());

        if (IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus()
                .equals(individualRequestStatus.getStatus())) {
            IErrorLog errorLog = new ErrorLog(errorCode, errorText);
            individualRequest.setErrorLog(errorLog);
        }

        individualRequests.add(individualRequest);
        domain.setIndividualRequests(individualRequests);

        final BulkFeedbackResponseType jaxb = transformer.transformDomainToJaxb(domain);

        // Check the domain object has been transformed
        BulkRequestStatusType bulkRequestStatusType = jaxb.getBulkRequestStatus();
        assertNotNull(bulkRequestStatusType, "Bulk request status type should not be null");

        assertEquals(StatusCodeType.OK.value(),
                     bulkRequestStatusType.getStatus().getCode().value(),
                     "The status does not match");
        assertEquals(numberOfRequest,
                     bulkRequestStatusType.getRequestCount(),
                     "The number of requests does not match");
        assertEquals(sdtBulkReference,
                     bulkRequestStatusType.getSdtBulkReference(),
                     "The SDT Bulk Reference does not match");
        assertEquals(customerRef,
                     bulkRequestStatusType.getCustomerReference(),
                     "The Customer Reference does not match");
        assertEquals(Utilities.convertLocalDateTimeToCalendar(createdDate),
                     bulkRequestStatusType.getSubmittedDate(),
                     "The created date does not match");
        assertEquals(submissionStatus,
                     bulkRequestStatusType.getBulkStatus().getCode().value(),
                     "The submission status does not match");
        assertEquals(AbstractTransformer.SDT_SERVICE,
                     bulkRequestStatusType.getSdtService(),
                     "The SDT service does not match");

        final List<ResponseType> responseTypes = jaxb.getResponses().getResponse();
        assertEquals(1, responseTypes.size(), "Unexpected number of response types");

        ResponseType responseType = responseTypes.get(0);
        assertEquals(customerRequestRef,
                     responseType.getRequestId(),
                     "Request ID for individual request does not match");
        assertNotNull(responseType.getResponseDetail(), "ResponseDetail should not be null");

        IndividualStatusType individualStatusType = responseType.getStatus();
        assertNotNull(individualStatusType, "Individual status type should not be null");
        assertEquals(expectedIndividualStatusCodeType.value(), individualStatusType.getCode().value(),
                "Status for individual request does not match");

        final ErrorType errorType = individualStatusType.getError();
        if (IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus()
                .equals(individualRequestStatus.getStatus())) {
            assertNotNull(errorType, "Error type should not be null");
            assertEquals(errorCode, errorType.getCode(), "Error code for individual request does not match");
            assertEquals(errorText, errorType.getDescription(), "Error text for individual request does not match");
        } else {
            assertNull(errorType, "Error type should be null");
        }
    }

    private static Stream<Arguments> domainAndJaxbStatuses() {
        return Stream.of(
                arguments(
                        IIndividualRequest.IndividualRequestStatus.ACCEPTED,
                        IndividualStatusCodeType.ACCEPTED
                ),
                arguments(
                        IIndividualRequest.IndividualRequestStatus.AWAITING_DATA,
                        IndividualStatusCodeType.AWAITING_DATA
                ),
                arguments(
                        IIndividualRequest.IndividualRequestStatus.CASE_LOCKED,
                        IndividualStatusCodeType.CASE_LOCKED
                ),
                arguments(
                        IIndividualRequest.IndividualRequestStatus.FORWARDED,
                        IndividualStatusCodeType.FORWARDED
                ),
                arguments(
                        IIndividualRequest.IndividualRequestStatus.INITIALLY_ACCEPTED,
                        IndividualStatusCodeType.INITIALLY_ACCEPTED
                ),
                arguments(
                        IIndividualRequest.IndividualRequestStatus.RECEIVED,
                        IndividualStatusCodeType.RECEIVED
                ),
                arguments(
                        IIndividualRequest.IndividualRequestStatus.REJECTED,
                        IndividualStatusCodeType.REJECTED
                )
        );
    }
}
