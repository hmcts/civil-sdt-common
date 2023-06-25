package uk.gov.moj.sdt.cmc.consumers.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClaimStatusUpdateResponseTest {

    @Test
    void testEnumSerialisation() throws JsonProcessingException {
        ClaimStatusUpdateResponse response = new ClaimStatusUpdateResponse();
        response.setProcessingStatus(ProcessingStatus.PROCESSED);
        String json = new ObjectMapper().writeValueAsString(response);
        assertNotNull(json);
        assertTrue(json.contains("PROCESSED"));
    }

    @Test
    void testEnumDeserialization() throws JsonProcessingException {
        String json = "{\"processingStatus\":\"QUEUED\"}";
        ClaimStatusUpdateResponse response = new ObjectMapper().readValue(json, ClaimStatusUpdateResponse.class);
        assertNotNull(response);
        assertEquals(ProcessingStatus.QUEUED, response.getProcessingStatus());
    }
}
