package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.request.ClaimStatusUpdateRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimStatusUpdateResponse;
import uk.gov.moj.sdt.cmc.consumers.response.ProcessingStatus;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MockClaimStatusUpdateServiceTest extends AbstractSdtUnitTestBase {

    String IDAM_ID_HEADER = "IDAMID";

    String SDT_REQUEST_ID = "SDTREQUESTID";

    private MockClaimStatusUpdateService mockClaimStatusUpdateService;

    @BeforeEach
    @Override
    public void setUp() {
        mockClaimStatusUpdateService = new MockClaimStatusUpdateService();
    }

    @Test
    void getClient() {
        ClaimStatusUpdateRequest claimStatusUpdateRequest = mock(ClaimStatusUpdateRequest.class);
        ClaimStatusUpdateResponse response = mockClaimStatusUpdateService.claimStatusUpdate(IDAM_ID_HEADER,
                                                                                            SDT_REQUEST_ID ,
                                                                                            claimStatusUpdateRequest);
        assertNotNull(response);
        assertEquals(ProcessingStatus.QUEUED, response.getProcessingStatus());
    }
}
