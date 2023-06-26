package uk.gov.moj.sdt.interceptors.out;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import uk.gov.moj.sdt.interceptors.AbstractServiceRequest;
import uk.gov.moj.sdt.interceptors.service.RequestDaoService;

/**
 * Class to intercept outgoing messages to audit them.
 *
 * @author d195274
 */
public class ServiceRequestOutboundInterceptor extends AbstractServiceRequest {
    /**
     * Default constructor.
     */
    public ServiceRequestOutboundInterceptor(RequestDaoService requestDaoService) {
        super(Phase.PREPARE_SEND_ENDING);
        setRequestDaoService(requestDaoService);
        addAfter(XmlOutboundInterceptor.class.getName());
    }

    /**
     * Create instance of {@link ServiceRequestOutboundInterceptor}.
     *
     * @param phase the phase of the CXF interceptor chain.
     */
    public ServiceRequestOutboundInterceptor(final String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        this.getRequestDaoService().persistEnvelope(this.readOutputMessage(message));
    }

}
