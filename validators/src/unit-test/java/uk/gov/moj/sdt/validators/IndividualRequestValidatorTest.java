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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

/**
 * Tests for {@link IndividualRequestValidatorTest}.
 *
 * @author d120520
 */

@ExtendWith(MockitoExtension.class)
class IndividualRequestValidatorTest extends AbstractValidatorUnitTest {

    /**
     * Data retention period.
     */
    private static final int DATA_RETENTION_PERIOD = 90;

    private static final String NODE_NAME_CLAIM_NUMBER = "claimNumber";

    /**
     * IIndividualRequestDao.
     */
    @Mock
    private IIndividualRequestDao mockIndividualRequestDao;

    /**
     * Parameter cache.
     */
    @Mock
    private ICacheable globalParameterCache;

    /**
     * Error Messages cache.
     */
    @Mock
    private ICacheable errorMessagesCache;

    /**
     * IndividualRequestValidator.
     */
    private IndividualRequestValidator validator;

    /**
     * IBulkCustomer.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * IIndividualRequest.
     */
    private IIndividualRequest individualRequest;

    /**
     * Error message.
     */
    private IErrorMessage errorMessage;

    @Mock
    private IBulkCustomerDao bulkCustomerDao;

    @Mock
    private IIndividualRequestDao individualRequestDao;

    @Mock
    private RequestTypeXmlNodeValidator mockRequestTypeXmlNodeValidator;

    /**
     * Setup of the Validator and Domain class instance.
     */
    @BeforeEach
    @Override
    public void setUp() {
        // create a bulk customer
        bulkCustomer = new BulkCustomer();
        bulkCustomer.setSdtCustomerId(12345L);

        BulkSubmission bulkSubmission = new BulkSubmission();
        bulkSubmission.setBulkCustomer(bulkCustomer);

        // create an individual request
        individualRequest = new IndividualRequest();
        individualRequest.setId(1L);
        individualRequest.setBulkSubmission(bulkSubmission);
        individualRequest.setCustomerRequestReference("customerRequestReference");

        // Setup global parameters cache
        IGlobalParameter globalParameter = new GlobalParameter();
        globalParameter.setName(IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name());
        globalParameter.setValue(Integer.toString(DATA_RETENTION_PERIOD));
        when(globalParameterCache.getValue(IGlobalParameter.class,
                IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name())).thenReturn(globalParameter);

        // subject of test
        validator = new IndividualRequestValidator(bulkCustomerDao, globalParameterCache,
                errorMessagesCache, individualRequestDao, mockRequestTypeXmlNodeValidator);
    }

    /**
     * The purpose of this test is to test an invalid request and test the exception.
     */
    @Test
    void testInvalidRequest() {
        errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(IErrorMessage.ErrorCode.DUP_CUST_REQID.name());
        errorMessage.setErrorText("Duplicate Unique Request Identifier submitted {0}.");

        when(errorMessagesCache.getValue(IErrorMessage.class, IErrorMessage.ErrorCode.DUP_CUST_REQID.name()))
                .thenReturn(errorMessage);

        when(mockIndividualRequestDao.getIndividualRequest(bulkCustomer,
                individualRequest.getCustomerRequestReference(), DATA_RETENTION_PERIOD)).thenReturn(individualRequest);

        // inject the bulk customer into the validator
        validator.setIndividualRequestDao(mockIndividualRequestDao);

        individualRequest.accept(validator, null);

        verify(mockIndividualRequestDao).getIndividualRequest(bulkCustomer,
                individualRequest.getCustomerRequestReference(), DATA_RETENTION_PERIOD);
        assertEquals("Duplicate Unique Request Identifier submitted " +
                individualRequest.getCustomerRequestReference() + ".", individualRequest.getErrorLog().getErrorText(),
                "Individual request has unexpected error text");
        assertEquals(REJECTED.getStatus(), individualRequest.getRequestStatus(),
                "Individual request status should be rejected");
    }

    @Test
    void testInvalidCMCRequest() {
        errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(IErrorMessage.ErrorCode.INVALID_CMC_REQUEST.name());
        errorMessage.setErrorText("Individual request {0} for CMC has an invalid request type {1}");

        when(errorMessagesCache.getValue(IErrorMessage.class, IErrorMessage.ErrorCode.INVALID_CMC_REQUEST.name()))
                .thenReturn(errorMessage);
        when(mockIndividualRequestDao.getIndividualRequest(bulkCustomer,
                individualRequest.getCustomerRequestReference(), DATA_RETENTION_PERIOD)).thenReturn(null);
        when(mockRequestTypeXmlNodeValidator.isCCDReference("", NODE_NAME_CLAIM_NUMBER)).thenReturn(true);
        when(mockRequestTypeXmlNodeValidator.isValidRequestType(null)).thenReturn(false);

        validator.setIndividualRequestDao(mockIndividualRequestDao);

        individualRequest.accept(validator, null);

        IErrorLog requestErrorLog = individualRequest.getErrorLog();
        assertNotNull(requestErrorLog, "Individual request should have an error log");
        assertEquals(IErrorMessage.ErrorCode.INVALID_CMC_REQUEST.name(), requestErrorLog.getErrorCode(),
                "Individual request has unexpected error code");
        assertEquals("Individual request customerRequestReference for CMC has an invalid request type null",
                requestErrorLog.getErrorText(), "Individual request has unexpected error text");

        assertEquals(REJECTED.getStatus(), individualRequest.getRequestStatus(),
                "Individual request status should be rejected");

        verify(errorMessagesCache).getValue(IErrorMessage.class, IErrorMessage.ErrorCode.INVALID_CMC_REQUEST.name());
        verify(mockIndividualRequestDao).getIndividualRequest(bulkCustomer,
                individualRequest.getCustomerRequestReference(), DATA_RETENTION_PERIOD);
        verify(mockRequestTypeXmlNodeValidator).isCCDReference("", NODE_NAME_CLAIM_NUMBER);
        verify(mockRequestTypeXmlNodeValidator).isValidRequestType(null);
    }

    /**
     * The purpose of this test is to pass a valid request.
     */
    @Test
    void testValidRequest() {
        when(mockIndividualRequestDao.getIndividualRequest(bulkCustomer,
                individualRequest.getCustomerRequestReference(), DATA_RETENTION_PERIOD)).thenReturn(null);
        when(mockRequestTypeXmlNodeValidator.isCCDReference("", NODE_NAME_CLAIM_NUMBER)).thenReturn(false);

        // inject the bulk customer into the validator
        validator.setIndividualRequestDao(mockIndividualRequestDao);

        individualRequest.accept(validator, null);

        assertNull(individualRequest.getErrorLog(), "Individual request should not have an error log");
        assertNotEquals(REJECTED.getStatus(), individualRequest.getRequestStatus(),
                "Individual request status should not be rejected");

        verify(mockIndividualRequestDao).getIndividualRequest(bulkCustomer,
                individualRequest.getCustomerRequestReference(), DATA_RETENTION_PERIOD);
        verify(mockRequestTypeXmlNodeValidator).isCCDReference("", NODE_NAME_CLAIM_NUMBER);
    }
}
