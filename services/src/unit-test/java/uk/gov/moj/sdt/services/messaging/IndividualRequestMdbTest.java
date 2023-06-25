package uk.gov.moj.sdt.services.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.services.TargetApplicationSubmissionService;
import uk.gov.moj.sdt.services.api.ITargetApplicationSubmissionService;
import uk.gov.moj.sdt.services.messaging.api.ISdtMessage;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test class for Message reader implementation.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
public class IndividualRequestMdbTest extends AbstractSdtUnitTestBase {
    /**
     * Mock service for the message processing.
     */
    @Mock
    private TargetApplicationSubmissionService mockTargetSubmissionService;

    /**
     * Individual Request Mdb Test instance.
     */
    private IndividualRequestMdb individualRequestMdb;

    private static final String TEST_SUCCESSFULLY_COMPLETED = "Test successfully completed";

    /**
     * Pre-testing initialization.
     */
    @BeforeEach
    @Override
    public void setUp() {
        individualRequestMdb = new IndividualRequestMdb(mockTargetSubmissionService);
    }

    /**
     * Method to test successful scenario for readMessage.
     *
     * @throws JMSException during the test setup
     */
    @Test
    public void readMessageSuccessTest() throws JMSException {

        // Create the actual message to send.
        final ISdtMessage sdtMessage = new SdtMessage();
        sdtMessage.setSdtRequestReference("ReadTest1");

        // Wrap the message in the ObjectMessage
        final ObjectMessage objMessage = mock(ObjectMessage.class);
        when(objMessage.getObject()).thenReturn((Serializable) sdtMessage);

        individualRequestMdb.readMessage(objMessage);

        verify(mockTargetSubmissionService).processRequestToSubmit(sdtMessage.getSdtRequestReference(), null);
        verify(objMessage).getObject();

        assertTrue(true,TEST_SUCCESSFULLY_COMPLETED);
    }

    /**
     * Test method to test the scenario where the service throws a Data Access Exception.
     *
     * @throws JMSException if any during the test setup
     */
    @Test
    public void readMessageDataAccessExceptionTest() throws JMSException {

        // Create the actual message to send
        final ISdtMessage sdtMessage = new SdtMessage();
        sdtMessage.setSdtRequestReference("ReadTest1");

        // Wrap the message in the ObjectMessage
        final ObjectMessage objMessage = mock(ObjectMessage.class);
        when(objMessage.getObject()).thenReturn((Serializable) sdtMessage);

        doThrow(new RuntimeException("Data error occurred"))
            .when(mockTargetSubmissionService).processRequestToSubmit(sdtMessage.getSdtRequestReference(), null);

        try {
            individualRequestMdb.readMessage(objMessage);
            fail("RuntimeException was expected to be thrown");
        } catch (final RuntimeException re) {
            assertTrue(true,"Expected to throw the exception");
        }

        verify(mockTargetSubmissionService).processRequestToSubmit(sdtMessage.getSdtRequestReference(), null);
        verify(objMessage).getObject();

        assertTrue(true,TEST_SUCCESSFULLY_COMPLETED);
    }

    @Test
    public void readMessageJMSExceptionTest() throws JMSException {

        // Wrap the message in the ObjectMessage
        final ObjectMessage objMessageMock = mock(ObjectMessage.class);

        doThrow(new JMSException("JMS error occurred")).when(objMessageMock).getObject();

        try {
            individualRequestMdb.readMessage(objMessageMock);
            fail("Exception should be thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getCause().getMessage(),"JMS error occurred");
        }
        verify(objMessageMock).getObject();
    }

    /**
     * Test method to test the scenario where the message is some other object other than
     * the ObjectMessage.
     *
     * @throws JMSException if any during the test setup
     */
    @Test
    public void readMessageInvalidObject() throws JMSException {

        final Message message = mock(Message.class);

        individualRequestMdb.readMessage(message);

        assertTrue(true,TEST_SUCCESSFULLY_COMPLETED);
    }

    @Test
    void setMockTargetSubmissionServiceTest(){

        ITargetApplicationSubmissionService targetApplicationSubmissionServiceMock = mock(ITargetApplicationSubmissionService.class);
        IndividualRequestMdb individualRequestMdbObj = new IndividualRequestMdb(targetApplicationSubmissionServiceMock);
        individualRequestMdbObj.setTargetAppSubmissionService(mockTargetSubmissionService);
        Object result = this.getAccessibleField(IndividualRequestMdb.class, "targetAppSubmissionService",
                                                ITargetApplicationSubmissionService.class, individualRequestMdbObj);

        assertEquals(mockTargetSubmissionService, result, "TargetSubmissionService should be set to an object");

    }
}
