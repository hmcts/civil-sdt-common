package uk.gov.moj.sdt.handlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.services.api.IBulkFeedbackService;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkRequestStatusType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

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
        wsReadBulkFeedbackRequestHandler = Mockito.spy(new WsReadBulkFeedbackRequestHandler(
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

        try (MockedStatic<VisitableTreeWalker> mockStaticVisitableTreeWalker = Mockito.mockStatic(VisitableTreeWalker.class)) {
            mockStaticVisitableTreeWalker.when(() -> VisitableTreeWalker.walk(mockBulkFeedbackRequest, "Validator"))
                .then((Answer<Void>) invocation -> null);

            result = wsReadBulkFeedbackRequestHandler.getBulkFeedback(mockBulkFeedbackRequestType);

            verify(mockTransformer).transformJaxbToDomain(mockBulkFeedbackRequestType);
            verifyStatic(VisitableTreeWalker.class);
            VisitableTreeWalker.walk(mockBulkFeedbackRequest, "Validator");
            verify(mockBulkFeedbackService).getBulkFeedback(mockBulkFeedbackRequest);
            verify(mockTransformer).transformDomainToJaxb(mockBulkSubmission);
        }

        testMetrics();
        Assertions.assertEquals(mockBulkFeedbackResponseType, result);
    }

    @Test
    public void testGetBulkFeedbackThrowsAbstractBusinessException() throws Exception {
        CustomerNotFoundException exception = new CustomerNotFoundException("MOCK_CODE", "MOCK_DESCRIPTION");

        when(mockTransformer.transformJaxbToDomain(mockBulkFeedbackRequestType))
            .thenThrow(exception);

        wsReadBulkFeedbackRequestHandler.getBulkFeedback(mockBulkFeedbackRequestType);

        verifyPrivate(wsReadBulkFeedbackRequestHandler)
            .invoke(
                "handleBusinessException",
                any(CustomerNotFoundException.class),
                any(BulkRequestStatusType.class)
            );

        testMetrics();
    }

    private void testMetrics() {
        String expectedBulkFeedbackCount = "count[1]";

        Assertions.assertTrue(SdtMetricsMBean.getMetrics().getBulkFeedbackStats().contains(expectedBulkFeedbackCount));
        Assertions.assertEquals(1, SdtMetricsMBean.getMetrics().getActiveBulkCustomers());
    }
}
