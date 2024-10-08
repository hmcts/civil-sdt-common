package uk.gov.moj.sdt.transformers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequestStatus;
import uk.gov.moj.sdt.cmc.model.JudgmentWarrantStatus;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.transformers.exception.JaxbXmlConversionException;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.ACCEPTED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.INITIALLY_ACCEPTED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

@ExtendWith(MockitoExtension.class)
class CMCFeedbackTransformerTest extends AbstractSdtUnitTestBase {

    private static final String SDT_REQUEST_ID = "MCOL-20240725120000-000000001-0000001";

    private static final String ERROR_INDIVIDUAL_REQUEST_IS_NULL = "Individual request should not be null";
    private static final String ERROR_UNEXPECTED_SDT_REQUEST_REF =
            "Individual request has unexpected SDT request reference";
    private static final String ERROR_UNEXPECTED_TARGET_APP_RESPONSE =
            "Individual request has unexpected target application response";

    private static final String RESPONSE_DATE_FORMAT = "yyyy-MM-ddXXX";

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

        String expectedElement = createXmlElement("warrantNumber", "12345678");
        String expectedTargetAppResponse = createResponseDetailXmlElement(expectedElement);
        String targetAppResponse = new String(individualRequest.getTargetApplicationResponse());
        assertEquals(expectedTargetAppResponse, targetAppResponse, ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    @Test
    void testCmcFeedbackEmpty() {
        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        assertNotNull(individualRequest, ERROR_INDIVIDUAL_REQUEST_IS_NULL);
        assertEquals(SDT_REQUEST_ID, individualRequest.getSdtRequestReference(), ERROR_UNEXPECTED_SDT_REQUEST_REF);

        String targetAppResponse = new String(individualRequest.getTargetApplicationResponse());
        String expectedTargetAppResponse = createResponseDetailXmlElement(null);
        assertEquals(expectedTargetAppResponse, targetAppResponse, ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    @Test
    void testCmcFeedbackAccepted() {
        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();
        cmcUpdateRequest.setRequestStatus(CMCUpdateRequestStatus.ACCEPTED);

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        assertNotNull(individualRequest, ERROR_INDIVIDUAL_REQUEST_IS_NULL);
        assertEquals(SDT_REQUEST_ID, individualRequest.getSdtRequestReference(), ERROR_UNEXPECTED_SDT_REQUEST_REF);

        assertIndividualRequestStatus(ACCEPTED.getStatus(), individualRequest);
    }

    @Test
    void testCmcFeedbackInitiallyAccepted() {
        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();
        cmcUpdateRequest.setRequestStatus(CMCUpdateRequestStatus.INITIALLY_ACCEPTED);

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        assertNotNull(individualRequest, ERROR_INDIVIDUAL_REQUEST_IS_NULL);
        assertEquals(SDT_REQUEST_ID, individualRequest.getSdtRequestReference(), ERROR_UNEXPECTED_SDT_REQUEST_REF);

        assertIndividualRequestStatus(INITIALLY_ACCEPTED.getStatus(), individualRequest);
    }

    @Test
    void testCmcFeedbackRejected() {
        String errorText = "Error text 1";

        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();
        cmcUpdateRequest.setRequestStatus(CMCUpdateRequestStatus.REJECTED);
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
        assertIndividualRequestStatus(REJECTED.getStatus(), individualRequest);

        String expectedElement = createXmlElement("fee", "100");
        String expectedTargetAppResponse = createResponseDetailXmlElement(expectedElement);
        String targetAppResponse = new String(individualRequest.getTargetApplicationResponse());
        assertEquals(expectedTargetAppResponse, targetAppResponse, ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
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

    @ParameterizedTest
    @MethodSource("fieldsIncludedInXml")
    void testXmlFieldsIncluded(CMCUpdateRequest cmcUpdateRequest, String elementName, String elementValue) {
        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        String expectedElement = createXmlElement(elementName, elementValue);
        String expectedTargetAppResponse = createResponseDetailXmlElement(expectedElement);
        assertEquals(expectedTargetAppResponse, new String(individualRequest.getTargetApplicationResponse()),
                ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    @ParameterizedTest(name = "{0} field")
    @MethodSource("fieldsExcludedFromXml")
    void testXmlFieldsExcluded(CMCUpdateRequest cmcUpdateRequest) {
        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);

        String expectedTargetAppResponse = createResponseDetailXmlElement(null);
        assertEquals(expectedTargetAppResponse, new String(individualRequest.getTargetApplicationResponse()),
                ERROR_UNEXPECTED_TARGET_APP_RESPONSE);
    }

    /**
     * Create an XML element in the format &lt;elementName&gt;elementValue&lt;/elementName&gt;.
     * If elementValue is null then an empty element in the format &lt;elementName/&gt; will be returned.
     *
     * @param elementName The name of the element
     * @param elementValue The value of the element.  Set to null to produce an empty element.
     * @return An XML element
     */
    private String createXmlElement(String elementName, String elementValue) {
        return elementValue == null ? "<" + elementName + "/>" :
                "<" + elementName + ">" + elementValue + "</" + elementName + ">";
    }

    private String createResponseDetailXmlElement(String elementValue) {
        return createXmlElement("mcolResponseDetail", elementValue);
    }

    private static Stream<Arguments> fieldsIncludedInXml() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RESPONSE_DATE_FORMAT);

        CMCUpdateRequest cmcUpdateRequestClaimNumber = new CMCUpdateRequest();
        cmcUpdateRequestClaimNumber.setClaimNumber("12131415");

        CMCUpdateRequest cmcUpdateRequestIssueDate = new CMCUpdateRequest();
        Date issueDate = new Calendar.Builder().setDate(2024, Calendar.JULY, 1).build().getTime();
        cmcUpdateRequestIssueDate.setIssueDate(issueDate);

        CMCUpdateRequest cmcUpdateRequestServiceDate = new CMCUpdateRequest();
        Date serviceDate = new Calendar.Builder().setDate(2024, Calendar.JULY, 2).build().getTime();
        cmcUpdateRequestServiceDate.setServiceDate(serviceDate);

        CMCUpdateRequest cmcUpdateRequestJudgmentEnteredDate = new CMCUpdateRequest();
        Date judgmentEnteredDate = new Calendar.Builder().setDate(2024, Calendar.JULY, 3).build().getTime();
        cmcUpdateRequestJudgmentEnteredDate.setJudgmentEnteredDate(judgmentEnteredDate);

        CMCUpdateRequest cmcUpdateRequestFirstPaymentDate = new CMCUpdateRequest();
        Date firstPaymentDate = new Calendar.Builder().setDate(2024, Calendar.JULY, 4).build().getTime();
        cmcUpdateRequestFirstPaymentDate.setFirstPaymentDate(firstPaymentDate);

        CMCUpdateRequest cmcUpdateRequestWarrantNumber = new CMCUpdateRequest();
        cmcUpdateRequestWarrantNumber.setWarrantNumber("87654321");

        CMCUpdateRequest cmcUpdateRequestCourtCode = new CMCUpdateRequest();
        cmcUpdateRequestCourtCode.setEnforcingCourtCode("TC");

        CMCUpdateRequest cmcUpdateRequestCourtName = new CMCUpdateRequest();
        cmcUpdateRequestCourtName.setEnforcingCourtName("Test Court");

        CMCUpdateRequest cmcUpdateRequestFee = new CMCUpdateRequest();
        cmcUpdateRequestFee.setFee(100L);

        CMCUpdateRequest cmcUpdateRequestJudgmentWarrantStatus = new CMCUpdateRequest();
        cmcUpdateRequestJudgmentWarrantStatus.setJudgmentWarrantStatus(JudgmentWarrantStatus.JUDGMENT_REJECTED_BY_CCBC);

        return Stream.of(
                arguments(cmcUpdateRequestClaimNumber, "claimNumber", "12131415"),
                arguments(cmcUpdateRequestIssueDate, "issueDate", simpleDateFormat.format(issueDate)),
                arguments(cmcUpdateRequestServiceDate, "serviceDate", simpleDateFormat.format(serviceDate)),
                arguments(cmcUpdateRequestJudgmentEnteredDate, "judgmentEnteredDate",
                        simpleDateFormat.format(judgmentEnteredDate)),
                arguments(cmcUpdateRequestFirstPaymentDate, "firstPaymentDate",
                        simpleDateFormat.format(firstPaymentDate)),
                arguments(cmcUpdateRequestWarrantNumber, "warrantNumber", "87654321"),
                arguments(cmcUpdateRequestCourtCode, "enforcingCourtCode", "TC"),
                arguments(cmcUpdateRequestCourtName, "enforcingCourtName", "Test Court"),
                arguments(cmcUpdateRequestFee, "fee", "100"),
                arguments(cmcUpdateRequestJudgmentWarrantStatus, "judgmentWarrantStatus",
                        JudgmentWarrantStatus.JUDGMENT_REJECTED_BY_CCBC.getMessage())
        );
    }

    private static Stream<Arguments> fieldsExcludedFromXml() {
        CMCUpdateRequest cmcUpdateRequestErrorCode = new CMCUpdateRequest();
        cmcUpdateRequestErrorCode.setErrorCode(99);

        CMCUpdateRequest cmcUpdateRequestErrorText = new CMCUpdateRequest();
        cmcUpdateRequestErrorText.setErrorText("Error text 99");

        CMCUpdateRequest cmcUpdateRequestRequestStatus = new CMCUpdateRequest();
        cmcUpdateRequestRequestStatus.setRequestStatus(CMCUpdateRequestStatus.ACCEPTED);

        return Stream.of(
                arguments(named("errorCode", cmcUpdateRequestErrorCode)),
                arguments(named("errorText", cmcUpdateRequestErrorText)),
                arguments(named("requestStatus", cmcUpdateRequestRequestStatus))
        );
    }

    private void assertIndividualRequestStatus(String expectedStatus, IIndividualRequest individualRequest) {
        assertEquals(expectedStatus, individualRequest.getRequestStatus(), "Individual request has unexpected status");
    }
}
