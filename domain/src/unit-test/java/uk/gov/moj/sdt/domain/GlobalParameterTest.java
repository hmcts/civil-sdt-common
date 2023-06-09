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
 * Unit tests for {@link GlobalParameter}.
 *
 * @author Ollie Smith
 */
package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Global Parameter Test")
class GlobalParameterTest extends AbstractSdtUnitTestBase {

    /**
     * Test subject.
     */

    private IGlobalParameter globalParameter;

    /**
     * Set up test data.
     */
    @BeforeEach
    @Override
    public void setUp() {
        globalParameter = new GlobalParameter();
        globalParameter.setId(1L);
        globalParameter.setDescription("Request delay for each individual");
        globalParameter.setName("MCOL_INDV_REQ_DELAY");
        globalParameter.setValue("10");
    }

    @Test
    @DisplayName("Test Global Parameter")
    void testGlobalParameter() {
        String expected = "MCOL_INDV_REQ_DELAY";
        String actual = globalParameter.toString();
        assertTrue(actual.contains(expected), "Should contain something");
    }

    @Test
    @DisplayName("Test ParameterKey enum order")
    void testGlobalParameterKeys() {
        assertEquals(0, IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.ordinal());
        assertEquals(1, IGlobalParameter.ParameterKey.TARGET_APP_TIMEOUT.ordinal());
        assertEquals(2, IGlobalParameter.ParameterKey.MAX_FORWARDING_ATTEMPTS.ordinal());
        assertEquals(3, IGlobalParameter.ParameterKey.MAX_CONCURRENT_INDV_REQ.ordinal());
        assertEquals(4, IGlobalParameter.ParameterKey.INDV_REQ_DELAY.ordinal());
        assertEquals(5, IGlobalParameter.ParameterKey.MAX_CONCURRENT_QUERY_REQ.ordinal());
        assertEquals(6, IGlobalParameter.ParameterKey.CONTACT_DETAILS.ordinal());
        assertEquals(7, IGlobalParameter.ParameterKey.TARGET_APP_RESP_TIMEOUT.ordinal());
    }
}
