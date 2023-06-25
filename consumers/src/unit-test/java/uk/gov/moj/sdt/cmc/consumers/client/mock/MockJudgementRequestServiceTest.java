package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.cmc.consumers.request.judgement.JudgementRequest;
import uk.gov.moj.sdt.cmc.consumers.response.judgement.JudgementResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class MockJudgementRequestServiceTest {

    private static final String IDAM_ID = "Test_IDAM_ID";

    private static final String SDT_REQUEST_ID = "MCOL-0000000001";

    private MockJudgementRequestService mockJudgmentRequestService;

    @BeforeEach
    public void setUp() {
        mockJudgmentRequestService = new MockJudgementRequestService();
    }

    @Test
    void shouldReturnJudgementResponse() {
        JudgementRequest judgementRequest = mock(JudgementRequest.class);
        JudgementResponse judgementResponse = mockJudgmentRequestService.requestJudgment(IDAM_ID, SDT_REQUEST_ID, judgementRequest);
        assertNotNull(judgementResponse);
        assertNotNull(judgementResponse.getFirstPaymentDate());
        assertNotNull(judgementResponse.getJudgmentEnteredDate());
    }
}
