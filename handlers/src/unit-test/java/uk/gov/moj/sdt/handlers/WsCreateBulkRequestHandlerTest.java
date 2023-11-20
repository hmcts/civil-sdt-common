package uk.gov.moj.sdt.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.services.api.IBulkSubmissionService;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.concurrent.InFlightMessage;
import uk.gov.moj.sdt.utils.concurrent.api.IInFlightMessage;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.api.IBulkSubmissionValidator;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WsCreateBulkRequestHandlerTest {

    @Mock
    IBulkSubmissionService mockBulkSubmissionService;

    @Mock
    IBulkSubmissionValidator mockBulkSubmissionValidator;

    @Mock
    ITransformer<BulkRequestType, BulkResponseType, IBulkSubmission, IBulkSubmission> mockTransformer;

    @Mock
    Map<String, IInFlightMessage> mockConcurrencyMap;

    @Mock
    BulkRequestType mockBulkRequestType;

    @Mock
    IBulkSubmission mockBulkSubmission;

    @Mock
    BulkResponseType mockBulkResponseType;

    @Mock
    HeaderType mockHeader;

    WsCreateBulkRequestHandler wsCreateBulkRequestHandler;

    @BeforeEach
    public void setUp() {
        wsCreateBulkRequestHandler = spy(new WsCreateBulkRequestHandler(
            mockBulkSubmissionService, mockBulkSubmissionValidator, mockTransformer, mockConcurrencyMap));
        wsCreateBulkRequestHandler.setBulkSubmissionService(mockBulkSubmissionService);
        wsCreateBulkRequestHandler.setBulkSubmissionValidator(mockBulkSubmissionValidator);
        wsCreateBulkRequestHandler.setTransformer(mockTransformer);

        SdtMetricsMBean.getMetrics().reset();

        when(mockBulkRequestType.getHeader()).thenReturn(mockHeader);
        when(mockHeader.getSdtCustomerId()).thenReturn(1L);
        when(mockHeader.getCustomerReference()).thenReturn("1");
    }

    @Test
    public void testSubmitBulk() {
        BulkResponseType result;

        when(mockTransformer.transformJaxbToDomain(mockBulkRequestType)).thenReturn(mockBulkSubmission);
        when(mockTransformer.transformDomainToJaxb(mockBulkSubmission)).thenReturn(mockBulkResponseType);

        InFlightMessage inFlightMessage = new InFlightMessage();
        inFlightMessage.setCompetingThreads(new HashMap<>());
        when(mockConcurrencyMap.get(anyString())).thenReturn(inFlightMessage);

        try (MockedStatic<VisitableTreeWalker> mockStaticVisitableTreeWalker = mockStatic(VisitableTreeWalker.class)) {
            mockStaticVisitableTreeWalker.when(() -> VisitableTreeWalker.walk(mockBulkSubmission, "Validator"))
                .then((Answer<Void>) invocation -> null);

            result = wsCreateBulkRequestHandler.submitBulk(mockBulkRequestType);

            mockStaticVisitableTreeWalker.verify(() -> VisitableTreeWalker.walk(mockBulkSubmission, "Validator"));

            verify(mockTransformer).transformJaxbToDomain(mockBulkRequestType);
            verify(mockBulkSubmissionValidator).checkIndividualRequests(mockBulkSubmission);
            verify(mockBulkSubmissionService).saveBulkSubmission(mockBulkSubmission);
            verify(mockTransformer).transformDomainToJaxb(mockBulkSubmission);
        }

        testMetrics();
        assertEquals(mockBulkResponseType, result);
    }

    @Test
    public void testSubmitBulkThrowsAbstractBusinessException() {
        CustomerNotFoundException exception = new CustomerNotFoundException("MOCK_CODE", "MOCK_DESCRIPTION");

        when(mockTransformer.transformJaxbToDomain(mockBulkRequestType))
            .thenThrow(exception);

        BulkResponseType bulkResponseType = wsCreateBulkRequestHandler.submitBulk(mockBulkRequestType);

        StatusType statusType = bulkResponseType.getStatus();
        assertEquals(StatusCodeType.ERROR, statusType.getCode(), "Bulk Response has unexpected status code");
        ErrorType errorType = statusType.getError();
        assertEquals("MOCK_CODE", errorType.getCode(), "Status error has unexpected code");
        assertEquals("MOCK_DESCRIPTION", errorType.getDescription(), "Status error has unexpected description");

        testMetrics();
    }

    private void testMetrics() {
        String expectedBulkSubmitCount = "count[1]";
        assertTrue(SdtMetricsMBean.getMetrics().getBulkSubmitStats().contains(expectedBulkSubmitCount));
        assertEquals(1, SdtMetricsMBean.getMetrics().getActiveBulkCustomers());
    }
}
