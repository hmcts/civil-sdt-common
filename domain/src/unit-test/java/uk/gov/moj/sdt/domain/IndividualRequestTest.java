package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link IndividualRequest}.
 *
 * @author Saurabh Agarwal
 */
@DisplayName("Writing Individual Request Tests")
class IndividualRequestTest extends AbstractSdtUnitTestBase {
    /**
     * Test subject.
     */
    private IIndividualRequest individualRequest;
    private static final String UPDATED_DATE_SHOULD_BE_POPULATED = "Updated date should be populated";
    private static final String FORWARDING_ATTEMPT_COUNT_MESSAGE = "Forwarding attempt count is incorrect";
    private static final String STATUS_IS_INCORRECT_MESSAGE = "Status is incorrect";
    private static final String REQUEST_TYPE_MESSAGE = "Request Type";

    private static final LocalDateTime createdDate = LocalDateTime.now();

    /**
     * Set up test data.
     */
    @BeforeEach
    @Override
    public void setUp() {
        individualRequest = new IndividualRequest();
    }

    @Test
    void testIndividualRequestSetters() {
        individualRequest.setId(1L);
        individualRequest.setDeadLetter(true);
        individualRequest.setCreatedDate(createdDate);
        individualRequest.setRequestType(REQUEST_TYPE_MESSAGE);
        individualRequest.setRequestPayload("Request Payload".getBytes());
        individualRequest.setSdtRequestReference("1");
        individualRequest.setTargetApplicationResponse("Target Application Response".getBytes());
        ErrorLog errorLog = new ErrorLog("ERR01", "Any old error message");
        individualRequest.setErrorLog(errorLog);

        assertTrue(individualRequest.isDeadLetter());
        assertEquals(1L, individualRequest.getId());
        assertEquals(createdDate, individualRequest.getCreatedDate());
        assertEquals(REQUEST_TYPE_MESSAGE, individualRequest.getRequestType());
        assertEquals("Request Payload", new String(individualRequest.getRequestPayload(), StandardCharsets.UTF_8));
        assertEquals("1", individualRequest.getSdtRequestReference());
        assertEquals("Target Application Response",
                new String(individualRequest.getTargetApplicationResponse(), StandardCharsets.UTF_8));
        assertEquals(errorLog.getErrorText(),
                individualRequest.getErrorLog().getErrorText());
    }

    /**
     * Tests that forwarding attempts is incremented correctly.
     */
    @Test
    @DisplayName("Test Increment Forwarding Attempts")
    void testIncrementForwardingAttempts() {
        individualRequest.incrementForwardingAttempts();

        assertEquals(IndividualRequestStatus.FORWARDED.getStatus(),
                     individualRequest.getRequestStatus(),
                     STATUS_IS_INCORRECT_MESSAGE
        );
        assertEquals(1, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        assertNotNull(individualRequest.getUpdatedDate(), UPDATED_DATE_SHOULD_BE_POPULATED);
    }

    /**
     * Tests that request is marked as accepted correctly.
     */
    @Test
    @DisplayName("Test Mark Request As Accepted")
    void testMarkRequestAsAccepted() {
        individualRequest.markRequestAsAccepted();

        assertEquals(IndividualRequestStatus.ACCEPTED.getStatus(),
                     individualRequest.getRequestStatus(),
                     STATUS_IS_INCORRECT_MESSAGE
        );
        assertEquals(0, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        assertNotNull(individualRequest.getUpdatedDate(), "Updated date should not be null");
        assertNotNull(individualRequest.getCompletedDate(), "Completed date should be populated");
    }

    /**
     * Tests that request is marked as initially accepted correctly.
     */
    @Test
    @DisplayName("Test Mark Request As Initially Accepted")
    void testMarkRequestAsInitiallyAccepted() {
        individualRequest.markRequestAsInitiallyAccepted();

        assertEquals(IndividualRequestStatus.INITIALLY_ACCEPTED.getStatus(),
                     individualRequest.getRequestStatus(),
                     STATUS_IS_INCORRECT_MESSAGE
        );
        assertEquals(0, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        assertNotNull(individualRequest.getUpdatedDate(), "Updated date shouldn't be populated");
    }

    /**
     * Tests that request is marked as awaiting data correctly.
     */
    @Test
    @DisplayName("Test Mark Request As Awaiting Data")
    void testMarkRequestAsAwaitingData() {
        individualRequest.markRequestAsAwaitingData();

        assertEquals(IndividualRequestStatus.AWAITING_DATA.getStatus(),
                     individualRequest.getRequestStatus(),
                     STATUS_IS_INCORRECT_MESSAGE
        );
        assertEquals(0, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        assertNotNull(individualRequest.getUpdatedDate(), "Updated date should be populated and not null");
    }

    /**
     * Tests that request is marked as rejected correctly.
     */
    @Test
    @DisplayName("Test Mark Request As Rejected")
    void testMarkRequestAsRejected() {
        final IErrorLog errorLog = new ErrorLog();
        individualRequest.markRequestAsRejected(errorLog);

        assertEquals(IndividualRequestStatus.REJECTED.getStatus(),
                     individualRequest.getRequestStatus(),
                     STATUS_IS_INCORRECT_MESSAGE
        );
        assertEquals(0, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        assertNotNull(individualRequest.getUpdatedDate(), "Request Updated date should be populated");
        assertNotNull(individualRequest.getCompletedDate(), "Completed date should be populated");
        assertEquals(errorLog, individualRequest.getErrorLog(), "Error log should be populated");
        assertEquals(individualRequest,
                     errorLog.getIndividualRequest(),
                     "Individual request should be associated with error log"
        );
    }

    @Test
    @DisplayName("Test Mark Request As Rejected with a null error log")
    void testMarkRequestAsRejectedErrorLog() {
        individualRequest.markRequestAsRejected(null);
        assertNull(individualRequest.getErrorLog(), "Error log should be null");
    }

    @Test
    @DisplayName("Test Individual Request Reference")
    void testIndividualRequestReference() {

        individualRequest.setSdtBulkReference("REF0202");
        assertEquals("REF0202", individualRequest.getSdtBulkReference(), "reference should be set");
        assertNull(individualRequest.getErrorLog(), "Error log should be null");
    }

    /**
     * Tests that forward attempt is reset correctly.
     */
    @Test
    @DisplayName("Test Reset Forwarding Attempts")
    void testResetForwardingAttempts() {
        individualRequest.setForwardingAttempts(5);
        assertEquals(5, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        individualRequest.resetForwardingAttempts();
        assertEquals(0, individualRequest.getForwardingAttempts(), FORWARDING_ATTEMPT_COUNT_MESSAGE);
        assertEquals(IndividualRequestStatus.RECEIVED.getStatus(),
                     individualRequest.getRequestStatus(),
                     STATUS_IS_INCORRECT_MESSAGE
        );
        assertNotNull(individualRequest.getUpdatedDate(), "IndividualRequest Updated date should be populated");
    }

    /**
     * Tests that check for enqueuing request correct.
     */
    @Test
    @DisplayName("Test Is Enqueue-able")
    void testIsEnqueueable() {
        individualRequest.setRequestStatus(IndividualRequestStatus.RECEIVED.getStatus());
        assertTrue(individualRequest.isEnqueueable(), "Request should be enqueue-able");

        individualRequest.setRequestStatus(IndividualRequestStatus.FORWARDED.getStatus());
        assertFalse(individualRequest.isEnqueueable(), "Request should not be enqueue-able");
    }

    /**
     * Tests that request reference is populated correctly.
     */
    @Test
    @DisplayName("Test Populate Sdt Request Reference")
    void testPopulateSdtRequestReference() {
        final IBulkSubmission bulkSubmission = new BulkSubmission();
        bulkSubmission.setSdtBulkReference("BULK-REF");

        individualRequest.setBulkSubmission(bulkSubmission);
        individualRequest.setLineNumber(1);
        individualRequest.populateReferences();

        assertEquals("BULK-REF-0000001", individualRequest.getSdtRequestReference(), "Request reference is incorrect");
        assertEquals("BULK-REF", individualRequest.getSdtBulkReference(), "Request reference is incorrect");
    }


    @Test
    @DisplayName("Test Request toString")
    void testIndividualRequestToString() {
        individualRequest.setRequestType(REQUEST_TYPE_MESSAGE);
        assertTrue(individualRequest.toString().contains(REQUEST_TYPE_MESSAGE), "Object to string should be populated");
    }

    @Test
    @DisplayName("Test Request Internal System Error")
    void testIndividualRequestSystemError(){
        individualRequest.setInternalSystemError("Internal System Error");
        assertEquals("Internal System Error", individualRequest.getInternalSystemError(),"Internal System error should be populated");
    }

}
