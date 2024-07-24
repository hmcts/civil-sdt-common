package uk.gov.moj.sdt.transformers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.transformers.api.ICMCFeedbackTransformer;
import uk.gov.moj.sdt.utils.cmc.exception.CMCException;

import java.nio.charset.StandardCharsets;

@Component("cmcFeedbackTransformer")
@Getter
@Setter
public class CMCFeedbackTransformer implements ICMCFeedbackTransformer {

    private XmlMapper xmlMapper;

    public CMCFeedbackTransformer() {
        xmlMapper = new XmlMapper();
    }

    @Override
    public IIndividualRequest transformJsonToDomain(String sdtRequestId, CMCUpdateRequest cmcUpdateRequest) {
        IIndividualRequest individualRequest = new IndividualRequest();

        individualRequest.setSdtRequestReference(sdtRequestId);

        String cmcUpdateRequestXml;
        try {
            cmcUpdateRequestXml = xmlMapper.writeValueAsString(cmcUpdateRequest);
        } catch (JsonProcessingException e) {
            throw new CMCException(e.getMessage(), e);
        }

        byte[] targetAppResponse =
                cmcUpdateRequestXml == null ? null : cmcUpdateRequestXml.getBytes(StandardCharsets.UTF_8);
        individualRequest.setTargetApplicationResponse(targetAppResponse);

        return individualRequest;
    }
}
