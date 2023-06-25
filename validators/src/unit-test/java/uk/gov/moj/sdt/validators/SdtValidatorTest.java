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
package uk.gov.moj.sdt.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;
import uk.gov.moj.sdt.validators.exception.CustomerNotSetupException;
import uk.gov.moj.sdt.validators.exception.CustomerReferenceNotUniqueException;
import uk.gov.moj.sdt.validators.exception.DuplicateUserRequestIdentifierException;
import uk.gov.moj.sdt.validators.exception.InvalidBulkReferenceException;
import uk.gov.moj.sdt.validators.exception.RequestCountMismatchException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Class for SDT Validator.
 *
 * @author d164190
 */
@ExtendWith(MockitoExtension.class)
class SdtValidatorTest extends AbstractSdtUnitTestBase {

    private static final String ERR_CODE_BULK_REF_INVALID = "BULK_REF_INVALID";
    private static final String ERR_CODE_CONTACT_DETAILS = "CONTACT_DETAILS";
    private static final String ERR_CODE_CUST_ID_INVALID = "CUST_ID_INVALID";
    private static final String ERR_CODE_CUST_NOT_SETUP = "CUST_NOT_SETUP";
    private static final String ERR_CODE_DUP_CUST_FILEID = "DUP_CUST_FILEID";
    private static final String ERR_CODE_DUP_CUST_REQID = "DUP_CUST_REQID";
    private static final String ERR_CODE_REQ_COUNT_MISMATCH = "REQ_COUNT_MISMATCH";

    private static final String ERR_MSG_FAILED_WITH_CODE = "Failed with code";
    private static final String ERR_MSG_SUBSTITUTION_VALUE_INCORRECT = "Substitution value incorrect";

    /**
     * Mock Error messages cache.
     */
    @Mock
    private ICacheable mockErrorMessagesCache;

    /**
     * Mock global parameters cache.
     */
    @Mock
    private ICacheable mockGlobalParameterCache;

    /**
     * Validator to run the test through.
     */
    private BulkSubmissionValidator validator;

    /**
     * Array containing the error messages.
     */
    private IErrorMessage[] result;

    /**
     * Global parameter.
     */
    private IGlobalParameter globalParam;

    @Mock
    private IBulkCustomerDao bulkCustomerDao;

    @Mock
    private IBulkSubmissionDao bulkSubmissionDao;

    @Mock
    private RequestTypeXmlNodeValidator requestTypeXmlNodeValidator;

    /**
     * Set up test artefact.
     */
    @BeforeEach
    @Override
    public void setUpLocalTests() {
        validator = new BulkSubmissionValidator(bulkCustomerDao,
                                                mockGlobalParameterCache,
                                                mockErrorMessagesCache,
                                                bulkSubmissionDao,
                                                requestTypeXmlNodeValidator,
                                                new ConcurrentHashMap<>());
        validator.setErrorMessagesCache(mockErrorMessagesCache);
        validator.setGlobalParameterCache(mockGlobalParameterCache);

        result = new ErrorMessage[7];
        result[0] = new ErrorMessage();
        result[0].setErrorCode(ERR_CODE_DUP_CUST_FILEID);
        result[0].setErrorText("Duplicate User File Reference {0} supplied. This was previously used to submit a "
                + "Bulk Request on {1} and the SDT Bulk Reference {2} was allocated.");

        result[1] = new ErrorMessage();
        result[1].setErrorCode(ERR_CODE_REQ_COUNT_MISMATCH);
        result[1].setErrorText("Unexpected Total Number of Requests identified. {0} requested identified,"
                + " {1} requests expected in Bulk Request {2}.");

        result[2] = new ErrorMessage();
        result[2].setErrorCode("SDT_INT_ERR");
        result[2].setErrorText("A system error has occurred. Please contact {0} for assistance.");

        result[3] = new ErrorMessage();
        result[3].setErrorCode(ERR_CODE_CUST_NOT_SETUP);
        result[3].setErrorText("The Bulk Customer organisation is not setup to send Service Request messages to "
                + "the {0}. Please contact {1} for assistance.");

        result[4] = new ErrorMessage();
        result[4].setErrorCode(ERR_CODE_CUST_ID_INVALID);
        result[4].setErrorText("The Bulk Customer organisation does not have an SDT Customer ID set up."
                + " Please contact {0} for assistance.");

        result[5] = new ErrorMessage();
        result[5].setErrorCode(ERR_CODE_BULK_REF_INVALID);
        result[5].setErrorText("There is no Bulk Request submission associated with your account for the supplied"
                + " SDT Bulk Reference {0}.");

        result[6] = new ErrorMessage();
        result[6].setErrorCode(ERR_CODE_DUP_CUST_REQID);
        result[6].setErrorText("Duplicate Unique Request Identifier submitted {0}.");

        globalParam = new GlobalParameter();
        globalParam.setName(ERR_CODE_CONTACT_DETAILS);
        globalParam.setValue("TBD");
    }

    /**
     * Test that the correct message is retrieved.
     */
    @Test
    void testGetErrorMessage() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_DUP_CUST_REQID)).thenReturn(result[6]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("MCOL0000012345");

        final String description = validator.getErrorMessage(replacements, IErrorMessage.ErrorCode.DUP_CUST_REQID);
        verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_DUP_CUST_REQID);
        assertTrue(description.contains("Duplicate Unique Request Identifier submitted MCOL0000012345."),
                "Incorrect Message and/or substitution");
    }

    /**
     *
     */
    @Test
    void testGetContactDetails() {
        when(mockGlobalParameterCache.getValue(IGlobalParameter.class, ERR_CODE_CONTACT_DETAILS)).thenReturn(
                globalParam);

        final String contactDetails = validator.getContactDetails();
        verify(mockGlobalParameterCache).getValue(IGlobalParameter.class, ERR_CODE_CONTACT_DETAILS);
        assertTrue(contactDetails.contains("TBD"), "Incorrect contact details");
    }

    /**
     * Test that the correct exception is created.
     */
    @Test
    void testCreateValidationExceptionDupCustFileid() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_DUP_CUST_FILEID))
                .thenReturn(result[0]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("MCOL00001234");
        replacements.add("09/10/2013");
        replacements.add("SDT00001234");

        try {
            validator.createValidationException(replacements, IErrorMessage.ErrorCode.DUP_CUST_FILEID);
            fail("Failed to throw expected CustomerReferenceNotUniqueException for error code DUP_CUST_FILEID");
        } catch (final CustomerReferenceNotUniqueException e) {
            verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_DUP_CUST_FILEID);
            assertTrue(e.getMessage().contains(ERR_CODE_DUP_CUST_FILEID), ERR_MSG_FAILED_WITH_CODE);
            assertTrue(e.getMessage().contains("MCOL00001234"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
            assertTrue(e.getMessage().contains("09/10/2013"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
            assertTrue(e.getMessage().contains("SDT00001234"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * Test that the correct exception is created.
     */
    @Test
    void testCreateValidationExceptionReqCountMismatch() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_REQ_COUNT_MISMATCH)).thenReturn(result[1]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("9");
        replacements.add("10");
        replacements.add("MCOL0000123");
        try {
            validator.createValidationException(replacements, IErrorMessage.ErrorCode.REQ_COUNT_MISMATCH);
            fail("Failed to throw expected RequestCountMismatchException for error code REQ_COUNT_MISMATCH");
        } catch (final RequestCountMismatchException e) {
            verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_REQ_COUNT_MISMATCH);
            assertTrue(e.getMessage().contains(ERR_CODE_REQ_COUNT_MISMATCH), ERR_MSG_FAILED_WITH_CODE);
            assertTrue(e.getMessage().contains("MCOL0000123"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * Test that the correct exception is created.
     */
    @Test
    void testCreateValidationExceptionCustNotSetup() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_CUST_NOT_SETUP)).thenReturn(result[3]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("MCOL");
        replacements.add("TBD");

        try {
            validator.createValidationException(replacements, IErrorMessage.ErrorCode.CUST_NOT_SETUP);
            fail("Failed to throw expected CustomerNotSetupException for error code CUST_NOT_SETUP");
        } catch (final CustomerNotSetupException e) {
            verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_CUST_NOT_SETUP);
            assertTrue(e.getMessage().contains(ERR_CODE_CUST_NOT_SETUP), ERR_MSG_FAILED_WITH_CODE);
            assertTrue(e.getMessage().contains("MCOL"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
            assertTrue(e.getMessage().contains("TBD"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * Test that the correct exception is created.
     */
    @Test
    void testCreateValidationExceptionCustRefMissing() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_CUST_ID_INVALID)).thenReturn(result[4]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("TBD");

        try {
            validator.createValidationException(replacements, IErrorMessage.ErrorCode.CUST_ID_INVALID);
            fail("Failed to throw expected CustomerNotFoundException for error code CUST_ID_INVALID");
        } catch (final CustomerNotFoundException e) {
            verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_CUST_ID_INVALID);
            assertTrue(e.getMessage().contains(ERR_CODE_CUST_ID_INVALID), ERR_MSG_FAILED_WITH_CODE);
            assertTrue(e.getMessage().contains("TBD"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * Test that the correct exception is created.
     */
    @Test
    void testCreateValidationExceptionBulkRefInvalid() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_BULK_REF_INVALID)).thenReturn(result[5]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("MCOL000001234");

        try {
            validator.createValidationException(replacements, IErrorMessage.ErrorCode.BULK_REF_INVALID);
            fail("Failed to throw expected InvalidBulkReferenceException for error code BULK_REF_INVALID");
        } catch (final InvalidBulkReferenceException e) {
            verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_BULK_REF_INVALID);
            assertTrue(e.getMessage().contains(ERR_CODE_BULK_REF_INVALID), ERR_MSG_FAILED_WITH_CODE);
            assertTrue(e.getMessage().contains("MCOL000001234"), ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
        }
    }

    /**
     * Test that the correct exception is created.
     */
    @Test
    void testCreateValidationExceptionDupCustReqid() {
        when(mockErrorMessagesCache.getValue(IErrorMessage.class, ERR_CODE_DUP_CUST_REQID)).thenReturn(result[6]);

        List<String> replacements = null;
        replacements = new ArrayList<>();
        replacements.add("MCOL000009876");

        try {
            validator.createValidationException(replacements, IErrorMessage.ErrorCode.DUP_CUST_REQID);
            fail("Failed to throw expected DuplicateUserRequestIdentifierException for error code"
                    + " DUP_CUST_REQID");
        } catch (final DuplicateUserRequestIdentifierException e) {
            verify(mockErrorMessagesCache).getValue(IErrorMessage.class, ERR_CODE_DUP_CUST_REQID);
            assertTrue(e.getMessage().contains(ERR_CODE_DUP_CUST_REQID), ERR_MSG_FAILED_WITH_CODE);
            assertTrue(e.getMessage().contains("MCOL000009876"),ERR_MSG_SUBSTITUTION_VALUE_INCORRECT);
        }
    }
}
