package uk.gov.moj.sdt.interceptors.out;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.Phase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.ServiceRequestDao;
import uk.gov.moj.sdt.domain.ServiceRequest;
import uk.gov.moj.sdt.interceptors.service.RequestDaoService;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test that faults are correctly intercepted.
 *
 * @author d195274
 */
@ExtendWith(MockitoExtension.class)
class ServiceRequestOutboundInterceptorTest extends AbstractSdtUnitTestBase {

    @Mock
    ServiceRequestDao mockServiceRequestDao;

    RequestDaoService requestDaoService;

    @Override
    protected void setUpLocalTests() {
        requestDaoService = new RequestDaoService(mockServiceRequestDao);
    }

    /**
     * Test method for
     * {@link ServiceRequestOutboundInterceptor
     * #handleMessage(org.apache.cxf.binding.soap.SoapMessage)}.
     */
    @Test
    void testHandleMessage() throws IOException {
        SdtContext.getContext().setServiceRequestId(1L);
        String data = "<xml>content</xml>";
        final SoapMessage soapMessage = getDummySoapMessageWithCachedOutputStream(data);
        final ServiceRequestOutboundInterceptor serviceRequestOutboundInterceptor = new ServiceRequestOutboundInterceptor(requestDaoService);
        final ServiceRequest serviceRequest = new ServiceRequest();
        when(mockServiceRequestDao.fetch(ServiceRequest.class, 1L)).thenReturn(serviceRequest);

        assertNull(serviceRequest.getResponseDateTime());
        assertNull(serviceRequest.getResponsePayload());
        serviceRequestOutboundInterceptor.handleMessage(soapMessage);
        assertNotNull(serviceRequest.getResponseDateTime());
        String response = new String(serviceRequest.getResponsePayload(), StandardCharsets.UTF_8);
        assertTrue(response.contains(data));
        verify(mockServiceRequestDao).persist(serviceRequest);
    }

    @Test
    void testHandleMessagePhaseConstructor() {
        final ServiceRequestOutboundInterceptor serviceRequestOutboundInterceptor =
            new ServiceRequestOutboundInterceptor(Phase.SETUP);

        assertNotNull(serviceRequestOutboundInterceptor);
    }

    /**
     * Builds a dummy soap message.
     * <p>
     * the path to the xml file containing the soap message.
     *
     * @return a soap message
     */
    private SoapMessage getDummySoapMessageWithCachedOutputStream(String data) throws IOException {
        SoapMessage soapMessage = new SoapMessage(new MessageImpl());
        try (CachedOutputStream cachedOutputStream = new CachedOutputStream()) {
            cachedOutputStream.write(data.getBytes());
            soapMessage.setContent(OutputStream.class, cachedOutputStream);
        }
        return soapMessage;
    }

}
