package uk.gov.moj.sdt.transformers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequestStatus;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.transformers.api.ICMCFeedbackTransformer;
import uk.gov.moj.sdt.transformers.exception.JaxbXmlConversionException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Component("cmcFeedbackTransformer")
@Getter
@Setter
@Slf4j
public class CMCFeedbackTransformer implements ICMCFeedbackTransformer {

    private JAXBContext jaxbContext;

    public CMCFeedbackTransformer() {
        try {
            jaxbContext = JAXBContext.newInstance(CMCUpdateRequest.class);
        } catch (JAXBException e) {
            throw new JaxbXmlConversionException("Could not create JAXBContext", e);
        }
    }

    @Override
    public IIndividualRequest transformJsonToDomain(String sdtRequestId, CMCUpdateRequest cmcUpdateRequest) {
        log.debug("Transform from CMCUpdateRequest for sdtRequestId [{}] to IIndividualRequest", sdtRequestId);

        IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(sdtRequestId);

        CMCUpdateRequestStatus status = cmcUpdateRequest.getRequestStatus();

        if (CMCUpdateRequestStatus.REJECTED.equals(status)) {
            IErrorLog errorLog = new ErrorLog();

            errorLog.setErrorCode(String.valueOf(cmcUpdateRequest.getErrorCode()));
            errorLog.setErrorText(cmcUpdateRequest.getErrorText());

            individualRequest.markRequestAsRejected(errorLog);
        } else if (CMCUpdateRequestStatus.ACCEPTED.equals(status)) {
            individualRequest.markRequestAsAccepted();
        } else if (CMCUpdateRequestStatus.INITIALLY_ACCEPTED.equals(status)) {
            individualRequest.markRequestAsInitiallyAccepted();
        }

        String cmcUpdateRequestXml = convertToXml(cmcUpdateRequest);

        byte[] targetAppResponse =
                cmcUpdateRequestXml.isEmpty() ? null : cmcUpdateRequestXml.getBytes(StandardCharsets.UTF_8);
        individualRequest.setTargetApplicationResponse(targetAppResponse);

        return individualRequest;
    }

    private String convertToXml(CMCUpdateRequest cmcUpdateRequest) {
        log.debug("Convert CMCUpdateRequest to XML");

        String xml;

        try {
            Marshaller marshaller = jaxbContext.createMarshaller();

            // Exclude XML declaration from output
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(cmcUpdateRequest, stringWriter);

            xml = stringWriter.toString();

        } catch (JAXBException e) {
            throw new JaxbXmlConversionException("Could not convert CMCUpdateRequest to XML", e);
        }

        return xml;
    }
}
