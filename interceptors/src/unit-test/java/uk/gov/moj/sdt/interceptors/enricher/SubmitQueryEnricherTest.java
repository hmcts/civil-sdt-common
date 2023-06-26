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
package uk.gov.moj.sdt.interceptors.enricher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link uk.gov.moj.sdt.interceptors.enricher.SubmitQueryEnricher}.
 *
 * @author d276205
 */
class SubmitQueryEnricherTest extends AbstractSdtUnitTestBase {
    /**
     * Subject for test.
     */
    private SubmitQueryEnricher enricher;

    /**
     * Setup.
     */
    @BeforeEach
    @Override
    public void setUp() {
        enricher = new SubmitQueryEnricher();
        enricher.setInsertionTag("results");
        enricher.setParentTag("submitQueryResponse");
    }

    /**
     * Test success flow.
     */
    @Test
    void testSuccess() {
        SdtContext.getContext().setRawOutXml("<record></record>");
        final String result = enricher.enrichXml("<ns1:submitQueryResponse><ns2:results/></ns1:submitQueryResponse>");
        final String expected = "<ns1:submitQueryResponse><ns2:results>" +
                "<record></record></ns2:results></ns1:submitQueryResponse>";
        assertEquals(expected, result);
    }

    /**
     * Test success flow where insertion tag has no preceding namespace.
     */
    @Test
    void testSuccessNoNamespace() {
        SdtContext.getContext().setRawOutXml("<record></record>");
        final String result = enricher.enrichXml("<submitQueryResponse><results/></submitQueryResponse>");
        final String expected = "<submitQueryResponse><results>" +
                "<record></record></results></submitQueryResponse>";
        assertEquals(expected, result);
    }

    /**
     * Test for null value.
     */
    @Test
    void testForNullValue() {
        SdtContext.getContext().setRawOutXml(null);
        final String result = enricher.enrichXml("<ns1:submitQueryResponse><ns2:results/></ns1:submitQueryResponse>");
        final String expected = "<ns1:submitQueryResponse><ns2:results/></ns1:submitQueryResponse>";
        assertEquals(expected, result);
    }

    /**
     * Test for empty string value.
     */
    @Test
    void testForEmptyValue() {
        SdtContext.getContext().setRawOutXml("");
        final String result = enricher.enrichXml("<ns1:submitQueryResponse><ns2:results/></ns1:submitQueryResponse>");
        final String expected = "<ns1:submitQueryResponse><ns2:results/></ns1:submitQueryResponse>";
        assertEquals(expected, result);
    }

}
