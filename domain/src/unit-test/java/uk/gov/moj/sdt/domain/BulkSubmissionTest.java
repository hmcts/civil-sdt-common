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

package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BulkSubmissionTest extends AbstractSdtUnitTestBase {

    private IBulkSubmission bulkSubmission;

    private IServiceRequest mockServiceRequest;

    private IBulkCustomer mockBulkCustomer;

    private ITargetApplication mockTargetApplication;

    private  final String PAYLOAD = "Payload";

    private final LocalDateTime createdDate = LocalDateTime.now();

    @BeforeEach
    @Override
    public void setUpLocalTests() {
        mockServiceRequest = Mockito.mock(ServiceRequest.class);
        mockBulkCustomer = Mockito.mock(IBulkCustomer.class);
        mockTargetApplication = Mockito.mock(ITargetApplication.class);
        bulkSubmission = new BulkSubmission();
        bulkSubmission.setErrorCode("ERROR");
        bulkSubmission.setServiceRequest(mockServiceRequest);
        bulkSubmission.setBulkCustomer(mockBulkCustomer);
        bulkSubmission.setTargetApplication(mockTargetApplication);
        bulkSubmission.setCustomerReference("1");
        bulkSubmission.setCreatedDate(createdDate);
        bulkSubmission.setNumberOfRequest(1L);
        bulkSubmission.setId(1L);
        bulkSubmission.setPayload(PAYLOAD);
        bulkSubmission.setErrorText("This is an Error");

    }

    @DisplayName("Test Bulk Submission")
    @Test
    void testBulkSubmission() {
        //given
        bulkSubmission.setSubmissionStatus("Uploaded");
        //when
        bulkSubmission.markAsValidated();
        //then

        assertNotEquals(
            bulkSubmission.getSubmissionStatus(),
            IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus()
        );

        assertEquals("ERROR", bulkSubmission.getErrorCode(), "Error code should be set");
        assertEquals(mockServiceRequest, bulkSubmission.getServiceRequest());
        assertEquals(mockTargetApplication, bulkSubmission.getTargetApplication());
        assertEquals(mockBulkCustomer, bulkSubmission.getBulkCustomer());
        assertEquals("1", bulkSubmission.getCustomerReference());
        assertEquals(createdDate, bulkSubmission.getCreatedDate());
        assertEquals(1L, bulkSubmission.getNumberOfRequest());
        assertEquals("Validated", bulkSubmission.getSubmissionStatus());
        assertEquals("This is an Error", bulkSubmission.getErrorText());
        assertTrue(bulkSubmission.hasError());

        }

    @DisplayName("Test Bulk Submission Status")
    @Test
    void testBulkSubmissionStatus(){
        //given
        bulkSubmission.setSubmissionStatus("Completed");
        //when
        bulkSubmission.markAsValidated();
        //then
        assertNotEquals("Validated", bulkSubmission.getSubmissionStatus());
        assertEquals(PAYLOAD, bulkSubmission.getPayload());

    }

    @DisplayName("Test Bulk Submission toString")
    @Test
    void testBulkSubmissionToString(){
        assertTrue(bulkSubmission.toString().contains("ERROR"), "Should contain something");
    }

    @DisplayName("Test Bulk Submission getId")
    @Test
    void testBulkSubmissionGetId(){
        assertNotNull(bulkSubmission.getId());
    }

    @DisplayName("Test Bulk Submission hasError")
    @Test
    void testBulkSubmissionHasError(){
        bulkSubmission.setErrorCode(null);
        assertFalse(bulkSubmission.hasError());
    }

    @Test
    void testBulkSubmissionIndividualRequests() {
        List<IIndividualRequest> individualRequestList = new ArrayList<>();
        IIndividualRequest mockIndividualRequest = new IndividualRequest();
        bulkSubmission.setIndividualRequests(individualRequestList);
        bulkSubmission.addIndividualRequest(mockIndividualRequest);
        assertNotNull(bulkSubmission.getIndividualRequests());
        assertEquals(1, bulkSubmission.getIndividualRequests().size());
    }

    @Test
    void testBulkSubmissionMarkedAsComplete() {
        bulkSubmission.markAsCompleted();
        assertEquals(IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus(), bulkSubmission.getSubmissionStatus());
        assertNotNull(bulkSubmission.getUpdatedDate());
        assertNotNull(bulkSubmission.getCompletedDate());
    }

}
