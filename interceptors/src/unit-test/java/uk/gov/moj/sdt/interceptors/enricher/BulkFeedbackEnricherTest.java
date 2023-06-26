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
 * $Id: SubmitQueryEnricherTest.java 17032 2013-09-12 15:25:50Z agarwals $
 * $LastChangedRevision: 17032 $
 * $LastChangedDate: 2013-09-12 16:25:50 +0100 (Thu, 12 Sep 2013) $
 * $LastChangedBy: agarwals $ */
package uk.gov.moj.sdt.interceptors.enricher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.Utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link uk.gov.moj.sdt.interceptors.enricher.BulkFeedbackEnricher}.
 *
 * @author Robin Compston.
 */
class BulkFeedbackEnricherTest extends AbstractSdtUnitTestBase {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkFeedbackEnricherTest.class);

    /**
     * Subject for test.
     */
    private BulkFeedbackEnricher enricher;

    /**
     * Setup for this test.
     */
    @BeforeEach
    public void setUp() {
        // Create enricher to be tested.
        enricher = new BulkFeedbackEnricher();
        enricher.setInsertionTag("responses");
        enricher.setParentTag("bulkFeedbackResponse");
    }

    /**
     * Test enrichment of single response with no error.
     */
    @Test
    void testSingleResponse() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B1",
                        "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestId=\"USER_REQUEST_ID_B1\" requestType=\"mcolClaim\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"/></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";

        // Call the enricher.
        final String result = enricher.enrichXml(inXml);

        // Check the enriched XML.
        final String expected =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestId=\"USER_REQUEST_ID_B1\" requestType=\"mcolClaim\"><ns5:responseDetail><fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail></ns5:responseDetail><ns5:status code=\"Initially Accepted\"/></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";
        // CHECKSTYLE:ON

        assertEquals(expected, result, "XML enriched by BulkFeedbackEnricher is not as expected:");
    }

    /**
     * Test enrichment of single response (with no namespaces in front of tags) with no error.
     */
    @Test
    void testSingleResponseNoNamespace() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B1",
                        "<mcolResponseDetail><claimNumber>12345678</claimNumber><issueDate>2012-11-11</issueDate><serviceDate>2012-11-11</serviceDate><warrantNumber>12345678</warrantNumber><enforcingCourtCode>123</enforcingCourtCode><enforcingCourtName>enforcing_court_name</enforcingCourtName><fee>9999</fee></mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><bulkRequestStatus><customerReference>USER_FILE_REFERENCE_B1</customerReference><sdtBulkReference>MCOL_20130722_B00000001</sdtBulkReference><submittedDate>2013-07-22T13:00:00+01:00</submittedDate><sdtService>SDT Commissioning</sdtService><requestCount>16</requestCount><bulkStatus code=\"Validated\"/></bulkRequestStatus><responses><response requestId=\"USER_REQUEST_ID_B1\" requestType=\"mcolClaim\"><responseDetail/><status code=\"Initially Accepted\"/></response></responses></bulkFeedbackResponse></soap:Body></soap:Envelope>";

        // Call the enricher.
        final String result = enricher.enrichXml(inXml);

        // Check the enriched XML.
        final String expected =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><bulkRequestStatus><customerReference>USER_FILE_REFERENCE_B1</customerReference><sdtBulkReference>MCOL_20130722_B00000001</sdtBulkReference><submittedDate>2013-07-22T13:00:00+01:00</submittedDate><sdtService>SDT Commissioning</sdtService><requestCount>16</requestCount><bulkStatus code=\"Validated\"/></bulkRequestStatus><responses><response requestId=\"USER_REQUEST_ID_B1\" requestType=\"mcolClaim\"><responseDetail><mcolResponseDetail><claimNumber>12345678</claimNumber><issueDate>2012-11-11</issueDate><serviceDate>2012-11-11</serviceDate><warrantNumber>12345678</warrantNumber><enforcingCourtCode>123</enforcingCourtCode><enforcingCourtName>enforcing_court_name</enforcingCourtName><fee>9999</fee></mcolResponseDetail></responseDetail><status code=\"Initially Accepted\"/></response></responses></bulkFeedbackResponse></soap:Body></soap:Envelope>";
        // CHECKSTYLE:ON

        assertEquals(expected, result, "XML enriched by BulkFeedbackEnricher is not as expected:");
    }

    /**
     * Test enrichment of single response with the position of the requestId and requestType attributes reversed.
     */
    @Test
    void testSingleResponseReversedAttributes() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B1",
                        "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"/></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";

        // Call the enricher.
        final String result = enricher.enrichXml(inXml);

        // Check the enriched XML.
        final String expected =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail><fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail></ns5:responseDetail><ns5:status code=\"Initially Accepted\"/></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";
        // CHECKSTYLE:ON

        assertEquals(expected, result);
    }

    /**
     * Test enrichment of multiple responses.
     */
    @Test
    void testMultipleResponse() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B1",
                        "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B2",
                        "<fake:mcolResponseDetail><phoney:claimNumber>987654321</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"/></ns5:response><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B2\"><ns5:responseDetail/><ns5:status code=\"Rejected\"><ns2:error><ns2:code>39</ns2:code><ns2:description>First defendant's postcode is not in England or Wales.</ns2:description></ns2:error></ns5:status></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";

        // Call the enricher.
        final String result = enricher.enrichXml(inXml);

        // Check the enriched XML.
        final String expected =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail><fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail></ns5:responseDetail><ns5:status code=\"Initially Accepted\"/></ns5:response><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B2\"><ns5:responseDetail><fake:mcolResponseDetail><phoney:claimNumber>987654321</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail></ns5:responseDetail><ns5:status code=\"Rejected\"><ns2:error><ns2:code>39</ns2:code><ns2:description>First defendant's postcode is not in England or Wales.</ns2:description></ns2:error></ns5:status></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";
        // CHECKSTYLE:ON

        assertEquals(expected, result);
    }

    /**
     * Test enrichment of XML which lacks one of the request ids in the targetApplicationRespMap.
     */
    @Test
    void testMissingRequestId() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B3",
                        "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"/></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";
        // CHECKSTYLE:ON

        try {
            // Call the enricher.
            enricher.enrichXml(inXml);

            fail("Failed to throw expected UnsupportedOperationException for missing request id");
        } catch (final UnsupportedOperationException e) {
            if (!e.getMessage().equals(
                    "Failure to find matching request in outgoing bulk feedback XML for request "
                            + "id[USER_REQUEST_ID_B3].")) {
                fail("Failed to throw expected UnsupportedOperationException for missing request id.");
            }
        }
    }

    /**
     * Test failure to enrich one of the requests in the outgoing XML.
     */
    @Test
    void testUnenrichedResponse() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B1",
                        "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"/></ns5:response><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B2\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"><ns2:error><ns2:code>39</ns2:code><ns2:description>First defendant's postcode is not in England or Wales.</ns2:description></ns2:error></ns5:status></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";

        try {
            // Call the enricher.
            enricher.enrichXml(inXml);

            fail("Failed to throw expected UnsupportedOperationException for incomplete enrichment.");
        } catch (final UnsupportedOperationException e) {
            if (!e.getMessage()
                    .equals(
                            "Detected unenriched response tag[<ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B2\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"] within bulk feedback response XML.")) {
                fail("Failed to throw expected UnsupportedOperationException for missing parent tag.");
            }
        }
    }

    /**
     * Test failure to enrich one of the requests in the outgoing XML.
     */
    @Test
    void testUnenrichedRejectedResponse() {
        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();
        // CHECKSTYLE:OFF Line length is acceptable
        targetApplicationRespMap
                .put("USER_REQUEST_ID_B1",
                        "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");

        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml =
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail/><ns5:status code=\"Initially Accepted\"/></ns5:response><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B2\"><ns5:responseDetail/><ns5:status code=\"Rejected\"><ns2:error><ns2:code>39</ns2:code><ns2:description>First defendant's postcode is not in England or Wales.</ns2:description></ns2:error></ns5:status></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";

        try {
            // Call the enricher.
            final String result = enricher.enrichXml(inXml);

            // Check the enriched XML.
            final String expected =
                    "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><ns5:bulkFeedbackResponse xmlns=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:ns3=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:ns4=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\" xmlns:ns5=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:ns6=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:ns7=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\"><ns5:bulkRequestStatus><ns5:customerReference>USER_FILE_REFERENCE_B1</ns5:customerReference><ns5:sdtBulkReference>MCOL_20130722_B00000001</ns5:sdtBulkReference><ns5:submittedDate>2013-07-22T13:00:00+01:00</ns5:submittedDate><ns5:sdtService>SDT Commissioning</ns5:sdtService><ns5:requestCount>16</ns5:requestCount><ns5:bulkStatus code=\"Validated\"/></ns5:bulkRequestStatus><ns5:responses><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B1\"><ns5:responseDetail><fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail></ns5:responseDetail><ns5:status code=\"Initially Accepted\"/></ns5:response><ns5:response requestType=\"mcolClaim\" requestId=\"USER_REQUEST_ID_B2\"><ns5:responseDetail/><ns5:status code=\"Rejected\"><ns2:error><ns2:code>39</ns2:code><ns2:description>First defendant's postcode is not in England or Wales.</ns2:description></ns2:error></ns5:status></ns5:response></ns5:responses></ns5:bulkFeedbackResponse></soap:Body></soap:Envelope>";
            // CHECKSTYLE:ON

            assertEquals(expected, result);
        } catch (final UnsupportedOperationException e) {
            fail("Unexpected exception thrown [" + e.getMessage() + "].");
        }
    }

    /**
     * Test performance of enrichment step for very large response.
     *
     * @throws IOException if test data file not found.
     */
    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    void testLargeFeedbackResponse() throws IOException {

        // Create map to hold fake responses from MCOL.
        final Map<String, String> targetApplicationRespMap = new HashMap<>();

        for (int i = 1; i <= 2000; i++) {

            // CHECKSTYLE:OFF Line length is acceptable
            targetApplicationRespMap
                    .put("Req0-" + i,
                            "<fake:mcolResponseDetail><phoney:claimNumber>12345678</phoney:claimNumber><phoney:issueDate>2012-11-11</phoney:issueDate><phoney:serviceDate>2012-11-11</phoney:serviceDate><phoney:warrantNumber>12345678</phoney:warrantNumber><phoney:enforcingCourtCode>123</phoney:enforcingCourtCode><phoney:enforcingCourtName>enforcing_court_name</phoney:enforcingCourtName><phoney:fee>9999</phoney:fee></fake:mcolResponseDetail>");
        }
        // Put the map in the thread local context as if it had been populated by the bulk feedback service with values
        // from the SDT database.
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        // Setup the XML to be enriched.
        final String inXml = Utilities.getRawXml("src/unit-test/resources/", "testLargeFeedbackResponse.xml");

        LOGGER.info("Start enrichment of " + targetApplicationRespMap.size() + " responses.");

        // Call the enricher.
        enricher.enrichXml(inXml);

        LOGGER.info("End enrichment of " + targetApplicationRespMap.size() + " responses.");
    }

}
