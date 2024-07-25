package uk.gov.moj.sdt.transformers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.transformers.exception.JaxbXmlConversionException;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CMCFeedbackTransformerTest extends AbstractSdtUnitTestBase {

    private static final String SDT_REQUEST_ID = "MCOL-20240725120000-000000001-0000001";

    private static final String ERROR_INDIVIDUAL_REQUEST_IS_NULL = "Individual request should not be null";
    private static final String ERROR_UNEXPECTED_SDT_REQUEST_REF =
            "Individual request has unexpected SDT request reference";
    private static final String ERROR_UNEXPECTED_TARGET_APP_RESPONSE =
            "Individual request has unexpected target application response";

    @Mock
    private JAXBContext mockJaxbContext;

    private CMCFeedbackTransformer cmcFeedbackTransformer;

    @Override
    protected void setUpLocalTests() {
        cmcFeedbackTransformer = new CMCFeedbackTransformer();
    }

    @Test
    void testCmcFeedback() {
        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();
        cmcUpdateRequest.setWarrantNumber("12345678");

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        assertNotNull(individualRequest, ERROR_INDIVIDUAL_REQUEST_IS_NULL);
        assertEquals(SDT_REQUEST_ID, individualRequest.getSdtRequestReference(), ERROR_UNEXPECTED_SDT_REQUEST_REF);
        String targetAppResponse = new String(individualRequest.getTargetApplicationResponse());
        assertEquals("<cmcResponseDetail><warrantNumber>12345678</warrantNumber></cmcResponseDetail>",
                targetAppResponse, ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    @Test
    void testCmcFeedbackEmpty() {
        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        assertNotNull(individualRequest, ERROR_INDIVIDUAL_REQUEST_IS_NULL);
        assertEquals(SDT_REQUEST_ID, individualRequest.getSdtRequestReference(), ERROR_UNEXPECTED_SDT_REQUEST_REF);
        String targetAppResponse = new String(individualRequest.getTargetApplicationResponse());
        assertEquals("<cmcResponseDetail/>", targetAppResponse, ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    @Test
    void testCmcFeedbackError() {
        String errorText = "Error text 1";

        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();
        cmcUpdateRequest.setErrorCode(1);
        cmcUpdateRequest.setErrorText(errorText);
        cmcUpdateRequest.setFee(100L);

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        assertNotNull(individualRequest, ERROR_INDIVIDUAL_REQUEST_IS_NULL);
        assertEquals(SDT_REQUEST_ID, individualRequest.getSdtRequestReference(), ERROR_UNEXPECTED_SDT_REQUEST_REF);

        IErrorLog errorLog = individualRequest.getErrorLog();
        assertNotNull(errorLog, "Error log should not be null");
        assertEquals("1", errorLog.getErrorCode(), "Error log has unexpected error code");
        assertEquals(errorText, errorLog.getErrorText(), "Error log has unexpected error text");
        assertEquals(IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus(),
                individualRequest.getRequestStatus(), "Individual request has unexpected status");

        String targetAppResponse = new String(individualRequest.getTargetApplicationResponse());
        assertEquals("<cmcResponseDetail><fee>100</fee></cmcResponseDetail>", targetAppResponse,
                ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    @Test
    void testConvertToXmlException() throws JAXBException {
        cmcFeedbackTransformer.setJaxbContext(mockJaxbContext);

        when(mockJaxbContext.createMarshaller()).thenThrow(new JAXBException("Test"));

        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();

        try {
            cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);
            fail("JaxbXmlConversionException should be raised");
        } catch (JaxbXmlConversionException e) {
            assertEquals("Could not convert CMCUpdateRequest to XML", e.getMessage());
        }
    }
}
