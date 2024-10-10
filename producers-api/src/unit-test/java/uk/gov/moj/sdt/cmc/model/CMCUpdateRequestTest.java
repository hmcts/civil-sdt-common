package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CMCUpdateRequestTest extends AbstractSdtUnitTestBase {

    private ObjectMapper objectMapper;

    @Override
    protected void setUpLocalTests() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testConvertToObjectAllFields() throws JsonProcessingException {
        String updateJson = """
                {
                  "errorCode" : "1",
                  "errorText" : "Test Error",
                  "claimNumber" : "12345678",
                  "issueDate" : "2023-01-01",
                  "serviceDate" : "2024-02-02",
                  "judgmentEnteredDate" : "2024-03-03",
                  "firstPaymentDate" : "2024-04-04",
                  "warrantNumber" : "W87654321",
                  "enforcingCourtCode" : "123",
                  "enforcingCourtName" : "Test Court",
                  "fee" : 100,
                  "judgmentWarrantStatus" : "Judgment accepted by CCBC.",
                  "requestStatus" : "Accepted"
                }""";

        CMCUpdateRequest cmcUpdateRequest = objectMapper.readValue(updateJson, CMCUpdateRequest.class);

        assertNotNull(cmcUpdateRequest, "CMCUpdateRequest should not be null");
        assertEquals("1",
                     String.valueOf(cmcUpdateRequest.getErrorCode()),
                     "CMCUpdateRequest has unexpected error code");
        assertEquals("Test Error", cmcUpdateRequest.getErrorText(), "CMCUpdateRequest has unexpected error text");
        assertEquals("12345678", cmcUpdateRequest.getClaimNumber(), "CMCUpdateRequest has unexpected claim number");
        assertEquals(createDate(2023, 1, 1),
                     cmcUpdateRequest.getIssueDate(),
                     "CMCUpdateRequest has unexpected issue date");
        assertEquals(createDate(2024, 2, 2),
                     cmcUpdateRequest.getServiceDate(),
                     "CMCUpdateRequest has unexpected service date");
        assertEquals(createDate(2024, 3, 3),
                     cmcUpdateRequest.getJudgmentEnteredDate(),
                     "CMCUpdateRequest has unexpected judgment entered date");
        assertEquals(createDate(2024, 4, 4),
                     cmcUpdateRequest.getFirstPaymentDate(),
                     "CMCUpdateRequest has unexpected first payment date");
        assertEquals("W87654321",
                     cmcUpdateRequest.getWarrantNumber(),
                     "CMCUpdateRequest has unexpected warrant number");
        assertEquals("123",
                     cmcUpdateRequest.getEnforcingCourtCode(),
                     "CMCUpdateRequest has unexpected enforcing court code");
        assertEquals("Test Court",
                     cmcUpdateRequest.getEnforcingCourtName(),
                     "CMCUpdateRequest has unexpected enforcing court name");
        assertEquals(100L, cmcUpdateRequest.getFee(), "CMCUpdateRequest has unexpected fee");
        assertEquals("Judgment accepted by CCBC.",
                     cmcUpdateRequest.getJudgmentWarrantStatus().getMessage(),
                     "CMCUpdateRequest has unexpected judgment warrant status");
        assertEquals("Accepted",
                     cmcUpdateRequest.getRequestStatus().getStatus(),
                     "CMCUpdateRequest has unexpected request status");
    }

    @Test
    void testConvertToObjectMandatoryFieldsOnly() throws JsonProcessingException {
        String updateJson = """
                {
                  "requestStatus" : "Accepted"
                }""";

        CMCUpdateRequest cmcUpdateRequest = objectMapper.readValue(updateJson, CMCUpdateRequest.class);

        assertNotNull(cmcUpdateRequest, "CMCUpdateRequest should not be null");
        assertEquals("Accepted",
                cmcUpdateRequest.getRequestStatus().getStatus(),
                "CMCUpdateRequest has unexpected request status");
        assertNull(cmcUpdateRequest.getErrorCode(), "CMCUpdateRequest error code should be null");
        assertNull(cmcUpdateRequest.getErrorText(), "CMCUpdateRequest error text should be null");
        assertNull(cmcUpdateRequest.getClaimNumber(), "CMCUpdateRequest claim number should be null");
        assertNull(cmcUpdateRequest.getIssueDate(), "CMCUpdateRequest issue date should be null");
        assertNull(cmcUpdateRequest.getServiceDate(), "CMCUpdateRequest service date should be null");
        assertNull(cmcUpdateRequest.getJudgmentEnteredDate(), "CMCUpdateRequest judgment entered date should be null");
        assertNull(cmcUpdateRequest.getFirstPaymentDate(), "CMCUpdateRequest first payment date should be null");
        assertNull(cmcUpdateRequest.getWarrantNumber(), "CMCUpdateRequest warrant number should be null");
        assertNull(cmcUpdateRequest.getEnforcingCourtCode(), "CMCUpdateRequest enforcing court code should be null");
        assertNull(cmcUpdateRequest.getEnforcingCourtName(), "CMCUpdateRequest enforcing court name should be null");
        assertNull(cmcUpdateRequest.getFee(), "CMCUpdateRequest fee should be null");
        assertNull(cmcUpdateRequest.getJudgmentWarrantStatus(),
                   "CMCUpdateRequest judgment warrant status should be null");
    }

    private Date createDate(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        return Date.from(date.atStartOfDay().atZone(ZoneId.of("Europe/London")).toInstant());
    }
}
