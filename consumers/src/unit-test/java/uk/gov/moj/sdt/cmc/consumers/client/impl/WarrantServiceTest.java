package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.request.WarrantRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WarrantServiceTest extends AbstractSdtUnitTestBase {

    String AUTHORISATION = "";

    String SERVICE_AUTHORISATION = "";

    String IDAM_ID_HEADER = "IDAMID";

    String SDT_REQUEST_ID = "SDTREQUESTID";

    @Mock
    private CMCApi cmcApi;

    private WarrantService warrantService;

    @BeforeEach
    @Override
    public void setUp() {
        warrantService = new WarrantService(cmcApi);
    }

    @Test
    void warrentRequest() {
        WarrantRequest request = mock(WarrantRequest.class);
        warrantService.warrantRequest(AUTHORISATION, SERVICE_AUTHORISATION, IDAM_ID_HEADER, SDT_REQUEST_ID, request);
        verify(cmcApi).warrantRequest(AUTHORISATION, SERVICE_AUTHORISATION, IDAM_ID_HEADER, SDT_REQUEST_ID, request);
    }

}
