package uk.gov.moj.sdt.interceptors.in;

import org.apache.cxf.binding.soap.SoapMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.apache.cxf.message.Message.PROTOCOL_HEADERS;
import static org.apache.cxf.phase.Phase.RECEIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdamIdInboundInterceptorTest extends AbstractSdtUnitTestBase {

    private static final String HEADER_CUSTOMER_IDAM_ID = "customerIdamId";

    private static final String HEADER_CONNECTION = "connection";

    @Mock
    SoapMessage mockSoapMessage;

    @Spy
    SdtContext spySdtContext;

    /** Class being tested. */
    IdamIdInboundInterceptor idamIdInboundInterceptor;

    @Override
    protected void setUpLocalTests() {
        idamIdInboundInterceptor = new IdamIdInboundInterceptor();
    }

    @Test
    void interceptorPhaseTest() {
        String interceptorPhase = idamIdInboundInterceptor.getPhase();
        assertEquals(RECEIVE, interceptorPhase, "IdamIdInboundInterceptor has unexpected phase");
    }

    @Test
    void interceptorPositionTest() {
        Set<String> beforeInterceptors = idamIdInboundInterceptor.getBefore();
        assertEquals(0,
                     beforeInterceptors.size(),
                     "IdamIdInboundInterceptor should not be executed before any other interceptors");

        Set<String> afterInterceptors = idamIdInboundInterceptor.getAfter();
        assertEquals(1,
                     afterInterceptors.size(),
                     "Should be one interceptor executed before IdamIdInboundInterceptor");
        assertTrue(afterInterceptors.contains(ServiceRequestInboundInterceptor.class.getName()),
                   "IdamIdInboundInterceptor should be executed after ServiceRequestInboundInterceptor");
    }

    @Test
    void handleMessageTest() {
        String idamIdValue = "test_email@test.com";
        Map<String, List<String>> idamIdHeader = createHeader(HEADER_CUSTOMER_IDAM_ID, idamIdValue);

        when(mockSoapMessage.get(PROTOCOL_HEADERS)).thenReturn(idamIdHeader);

        try (MockedStatic<SdtContext> mockStaticSdtContext = mockStatic(SdtContext.class)) {
            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(spySdtContext);

            idamIdInboundInterceptor.handleMessage(mockSoapMessage);

            verify(spySdtContext).setCustomerIdamId(idamIdValue);
            assertEquals(idamIdValue,
                         spySdtContext.getCustomerIdamId(),
                         "SdtContext customer IDAM id not set to expected value");
        }
    }

    @Test
    void handleMessageNoHeadersTest() {
        when(mockSoapMessage.get(PROTOCOL_HEADERS)).thenReturn(null);

        try (MockedStatic<SdtContext> mockStaticSdtContext = mockStatic(SdtContext.class)) {
            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(spySdtContext);

            idamIdInboundInterceptor.handleMessage(mockSoapMessage);

            verify(spySdtContext, never()).setCustomerIdamId(anyString());
            assertNull(spySdtContext.getCustomerIdamId(), "SdtContext should not have a customer IDAM id");
        }
    }

    @Test
    void handleMessageNoIdamIdHeaderTest() {
        String connectionValue = "Keep-Alive";
        Map<String, List<String>> connectionHeader = createHeader(HEADER_CONNECTION, connectionValue);

        when(mockSoapMessage.get(PROTOCOL_HEADERS)).thenReturn(connectionHeader);

        try (MockedStatic<SdtContext> mockStaticSdtContext = mockStatic(SdtContext.class)) {
            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(spySdtContext);

            idamIdInboundInterceptor.handleMessage(mockSoapMessage);

            verify(spySdtContext, never()).setCustomerIdamId(anyString());
            assertNull(spySdtContext.getCustomerIdamId(), "SdtContext should not have a customer IDAM id");
        }
    }

    private Map<String, List<String>> createHeader(String headerName, String headerValue) {
        List<String> valueList = new ArrayList<>();
        valueList.add(headerValue);

        Map<String, List<String>> header = new TreeMap<>();
        header.put(headerName, valueList);

        return header;
    }
}
