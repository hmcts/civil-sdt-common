package uk.gov.moj.sdt.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.services.api.ISubmitQueryService;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WsReadSubmitQueryHandlerTest {

    @Mock
    ISubmitQueryService mockSubmitQueryService;

    @Mock
    ITransformer<SubmitQueryRequestType, SubmitQueryResponseType, ISubmitQueryRequest, ISubmitQueryRequest> mockTransformer;

    @Mock
    SubmitQueryRequestType mockSubmitQueryRequestType;

    @Mock
    SubmitQueryResponseType mockSubmitQueryResponseType;

    @Mock
    ISubmitQueryRequest mockSubmitQueryRequest;

    @Mock
    HeaderType mockHeaderType;

    WsReadSubmitQueryHandler wsReadSubmitQueryHandler;

    @BeforeEach
    public void setUp() {
        wsReadSubmitQueryHandler = spy(new WsReadSubmitQueryHandler(
            mockSubmitQueryService, mockTransformer));
        wsReadSubmitQueryHandler.setSubmitQueryService(mockSubmitQueryService);
        wsReadSubmitQueryHandler.setTransformer(mockTransformer);

        SdtMetricsMBean.getMetrics().reset();

        when(mockSubmitQueryRequestType.getHeader()).thenReturn(mockHeaderType);
        when(mockHeaderType.getSdtCustomerId()).thenReturn(1L);
        when(mockHeaderType.getQueryReference()).thenReturn("1");
    }

    @Test
    public void testSubmitQuery() {

        when(mockTransformer.transformJaxbToDomain(mockSubmitQueryRequestType))
            .thenReturn(mockSubmitQueryRequest);
        when(mockTransformer.transformDomainToJaxb(mockSubmitQueryRequest))
            .thenReturn(mockSubmitQueryResponseType);

        SubmitQueryResponseType result;

        try (MockedStatic<VisitableTreeWalker> mockStaticVisitableTreeWalker = mockStatic(VisitableTreeWalker.class)) {
            mockStaticVisitableTreeWalker.when(() -> VisitableTreeWalker.walk(mockSubmitQueryRequest, "Validator"))
                .then((Answer<Void>) invocation -> null);

            result = wsReadSubmitQueryHandler.submitQuery(mockSubmitQueryRequestType);

            mockStaticVisitableTreeWalker.verify(() -> VisitableTreeWalker.walk(mockSubmitQueryRequest, "Validator"));

            verify(mockSubmitQueryService).submitQuery(mockSubmitQueryRequest);
        }

        String expectedSubmitQueryCount = "count[1]";
        assertTrue(SdtMetricsMBean.getMetrics().getSubmitQueryStats().contains(expectedSubmitQueryCount));
        assertNotNull(result);
        assertEquals(mockSubmitQueryResponseType, result);
    }

    @Test
    public void testSubmitQueryThrowsAbstractBusinessException() {

        CustomerNotFoundException exception = new CustomerNotFoundException("MOCK_CODE", "MOCK_DESCRIPTION");

        when(mockTransformer.transformJaxbToDomain(mockSubmitQueryRequestType))
            .thenThrow(exception);

        SubmitQueryResponseType submitQueryResponseType =
                wsReadSubmitQueryHandler.submitQuery(mockSubmitQueryRequestType);

        StatusType statusType = submitQueryResponseType.getStatus();
        assertEquals(StatusCodeType.ERROR, statusType.getCode(), "SubmitQuery response has unexpected status code");
        ErrorType errorType = statusType.getError();
        assertEquals("MOCK_CODE", errorType.getCode(), "Status error has unexpected code");
        assertEquals("MOCK_DESCRIPTION", errorType.getDescription(), "Status error has unexpected description");

        String expectedSubmitQueryCount = "count[1]";
        assertTrue(SdtMetricsMBean.getMetrics().getSubmitQueryStats().contains(expectedSubmitQueryCount));
    }
}
