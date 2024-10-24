package uk.gov.moj.sdt.utils.cmc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.cmc.exception.CMCUnsupportedRequestTypeException;
import uk.gov.moj.sdt.utils.cmc.xml.XmlElementValueReader;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTypeXmlNodeValidatorTest extends AbstractSdtUnitTestBase {

    private static final String REQUEST_PAYLOAD_CCD_CLAIM_REF = "<claimNumber>1234-1234-1234-1234</claimNumber>";
    private static final String REQUEST_PAYLOAD_NON_CCD_CLAIM_REF = "<claimNumber>9QZ00005</claimNumber>";
    private static final String NODE_NAME_CLAIM_NUMBER = "claimNumber";
    private static final String CCD_CLAIM_REF = "1234-1234-1234-1234";
    private static final String NON_CCD_CLAIM_REF = "9QZ00005";

    @Mock
    private CCDReferenceValidator mockCCDReferenceValidator;

    @Mock
    private XmlElementValueReader mockXmlElementValueReader;

    private RequestTypeXmlNodeValidator requestTypeXmlNodeValidator;

    @Override
    protected void setUpLocalTests() {
        requestTypeXmlNodeValidator =
                new RequestTypeXmlNodeValidator(mockCCDReferenceValidator, mockXmlElementValueReader);
    }

    @Test
    void testIsCmcClaimRequest() {
        assertTrue(requestTypeXmlNodeValidator.isCMCClaimRequest(RequestType.CLAIM.getType(), true),
                   "The combination of CLAIM and true should be flagged as a CMC claim request");
    }

    @ParameterizedTest
    @MethodSource("invalidCmcClaimRequest")
    void testNotIsCmcClaimRequest(RequestType requestType, Boolean readyForAlternateService) {
        String reqType = requestType == null ? null : requestType.getType();
        assertFalse(requestTypeXmlNodeValidator.isCMCClaimRequest(reqType, readyForAlternateService),
                    "This combination should not be flagged as a CMC claim request");
    }

    @Test
    void testIsCmcRequestType() {
        when(mockXmlElementValueReader.getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER))
                .thenReturn(CCD_CLAIM_REF);
        when(mockCCDReferenceValidator.isValidCCDReference(CCD_CLAIM_REF)).thenReturn(true);

        assertTrue(requestTypeXmlNodeValidator.isCMCRequestType(RequestType.JUDGMENT.getType(),
                                                                REQUEST_PAYLOAD_CCD_CLAIM_REF,
                                                                NODE_NAME_CLAIM_NUMBER,
                                                                false),
                   "This request type with a CCD claim ref should be flagged as a CMC request type");

        verify(mockXmlElementValueReader).getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER);
        verify(mockCCDReferenceValidator).isValidCCDReference(CCD_CLAIM_REF);
    }

    @Test
    void testNotIsCmcRequestType() {
        when(mockXmlElementValueReader.getElementValue(REQUEST_PAYLOAD_NON_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER))
                .thenReturn(NON_CCD_CLAIM_REF);
        when(mockCCDReferenceValidator.isValidCCDReference(NON_CCD_CLAIM_REF)).thenReturn(false);

        assertFalse(requestTypeXmlNodeValidator.isCMCRequestType(RequestType.JUDGMENT.getType(),
                                                                 REQUEST_PAYLOAD_NON_CCD_CLAIM_REF,
                                                                 NODE_NAME_CLAIM_NUMBER,
                                                                 false),
                    "This request type with a non-CCD claim ref should not be flagged as a CMC request type");

        verify(mockXmlElementValueReader).getElementValue(REQUEST_PAYLOAD_NON_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER);
        verify(mockCCDReferenceValidator).isValidCCDReference(NON_CCD_CLAIM_REF);
    }

    @ParameterizedTest
    @MethodSource("validCmcRequestTypes")
    void testIsCmcRequestTypeValidType(String requestType) {
        when(mockXmlElementValueReader.getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER))
                .thenReturn(CCD_CLAIM_REF);
        when(mockCCDReferenceValidator.isValidCCDReference(CCD_CLAIM_REF)).thenReturn(true);

        assertTrue(requestTypeXmlNodeValidator.isCMCRequestType(requestType,
                                                                REQUEST_PAYLOAD_CCD_CLAIM_REF,
                                                                NODE_NAME_CLAIM_NUMBER,
                                                                true),
                   "This request type with a CCD claim ref should be flagged as a CMC request type");

        verify(mockXmlElementValueReader).getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER);
        verify(mockCCDReferenceValidator).isValidCCDReference(CCD_CLAIM_REF);
    }

    @ParameterizedTest
    @ValueSource(strings = {"mcolSetAside"})
    @NullSource
    void testIsCmcRequestTypeInvalidType(String requestType) {
        when(mockXmlElementValueReader.getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER))
                .thenReturn(CCD_CLAIM_REF);
        when(mockCCDReferenceValidator.isValidCCDReference(CCD_CLAIM_REF)).thenReturn(true);

        CMCUnsupportedRequestTypeException exception =
                assertThrows(CMCUnsupportedRequestTypeException.class,
                             () -> requestTypeXmlNodeValidator.isCMCRequestType(requestType,
                                                                                REQUEST_PAYLOAD_CCD_CLAIM_REF,
                                                                                NODE_NAME_CLAIM_NUMBER,
                                                                                true),
                             "A CMCUnsupportedRequestTypeException should be thrown for this request type"
        );
        assertEquals("Request Type " + requestType + " not supported",
                     exception.getMessage(),
                     "CMCUnsupportedRequestTypeException has unexpected message");

        verify(mockXmlElementValueReader).getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER);
        verify(mockCCDReferenceValidator).isValidCCDReference(CCD_CLAIM_REF);
    }

    @ParameterizedTest
    @MethodSource("validCmcRequestTypes")
    void testIsValidRequestType(String requestType) {
        assertTrue(requestTypeXmlNodeValidator.isValidRequestType(requestType),
                   "This request type should be a valid request type");
    }

    @ParameterizedTest
    @ValueSource(strings = {"mcolSetAside"})
    @NullSource
    void testNotIsValidRequestType(String requestType) {
        assertFalse(requestTypeXmlNodeValidator.isValidRequestType(requestType),
                    "This request type should not be a valid request type");
    }

    @Test
    void testIsCCDReference() {
        when(mockXmlElementValueReader.getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER))
                .thenReturn(CCD_CLAIM_REF);
        when(mockCCDReferenceValidator.isValidCCDReference(CCD_CLAIM_REF)).thenReturn(true);

        assertTrue(requestTypeXmlNodeValidator.isCCDReference(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER),
                   "Claim reference should be a CCD reference");

        verify(mockXmlElementValueReader).getElementValue(REQUEST_PAYLOAD_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER);
        verify(mockCCDReferenceValidator).isValidCCDReference(CCD_CLAIM_REF);
    }

    @Test
    void testNotIsCCDReference() {
        when(mockXmlElementValueReader.getElementValue(REQUEST_PAYLOAD_NON_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER))
                .thenReturn(NON_CCD_CLAIM_REF);
        when(mockCCDReferenceValidator.isValidCCDReference(NON_CCD_CLAIM_REF)).thenReturn(false);

        assertFalse(requestTypeXmlNodeValidator.isCCDReference(REQUEST_PAYLOAD_NON_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER),
                    "Claim reference should not be a CCD reference");

        verify(mockXmlElementValueReader).getElementValue(REQUEST_PAYLOAD_NON_CCD_CLAIM_REF, NODE_NAME_CLAIM_NUMBER);
        verify(mockCCDReferenceValidator).isValidCCDReference(NON_CCD_CLAIM_REF);
    }

    private static Stream<Arguments> validCmcRequestTypes() {
        return Stream.of(
                arguments("mcolBreathingSpace"),
                arguments("mcolClaimStatusUpdate"),
                arguments("mcolJudgment"),
                arguments("mcolJudgmentWarrant"),
                arguments("mcolWarrant")
        );
    }

    private static Stream<Arguments> invalidCmcClaimRequest() {
        return Stream.of(
                arguments(RequestType.JUDGMENT, true),
                arguments(RequestType.JUDGMENT, false),
                arguments(null, true),
                arguments(null, false),
                arguments(RequestType.JUDGMENT, null),
                arguments(null, null)
        );
    }
}
