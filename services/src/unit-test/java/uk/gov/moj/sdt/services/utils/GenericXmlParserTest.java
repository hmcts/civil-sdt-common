/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2013 by the Ministry of Justice. All rights reserved.
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
package uk.gov.moj.sdt.services.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the GenericXmlParser.
 *
 * @author d276205
 */
@ExtendWith(MockitoExtension.class)
class GenericXmlParserTest extends AbstractSdtUnitTestBase {

    private static final String URL_TARGET_APP = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp";
    private static final String URL_BULK_FEEDBACK_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema";
    private static final String URL_INDV_RESPONSE_SCHEMA = URL_TARGET_APP + "/IndvResponseSchema";
    private static final String URL_SUBMIT_QUERY_RESPONSE_SCHEMA = URL_TARGET_APP + "/SubmitQueryResponseSchema";
    private static final String FAILED_TO_FIND_EXPECTED_RESPONSE
            = "Failed to find expected response";
    private static final String TARGET_APP_DETAIL = "targetAppDetail";

    private static final String UNIT_TEST_RESOURCES_PATH = "src/unit-test/resources/";

    /**
     * The xml parser instance for testing.
     */
    private GenericXmlParser genericXmlParser;

    /**
     * Set up the classes and data required for testing.
     */
    @BeforeEach
    @Override
    public void setUp() {
        this.genericXmlParser = new GenericXmlParser();
    }

    /**
     * Test the method to get target app response xml for submit query response.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseSubmitQueryResponseSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_SUBMIT_QUERY_RESPONSE_SCHEMA, URL_SUBMIT_QUERY_RESPONSE_SCHEMA);

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testSubmitQueryResponse.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
       assertEquals(
                "<sub:mcolDefenceDetailxmlns:sub=\"" + URL_SUBMIT_QUERY_RESPONSE_SCHEMA+ "\"><mquer:claimNumberxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">13548968</mquer:claimNumber><mquer:defendantxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"defendantId=\"1\"><mquer:filedDatexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">2001-12-31T12:00:00</mquer:filedDate><mquer:responseTypexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">DE</mquer:responseType></mquer:defendant></sub:mcolDefenceDetail>",
                result.replaceAll("\\s+", ""),
               FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test the method to get target app response xml for submit query response.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseSubmitQueryResponseNoNamespaceSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put("http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema",
                URL_SUBMIT_QUERY_RESPONSE_SCHEMA);

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml =
                Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testSubmitQueryResponseNoNamespace.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals(
                "<mcolDefenceDetailxmlns=\"" + URL_SUBMIT_QUERY_RESPONSE_SCHEMA + "\"><mquer:claimNumberxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">13548968</mquer:claimNumber><mquer:defendantxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"defendantId=\"1\"><mquer:filedDatexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">2001-12-31T12:00:00</mquer:filedDate><mquer:responseTypexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">DE</mquer:responseType></mquer:defendant></mcolDefenceDetail>",
                result.replaceAll("\\s+", ""),
                FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test and embedded namespace - i.e. namespace defined as an attribute on tag.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testSubmitQueryResponseEmbeddedNamespaceSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);

        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_SUBMIT_QUERY_RESPONSE_SCHEMA,
                "http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema");
        genericXmlParser.setReplacementNamespaces(replacementNamespaces);

        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml =
                Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testSubmitQueryResponseEmbeddedNamespace.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals(
                "<ns1:mcolDefenceDetailxmlns:ns1=\"http://ws.sdt.moj.gov.uk/2013/mcol/QueryYyySchema\"someAttribute=\"someValue\"><mquer:claimNumberxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchemaMquer\">13548968</mquer:claimNumber><mquer:defendantxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchemaMquer\"defendantId=\"1\"><mquer:filedDatexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchemaMquer\">2001-12-31T12:00:00</mquer:filedDate><mquer:responseTypexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchemaMquer\">DE</mquer:responseType></mquer:defendant></ns1:mcolDefenceDetail>",
                result.replaceAll("\\s+", ""),
                FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test and embedded default namespace - i.e. no colon and namespace name.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testSubmitQueryResponseEmbeddedDefaultNamespaceSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_SUBMIT_QUERY_RESPONSE_SCHEMA,
                "http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema");

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml =
                Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testSubmitQueryResponseEmbeddedDefaultNamespace.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals(
                "<mcolDefenceDetailxmlns=\"http://ws.sdt.moj.gov.uk/2013/mcol/QueryXxxSchema\"><mquer:claimNumberxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">13548968</mquer:claimNumber><mquer:defendantxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"defendantId=\"1\"><mquer:filedDatexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">2001-12-31T12:00:00</mquer:filedDate><mquer:responseTypexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">DE</mquer:responseType></mquer:defendant></mcolDefenceDetail>",
                result.replaceAll("\\s+", ""),
                FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test the method to get target app response xml for submit query response when multiple records are returned in
     * result.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseSubmitQueryResponseForMultipleResultsSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_SUBMIT_QUERY_RESPONSE_SCHEMA,
                "http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema");

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml =
                Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testSubmitQueryResponseMultipleResult.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals(
                "<sub:mcolDefenceDetailxmlns:sub=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\"><mquer:claimNumberxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">13548968</mquer:claimNumber><mquer:defendantxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"defendantId=\"1\"><mquer:filedDatexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">2001-12-31T12:00:00</mquer:filedDate><mquer:responseTypexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">DE</mquer:responseType></mquer:defendant></sub:mcolDefenceDetail><sub:mcolDefenceDetailxmlns:sub=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\"><mquer:claimNumberxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">13548969</mquer:claimNumber><mquer:defendantxmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"defendantId=\"2\"><mquer:filedDatexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">2001-12-31T12:00:00</mquer:filedDate><mquer:responseTypexmlns:mquer=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">DE</mquer:responseType></mquer:defendant></sub:mcolDefenceDetail>",
                result.replaceAll("\\s+", ""),
                FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test the method to get target app response xml for individual request that has been rejected.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseIndividualResponseRejected() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_INDV_RESPONSE_SCHEMA, URL_BULK_FEEDBACK_RESPONSE_SCHEMA);

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);

        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testIndividualResponseRejected.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser to add the xml fragments into the payload of the individual reauests.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals("", result, FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test the method to get target app response xml for individual request.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseIndividualResponseSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_INDV_RESPONSE_SCHEMA, URL_BULK_FEEDBACK_RESPONSE_SCHEMA);

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);

        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testIndividualResponse.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser to add the xml fragments into the payload of the individual reauests.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals(
                "<ind:mcolResponseDetailxmlns:ind=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\"><mresp:claimNumberxmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">21346546</mresp:claimNumber><mresp:issueDatexmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">2001-01-01</mresp:issueDate><mresp:serviceDatexmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">2001-01-01</mresp:serviceDate><mresp:warrantNumberxmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">12345678</mresp:warrantNumber><mresp:enforcingCourtCodexmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">123</mresp:enforcingCourtCode><mresp:enforcingCourtNamexmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">CourtCode</mresp:enforcingCourtName><mresp:feexmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">0</mresp:fee><mresp:judgmentWarrantStatusxmlns:mresp=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">tns:additionalStatus</mresp:judgmentWarrantStatus></ind:mcolResponseDetail>",
                result.replaceAll("\\s+", ""),
                FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test the method to extract target app response xml for individual request when target app detail is not present.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseIndividualResponseEmptyDetailSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_INDV_RESPONSE_SCHEMA, URL_BULK_FEEDBACK_RESPONSE_SCHEMA);

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);

        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testIndividualResponseEmptyDetail.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser to add the xml fragments into the payload of the individual requests.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals("", result, FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

    /**
     * Test the method to get target app response xml for individual update.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void testParseIndividualUpdateRequestSuccess() throws Exception {

        genericXmlParser.setEnclosingTag(TARGET_APP_DETAIL);
        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(URL_INDV_RESPONSE_SCHEMA, URL_BULK_FEEDBACK_RESPONSE_SCHEMA);
        replacementNamespaces.put("http://ws.sdt.moj.gov.uk/2013/sdt/IndividualUpdateRequestSchema", URL_BULK_FEEDBACK_RESPONSE_SCHEMA);

        genericXmlParser.setReplacementNamespaces(replacementNamespaces);

        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml(UNIT_TEST_RESOURCES_PATH, "testIndividualUpdateRequest.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        // Now call the parser to add the xml fragments into the payload of the individual update request.
        final String result = this.genericXmlParser.parse();

        // CHECKSTYLE:OFF
        assertEquals(
                "<ureq:mcolResponseDetailxmlns:ureq=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\"><ns4:claimNumberxmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">A4XN5331</ns4:claimNumber><ns4:issueDatexmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">2014-06-26Z</ns4:issueDate><ns4:serviceDatexmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">2014-07-01Z</ns4:serviceDate><ns4:feexmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\">10500</ns4:fee><ns4:judgmentWarrantStatusxmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/mcol/ResponseDetailSchema\"></ns4:judgmentWarrantStatus></ureq:mcolResponseDetail>",
                result.replaceAll("\\s+", ""),
                FAILED_TO_FIND_EXPECTED_RESPONSE);
        // CHECKSTYLE:ON
    }

}
