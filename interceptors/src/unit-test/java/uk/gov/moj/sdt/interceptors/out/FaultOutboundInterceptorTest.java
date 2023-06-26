package uk.gov.moj.sdt.interceptors.out;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.MessageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.ServiceRequestDao;
import uk.gov.moj.sdt.domain.ServiceRequest;
import uk.gov.moj.sdt.interceptors.service.RequestDaoService;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

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
class FaultOutboundInterceptorTest extends AbstractSdtUnitTestBase {

    @Mock
    ServiceRequestDao mockServiceRequestDao;

    RequestDaoService requestDaoService;

    /**
     * Error message returned when a fault occurs.
     */
    private static final String ERROR_MESSAGE = "A SDT system component error " +
            "has occurred. Please contact the SDT support team for assistance";

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        requestDaoService = new RequestDaoService(mockServiceRequestDao);
    }

    /**
     * Test method for
     * {@link uk.gov.moj.sdt.interceptors.out.FaultOutboundInterceptor
     * #handleMessage(org.apache.cxf.binding.soap.SoapMessage)}.
     */
    @Test
    void testHandleMessage() {
        SdtContext.getContext().setServiceRequestId(1L);
        final SoapMessage soapMessage = getDummySoapMessageWithFault();
        final FaultOutboundInterceptor faultOutboundInterceptor = new FaultOutboundInterceptor(requestDaoService);
        final ServiceRequest serviceRequest = new ServiceRequest();
        when(mockServiceRequestDao.fetch(ServiceRequest.class, 1L)).thenReturn(serviceRequest);
        assertNull(serviceRequest.getResponseDateTime());
        assertNull(serviceRequest.getResponsePayload());

        faultOutboundInterceptor.handleMessage(soapMessage);
        assertNotNull(serviceRequest.getResponseDateTime());
        String response = new String(serviceRequest.getResponsePayload(), StandardCharsets.UTF_8);
        assertTrue(response.contains(ERROR_MESSAGE));
        verify(mockServiceRequestDao).persist(serviceRequest);
    }

    /**
     * Builds a dummy soap message.
     * <p>
     * the path to the xml file containing the soap message.
     *
     * @return a soap message
     */
    private SoapMessage getDummySoapMessageWithFault() {
        final SoapMessage soapMessage = new SoapMessage(new MessageImpl());
        soapMessage.setExchange(new ExchangeImpl());
        final Fault fault = new Fault(new RuntimeException(ERROR_MESSAGE));
        soapMessage.setContent(Exception.class, fault);
        return soapMessage;
    }

}
