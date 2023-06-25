package uk.gov.moj.sdt.handlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.services.api.IUpdateRequestService;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.UpdateRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.UpdateResponseType;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(MockitoExtension.class)
class WsUpdateItemHandlerTest {

    @Mock
    IUpdateRequestService mockUpdateRequestService;

    @Mock
    ITransformer<UpdateRequestType, UpdateResponseType, IIndividualRequest, IIndividualRequest> mockTransformer;

    @Mock
    IIndividualRequest mockIndividualRequest;

    @Mock
    UpdateRequestType mockUpdateRequestType;

    @Mock
    UpdateResponseType mockUpdateResponseType;

    @Mock
    HeaderType mockHeader;

    WsUpdateItemHandler wsUpdateItemHandler;

    @BeforeEach
    public void setUp() {
        wsUpdateItemHandler = new WsUpdateItemHandler(mockUpdateRequestService, mockTransformer);
        wsUpdateItemHandler.setUpdateRequestService(mockUpdateRequestService);
        wsUpdateItemHandler.setTransformer(mockTransformer);

        SdtMetricsMBean.getMetrics().reset();

        when(mockUpdateRequestType.getHeader()).thenReturn(mockHeader);
        when(mockHeader.getSdtRequestId()).thenReturn("1");

        when(mockTransformer.transformJaxbToDomain(mockUpdateRequestType))
            .thenReturn(mockIndividualRequest);
        when(mockTransformer.transformDomainToJaxb(mockIndividualRequest))
            .thenReturn(mockUpdateResponseType);
    }

    @Test
    public void testUpdateItem() {
        UpdateResponseType result = wsUpdateItemHandler.updateItem(mockUpdateRequestType);

        verify(mockTransformer).transformJaxbToDomain(mockUpdateRequestType);
        verify(mockUpdateRequestService).updateIndividualRequest(mockIndividualRequest);
        verify(mockTransformer).transformDomainToJaxb(mockIndividualRequest);

        String expected = "count[1]";

        Assertions.assertTrue(SdtMetricsMBean.getMetrics().getStatusUpdateStats().contains(expected));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockUpdateResponseType, result);
    }

}
