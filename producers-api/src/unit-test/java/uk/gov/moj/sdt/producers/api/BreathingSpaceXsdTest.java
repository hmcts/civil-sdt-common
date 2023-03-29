/* Copyrights and Licenses
 * 
 * Copyright (c) 2021 by the Ministry of Justice. All rights reserved.
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

package uk.gov.moj.sdt.producers.api;

import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.utils.AbstractSdtXmlTestBase;

/**
 * Class to test Breathing Space XSD validation.
 * 
 * @author Paul Ridout
 *
 */
class BreathingSpaceXsdTest extends AbstractSdtXmlTestBase {

    /**
     * The name of the xsd.
     */
    private static final String XSD_NAME = "BreathingSpace";

    /**
     * The name of the folder where XSD is stored.
     */
    private static final String XSD_DIR = "RootXSDs/";

    /**
     * The name of the folder where all valid/invalid XML is stored.
     */
    private static final String XML_DIR = "mcolType/";

    /**
     * The path of the xsd file.
     */
    private static final String XSD_PATH = XSD_DIR + XSD_NAME + ".xsd";

    /**
     * Test that a file containing valid XML passes XSD validation.
     */
    @Test
    void testValidXml() {
        final String condition = "Valid";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, null);
    }

    /**
     * Test that a file containing XML that is missing the claimNumber element fails XSD validation.
     */
    @Test
    void testInvalidXmlMandatoryMissingClaimNumber() {
        final String condition = "MandatoryMissingClaimNumber";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;
        final String errorFilePathname = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.ERROR_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, errorFilePathname);
    }

    /**
     * Test that a file containing XML that is missing the defendantId element fails XSD validation.
     */
    @Test
    void testInvalidXmlMandatoryMissingDefendantId() {
        final String condition = "MandatoryMissingDefendantId";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;
        final String errorFilePathname = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.ERROR_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, errorFilePathname);
    }

    /**
     * Test that a file containing XML that is missing the breathingSpaceNotificationType element fails XSD validation.
     */
    @Test
    void testInvalidXmlMandatoryMissingNotificationType() {
        final String condition = "MandatoryMissingNotificationType";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;
        final String errorFilePathname = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.ERROR_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, errorFilePathname);
    }

    /**
     * Test that a file containing XML that has an incorrect value for the claimNumber element fails XSD validation.
     */
    @Test
    void testInvalidXmlIncorrectClaimNumber() {
        final String condition = "IncorrectClaimNumber";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;
        final String errorFilePathname = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.ERROR_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, errorFilePathname);
    }

    /**
     * Test that a file containing XML that has an incorrect value for the defendantId element fails XSD validation.
     */
    @Test
    void testInvalidXmlIncorrectDefendantId() {
        final String condition = "IncorrectDefendantId";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;
        final String errorFilePathname = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.ERROR_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, errorFilePathname);
    }

    /**
     * Test that a file containing XML that has an incorrect value for the breathingSpaceNotificationType element fails
     * XSD validation.
     */
    @Test
    void testInvalidXmlIncorrectNotificationType() {
        final String condition = "IncorrectNotificationType";
        final String xmlPath = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.XML_FILE_SUFFIX;
        final String errorFilePathname = XML_DIR + XSD_NAME + condition + AbstractSdtXmlTestBase.ERROR_FILE_SUFFIX;

        validateXsd (xmlPath, XSD_PATH, errorFilePathname);
    }
}
