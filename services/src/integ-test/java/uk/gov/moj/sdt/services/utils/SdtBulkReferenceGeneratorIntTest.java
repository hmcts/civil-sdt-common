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

package uk.gov.moj.sdt.services.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.moj.sdt.services.config.ServicesTestConfig;
import uk.gov.moj.sdt.services.utils.api.ISdtBulkReferenceGenerator;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.TestConfig;

/**
 * Test class for SdtBulkReferenceGenerator.
 *
 * @author Manoj Kulkarni
 */
@ActiveProfiles("integ")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class, ServicesTestConfig.class })
@Sql(scripts = {"classpath:uk/gov/moj/sdt/services/sql/RefData.sql", "classpath:uk/gov/moj/sdt/services/sql/SubmitQueryServiceIntTest.sql"})
@Transactional
public class SdtBulkReferenceGeneratorIntTest extends AbstractIntegrationTest {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SdtBulkReferenceGeneratorIntTest.class);

    /**
     * Test method for the SDT bulk reference generation.
     */
    @Test
    @Transactional
    public void testGetSdtBulkReference() {
        final ISdtBulkReferenceGenerator referenceGenerator =
                (ISdtBulkReferenceGenerator) this.applicationContext
                        .getBean("SdtBulkReferenceGenerator");

        // Negative Test 1 - Supply blank application name
        try {
            referenceGenerator.getSdtBulkReference(null);
        } catch (final IllegalArgumentException e) {
            LOGGER.debug(e.getMessage());
            assertTrue(true);
        }

        // Negative Test 2 - Supply application name less than expected value of 4
        try {
            referenceGenerator.getSdtBulkReference("NCO");
        } catch (final IllegalArgumentException e) {
            LOGGER.debug(e.getMessage());
            assertTrue(true);
        }

        // Negative Test 3 - Supply application name more than expected value of 4
        try {
            referenceGenerator.getSdtBulkReference("MCOLS");
        } catch (final IllegalArgumentException e) {
            LOGGER.debug(e.getMessage());
            assertTrue(true);
        }

        final String bulkReferenceNumber = referenceGenerator.getSdtBulkReference("MCOL");
        LOGGER.debug("Generated reference number is " + bulkReferenceNumber);
        assertNotNull(bulkReferenceNumber);
        assertEquals(29, bulkReferenceNumber.length());
    }

}
