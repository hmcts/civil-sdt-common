package uk.gov.moj.sdt.services.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.moj.sdt.services.messaging.api.ISdtMessage;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Test class for testing the MessageWriter implementation.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
class MessageWriterTest extends AbstractSdtUnitTestBase {

    private static final String UNIT_TEST_QUEUE = "UnitTestQueue";

    private static final String UNIT_TEST = "UNITTEST";

    private static final String DLQ_SUFFIX = "/$deadletterqueue";

    private MessageWriter messageWriter;

    private static final String SUCCESS = "Success";

    private static final String NOT_EXPECTED_TO_FAIL = "Not Expected to fail";

    @Mock
    JmsTemplate mockJmsTemplate;

    /**
     * Set up the variables.
     */
    @BeforeEach
    @Override
    public void setUp() {
        QueueConfig queueConfig = new QueueConfig();
        Map<String, String> mockedMap = new HashMap<>();
        queueConfig.setTargetAppQueue(mockedMap);
        messageWriter = new MessageWriter(mockJmsTemplate, queueConfig);
    }

    /**
     * Test method to test the sending of message.
     */
    @Test
    void testQueueMessageWithEmptyTargetApp() {
        // Setup finished, now tell the mock what to expect.
        final ISdtMessage sdtMessage = new SdtMessage();
        sdtMessage.setSdtRequestReference("Test");

        // Send the message.
        try {
            messageWriter.queueMessage(sdtMessage, null, false);
            fail("Should have thrown an Illegal Argument Exception as the target app code is null.");
        } catch (final IllegalArgumentException e) {
            assertTrue(true, "Illegal Argument specified for the target application");
        }
        verify(mockJmsTemplate, never()).convertAndSend(anyString(), any(ISdtMessage.class));
    }

    /**
     * Test message with an invalid queue name i.e. where the target application
     * to queue name map is not valid
     */
    @Test
    void testQueueMessageWithInvalidTarget() {
        // Setup finished, now tell the mock what to expect.
        final ISdtMessage sdtMessage = new SdtMessage();
        sdtMessage.setSdtRequestReference("Test");

        // Send the message.
        try {
            messageWriter.queueMessage(sdtMessage, "UnitTest", false);
            fail("Should have thrown an Illegal Argument exception here as the target app code is invalid");
        } catch (final IllegalArgumentException e) {
            assertTrue(true, "Target application code does not have a mapped queue name");
        }
        verify(mockJmsTemplate, never()).convertAndSend(anyString(), any(ISdtMessage.class));
    }

    /**
     * Test for a valid queue message on the MessageWriter.
     */
    @Test
    void testQueueMessage() {
        // Setup finished, now tell the mock what to expect.
        final ISdtMessage sdtMessage = new SdtMessage();
        sdtMessage.setSdtRequestReference("Test");

        // Send the message.
        try {
            final Map<String, String> queueNameMap = new HashMap<>();
            queueNameMap.put(UNIT_TEST, UNIT_TEST_QUEUE);

            messageWriter.setQueueNameMap(queueNameMap);

            messageWriter.queueMessage(sdtMessage, UNIT_TEST, false);
            assertNotEquals(0L, sdtMessage.getMessageSentTimestamp());
            verify(mockJmsTemplate).convertAndSend(UNIT_TEST_QUEUE, sdtMessage);
        } catch (final IllegalArgumentException e) {
            fail(NOT_EXPECTED_TO_FAIL);
        }

    }

    /**
     * Test for a valid queue message on the MessageWriter for dead letter.
     */
    @Test
    void testQueueMessageForDeadLetter() {
            // Setup finished, now tell the mock what to expect.
            final ISdtMessage sdtMessage = new SdtMessage();
            sdtMessage.setSdtRequestReference("Test");

            // Send the message.
            try {
                final Map<String, String> queueNameMap = new HashMap<>();
                queueNameMap.put(UNIT_TEST, UNIT_TEST_QUEUE);

                messageWriter.setQueueNameMap(queueNameMap);

                messageWriter.queueMessage(sdtMessage, UNIT_TEST, true);
                assertNotEquals(0L, sdtMessage.getMessageSentTimestamp());
                verify(mockJmsTemplate).convertAndSend(UNIT_TEST_QUEUE + DLQ_SUFFIX, sdtMessage);
            } catch (final IllegalArgumentException e) {
                fail(NOT_EXPECTED_TO_FAIL);
            }
    }
}
