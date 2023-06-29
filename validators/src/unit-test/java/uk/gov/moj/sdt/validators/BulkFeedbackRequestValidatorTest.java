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
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkFeedbackRequest;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.validators.exception.InvalidBulkReferenceException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BulkCustomerValidatorTest}.
 *
 * @author d120520
 */

@ExtendWith(MockitoExtension.class)
class BulkFeedbackRequestValidatorTest extends AbstractValidatorUnitTest {

    /**
     * The IBulkSubmissionDao.
     */
    @Mock
    private IBulkSubmissionDao mockIBulkSubmissionDao;

    /**
     * Error messages cache.
     */
    @Mock
    private ICacheable errorMessagesCache;

    /**
     * Global Parameter cache.
     */
    @Mock
    private ICacheable globalParameterCache;

    /**
     * Subject for test.
     */
    private BulkFeedbackRequestValidator validator;

    /**
     * The IBulkFeedbackRequest.
     */
    private IBulkFeedbackRequest bulkFeedbackRequest;

    /**
     * bulkSubmission.
     */
    private IBulkSubmission bulkSubmission;

    /**
     * The string reference for our bulk request.
     */
    private static final String REFERENCE = " Bulk reference in request ";

    /**
     * requestId.
     */
    private long requestId;

    /**
     * Error message.
     */
    private IErrorMessage errorMessage;

    /**
     * Bulk Customer to use for the test.
     */
    private IBulkCustomer bulkCustomer;

    @Mock
    private IBulkCustomerDao bulkCustomerDao;

    @Mock
    private IBulkSubmissionDao bulkSubmissionDao;

    /**
     * Data retention period.
     */
    private int dataRetentionPeriod;

    /**
     * Setup of the Validator and Domain class instance.
     */
    @Override
    public void setUpLocalTests() {
        validator = new BulkFeedbackRequestValidator(bulkCustomerDao,
                                                     globalParameterCache,
                                                     errorMessagesCache,
                                                     bulkSubmissionDao);

        // create a bulk customer
        bulkCustomer = new BulkCustomer();
        bulkCustomer.setSdtCustomerId(12345L);

        bulkSubmission = new BulkSubmission();
        bulkSubmission.setBulkCustomer(bulkCustomer);
        bulkSubmission.setSdtBulkReference(REFERENCE);

        bulkFeedbackRequest = new BulkFeedbackRequest();

        // Setup bulk request for test.
        bulkFeedbackRequest.setId(requestId);
        bulkFeedbackRequest.setSdtBulkReference(REFERENCE);
        bulkFeedbackRequest.setBulkCustomer(bulkCustomer);

        // Set up Error messages cache
        errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(IErrorMessage.ErrorCode.BULK_REF_INVALID.name());
        errorMessage.setErrorText("There is no Bulk Request submission associated with your account for "
                + "the supplied SDT Bulk Reference {0}.");
        validator.setErrorMessagesCache(errorMessagesCache);

        final IGlobalParameter globalParameterData = new GlobalParameter();
        globalParameterData.setName(IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name());
        globalParameterData.setValue("90");
        when(globalParameterCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name())).thenReturn(globalParameterData);
        validator.setGlobalParameterCache(globalParameterCache);

        dataRetentionPeriod = 90;
    }

    /**
     * Test success flow.
     */
    @Test
    void testBulkReferenceFound() {
        // Tell the mock dao to return this request
        when(mockIBulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, REFERENCE, dataRetentionPeriod))
                .thenReturn(bulkSubmission);

        // Inject the mock dao into the validator.
        validator.setBulkSubmissionDao(mockIBulkSubmissionDao);

        // Validate the bulk customer.
        bulkFeedbackRequest.accept(validator, null);
        verify(mockIBulkSubmissionDao).getBulkSubmissionBySdtRef(bulkCustomer, REFERENCE, dataRetentionPeriod);
    }

    /**
     * Test success flow.
     */
    @Test
    void testBulkReferenceNotFound() {
        when(errorMessagesCache.getValue(IErrorMessage.class, IErrorMessage.ErrorCode.BULK_REF_INVALID.name()))
                .thenReturn(errorMessage);

        // Tell the mock dao to return this request
        when(mockIBulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, REFERENCE, dataRetentionPeriod))
                .thenReturn(null);

        // Inject the mock dao into the validator.
        validator.setBulkSubmissionDao(mockIBulkSubmissionDao);

        try {
            // Validate the request
            bulkFeedbackRequest.accept(validator, null);
            verify(mockIBulkSubmissionDao).getBulkSubmissionBySdtRef(bulkCustomer, REFERENCE, dataRetentionPeriod);
            fail("Failed to throw expected InvalidBulkReferenceException");

        } catch (final InvalidBulkReferenceException e) {
            assertTrue(true);
        }

    }
}
