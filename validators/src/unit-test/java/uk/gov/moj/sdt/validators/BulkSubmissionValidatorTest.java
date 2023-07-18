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
 * $Id: SubmitQueryEnricherTest.java 17032 2013-09-12 15:25:50Z agarwals $
 * $LastChangedRevision: 17032 $
 * $LastChangedDate: 2013-09-12 16:25:50 +0100 (Thu, 12 Sep 2013) $
 * $LastChangedBy: agarwals $ */

package uk.gov.moj.sdt.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;
import uk.gov.moj.sdt.validators.exception.CustomerNotSetupException;
import uk.gov.moj.sdt.validators.exception.CustomerReferenceNotUniqueException;
import uk.gov.moj.sdt.validators.exception.RequestCountMismatchException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.moj.sdt.domain.api.IBulkSubmission.BulkRequestStatus.COMPLETED;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.CUST_NOT_SETUP;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.DUPLD_CUST_REQID;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.DUP_CUST_FILEID;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.DUP_CUST_REQID;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.INVALID_CMC_REQUEST;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.NO_VALID_REQS;
import static uk.gov.moj.sdt.domain.api.IErrorMessage.ErrorCode.REQ_COUNT_MISMATCH;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;
import static uk.gov.moj.sdt.utils.cmc.RequestType.CLAIM;
import static uk.gov.moj.sdt.utils.cmc.RequestType.JUDGMENT;

/**
 * Tests for {@link BulkSubmissionValidatorTest}.
 *
 * @author d120520
 */
@ExtendWith(MockitoExtension.class)
class BulkSubmissionValidatorTest extends AbstractValidatorUnitTest {

    private static final String ERROR_CODE_INCORRECT = "Error code incorrect";

    private static final String SUBSTITUTION_VALUE_INCORRECT = "Substitution value incorrect";

    private static final Long SDT_CUSTOMER_ID = 12345L;

    private static final String APP_MCOL = "MCOL";

    private static final String NODE_CLAIM_NUMBER = "claimNumber";

    /**
     * Contact details for assistance.
     */
    private static final String CONTACT = "THE MOJ";

    /**
     * The bulk customer dao.
     */
    @Mock
    private IBulkCustomerDao mockIBulkCustomerDao;

    /**
     * Parameter cache.
     */
    @Mock
    private ICacheable globalParameterCache;

    /**
     * Error messages cache.
     */
    @Mock
    private ICacheable errorMessagesCache;

    /**
     * IBulkSubmission dao.
     */
    @Mock
    private IBulkSubmissionDao mockIBulkSubmissionDao;

    @Mock
    private RequestTypeXmlNodeValidator requestTypeXmlNodeValidator;

    /**
     * Subject for test.
     */
    private BulkSubmissionValidator validator;

    /**
     * bulk submission.
     */
    private BulkSubmission bulkSubmission;

    /**
     * The bulk customer.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * Error message.
     */
    private IErrorMessage errorMessage;

    /**
     * List of individual requests.
     */
    private List<IIndividualRequest> individualRequests;

    /**
     * The number of requests.
     */
    private static final long NUMBER_OF_REQUESTS = 1L;

    /**
     * Request id.
     */

    private static final long REQUEST_ID = 20L;

    /**
     * Data retention period.
     */
    private static final int DATA_RETENTION_PERIOD = 90;

    /**
     * SDT Bulk reference.
     */
    private static final String SDT_BULK_REFERENCE = "sdtBulkReference";

    /**
     * Current date time.
     */
    private static final LocalDateTime NOW = LocalDateTime.now();

    /**
     * Setup of the Validator and Domain class instance.
     */
    @Override
    public void setUpLocalTests() {
        // subject of test
        validator = new BulkSubmissionValidator(mockIBulkCustomerDao,
                                                globalParameterCache,
                                                errorMessagesCache,
                                                mockIBulkSubmissionDao,
                                                requestTypeXmlNodeValidator,
                                                new ConcurrentHashMap<>());
    }

    /**
     * This method sets up a bulk submission and puts a domain object in there (bulk customer).
     *
     * @param numberOfRequests the number of requests in this submission
     * @param bulkCustomer the bulk customer
     * @param individualRequests the list of individual requests
     * @param application the application name for this bulk submission (MCOL, PCOL etc)
     */

    /**
     * The purpose of this test is to have a clean run through these three conditions.
     * 1) The customer has access to the target application
     * 2) No invalid bulk submission
     * 3) The individual requests matches the bulk submission requests
     * <p>
     * <p>
     * Test conditions used that will pass this test are:
     * create a bulk submission with one request, one customer, one individual requests and using the MCOL app
     */
    @Test
    void testAllSuccess() {

        // set up a bulk customer to use the MCOL application
        bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));

        // create an individual request
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setId(REQUEST_ID);
        individualRequests = new ArrayList<>();
        individualRequests.add(individualRequest);

        createBulkSubmission(NUMBER_OF_REQUESTS, bulkCustomer, individualRequests, APP_MCOL);

        // set up the mock objects
        when(mockIBulkCustomerDao.getBulkCustomerBySdtId(SDT_CUSTOMER_ID)).thenReturn(bulkCustomer);

        // inject the bulk customer into the validator
        validator.setBulkCustomerDao(mockIBulkCustomerDao);

        when(mockIBulkSubmissionDao.getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                DATA_RETENTION_PERIOD)).thenReturn(null);

        validator.setBulkSubmissionDao(mockIBulkSubmissionDao);

        setupDataRetentionCache();

        bulkSubmission.accept(validator, null);

        verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(SDT_CUSTOMER_ID);
        verify(mockIBulkSubmissionDao).getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                DATA_RETENTION_PERIOD);
        verify(globalParameterCache).getValue(any(), anyString());

    }

    /**
     * The purpose of this test is to have a clean run through these three conditions.
     * 1) The customer has access to the target application
     * 2) No invalid bulk submission
     * 3) The second individual request has a duplicate request id
     */
    @Test
    void testDuplicateIndividualRequest() {

        // set up a bulk customer to use the MCOL application
        bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));

        // create an individual request
        individualRequests = new ArrayList<>();
        IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setId(1L);
        individualRequest.setCustomerRequestReference("Duplicate");
        individualRequests.add(individualRequest);
        // Set the duplicate request
        individualRequest = new IndividualRequest();
        individualRequest.setId(2L);
        individualRequest.setCustomerRequestReference("Duplicate");
        individualRequests.add(individualRequest);

        createBulkSubmission(individualRequests.size(), bulkCustomer, individualRequests, APP_MCOL);

        // set up the mock objects
        when(mockIBulkCustomerDao.getBulkCustomerBySdtId(SDT_CUSTOMER_ID)).thenReturn(bulkCustomer);

        // inject the bulk customer into the validator
        validator.setBulkCustomerDao(mockIBulkCustomerDao);

        when(mockIBulkSubmissionDao.getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                DATA_RETENTION_PERIOD)).thenReturn(null);

        validator.setBulkSubmissionDao(mockIBulkSubmissionDao);

        setupDataRetentionCache();

        // Set up Error messages cache
        errorMessage = createErrorMessage(DUPLD_CUST_REQID,
                "Unique Request Identifier has been specified more than once within the originating Bulk Request.");
        when(errorMessagesCache.getValue(IErrorMessage.class, DUPLD_CUST_REQID.name())).thenReturn(errorMessage);

        validator.setErrorMessagesCache(errorMessagesCache);

        bulkSubmission.accept(validator, null);

        // Check the duplicate individual request has been rejected
        assertEquals(REJECTED.getStatus(), bulkSubmission
                .getIndividualRequests().get(1).getRequestStatus());
    }

    /**
     * This test will fail on the check customer has access to MCOL application and catch the exception.
     */
    @Test
    void testCustomerDoesNotHaveAccess() {
        try {

            // set up the data we are going to use for this customer
            // set up bulk customer with the application it can use

            // set up a bulk customer to use the PCOL application to make it error as the bulk submission sets MCOL.
            bulkCustomer = createCustomer(createBulkCustomerApplications("PCOL"));

            // set up the mock objects
            when(mockIBulkCustomerDao.getBulkCustomerBySdtId(SDT_CUSTOMER_ID)).thenReturn(bulkCustomer);

            // inject the bulk customer into the validator
            validator.setBulkCustomerDao(mockIBulkCustomerDao);

            // create an individual request
            final IIndividualRequest individualRequest = new IndividualRequest();
            individualRequest.setId(REQUEST_ID);
            individualRequests = new ArrayList<>();
            individualRequests.add(individualRequest);

            createBulkSubmission(NUMBER_OF_REQUESTS, bulkCustomer, individualRequests, APP_MCOL);

            setupContactDetailsCache();

            // Set up Error messages cache
            errorMessage = createErrorMessage(CUST_NOT_SETUP,
                    "The Bulk Customer organisation is not setup to send Service Request messages to the " +
                            "{0}. Please contact {1} for assistance.");
            when(errorMessagesCache.getValue(IErrorMessage.class, CUST_NOT_SETUP.name())).thenReturn(errorMessage);

            validator.setErrorMessagesCache(errorMessagesCache);

            bulkSubmission.accept(validator, null);
            fail("Test failed to throw CustomerNotSetupException ");

        } catch (final CustomerNotSetupException e) {
            verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(SDT_CUSTOMER_ID);

            assertEquals(CUST_NOT_SETUP.name(), e.getErrorCode(), ERROR_CODE_INCORRECT);
            // CHECKSTYLE:OFF
            assertEquals("The Bulk Customer organisation is not setup to send Service Request messages to the MCOL. "
                    + "Please contact " + CONTACT + " for assistance.", e.getErrorDescription(),
                    SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * This test will contain an invalid bulk submission and catch the exception.
     */
    @Test
    void testInvalidBulkSubmission() {

        try {
            // set up a bulk customer to use the MCOL application to make it error as the bulk submission sets MCOL.
            bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));

            // set up the mock objects
            when(mockIBulkCustomerDao.getBulkCustomerBySdtId(SDT_CUSTOMER_ID)).thenReturn(bulkCustomer);

            // inject the bulk customer into the validator
            validator.setBulkCustomerDao(mockIBulkCustomerDao);

            // create an individual request
            final IIndividualRequest individualRequest = new IndividualRequest();
            individualRequest.setId(REQUEST_ID);
            individualRequests = new ArrayList<>();
            individualRequests.add(individualRequest);

            createBulkSubmission(NUMBER_OF_REQUESTS, bulkCustomer, individualRequests, APP_MCOL);

            when(mockIBulkSubmissionDao.getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                    DATA_RETENTION_PERIOD)).thenReturn(bulkSubmission);

            validator.setBulkSubmissionDao(mockIBulkSubmissionDao);

            setupDataRetentionCache();

            // Set up Error messages cache
            errorMessage = createErrorMessage(DUP_CUST_FILEID,
                    "Duplicate User File Reference {0} supplied. This was previously used to submit a " +
                            "Bulk Request on {1} and the SDT Bulk Reference {2} was allocated.");
            when(errorMessagesCache.getValue(IErrorMessage.class, DUP_CUST_FILEID.name())).thenReturn(errorMessage);

            validator.setErrorMessagesCache(errorMessagesCache);

            bulkSubmission.accept(validator, null);
            fail("Test failed to throw CustomerReferenceNotUniqueException ");

        } catch (final CustomerReferenceNotUniqueException e) {
            verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(SDT_CUSTOMER_ID);
            verify(mockIBulkSubmissionDao).getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                    DATA_RETENTION_PERIOD);
            assertEquals(DUP_CUST_FILEID.name(), e.getErrorCode(), ERROR_CODE_INCORRECT);
            assertEquals("Duplicate User File Reference " + bulkSubmission.getCustomerReference() + " supplied. " +
                    "This was previously used to submit a Bulk Request on " + Utilities.formatDateTimeForMessage(NOW) +
                    " and the SDT Bulk Reference " + SDT_BULK_REFERENCE + " was allocated.", e.getErrorDescription(),
                    SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * This test will have an incorrect number of requests and catch the exception.
     */
    @Test
    void testRequestCountDoesNotMatch() {

        final long mismatchTotal = 15;
        try {
            // set up a bulk customer to use the MCOL application
            bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));

            // create an individual request
            final IIndividualRequest individualRequest = new IndividualRequest();
            individualRequest.setId(REQUEST_ID);
            individualRequests = new ArrayList<>();
            individualRequests.add(individualRequest);

            createBulkSubmission(mismatchTotal, bulkCustomer, individualRequests, APP_MCOL);

            // set up the mock objects
            when(mockIBulkCustomerDao.getBulkCustomerBySdtId(SDT_CUSTOMER_ID)).thenReturn(bulkCustomer);

            // inject the bulk customer into the validator
            validator.setBulkCustomerDao(mockIBulkCustomerDao);

            when(mockIBulkSubmissionDao.getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                    DATA_RETENTION_PERIOD)).thenReturn(null);

            validator.setBulkSubmissionDao(mockIBulkSubmissionDao);

            setupDataRetentionCache();

            // Set up Error messages cache
            errorMessage = createErrorMessage(REQ_COUNT_MISMATCH,
                    "Unexpected Total Number of Requests identified. {0} requested identified, " +
                            "{1} requests expected in Bulk Request {2}.");
            when(errorMessagesCache.getValue(IErrorMessage.class, REQ_COUNT_MISMATCH.name())).thenReturn(errorMessage);

            validator.setErrorMessagesCache(errorMessagesCache);

            bulkSubmission.accept(validator, null);

            fail("Test failed to throw RequestCountMismatchException ");
        } catch (final RequestCountMismatchException e) {
            verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(SDT_CUSTOMER_ID);
            verify(mockIBulkSubmissionDao).getBulkSubmission(bulkCustomer, bulkSubmission.getCustomerReference(),
                    DATA_RETENTION_PERIOD);
            assertEquals(REQ_COUNT_MISMATCH.name(), e.getErrorCode(), ERROR_CODE_INCORRECT);
            assertEquals("Unexpected Total Number of Requests identified. 1 requested identified, " +
                    mismatchTotal + " requests expected in Bulk Request " + bulkSubmission.getCustomerReference() + ".",
                    e.getErrorDescription(), SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * This test the scenario where all individual requests of a bulk submission are rejected.
     */
    @Test
    void testIndividualRequestAllRejected() {
        // set up the data we are going to use for this customer
        // set up bulk customer with the application it can use
        bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));

        // inject the bulk customer into the validator
        validator.setBulkCustomerDao(mockIBulkCustomerDao);

        // reset the list of individual request
        individualRequests = new ArrayList<>();

        // Rejected error
        IErrorLog errorLog =
                new ErrorLog(DUP_CUST_REQID.name(), "Duplicate Unique Request Identifier submitted {0}");

        // create an individual request 1
        IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setId(1L);
        individualRequest.markRequestAsRejected(errorLog);
        individualRequests.add(individualRequest);

        // create an individual request 2
        individualRequest = new IndividualRequest();
        individualRequest.setId(2L);
        individualRequest.markRequestAsRejected(errorLog);
        individualRequests.add(individualRequest);

        // create an individual request 3
        individualRequest = new IndividualRequest();
        individualRequest.setId(3L);
        individualRequest.markRequestAsRejected(errorLog);
        individualRequests.add(individualRequest);

        createBulkSubmission(3, bulkCustomer, individualRequests, APP_MCOL);

        // Set up Error messages cache
        errorMessage = createNoValidReqsErrorMessage();

        when(errorMessagesCache.getValue(IErrorMessage.class, NO_VALID_REQS.name())).thenReturn(errorMessage);

        validator.setErrorMessagesCache(errorMessagesCache);

        // Test the method
        validator.checkIndividualRequests(bulkSubmission);

        assertEquals(IBulkSubmission.BulkRequestStatus.COMPLETED.getStatus(),
                bulkSubmission.getSubmissionStatus());
        assertEquals(NO_VALID_REQS.name(), bulkSubmission.getErrorCode());
    }

    @Test
    void testValidateCMCRequestsAllValid() {
        bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));
        String requestPayload = "test request payload";

        individualRequests = new ArrayList<>();

        IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setId(1L);
        individualRequest.setRequestType(JUDGMENT.getType());
        individualRequest.setRequestPayload(requestPayload.getBytes(StandardCharsets.UTF_8));
        individualRequests.add(individualRequest);

        createBulkSubmission(1, bulkCustomer, individualRequests, APP_MCOL);

        when(requestTypeXmlNodeValidator.isCCDReference(requestPayload, NODE_CLAIM_NUMBER)).thenReturn(true);
        when(requestTypeXmlNodeValidator.isValidRequestType(JUDGMENT.getType())).thenReturn(true);

        validator.validateCMCRequests(bulkSubmission);

        assertNotEquals(REJECTED.getStatus(), individualRequest.getRequestStatus(),
                "Individual request status should not be rejected");
        assertNull(individualRequest.getErrorLog(), "Individual request should not have an error log");

        assertNull(bulkSubmission.getErrorCode(), "Bulk submission should not have an error code");
        assertNull(bulkSubmission.getErrorText(), "Bulk submission should not have error text");
        assertNull(bulkSubmission.getSubmissionStatus(), "Bulk submission has unexpected status");

        verify(requestTypeXmlNodeValidator).isCCDReference(requestPayload, NODE_CLAIM_NUMBER);
        verify(requestTypeXmlNodeValidator).isValidRequestType(JUDGMENT.getType());
    }

    @Test
    void testValidateCMCRequestsAllInvalid() {
        bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));
        String requestPayload = "test request payload";

        individualRequests = new ArrayList<>();

        IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setId(1L);
        individualRequest.setRequestType(CLAIM.getType());
        individualRequest.setCustomerRequestReference("customerRequestRef");
        individualRequest.setRequestPayload(requestPayload.getBytes(StandardCharsets.UTF_8));
        individualRequests.add(individualRequest);

        createBulkSubmission(1, bulkCustomer, individualRequests, APP_MCOL);

        when(requestTypeXmlNodeValidator.isCCDReference(requestPayload, NODE_CLAIM_NUMBER)).thenReturn(true);
        when(requestTypeXmlNodeValidator.isValidRequestType(CLAIM.getType())).thenReturn(false);

        IErrorMessage invalidCMCRequestErr = createErrorMessage(INVALID_CMC_REQUEST,
                "Individual request {0} for CMC has an invalid request type {1}");
        when(errorMessagesCache.getValue(IErrorMessage.class, INVALID_CMC_REQUEST.name()))
                .thenReturn(invalidCMCRequestErr);

        IErrorMessage noValidReqsErr = createNoValidReqsErrorMessage();
        when(errorMessagesCache.getValue(IErrorMessage.class, NO_VALID_REQS.name())).thenReturn(noValidReqsErr);

        validator.validateCMCRequests(bulkSubmission);

        assertEquals(REJECTED.getStatus(), individualRequest.getRequestStatus(),
                "Individual request status should be rejected");
        IErrorLog errorLog = individualRequest.getErrorLog();
        assertNotNull(errorLog, "Individual request should have an error log");
        assertEquals(INVALID_CMC_REQUEST.name(), errorLog.getErrorCode(),
                "Individual request error log has unexpected error code");
        assertEquals("Individual request customerRequestRef for CMC has an invalid request type mcolClaim",
                errorLog.getErrorText(), "Individual request error log has unexpected error text");
        assertNull(individualRequest.getRequestPayload(), "Individual request should not have a request payload");

        assertEquals(NO_VALID_REQS.name(), bulkSubmission.getErrorCode(), "Bulk submission has unexpected error code");
        assertEquals("The submitted Bulk Request Customer Reference does not contain valid individual Requests.",
                bulkSubmission.getErrorText(), "Bulk submission has unexpected error text");
        assertEquals(COMPLETED.getStatus(), bulkSubmission.getSubmissionStatus(),
                "Bulk submission has unexpected submission status");

        verify(requestTypeXmlNodeValidator).isCCDReference(requestPayload, NODE_CLAIM_NUMBER);
        verify(requestTypeXmlNodeValidator).isValidRequestType(CLAIM.getType());
        verify(errorMessagesCache).getValue(IErrorMessage.class, INVALID_CMC_REQUEST.name());
        verify(errorMessagesCache).getValue(IErrorMessage.class, NO_VALID_REQS.name());
    }

    @Test
    void testValidateCMCRequestsOneAlreadyRejected() {
        bulkCustomer = createCustomer(createBulkCustomerApplications(APP_MCOL));

        individualRequests = new ArrayList<>();

        // Set up a request that is already rejected
        IIndividualRequest individualRequestOne = new IndividualRequest();
        individualRequestOne.setId(1L);
        individualRequestOne.setRequestType(CLAIM.getType());
        individualRequestOne.setCustomerRequestReference("customerRequestRef1");
        individualRequestOne.setRequestStatus(REJECTED.getStatus());
        individualRequests.add(individualRequestOne);

        // Set up a valid CMC request
        IIndividualRequest individualRequestTwo = new IndividualRequest();
        individualRequestTwo.setId(2L);
        individualRequestTwo.setRequestType(JUDGMENT.getType());
        individualRequestTwo.setCustomerRequestReference("customerRequestRef2");
        individualRequestTwo.setRequestPayload(null);
        individualRequests.add(individualRequestTwo);

        createBulkSubmission(2, bulkCustomer, individualRequests, APP_MCOL);

        when(requestTypeXmlNodeValidator.isCCDReference("", NODE_CLAIM_NUMBER)).thenReturn(true);
        when(requestTypeXmlNodeValidator.isValidRequestType(JUDGMENT.getType())).thenReturn(true);

        validator.validateCMCRequests(bulkSubmission);

        assertNotEquals(REJECTED.getStatus(), individualRequestTwo.getRequestStatus(),
                "Individual request two status should not be rejected");
        assertNull(individualRequestTwo.getErrorLog(), "Individual request two should not have an error log");

        assertNull(bulkSubmission.getErrorCode(), "Bulk submission should not have an error code");
        assertNull(bulkSubmission.getErrorText(), "Bulk submission should not have error text");
        assertNull(bulkSubmission.getSubmissionStatus(), "Bulk submission has unexpected status");

        verify(requestTypeXmlNodeValidator).isCCDReference("", NODE_CLAIM_NUMBER);
        verify(requestTypeXmlNodeValidator).isValidRequestType(JUDGMENT.getType());
    }

    private void createBulkSubmission(final long numberOfRequests, final IBulkCustomer bulkCustomer,
                                      final List<IIndividualRequest> individualRequests, final String application) {

        bulkSubmission = new BulkSubmission();
        bulkSubmission.setBulkCustomer(bulkCustomer);
        bulkSubmission.setCustomerReference("Customer Reference");
        bulkSubmission.setTargetApplication(createTargetApp(application));
        bulkSubmission.setNumberOfRequest(numberOfRequests);
        bulkSubmission.setIndividualRequests(individualRequests);
        bulkSubmission.setCreatedDate(NOW);
        bulkSubmission.setSdtBulkReference(SDT_BULK_REFERENCE);
    }

    /**
     * Method to mock and test GlobalParameterCache.
     */
    private void setupDataRetentionCache() {
        // Setup data retention period
        final IGlobalParameter globalParameterData = new GlobalParameter();
        globalParameterData.setName(IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name());
        globalParameterData.setValue("90");

        when(globalParameterCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name())).thenReturn(globalParameterData);

        validator.setGlobalParameterCache(globalParameterCache);
    }

    /**
     * Method to mock and test GlobalParameterCache.
     */
    private void setupContactDetailsCache() {

        // Set up contact details
        final IGlobalParameter globalParameterContact = new GlobalParameter();
        globalParameterContact.setName(IGlobalParameter.ParameterKey.CONTACT_DETAILS.name());
        globalParameterContact.setValue(CONTACT);

        when(globalParameterCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.CONTACT_DETAILS.name())).thenReturn(globalParameterContact);
        validator.setGlobalParameterCache(globalParameterCache);
    }

    private IErrorMessage createErrorMessage(IErrorMessage.ErrorCode errorCode, String errorText) {
        IErrorMessage errMessage = new ErrorMessage();
        errMessage.setErrorCode(errorCode.name());
        errMessage.setErrorText(errorText);
        return errMessage;
    }

    private IErrorMessage createNoValidReqsErrorMessage() {
        return createErrorMessage(NO_VALID_REQS,
                "The submitted Bulk Request {0} does not contain valid individual Requests.");
    }
}
