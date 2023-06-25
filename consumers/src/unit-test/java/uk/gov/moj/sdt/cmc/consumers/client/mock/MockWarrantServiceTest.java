package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.request.WarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ProcessingStatus;
import uk.gov.moj.sdt.cmc.consumers.response.WarrantResponse;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MockWarrantServiceTest extends AbstractSdtUnitTestBase {

    String AUTHORISATION = "";

    String SERVICE_AUTHORISATION = "";

    String IDAM_ID_HEADER = "IDAMID";

    String SDT_REQUEST_ID = "SDTREQUESTID";

    private MockWarrantService mockWarrantService;

    @BeforeEach
    @Override
    public void setUp() {
        mockWarrantService = new MockWarrantService();
    }

    @Test
    void getClient() {
        WarrantRequest request = mock(WarrantRequest.class);
        WarrantResponse response = mockWarrantService.warrantRequest(AUTHORISATION,
                                                                     SERVICE_AUTHORISATION,
                                                                     IDAM_ID_HEADER,
                                                                     SDT_REQUEST_ID , request);
        assertNotNull(response);
        assertEquals(45678L, response.getFee());
        assertEquals("123456", response.getWarrantNumber());
    }
}
