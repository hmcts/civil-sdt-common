package uk.gov.moj.sdt.cmc.consumers.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BreathingSpaceResponseTest {


    @Test
    void testEnumSerialisation() throws JsonProcessingException {
        BreathingSpaceResponse response = new BreathingSpaceResponse();
        response.setProcessingStatus(ProcessingStatus.PROCESSED);
        String json = new ObjectMapper().writeValueAsString(response);
        assertNotNull(json);
    }

    @Test
    void testEnumDeserialization() throws JsonProcessingException {
        String json = "{\"processingStatus\":\"QUEUED\"}";
        BreathingSpaceResponse response = new ObjectMapper().readValue(json, BreathingSpaceResponse.class);
        assertNotNull(response);
        assertEquals(ProcessingStatus.QUEUED, response.getProcessingStatus());
    }
}
