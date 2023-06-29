package uk.gov.moj.sdt.handlers;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

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
        wsCreateBulkRequestHandler = Mockito.spy(new WsCreateBulkRequestHandler(
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

        try (MockedStatic<VisitableTreeWalker> mockStaticVisitableTreeWalker = Mockito.mockStatic(VisitableTreeWalker.class)) {
            mockStaticVisitableTreeWalker.when(() -> VisitableTreeWalker.walk(mockBulkSubmission, "Validator"))
                .then((Answer<Void>) invocation -> null);

            result = wsCreateBulkRequestHandler.submitBulk(mockBulkRequestType);

            verifyStatic(VisitableTreeWalker.class);
            VisitableTreeWalker.walk(mockBulkSubmission, "Validator");

            verify(mockTransformer).transformJaxbToDomain(mockBulkRequestType);
            verify(mockBulkSubmissionValidator).checkIndividualRequests(mockBulkSubmission);
            verify(mockBulkSubmissionService).saveBulkSubmission(mockBulkSubmission);
            verify(mockTransformer).transformDomainToJaxb(mockBulkSubmission);
        }

        testMetrics();
        Assertions.assertEquals(mockBulkResponseType, result);
    }

    @Test
    public void testSubmitBulkThrowsAbstractBusinessException() throws Exception {
        CustomerNotFoundException exception = new CustomerNotFoundException("MOCK_CODE", "MOCK_DESCRIPTION");

        when(mockTransformer.transformJaxbToDomain(mockBulkRequestType))
            .thenThrow(exception);

        wsCreateBulkRequestHandler.submitBulk(mockBulkRequestType);

        verifyPrivate(wsCreateBulkRequestHandler)
            .invoke(
                "handleBusinessException",
                any(CustomerNotFoundException.class),
                any(BulkResponseType.class)
            );

        testMetrics();
    }

    private void testMetrics() {
        String expectedBulkSubmitCount = "count[1]";
        Assertions.assertTrue(SdtMetricsMBean.getMetrics().getBulkSubmitStats().contains(expectedBulkSubmitCount));
        Assertions.assertEquals(1, SdtMetricsMBean.getMetrics().getActiveBulkCustomers());
    }
}
