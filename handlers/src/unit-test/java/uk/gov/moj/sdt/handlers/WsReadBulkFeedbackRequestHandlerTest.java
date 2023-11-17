package uk.gov.moj.sdt.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.services.api.IBulkFeedbackService;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WsReadBulkFeedbackRequestHandlerTest {

    @Mock
    IBulkFeedbackService mockBulkFeedbackService;

    @Mock
    ITransformer<BulkFeedbackRequestType, BulkFeedbackResponseType, IBulkFeedbackRequest, IBulkSubmission> mockTransformer;

    @Mock
    BulkFeedbackRequestType mockBulkFeedbackRequestType;

    @Mock
    HeaderType mockHeader;

    @Mock
    IBulkFeedbackRequest mockBulkFeedbackRequest;

    @Mock
    BulkFeedbackResponseType mockBulkFeedbackResponseType;

    @Mock
    IBulkSubmission mockBulkSubmission;

    WsReadBulkFeedbackRequestHandler wsReadBulkFeedbackRequestHandler;

    @BeforeEach
    public void setUp() {
        wsReadBulkFeedbackRequestHandler = spy(new WsReadBulkFeedbackRequestHandler(
            mockBulkFeedbackService, mockTransformer));
        wsReadBulkFeedbackRequestHandler.setBulkFeedbackService(mockBulkFeedbackService);
        wsReadBulkFeedbackRequestHandler.setTransformer(mockTransformer);

        SdtMetricsMBean.getMetrics().reset();

        when(mockBulkFeedbackRequestType.getHeader()).thenReturn(mockHeader);
        when(mockHeader.getSdtCustomerId()).thenReturn(1L);
        when(mockHeader.getSdtBulkReference()).thenReturn("1");
    }

    @Test
    public void testGetBulkFeedback() {
        when(mockTransformer.transformJaxbToDomain(mockBulkFeedbackRequestType)).thenReturn(mockBulkFeedbackRequest);
        when(mockBulkFeedbackService.getBulkFeedback(mockBulkFeedbackRequest)).thenReturn(mockBulkSubmission);
        when(mockTransformer.transformDomainToJaxb(mockBulkSubmission)).thenReturn(mockBulkFeedbackResponseType);

        BulkFeedbackResponseType result;

        try (MockedStatic<VisitableTreeWalker> mockStaticVisitableTreeWalker = mockStatic(VisitableTreeWalker.class)) {
            mockStaticVisitableTreeWalker.when(() -> VisitableTreeWalker.walk(mockBulkFeedbackRequest, "Validator"))
                .then((Answer<Void>) invocation -> null);

            result = wsReadBulkFeedbackRequestHandler.getBulkFeedback(mockBulkFeedbackRequestType);

            verify(mockTransformer).transformJaxbToDomain(mockBulkFeedbackRequestType);
            mockStaticVisitableTreeWalker.verify(() -> VisitableTreeWalker.walk(mockBulkFeedbackRequest, "Validator"));
            verify(mockBulkFeedbackService).getBulkFeedback(mockBulkFeedbackRequest);
            verify(mockTransformer).transformDomainToJaxb(mockBulkSubmission);
        }

        testMetrics();
        assertEquals(mockBulkFeedbackResponseType, result);
    }

    @Test
    public void testGetBulkFeedbackThrowsAbstractBusinessException() {
        CustomerNotFoundException exception = new CustomerNotFoundException("MOCK_CODE", "MOCK_DESCRIPTION");

        when(mockTransformer.transformJaxbToDomain(mockBulkFeedbackRequestType))
            .thenThrow(exception);

        BulkFeedbackResponseType bulkFeedbackResponseType =
                wsReadBulkFeedbackRequestHandler.getBulkFeedback(mockBulkFeedbackRequestType);

        StatusType statusType = bulkFeedbackResponseType.getBulkRequestStatus().getStatus();
        assertEquals(StatusCodeType.ERROR, statusType.getCode(), "BulkFeedback response has unexpected status code");
        ErrorType errorType = statusType.getError();
        assertEquals("MOCK_CODE", errorType.getCode(), "Status error has unexpected code");
        assertEquals("MOCK_DESCRIPTION", errorType.getDescription(), "Status error has unexpected description");

        testMetrics();
    }

    private void testMetrics() {
        String expectedBulkFeedbackCount = "count[1]";

        assertTrue(SdtMetricsMBean.getMetrics().getBulkFeedbackStats().contains(expectedBulkFeedbackCount));
        assertEquals(1, SdtMetricsMBean.getMetrics().getActiveBulkCustomers());
    }
}
