package uk.gov.moj.sdt.consumers.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomListenerTest {

    CustomListener customListener;

    @Mock
    ServletContextEvent mockServletContextEvent;

    @Mock
    Logger mockLogger;

    @Test
    void testContextMethods() {
        try (MockedStatic<LoggerFactory> mockLoggerFactory = mockStatic(LoggerFactory.class)) {
            mockLoggerFactory.when(() -> LoggerFactory.getLogger(any(Class.class)))
                    .thenReturn(mockLogger);

            customListener = new CustomListener();
            customListener.contextInitialized(mockServletContextEvent);
            customListener.contextDestroyed(mockServletContextEvent);

            verify(mockLogger).info("CustomListener is initialized");
            verify(mockLogger).info("CustomListener is destroyed");
        }

    }
}
