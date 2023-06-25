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
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class BulkCustomerValidatorTest {

    /**
     * IBulkCustomer dao.
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
     * Subject for test.
     */
    private BulkCustomerValidator validator;

    /**
     * The bulk customer.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * Global parameter.
     */
    private IGlobalParameter globalParameter;

    /**
     * Error message.
     */
    private IErrorMessage errorMessage;

    /**
     * Contact details for assistance.
     */
    private String contact = "THE MOJ";

    /**
     * SDT Customer Id used for testing.
     */
    private long sdtCustomerId = 12345;

    /**
     * Setup of the Validator and Domain class instance.
     */
    @BeforeEach
    public void setUp() {
        validator = new BulkCustomerValidator(mockIBulkCustomerDao, globalParameterCache, errorMessagesCache);

        bulkCustomer = new BulkCustomer();

        // Set up Global parameters cache
        globalParameter = new GlobalParameter();
        globalParameter.setName(IGlobalParameter.ParameterKey.CONTACT_DETAILS.name());
        globalParameter.setValue(contact);
        validator.setGlobalParameterCache(globalParameterCache);

        // Set up Error messages cache
        errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(IErrorMessage.ErrorCode.CUST_ID_INVALID.name());
        errorMessage.setErrorText("The Bulk Customer organisation does not have an SDT Customer ID set up. "
                + "Please contact {1} for assistance.");
        validator.setErrorMessagesCache(errorMessagesCache);
    }

    /**
     * Test success flow.
     */
    @Test
    public void testCustomerFound() {
        // Setup bulk customer for test.
        bulkCustomer.setSdtCustomerId(12345L);

        // Tell the mock dao to return the same bulk customer.
        when(mockIBulkCustomerDao.getBulkCustomerBySdtId(sdtCustomerId)).thenReturn(bulkCustomer);

        // Inject the mock dao into the validator.
        validator.setBulkCustomerDao(mockIBulkCustomerDao);

        // Validate the bulk customer.
        bulkCustomer.accept(validator, null);
        verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(sdtCustomerId);

        assertEquals(bulkCustomer.getSdtCustomerId(), sdtCustomerId);
        assertTrue(true);
    }

    /**
     * Test success flow.
     */
    @Test
    public void testCustomerNotFound() {
        when(globalParameterCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.CONTACT_DETAILS.name())).thenReturn(globalParameter);

        when(errorMessagesCache.getValue(IErrorMessage.class, IErrorMessage.ErrorCode.CUST_ID_INVALID.name()))
                .thenReturn(errorMessage);

        bulkCustomer.setSdtCustomerId(12345L);

        // Tell the mock dao to return the same bulk customer.
        when(mockIBulkCustomerDao.getBulkCustomerBySdtId(sdtCustomerId)).thenReturn(null);

        // Inject the mock dao into the validator.
        validator.setBulkCustomerDao(mockIBulkCustomerDao);

        try {
            // Validate the bulk customer.
            bulkCustomer.accept(validator, null);

            fail("Test failed to throw CustomerNotFoundException.");
        } catch (final CustomerNotFoundException e) {
            verify(mockIBulkCustomerDao).getBulkCustomerBySdtId(sdtCustomerId);

            // [^\[]*\[CUST_NOT_SETUP\][^\[]*\[12345\].*
            assertTrue(e.getErrorCode().equals(IErrorMessage.ErrorCode.CUST_ID_INVALID.name()),
                    "Error code incorrect");
            assertTrue(e.getErrorDescription().equals(
                            "The Bulk Customer organisation does not have an SDT Customer ID set up. Please contact " +
                                    contact + " for assistance."),
                    "Substitution value incorrect");
        }
    }
}
