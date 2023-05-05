package uk.gov.moj.sdt.utils.cmc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.utils.cmc.exception.CMCException;
import uk.gov.moj.sdt.utils.cmc.xml.XmlElementValueReader;

import static uk.gov.moj.sdt.utils.cmc.RequestType.BREATHING_SPACE;
import static uk.gov.moj.sdt.utils.cmc.RequestType.CLAIM_STATUS_UPDATE;
import static uk.gov.moj.sdt.utils.cmc.RequestType.JUDGMENT;
import static uk.gov.moj.sdt.utils.cmc.RequestType.JUDGMENT_WARRANT;
import static uk.gov.moj.sdt.utils.cmc.RequestType.WARRANT;

@Component
public class RequestTypeXmlNodeValidator {

    private CCDReferenceValidator ccdReferenceValidator;

    private XmlElementValueReader xmlReader;

    @Autowired
    public RequestTypeXmlNodeValidator(CCDReferenceValidator ccdReferenceValidator,
                                       XmlElementValueReader xmlReader) {
        this.ccdReferenceValidator = ccdReferenceValidator;
        this.xmlReader = xmlReader;
    }

    public boolean isCMCRequestType(String requestType, String requestPayload, String xmlNodeName, boolean throwException) {
        if (isCCDReference(requestPayload, xmlNodeName)) {
            if (!isValidRequestType(requestType) && throwException) {
                throw new CMCException(String.format("Request Type: %s not supported", requestType));
            }
            return true;
        }
        return false;
    }

    public boolean isValidRequestType(String requestType) {
        return requestType != null && (JUDGMENT.getType().equalsIgnoreCase(requestType)
            || WARRANT.getType().equalsIgnoreCase(requestType)
            || CLAIM_STATUS_UPDATE.getType().equalsIgnoreCase(requestType)
            || JUDGMENT_WARRANT.getType().equalsIgnoreCase(requestType)
            || BREATHING_SPACE.getType().equalsIgnoreCase(requestType));
    }

    public boolean isCCDReference(String requestPayload, String xmlNodeName) {
        String elementValue =  xmlReader.getElementValue(requestPayload, xmlNodeName);
        return ccdReferenceValidator.isValidCCDReference(elementValue);
    }
}
