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
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.SubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.validators.exception.CustomerNotSetupException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SubmitQueryRequestValidatorTest}.
 *
 * @author d120520
 */

@ExtendWith(MockitoExtension.class)
class SubmitQueryRequestValidatorTest extends AbstractValidatorUnitTest {

    /**
     * SubmitQueryRequestValidator.
     */
    private SubmitQueryRequestValidator validator;

    /**
     * SubmitQueryRequest.
     */
    private SubmitQueryRequest submitQueryRequest;

    /**
     * IBulkCustomer.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * IBulkCustomerDao.
     */
    @Mock
    private IBulkCustomerDao mockIBulkCustomerDao;

    /**
     * Parameter cache.
     */
    @Mock
    private ICacheable globalParameterCache;

    /**
     * Global parameter.
     */
    private IGlobalParameter globalParameter;

    /**
     * Contact details for assistance.
     */
    private String contact = "THE MOJ";

    /**
     * Error messages cache.
     */
    @Mock
    private ICacheable errorMessagesCache;

    @Mock
    private IBulkCustomerDao bulkCustomerDao;

    /**
     * Setup of the Validator and Domain class instance.
     */
    @BeforeEach
    @Override
    public void setUpLocalTests() {
        bulkCustomer = createCustomer(createBulkCustomerApplications("PCOL"));

        submitQueryRequest = new SubmitQueryRequest();
        submitQueryRequest.setBulkCustomer(bulkCustomer);

        // Set up Global parameters cache
        globalParameter = new GlobalParameter();
        globalParameter.setName(IGlobalParameter.ParameterKey.CONTACT_DETAILS.name());
        globalParameter.setValue(contact);
        validator= new SubmitQueryRequestValidator(bulkCustomerDao, globalParameterCache, errorMessagesCache);
    }

    /**
     * test the scenario where the customer does not have access to the target application.
     */
    @Test
    void testCustomerDoesNotHaveAccess() {
        when(globalParameterCache.getValue(IGlobalParameter.class,
                IGlobalParameter.ParameterKey.CONTACT_DETAILS.name())).thenReturn(globalParameter);

        final String mcolCode = "MCOL CODE";
        try {

            submitQueryRequest.setTargetApplication(createTargetApp(mcolCode));
            // set up the mock objects
            when(mockIBulkCustomerDao.getBulkCustomerBySdtId(12345L)).thenReturn(bulkCustomer);

            // Set up Error messages cache
            IErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setErrorCode(IErrorMessage.ErrorCode.CUST_NOT_SETUP.name());
            errorMessage.setErrorText("The Bulk Customer organisation is not setup to send Service "
                    + "Request messages to the {0}. Please contact {1} for assistance.");
            when(errorMessagesCache.getValue(IErrorMessage.class, IErrorMessage.ErrorCode.CUST_NOT_SETUP.name()))
                    .thenReturn(errorMessage);
            validator.setErrorMessagesCache(errorMessagesCache);

            validator.setBulkCustomerDao(mockIBulkCustomerDao);

            submitQueryRequest.accept(validator, null);
            fail("Test failed to throw CustomerNotSetupException ");

        } catch (final CustomerNotSetupException e) {
            verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(12345L);

            assertEquals(e.getErrorCode(), IErrorMessage.ErrorCode.CUST_NOT_SETUP.name());
            assertEquals(e.getErrorDescription(),
                    "The Bulk Customer organisation is not setup to send Service Request messages to the " + mcolCode +
                            ". Please contact " + contact + " for assistance.");
        }
    }

    /**
     * test the scenario where the customer has access to the target application.
     */
    @Test
    void testCustomerHasAccess() {

        submitQueryRequest.setTargetApplication(createTargetApp("PCOL"));
        // set up the mock objects
        when(mockIBulkCustomerDao.getBulkCustomerBySdtId(12345L)).thenReturn(bulkCustomer);

        validator.setBulkCustomerDao(mockIBulkCustomerDao);

        submitQueryRequest.accept(validator, null);
    }
}
