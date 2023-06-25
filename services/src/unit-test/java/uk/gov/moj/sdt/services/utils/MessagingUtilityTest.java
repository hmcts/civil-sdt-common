package uk.gov.moj.sdt.services.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.services.messaging.SdtMessage;
import uk.gov.moj.sdt.services.messaging.api.IMessageWriter;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.transaction.synchronizer.api.IMessageSynchronizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessagingUtilityTest extends AbstractSdtUnitTestBase {

    @Mock
    IMessageSynchronizer messageSynchronizer;

    @Mock
    IMessageWriter messageWriter;

    @Test
    public void testEnqueueRequestNew() {
        // Given
        IIndividualRequest individualRequest = mock(IIndividualRequest.class);
        IBulkSubmission bulkSubmission = mock(IBulkSubmission.class);
        ITargetApplication targetApplication = mock(ITargetApplication.class);

        when(individualRequest.getBulkSubmission()).thenReturn(bulkSubmission);
        when(bulkSubmission.getTargetApplication()).thenReturn(targetApplication);
        when(targetApplication.getTargetApplicationCode()).thenReturn("targetAppCode");
        when(individualRequest.getSdtRequestReference()).thenReturn("sdtRequestReference");

        MessagingUtility messagingUtility = new MessagingUtility(messageWriter,messageSynchronizer);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        //When
        messagingUtility.enqueueRequest(individualRequest);

        // Then
        verify(messageSynchronizer).execute(runnableCaptor.capture());
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(messageWriter).queueMessage(any(SdtMessage.class), eq("targetAppCode"), eq(false));
    }

    @Test
    void testMessageSynchronizer() {

        MessagingUtility messagingUtility = new MessagingUtility(messageWriter,messageSynchronizer);

        IMessageSynchronizer mockMessageSynchronizer = mock(IMessageSynchronizer.class);

        messagingUtility.setMessageSynchronizer(mockMessageSynchronizer);

        assertEquals(mockMessageSynchronizer, messagingUtility.getMessageSynchronizer());
    }

    @Test
    void testMessageWriter() {

        MessagingUtility messagingUtility = new MessagingUtility(messageWriter,messageSynchronizer);

        IMessageWriter mockMessageWriter = mock(IMessageWriter.class);

        messagingUtility.setMessageWriter(mockMessageWriter);

        assertEquals(mockMessageWriter, messagingUtility.getMessageWriter());
    }

}
