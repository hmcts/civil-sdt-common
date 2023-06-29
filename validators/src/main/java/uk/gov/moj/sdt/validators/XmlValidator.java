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
 * $LastChangedBy: agarwals $ */
package uk.gov.moj.sdt.validators;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.validators.XmlValidationDetails.Result;
import uk.gov.moj.sdt.validators.api.IXmlValidator;

/**
 * {@inheritDoc}.
 *
 * @author Simon Holmes
 */
public class XmlValidator implements IXmlValidator {

    /**
     * Logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlValidator.class);

    /**
     * Fully qualified name of class that implements SAXParserFactory.
     */
    private static final String SAX_PARSER_FACTORY_IMPL_CLASS = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

    /**
     * The xml that requires validation.
     */
    private String xmlToValidate;

    /**
     * The file path of the xsd.
     */
    private String xsdFilePath;

    /**
     * No-argument constructor.
     */
    public XmlValidator() {
    }

    /**
     * Constructor.
     *
     * @param xmlToValidate is the xml that needs validation.
     * @param xsdPath       is the directory path of the xsd path
     */
    public XmlValidator(final String xmlToValidate, final String xsdPath) {
        this.xmlToValidate = xmlToValidate;
        this.xsdFilePath = xsdPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidationDetails validateXml() {
        String exceptionMessage = "";

        try {
            return evaluateXsd();
        } catch (final SAXException e) {
            LOGGER.error("Exception while validating XML with XSD [" + xsdFilePath + "]", e);
            exceptionMessage = e.getMessage();
        } catch (final ParserConfigurationException e) {
            LOGGER.error("Exception while validating XML with XSD [" + xsdFilePath + "]", e);
            exceptionMessage = e.getMessage();
        } catch (final IOException e) {
            LOGGER.error("Exception while validating XML with XSD [" + xsdFilePath + "]", e);
            exceptionMessage = e.getMessage();
        }

        final List<String> errorMessageList = new ArrayList<String>();
        errorMessageList.add(exceptionMessage);
        return new XmlValidationDetails(Result.FAIL, errorMessageList);
    }

    /**
     * @return A XmlValidationDetails object containing SUCCESS/FAIL enum and if fail, error messages.
     * @throws SAXException                 a sax exception.
     * @throws ParserConfigurationException a paser config exception.
     * @throws IOException                  io exception
     */
    private XmlValidationDetails evaluateXsd() throws SAXException, ParserConfigurationException, IOException {
        // Create schema factory amd set XSD file as the schema for factory to
        // create.
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        final File xsdFile = Utilities.retrieveFile(xsdFilePath);

        final Schema schema = schemaFactory.newSchema(xsdFile);

        // Create SAX parser factory; turn on validation and check all name
        // spaces; use given schema for validation.
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance(SAX_PARSER_FACTORY_IMPL_CLASS, null);
        // saxFactory.setValidating (true);
        saxFactory.setNamespaceAware(true);
        saxFactory.setSchema(schema);

        // Create SAX parser to do validation (not DOM as we do not need to
        // create a document).
        final SAXParser parser = saxFactory.newSAXParser();
        return parseXml(parser);
    }

    /**
     * Parse the xml and check for errors.
     *
     * @param parser the xml parser
     * @return A XmlValidationDetails object containing SUCCESS/FAIL enum and if fail, error messages.
     * @throws IOException  ioException
     * @throws SAXException saxException
     */
    private XmlValidationDetails parseXml(final SAXParser parser) throws SAXException, IOException {
        // convert incoming XML(String) to an Input Stream to allow the parser to read it.
        final InputStream xmlStream = IOUtils.toInputStream(xmlToValidate);

        // Create the details object to store the result of the validation.
        final XmlValidationDetails xmlValidationDetails = new XmlValidationDetails(Result.PASS);

        // Use the XSD file to validate the proof XML.
        parser.parse(xmlStream, new DefaultHandler() {
            // Anonymous Default Handler to implement required error logic.
            public void error(final SAXParseException e) throws SAXException {
                // If the first error to occur in the file.
                if (xmlValidationDetails.getResult() == XmlValidationDetails.Result.PASS) {
                    // Change state to FAIL.
                    xmlValidationDetails.setResult(XmlValidationDetails.Result.FAIL);
                    // Create an empty list of result messages.
                    xmlValidationDetails.setResultMessages(new ArrayList<String>());
                }
                // Add the error message to the list.
                xmlValidationDetails.getResultMessages().add(e.getMessage());
                return;
            }
        });
        return xmlValidationDetails;
    }

    /**
     * @return the xmlToValidate
     */
    public String getXmlToValidate() {
        return xmlToValidate;
    }

    /**
     * @param xmlToValidate the xmlToValidate to set
     */
    public void setXmlToValidate(final String xmlToValidate) {
        this.xmlToValidate = xmlToValidate;
    }

    /**
     * @return the xsdPath
     */
    public String getXsdPath() {
        return xsdFilePath;
    }

    /**
     * @param xsdPath the xsdPath to set
     */
    public void setXsdPath(final String xsdPath) {
        this.xsdFilePath = xsdPath;
    }
}
