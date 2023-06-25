package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.request.ClaimStatusUpdateRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClaimStatusUpdateServiceTest extends AbstractSdtUnitTestBase {

    String IDAM_ID_HEADER = "IDAMID";

    String SDT_REQUEST_ID = "SDTREQUESTID";

    @Mock
    private CMCApi cmcApi;

    private ClaimStatusUpdateService claimStatusUpdateService;

    @BeforeEach
    @Override
    public void setUp() {
        claimStatusUpdateService = new ClaimStatusUpdateService(cmcApi);
    }

    @Test
    void getClient() {
        ClaimStatusUpdateRequest claimStatusUpdateRequest = mock(ClaimStatusUpdateRequest.class);
        claimStatusUpdateService.claimStatusUpdate(IDAM_ID_HEADER, SDT_REQUEST_ID, claimStatusUpdateRequest);
        verify(cmcApi).claimStatusUpdate(IDAM_ID_HEADER, SDT_REQUEST_ID, claimStatusUpdateRequest);
    }

}
