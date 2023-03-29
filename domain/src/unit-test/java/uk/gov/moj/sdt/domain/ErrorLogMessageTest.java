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
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$ */

package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ErrorLog}.
 * Unit tests for {@link ErrorMessage}.
 *
 * @author Ollie Smith
 */
@DisplayName("Error Log and Error Message Test")
class ErrorLogMessageTest extends AbstractSdtUnitTestBase {
    /**
     * Test subject.
     */
    private IErrorLog errorLog;

    private IErrorMessage errorMessage;

    /**
     * Set up test data.
     */
    @Override
    @BeforeEach
    public void setUp() {
        errorLog = new ErrorLog();
        errorLog.setErrorCode(IErrorMessage.ErrorCode.BULK_REF_INVALID.name());
        errorLog.setId(1L);
        errorLog.setErrorText("The Bulk Customer does not have an SDT ID set");
        errorLog.setCreatedDate(LocalDateTime.now());
        errorLog.setUpdatedDate(LocalDateTime.now());

        errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(IErrorMessage.ErrorCode.CUST_ID_INVALID.name());
        errorMessage.setErrorText("The Customer does not have an SDT Customer ID set up. "
                                      + "Please contact {1} for assistance.");
        errorMessage.setErrorDescription("Error occurred due to no SDT Customer ID set");
        errorMessage.setId(1L);
    }


    @Test
    @DisplayName("Test Error Log")
    void testErrorLog() {
        String expectedErrorLog = "The Bulk Customer does not have an SDT ID set";
        assertNotNull(errorLog,"ErrorLog should be populated");
        assertNotNull(errorLog.toString(),"ErrorLog toString should be populated");
        String actualErrorLog = errorLog.toString();
        assertTrue(actualErrorLog.contains(expectedErrorLog),"Should contain something");
    }

    @Test
    @DisplayName("Test Error Message")
    void testErrorMessage() {
        String expectedErrorMessage = "Error occurred due to no SDT Customer ID set";
        assertNotNull(errorMessage,"ErrorMessage should be populated");
        assertNotNull(errorMessage.toString(),"ErrorMessage toString should be populated");
        String actualErrorMessage = errorMessage.toString();
        assertTrue(actualErrorMessage.contains(expectedErrorMessage),"Should contain something");
        assertEquals(1L, errorMessage.getId());
    }

    @Test
    void testErrorLogConstructorWithData() {
        String errorCode = IErrorMessage.ErrorCode.SDT_INT_ERR.name();
        String errorText =  "SDT Internal Error";
        ErrorLog errorLog2 = new ErrorLog(errorCode, errorText);

        assertEquals(errorCode, errorLog2.getErrorCode());
        assertEquals(errorText, errorLog2.getErrorText());
    }

}
