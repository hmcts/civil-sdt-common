package uk.gov.moj.sdt.interceptors.in;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.interceptors.AbstractSdtInterceptor;
import uk.gov.moj.sdt.utils.SdtContext;

import java.util.List;
import java.util.Map;

import static org.apache.cxf.phase.Phase.RECEIVE;

@Component("IdamIdInboundInterceptor")
public class IdamIdInboundInterceptor extends AbstractSdtInterceptor {

    private static final String HEADER_CUSTOMER_IDAM_ID = "customerIdamId";

    public IdamIdInboundInterceptor() {
        this(RECEIVE);
        addAfter(ServiceRequestInboundInterceptor.class.getName());
    }

    public IdamIdInboundInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(SoapMessage soapMessage) {
        Map<String, List<String>> protocolHeaders =
            CastUtils.cast((Map<?, ?>) soapMessage.get(Message.PROTOCOL_HEADERS));

        if(protocolHeaders != null) {
            // If message headers contains the customerIdamId header then extract the value and add to SDT Context
            List<String> customerIdamId = protocolHeaders.get(HEADER_CUSTOMER_IDAM_ID);
            if (customerIdamId != null) {
                SdtContext.getContext().setCustomerIdamId(customerIdamId.get(0));
            }
        }
    }
}
