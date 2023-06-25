package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.request.BreathingSpaceRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BreathingSpaceServiceTest extends AbstractSdtUnitTestBase {

    String IDAM_ID_HEADER = "IDAMID";

    String SDT_REQUEST_ID = "SDTREQUESTID";

    @Mock
    private CMCApi cmcApi;

    private BreathingSpaceService breathingSpaceService;

    @BeforeEach
    @Override
    public void setUp() {
        breathingSpaceService = new BreathingSpaceService(cmcApi);
    }

    @Test
    void getClient() {
        BreathingSpaceRequest breathingSpaceRequest = mock(BreathingSpaceRequest.class);
        breathingSpaceService.breathingSpace(IDAM_ID_HEADER, SDT_REQUEST_ID, breathingSpaceRequest);
        verify(cmcApi).breathingSpace(IDAM_ID_HEADER, SDT_REQUEST_ID, breathingSpaceRequest);
    }

}
