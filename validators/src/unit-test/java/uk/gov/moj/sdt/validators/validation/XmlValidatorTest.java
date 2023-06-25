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
 * $Id: ClaimXsdTest.java 16414 2013-05-29 10:56:45Z agarwals $
 * $LastChangedRevision: 16414 $
 * $LastChangedDate: 2013-05-29 11:56:45 +0100 (Wed, 29 May 2013) $
 * $LastChangedBy: holmessm $ */
package uk.gov.moj.sdt.validators.validation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.validators.XmlValidationDetails;
import uk.gov.moj.sdt.validators.XmlValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for XmlValidation.
 *
 * @author Simon Holmes
 */
@ExtendWith(MockitoExtension.class)
class XmlValidatorTest extends AbstractSdtUnitTestBase {
    /**
     * Path of the xsd.
     */
    private static final String XSD_PATH = "src/unit-test/resources/validation/xsd/testXSD.xsd";

    private static final String XML_PATH = "src/unit-test/resources/validation/xml/";

    /**
     * Test validateXML method with valid XML.
     * <p>
     * A SUCCESS enum value should be returned.
     *
     * @throws IOException an IO Exception
     */
    @Test
    void testValidateXmlValidXml() throws IOException {

        File myFile;
        String theXmlToValidate = "";

        myFile =
                new File(Utilities.checkFileExists(XML_PATH, "testXMLValid.xml",
                        false));

        theXmlToValidate = FileUtils.readFileToString(myFile, Charset.defaultCharset());

        final XmlValidator xmlValidation = new XmlValidator(theXmlToValidate, XSD_PATH);
        final XmlValidationDetails xmlValidationDetails = xmlValidation.validateXml();

        assertEquals(XmlValidationDetails.Result.PASS, xmlValidationDetails.getResult(),
                "The given XML is no longer valid.");
    }

    /**
     * Test validateXML method with valid XML.
     * <p>
     * A FAIL enum value should be returned with error messages in the array list.
     *
     * @throws IOException an IO Exception
     */
    @Test
    void testValidateXmlInvalidXml() throws IOException {

        File myFile;
        String theXmlToValidate = "";

        myFile =
                new File(Utilities.checkFileExists(XML_PATH, "testXMLInvalid.xml",
                        false));

        theXmlToValidate = FileUtils.readFileToString(myFile, Charset.defaultCharset());

        final XmlValidator xmlValidation = new XmlValidator(theXmlToValidate, XSD_PATH);
        final XmlValidationDetails xmlValidationDetails = xmlValidation.validateXml();

        assertEquals(XmlValidationDetails.Result.FAIL, xmlValidationDetails.getResult());
        assertEquals(24, xmlValidationDetails.getResultMessages().size(),
                "The given XML is now valid.");
    }

    /**
     * Test validateXML when there is no XSD.
     * <p>
     * A FAIL enum value should be returned with error messages in the array list.
     *
     * @throws IOException an IO Exception
     */
    @Test
    void testValidateXmlNoXsd() throws IOException {
        final String xsdPathInvalid = "invalidDir/FileDoesNotExist.xsd";

        File myFile;
        String theXmlToValidate = "";

        myFile =
                new File(Utilities.checkFileExists(XML_PATH, "testXMLInvalid.xml",
                        false));

        theXmlToValidate = FileUtils.readFileToString(myFile, Charset.defaultCharset());

        final XmlValidator xmlValidation = new XmlValidator(theXmlToValidate, xsdPathInvalid);
        final XmlValidationDetails xmlValidationDetails = xmlValidation.validateXml();

        assertEquals(XmlValidationDetails.Result.FAIL, xmlValidationDetails.getResult());

        final String errorMessage = "** ERROR - Unable to find the " + "file [FileDoesNotExist.xsd]";

        assertEquals(errorMessage, xmlValidationDetails.getResultMessages().get(0),
                "The XML is now considered valid against an XSD.");
    }
}
