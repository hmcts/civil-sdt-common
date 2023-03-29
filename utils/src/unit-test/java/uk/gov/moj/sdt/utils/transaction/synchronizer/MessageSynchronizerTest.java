package uk.gov.moj.sdt.utils.transaction.synchronizer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

import java.util.ArrayList;
import java.util.List;

import static ch.qos.logback.classic.Level.DEBUG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_COMMITTED;
import static org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK;

@ExtendWith(MockitoExtension.class)
class MessageSynchronizerTest extends AbstractSdtUnitTestBase {

    private static final String RUNNABLES_NULL_OR_EMPTY = "The 'runnables' collection should neither be null nor empty";

    @Mock
    private Runnable mockCommand;

    @Mock
    private SdtContext mockSdtContext;

    /** Class being tested. */
    private MessageSynchronizer messageSynchronizer;

    @Override
    public void setUpLocalTests() {
        messageSynchronizer = new MessageSynchronizer();
    }

    @Test
    void testInactiveTransaction() {
        try (
            MockedStatic<TransactionSynchronizationManager> mockStaticTransactionSynchronizationManager
                = Mockito.mockStatic(TransactionSynchronizationManager.class)
        ) {
            mockStaticTransactionSynchronizationManager.when(TransactionSynchronizationManager::isSynchronizationActive).thenReturn(false);

            messageSynchronizer.synchronizeTask(mockCommand);

            verify(mockCommand).run();
        }
    }

    @Test
    void testActiveTransaction() {
        try (
            // Set up static mocked objects for use when static methods are called
            MockedStatic<TransactionSynchronizationManager> mockStaticTransactionSynchronizationManager
                = Mockito.mockStatic(TransactionSynchronizationManager.class);
            MockedStatic<SdtContext> mockStaticSdtContext
                = Mockito.mockStatic(SdtContext.class)
        ) {
            mockStaticTransactionSynchronizationManager.when(TransactionSynchronizationManager::isSynchronizationActive).thenReturn(true);
            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(mockSdtContext);
            when(mockSdtContext.addSynchronisationTask(mockCommand)).thenReturn(true);

            // Call synchronizeTask() so both it and the execute() method can be covered in one go
            messageSynchronizer.synchronizeTask(mockCommand);

            mockStaticTransactionSynchronizationManager.verify(TransactionSynchronizationManager::isSynchronizationActive);
            mockStaticSdtContext.verify(SdtContext::getContext);
            verify(mockSdtContext).addSynchronisationTask(mockCommand);
            mockStaticTransactionSynchronizationManager.verify(() -> TransactionSynchronizationManager.registerSynchronization(any()));
        }
    }

    @Test
    void testActiveTransactionNotInitialised() {
        try (
            MockedStatic<TransactionSynchronizationManager> mockStaticTransactionSynchronizationManager
                = Mockito.mockStatic(TransactionSynchronizationManager.class);
            MockedStatic<SdtContext> mockStaticSdtContext
                = Mockito.mockStatic(SdtContext.class)
        ) {
            mockStaticTransactionSynchronizationManager.when(TransactionSynchronizationManager::isSynchronizationActive).thenReturn(true);
            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(mockSdtContext);
            when(mockSdtContext.addSynchronisationTask(mockCommand)).thenReturn(false);

            messageSynchronizer.synchronizeTask(mockCommand);

            mockStaticTransactionSynchronizationManager.verify(TransactionSynchronizationManager::isSynchronizationActive);
            mockStaticSdtContext.verify(SdtContext::getContext);
            verify(mockSdtContext).addSynchronisationTask(mockCommand);
            mockStaticTransactionSynchronizationManager.verify(() -> TransactionSynchronizationManager.registerSynchronization(any()), never());
        }
    }

    @Test
    void testAfterCommit() {
        // This test also covers the constructor and run method for the private ExecuteRunnable inner class

        try (
            MockedStatic<SdtContext> mockStaticSdtContext
                = Mockito.mockStatic(SdtContext.class)
        ) {
            List<Runnable> runnableList = new ArrayList<>();
            runnableList.add(mockCommand);

            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(mockSdtContext);
            when(mockSdtContext.getSynchronisationTasks()).thenReturn(runnableList);

            messageSynchronizer.afterCommit();

            mockStaticSdtContext.verify(SdtContext::getContext);
            verify(mockSdtContext).getSynchronisationTasks();
        }
    }

    @Test
    void testAfterCommitEmptyList() {
        // This test also covers the constructor for the private ExecuteRunnable inner class.  Unable to cover null
        // runnable list path of constructor as use of UnmodifiableList prevents it being called with a null list.

        try (
            MockedStatic<SdtContext> mockStaticSdtContext
                = Mockito.mockStatic(SdtContext.class)
        ) {
            List<Runnable> emptyRunnableList = new ArrayList<>();

            mockStaticSdtContext.when(SdtContext::getContext).thenReturn(mockSdtContext);
            when(mockSdtContext.getSynchronisationTasks()).thenReturn(emptyRunnableList);

            try{
                messageSynchronizer.afterCommit();
                fail("Expected an IllegalArgumentException to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals(RUNNABLES_NULL_OR_EMPTY, e.getMessage(), "IllegalArgumentException has unexpected message");
            }

            mockStaticSdtContext.verify(SdtContext::getContext);
            verify(mockSdtContext).getSynchronisationTasks();
        }
    }

    private void assertAfterCompletion(int transactionStatus, String expectedMessage) {
        // Get logger for class being tested
        Logger logger = (Logger) LoggerFactory.getLogger(MessageSynchronizer.class);

        // Set logging level to debug so that afterCompletion logging is captured
        logger.setLevel(DEBUG);

        // Create appender and add to logger
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        messageSynchronizer.afterCompletion(transactionStatus);

        // Check first (and only) entry in log output
        List<ILoggingEvent> logList = listAppender.list;
        assertEquals(expectedMessage, logList.get(0).getFormattedMessage());

        // Tidy up
        logger.detachAndStopAllAppenders();
    }

    @Test
    void testAfterCompletionCommitted() {
        assertAfterCompletion(STATUS_COMMITTED, "Transaction completed with status Committed");
    }

    @Test
    void testAfterCompletionRollback() {
        assertAfterCompletion(STATUS_ROLLED_BACK, "Transaction completed with status Rollback");
    }
}

