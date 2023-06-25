package uk.gov.moj.sdt.services.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.Utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.ERROR;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the IndividualRequestsXmlParser.
 *
 * @author D303894
 */
class IndividualRequestsXmlParserTest extends AbstractSdtUnitTestBase {

    private static final String FORWARDED = "Forwarded";

    /**
     * The individual request xml parser instance for testing.
     */
    private IndividualRequestsXmlParser individualRequestsXmlParser;

    /**
     * Set up the classes and data required for testing.
     */
    @BeforeEach
    @Override
    public void setUp() {
        this.individualRequestsXmlParser = new IndividualRequestsXmlParser();

        final Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put("http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema",
                "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema");

        individualRequestsXmlParser.setReplacementNamespaces(replacementNamespaces);
    }

    /**
     * Test the method to get individual requests raw xml map.
     *
     * @throws Exception if there is any IO problems
     */
    @Test
    void getIndividualRequestsRawXmlMap() throws IOException {
        Logger logger = (Logger) LoggerFactory.getLogger(IndividualRequestsXmlParser.class);
        logger.setLevel(ERROR);

        // Create appender and add to logger
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml("src/unit-test/resources/", "testXMLValid.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        final List<IIndividualRequest> requests = new ArrayList<>();

        // Create array list of individual requests as if these had been created by CXF in parsing the inbound SOAP
        // message and then transformed into domain objects.
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setCustomerRequestReference("1");
        individualRequest.setRequestStatus(FORWARDED);

        final IIndividualRequest individualRequest2 = new IndividualRequest();
        individualRequest2.setCustomerRequestReference("2");
        individualRequest2.setRequestStatus(FORWARDED);

        final IIndividualRequest individualRequest3 = new IndividualRequest();
        individualRequest3.setCustomerRequestReference("3");
        individualRequest3.setRequestStatus(FORWARDED);

        requests.add(individualRequest);
        requests.add(individualRequest2);
        requests.add(individualRequest3);

        // Now call the parser to add the xml fragments into the payload of the individual reauests.
        this.individualRequestsXmlParser.populateRawRequest(requests);

        List<ILoggingEvent> logList = listAppender.list;
        logger.detachAndStopAllAppenders();
        // CHECKSTYLE:OFF
        assertEquals(
                "<bul:mcolClaimStatusUpdatexmlns:bul=\"http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema\"><cla1:claimNumberxmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">claim123</cla1:claimNumber><cla1:defendantIdxmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">1</cla1:defendantId><cla1:notificationTypexmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">MP</cla1:notificationType><cla1:paidInFullDatexmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">2012-01-01</cla1:paidInFullDate></bul:mcolClaimStatusUpdate>",
                new String(requests.get(0).getRequestPayload(), StandardCharsets.UTF_8).replaceAll("\\s+", ""),
                "Failed to find correct payload for request 1");
        assertEquals(
                "<bul:mcolClaimStatusUpdatexmlns:bul=\"http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema\"><cla1:claimNumberxmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">claim124</cla1:claimNumber><cla1:defendantIdxmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">1</cla1:defendantId><cla1:notificationTypexmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">MP</cla1:notificationType><cla1:paidInFullDatexmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">2012-02-01</cla1:paidInFullDate></bul:mcolClaimStatusUpdate>",
                new String(requests.get(1).getRequestPayload(), StandardCharsets.UTF_8).replaceAll("\\s+", ""),
                "Failed to find correct payload for request 2");
        assertEquals(
                "<bul:mcolClaimxmlns:bul=\"http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema\"><cla1:claimNumberxmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">claim125</cla1:claimNumber><cla1:defendantIdxmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">1</cla1:defendantId><cla1:notificationTypexmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">MP</cla1:notificationType><cla1:paidInFullDatexmlns:cla1=\"http://ws.sdt.moj.gov.uk/2013/mcol/ClaimStatusUpdateSchema\">2012-03-01</cla1:paidInFullDate></bul:mcolClaim>",
                new String(requests.get(2).getRequestPayload(), StandardCharsets.UTF_8).replaceAll("\\s+", ""),
                "Failed to find correct payload for request 3");
        // CHECKSTYLE:ON


        assertFalse(verifyLog(logList,"Started parsing raw xml to extract payload for"));
        assertFalse(verifyLog(logList,"Found match: requestId"));
        assertFalse(verifyLog(logList,"Raw XML enhanced with namespaces"));
    }

    /**
     * Tests performance of parsing large xml request.
     *
     * @throws IOException if unable to read file.
     */
    @Test
    @Timeout(30000)
    void testParserPerformance() throws IOException {
        Logger logger = (Logger) LoggerFactory.getLogger(IndividualRequestsXmlParser.class);

        // Set logging level to debug so that afterCompletion logging is captured
        logger.setLevel(DEBUG);

        // Create appender and add to logger
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Load xml into SdtContext as if the inbound interceptor had run.
        final String rawXml = Utilities.getRawXml("src/unit-test/resources/", "testLargeBulkRequest.xml");

        SdtContext.getContext().setRawInXml(rawXml);

        final List<IIndividualRequest> requests = new ArrayList<>();

        // Create array list of individual requests as if these had been created by CXF in parsing the inbound SOAP
        // message and then transformed into domain objects.
        for (int i = 1; i <= 2000; i++) {

            final IIndividualRequest individualRequest = new IndividualRequest();
            individualRequest.setCustomerRequestReference("Req0-" + i);
            individualRequest.setRequestStatus(FORWARDED);
            requests.add(individualRequest);

        }

        // Now call the parser to add the xml fragments into the payload of the individual requests.
        this.individualRequestsXmlParser.populateRawRequest(requests);

        int requestIndex = 0;
        for (IIndividualRequest request : requests) {
            requestIndex++;
            assertNotNull(request.getRequestPayload(),
                    "Payload should have been populated in request#" + requestIndex);
        }

        List<ILoggingEvent> logList = listAppender.list;
        logger.detachAndStopAllAppenders();

        assertTrue(verifyLog(logList,"Started parsing raw xml to extract payload for"));
        assertTrue(verifyLog(logList,"Found match: requestId"));
        assertTrue(verifyLog(logList,"Raw XML enhanced with namespaces"));
    }

    private static boolean verifyLog(List<ILoggingEvent> logList, String message) {
        boolean verifyLog = false;
        for (ILoggingEvent log : logList) {
            if (log.getFormattedMessage().contains(message)) {
                verifyLog = true;
                break;
            }
        }
        return verifyLog;
    }

}
