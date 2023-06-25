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
package uk.gov.moj.sdt.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.ServiceRouting;
import uk.gov.moj.sdt.domain.ServiceType;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.services.utils.api.IMessagingUtility;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.cmc.CCDReferenceValidator;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;
import uk.gov.moj.sdt.utils.cmc.xml.XmlElementValueReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for Update Request Service.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
class UpdateRequestServiceTest extends AbstractSdtUnitTestBase {

    /**
     * Mocked Individual Request Dao object.
     */
    @Mock
    private IIndividualRequestDao mockIndividualRequestDao;

    /**
     * Mocked messaging utility.
     */
    @Mock
    private IMessagingUtility mockMessagingUtility;

    @Mock
    private XmlElementValueReader xmlReader;

    @Mock
    CCDReferenceValidator ccdReferenceValidator;

    private RequestTypeXmlNodeValidator requestTypeXmlNodeValidator;

    /**
     * Update Request Service object.
     */
    private UpdateRequestService updateRequestService;

    private static final String BULK_SUBMISSION_STATUS_IS_INCORRECT = "Bulk submission status is incorrect";

    private static final String BULK_SUBMISSION_COMPLETED_DATE_SHOULD_BE_POPULATED =
            "Bulk submission completed date should be populated";

    private static final String BULK_SUBMISSION_UPDATED_DATE_SHOULD_BE_POPULATED =
            "Bulk submission updated date should be populated";
    private static final String INDIVIDUAL_REQUEST_STATUS_IS_INCORRECT = "Individual request status is incorrect";

    private static final String SDT_REQUEST_REF = "MCOL_IREQ_0001";

    private static final String DUMMY_TARGET_RESP = "response";

    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    @Override
    public void setUp() {
        // Instantiate all the mocked objects and set them in the target application submission service
        final GenericXmlParser genericParser = new GenericXmlParser();
        genericParser.setEnclosingTag("targetAppDetail");
        requestTypeXmlNodeValidator = new RequestTypeXmlNodeValidator(ccdReferenceValidator, xmlReader);
        updateRequestService = new UpdateRequestService(mockIndividualRequestDao,
                                                        genericParser,
                                                        mockMessagingUtility,
                                                        requestTypeXmlNodeValidator
        );

    }

    /**
     * Method to test all successful scenario for the update individual request method.
     */
    @Test
    void updateIndividualRequestRejected() {
        final IIndividualRequest individualRequestParam = this.getRejectedIndividualRequestFromTargetApp();

        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(individualRequestParam.getSdtRequestReference());
        this.setUpIndividualRequest(individualRequest);

        when(this.mockIndividualRequestDao.getRequestBySdtReference(individualRequestParam
                        .getSdtRequestReference())).thenReturn(individualRequest);

        this.mockIndividualRequestDao.persist(individualRequest);

        final IBulkSubmission bulkSubmission = individualRequest.getBulkSubmission();

        when(this.mockIndividualRequestDao.queryAsCount(same(IndividualRequest.class), any(Supplier.class))).thenReturn(0L);

        mockIndividualRequestDao.persist(bulkSubmission);

        // Setup dummy target response
        SdtContext.getContext().setRawInXml(DUMMY_TARGET_RESP);

        this.updateRequestService.updateIndividualRequest(individualRequestParam);
        verify(mockIndividualRequestDao).queryAsCount(same(IndividualRequest.class), any(Supplier.class));

        assertEquals(IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus(),
                individualRequest.getRequestStatus(), INDIVIDUAL_REQUEST_STATUS_IS_INCORRECT);
        assertNotNull(individualRequest.getErrorLog(), "Individual request should have error");
        assertEquals(IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus(),
                individualRequest.getBulkSubmission().getSubmissionStatus(), BULK_SUBMISSION_STATUS_IS_INCORRECT);
        assertNotNull(individualRequest.getBulkSubmission().getCompletedDate(),
                BULK_SUBMISSION_COMPLETED_DATE_SHOULD_BE_POPULATED);
        assertNotNull(individualRequest.getBulkSubmission().getUpdatedDate(),
                BULK_SUBMISSION_UPDATED_DATE_SHOULD_BE_POPULATED);
    }

    /**
     * Method to test all successful scenario for the update individual request method.
     */
    @Test
    void updateIndividualRequestAccepted() {
        final IIndividualRequest individualRequestParam = this.getAcceptedIndividualRequestFromTargetApp();

        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(individualRequestParam.getSdtRequestReference());
        this.setUpIndividualRequest(individualRequest);

        when(this.mockIndividualRequestDao.getRequestBySdtReference(individualRequestParam
                        .getSdtRequestReference())).thenReturn(individualRequest);

        this.mockIndividualRequestDao.persist(individualRequest);

        when(this.mockIndividualRequestDao.queryAsCount(same(IndividualRequest.class), any(Supplier.class))).thenReturn(0L);

        final IBulkSubmission bulkSubmission = individualRequest.getBulkSubmission();
        mockIndividualRequestDao.persist(bulkSubmission);

        // Setup dummy target response
        SdtContext.getContext().setRawInXml(DUMMY_TARGET_RESP);

        this.updateRequestService.updateIndividualRequest(individualRequestParam);
        verify(mockIndividualRequestDao).queryAsCount(same(IndividualRequest.class), any(Supplier.class));

        assertEquals(IIndividualRequest.IndividualRequestStatus.ACCEPTED.getStatus(),
                individualRequest.getRequestStatus(), INDIVIDUAL_REQUEST_STATUS_IS_INCORRECT);
        assertNull(individualRequest.getErrorLog(), "Individual request should not have error");
        assertEquals(IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus(),
                individualRequest.getBulkSubmission().getSubmissionStatus(), BULK_SUBMISSION_STATUS_IS_INCORRECT);
        assertNotNull(individualRequest.getBulkSubmission().getCompletedDate(),
                BULK_SUBMISSION_COMPLETED_DATE_SHOULD_BE_POPULATED);
        assertNotNull(individualRequest.getBulkSubmission().getUpdatedDate(),
                BULK_SUBMISSION_UPDATED_DATE_SHOULD_BE_POPULATED);
    }

    /**
     * Method to test message resubmission scenario for the update individual request method.
     */
    @Test
    void updateIndividualRequestResubmit() {
        final IIndividualRequest individualRequestParam = this.getResubmitMessageIndividualRequestFromTargetApp();

        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(individualRequestParam.getSdtRequestReference());
        this.setUpIndividualRequest(individualRequest);

        when(this.mockIndividualRequestDao.getRequestBySdtReference(individualRequestParam
                        .getSdtRequestReference())).thenReturn(individualRequest);

        this.mockIndividualRequestDao.persist(individualRequest);

        when(this.mockIndividualRequestDao.queryAsCount(same(IndividualRequest.class), any(Supplier.class)))
            .thenReturn(0L);


        final IBulkSubmission bulkSubmission = individualRequest.getBulkSubmission();
        mockIndividualRequestDao.persist(bulkSubmission);

        this.mockMessagingUtility.enqueueRequest(individualRequest);

        // Setup dummy target response
        SdtContext.getContext().setRawInXml(DUMMY_TARGET_RESP);

        this.updateRequestService.updateIndividualRequest(individualRequestParam);
        verify(mockIndividualRequestDao).queryAsCount(same(IndividualRequest.class), any(Supplier.class));
        verify(mockMessagingUtility, times(2)).enqueueRequest(any());

        assertEquals(IIndividualRequest.IndividualRequestStatus.RECEIVED.getStatus(),
                individualRequest.getRequestStatus(), INDIVIDUAL_REQUEST_STATUS_IS_INCORRECT);
        assertNull(individualRequest.getErrorLog(), "Individual request should not have error");
        assertEquals(0, individualRequest.getForwardingAttempts(),
                "Forwarding attempts should be reset to 0");
        assertEquals(IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus(),
                individualRequest.getBulkSubmission().getSubmissionStatus(),
                BULK_SUBMISSION_STATUS_IS_INCORRECT);
        assertNotNull(individualRequest.getBulkSubmission().getCompletedDate(),
                BULK_SUBMISSION_COMPLETED_DATE_SHOULD_BE_POPULATED);
        assertNotNull(individualRequest.getBulkSubmission().getUpdatedDate(),
                BULK_SUBMISSION_UPDATED_DATE_SHOULD_BE_POPULATED);
    }

    /**
     * This method is to test the scenario that the individual request from the
     * target app is not found in the database.
     */
    @Test
    void updateIndividualRequestFail() {
        final IIndividualRequest individualRequestParam = this.getAcceptedIndividualRequestFromTargetApp();
        individualRequestParam.setSdtRequestReference("TEST");

        when(this.mockIndividualRequestDao.getRequestBySdtReference(individualRequestParam
                        .getSdtRequestReference())).thenReturn(null);

        this.updateRequestService.updateIndividualRequest(individualRequestParam);
        verify(mockIndividualRequestDao).getRequestBySdtReference(
                individualRequestParam.getSdtRequestReference());
        assertTrue(true, "Expected to pass");
    }

    /**
     * Method to test all successful scenario for the update individual request method
     * where the bulk submission still has some requests pending to process.
     */
    @Test
    void updateIndividualRequestBulkRequestPending() {
        final IIndividualRequest individualRequestParam = this.getAcceptedIndividualRequestFromTargetApp();

        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(individualRequestParam.getSdtRequestReference());
        this.setUpIndividualRequest(individualRequest);

        when(this.mockIndividualRequestDao.getRequestBySdtReference(individualRequestParam
                        .getSdtRequestReference())).thenReturn(individualRequest);

        individualRequest.setRequestStatus(individualRequestParam.getRequestStatus());

        this.mockIndividualRequestDao.persist(individualRequest);

        final List<IIndividualRequest> indRequests = new ArrayList<>();
        indRequests.add(individualRequest);

        when(this.mockIndividualRequestDao.queryAsCount(same(IndividualRequest.class), any(Supplier.class)))
            .thenReturn(Long.valueOf(indRequests.size()));

        // Setup dummy target response
        SdtContext.getContext().setRawInXml(DUMMY_TARGET_RESP);

        this.updateRequestService.updateIndividualRequest(individualRequestParam);
        verify(mockIndividualRequestDao).queryAsCount(same(IndividualRequest.class), any(Supplier.class));
        assertTrue(true, "Expected to pass");
    }

    /**
     * @return the individual request as obtained from the update item transformer
     */
    private IIndividualRequest getRejectedIndividualRequestFromTargetApp() {
        final IIndividualRequest domainObject = new IndividualRequest();
        domainObject.setSdtRequestReference(SDT_REQUEST_REF);

        final IErrorLog errorLog = new ErrorLog("INVALID_NAME", "Invalid name");
        domainObject.markRequestAsRejected(errorLog);

        return domainObject;
    }

    /**
     * @return the individual request as obtained from the update item transformer
     */
    private IIndividualRequest getResubmitMessageIndividualRequestFromTargetApp() {
        final IIndividualRequest domainObject = new IndividualRequest();
        domainObject.setSdtRequestReference(SDT_REQUEST_REF);

        domainObject.setRequestStatus(IIndividualRequest.IndividualRequestStatus.RESUBMIT_MESSAGE.getStatus());

        return domainObject;
    }

    /**
     * @return the individual request as obtained from the update item transformer
     */
    private IIndividualRequest getAcceptedIndividualRequestFromTargetApp() {
        final IIndividualRequest domainObject = new IndividualRequest();
        domainObject.setSdtRequestReference(SDT_REQUEST_REF);

        domainObject.markRequestAsAccepted();

        return domainObject;
    }

    /**
     * Set up a valid individual request object.
     *
     * @param request the individual request
     */
    private void setUpIndividualRequest(final IIndividualRequest request) {
        final IBulkSubmission bulkSubmission = new BulkSubmission();
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        final ITargetApplication targetApp = new TargetApplication();

        targetApp.setId(1L);
        targetApp.setTargetApplicationCode("MCOL");
        targetApp.setTargetApplicationName("TEST_TargetApp");
        final Set<IServiceRouting> serviceRoutings = new HashSet<>();

        final ServiceRouting serviceRouting = new ServiceRouting();
        serviceRouting.setId(1L);
        serviceRouting.setWebServiceEndpoint("MCOL_END_POINT");

        final IServiceType serviceType = new ServiceType();
        serviceType.setId(1L);
        serviceType.setName("RequestTest1");
        serviceType.setDescription("RequestTestDesc1");
        serviceType.setStatus("RequestTestStatus");

        serviceRouting.setServiceType(serviceType);

        serviceRoutings.add(serviceRouting);

        targetApp.setServiceRoutings(serviceRoutings);

        bulkSubmission.setTargetApplication(targetApp);

        bulkCustomer.setId(1L);
        bulkCustomer.setSdtCustomerId(10L);

        bulkSubmission.setBulkCustomer(bulkCustomer);
        bulkSubmission.setCustomerReference("TEST_CUST_REF");
        bulkSubmission.setId(1L);
        bulkSubmission.setNumberOfRequest(1);
        final List<IIndividualRequest> requests = new ArrayList<>();
        requests.add(request);

        bulkSubmission.setIndividualRequests(requests);

        request.setBulkSubmission(bulkSubmission);
    }

}
