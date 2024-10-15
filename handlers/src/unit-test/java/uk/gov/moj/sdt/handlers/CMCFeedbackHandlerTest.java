package uk.gov.moj.sdt.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequestStatus;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.services.api.ICMCFeedbackService;
import uk.gov.moj.sdt.transformers.api.ICMCFeedbackTransformer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CMCFeedbackHandlerTest {

    private static final String SDT_REQUEST_ID = "MCOL-20241008120000-000000001-0000001";

    @Mock
    private ICMCFeedbackTransformer mockCmcFeedbackTransformer;

    @Mock
    private ICMCFeedbackService mockCmcFeedbackService;

    private CMCFeedbackHandler cmcFeedbackHandler;

    @BeforeEach
    public void setUp() {
        cmcFeedbackHandler = new CMCFeedbackHandler(mockCmcFeedbackTransformer, mockCmcFeedbackService);
    }

    @Test
    void testCmcFeedback() {
        CMCUpdateRequest cmcUpdateRequest = createCmcUpdateRequest();
        IIndividualRequest individualRequest = createIndividualRequest();

        when(mockCmcFeedbackTransformer.transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest))
                .thenReturn(individualRequest);

        cmcFeedbackHandler.cmcFeedback(SDT_REQUEST_ID, cmcUpdateRequest);

        verify(mockCmcFeedbackTransformer).transformJsonToDomain(SDT_REQUEST_ID, cmcUpdateRequest);
        verify(mockCmcFeedbackService).cmcFeedback(individualRequest);
    }

    private CMCUpdateRequest createCmcUpdateRequest() {
        CMCUpdateRequest cmcUpdateRequest = new CMCUpdateRequest();

        cmcUpdateRequest.setRequestStatus(CMCUpdateRequestStatus.ACCEPTED);

        return cmcUpdateRequest;
    }

    private IIndividualRequest createIndividualRequest() {
        IIndividualRequest individualRequest = new IndividualRequest();

        individualRequest.setRequestStatus(IIndividualRequest.IndividualRequestStatus.ACCEPTED.getStatus());

        return individualRequest;
    }
}
