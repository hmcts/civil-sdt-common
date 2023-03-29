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

/**
 * Unit tests for {@link BulkFeedbackRequest}.
 *
 * @author Ollie Smith
 */
package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BulkFeedbackRequestTest {

    IBulkFeedbackRequest bulkFeedbackRequest;

    @Mock
    IBulkCustomer mockBulkCustomer;

    @BeforeEach
    public void setUp() {
        bulkFeedbackRequest = new BulkFeedbackRequest();
        mockBulkCustomer = Mockito.mock(IBulkCustomer.class);
        bulkFeedbackRequest.setSdtBulkReference("BULRef:1234");
        bulkFeedbackRequest.setBulkCustomer(mockBulkCustomer);
        bulkFeedbackRequest.setId(1L);
    }


    @Test
    @DisplayName("Test Bulk Feedback Request")
    void testBulkFeedbackRequest() {
        String expectedSdtBulkRef = "BULRef:1234";
        assertNotNull(bulkFeedbackRequest,"BulkFeedbackRequest should be populated");
        assertEquals(expectedSdtBulkRef, bulkFeedbackRequest.getSdtBulkReference());
        assertEquals(mockBulkCustomer, bulkFeedbackRequest.getBulkCustomer());
        assertEquals(1L, bulkFeedbackRequest.getId());
        String actual = bulkFeedbackRequest.toString();
        assertTrue(actual.contains(expectedSdtBulkRef),"Should contain something");
    }
}
