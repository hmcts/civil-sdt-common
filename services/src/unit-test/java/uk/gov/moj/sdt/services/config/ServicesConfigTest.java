package uk.gov.moj.sdt.services.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import uk.gov.moj.sdt.services.messaging.QueueConfig;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.services.utils.IndividualRequestsXmlParser;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicesConfigTest extends AbstractSdtUnitTestBase {

    private static final String FIELD_REPLACEMENT_NAMESPACES = "replacementNamespaces";

    @Mock
    private ConnectionFactory mockConnectionFactory;

    @Mock
    private PlatformTransactionManager mockPlatformTransactionManager;

    @Mock
    private QueueConfig mockQueueConfig;

    @Test
    void testMessageListenerContainer() {

        ServicesConfig servicesConfig = new ServicesConfig();
        MessageListenerAdapter mockMessageListenerAdaptor = mock(MessageListenerAdapter.class);
        Map<String, String> targetAppQueue = new HashMap<>();
        targetAppQueue.put("MCOL", "MCOL");

        when(mockQueueConfig.getTargetAppQueue()).thenReturn(targetAppQueue);

        DefaultMessageListenerContainer defaultMessageListenerContainer = servicesConfig.messageListenerContainer(
            mockConnectionFactory, mockPlatformTransactionManager, mockQueueConfig, mockMessageListenerAdaptor);

        assertNotNull(defaultMessageListenerContainer);
        assertEquals(0, defaultMessageListenerContainer.getActiveConsumerCount());
        assertEquals(4, defaultMessageListenerContainer.getCacheLevel());
        assertEquals(5, defaultMessageListenerContainer.getMaxConcurrentConsumers());
        assertEquals(5, defaultMessageListenerContainer.getIdleConsumerLimit());
        assertEquals(4, defaultMessageListenerContainer.getCacheLevel());
        assertNull(defaultMessageListenerContainer.getSubscriptionName());
        assertEquals("MCOL", defaultMessageListenerContainer.getDestinationName());
        assertEquals(mockConnectionFactory, defaultMessageListenerContainer.getConnectionFactory());

        verify(mockQueueConfig).getTargetAppQueue();
    }

    @Test
    void testSubmitQueryResponseXmlParser() {

        ServicesConfig servicesConfig = new ServicesConfig();
        GenericXmlParser genericXmlParser = servicesConfig.submitQueryResponseXmlParser();

        assertNotNull(genericXmlParser);
        assertEquals("targetAppDetail", genericXmlParser.getEnclosingTag());

        Map<String, String> replacementNamespaces =
            (Map<String, String>) getAccessibleField(GenericXmlParser.class,
                                                     FIELD_REPLACEMENT_NAMESPACES,
                                                     Map.class,
                                                     genericXmlParser);

        assertNotNull(replacementNamespaces);
        assertEquals(1, replacementNamespaces.size());
        assertEquals(ServicesConfig.SUBMIT_QUERY_RESPONSE_SCHEMA,
                     replacementNamespaces.get("http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryResponseSchema"));
    }

    @Test
    void testSubmitQueryRequestXmlParser() {

        ServicesConfig servicesConfig = new ServicesConfig();
        GenericXmlParser genericXmlParser = servicesConfig.submitQueryRequestXmlParser();

        assertNotNull(genericXmlParser);
        assertEquals("criterion", genericXmlParser.getEnclosingTag());

        Map<String, String> replacementNamespaces =
            (Map<String, String>) getAccessibleField(GenericXmlParser.class,
                                                     FIELD_REPLACEMENT_NAMESPACES,
                                                     Map.class,
                                                     genericXmlParser);

        assertNotNull(replacementNamespaces);
        assertEquals(1, replacementNamespaces.size());
        assertEquals(ServicesConfig.TARGET_APP_SUBMIT_QUERY_REQUEST_SCHEMA,
                     replacementNamespaces.get(ServicesConfig.SDT_SUBMIT_QUERY_REQUEST_SCHEMA));
    }

    @Test
    void testIndividualResponseXmlParser() {

        ServicesConfig servicesConfig = new ServicesConfig();
        GenericXmlParser genericXmlParser = servicesConfig.individualResponseXmlParser();

        assertNotNull(genericXmlParser);
        assertEquals("targetAppDetail", genericXmlParser.getEnclosingTag());

        Map<String, String> replacementNamespaces =
            (Map<String, String>) getAccessibleField(GenericXmlParser.class,
                                                     FIELD_REPLACEMENT_NAMESPACES,
                                                     Map.class,
                                                     genericXmlParser);

        assertNotNull(replacementNamespaces);
        assertEquals(2, replacementNamespaces.size());
        assertEquals(ServicesConfig.SDT_BULK_FEEDBACK_RESPONSE_SCHEMA,
                     replacementNamespaces.get(ServicesConfig.SDT_TARGET_APP_INDV_RESPONSE_SCHEMA));
        assertEquals(ServicesConfig.SDT_BULK_FEEDBACK_RESPONSE_SCHEMA,
                     replacementNamespaces.get(ServicesConfig.SDT_INDIVIDUAL_UPDATE_REQUEST_SCHEMA));
    }

    @Test
    void testIndividualRequestsXmlParser() {

        ServicesConfig servicesConfig = new ServicesConfig();
        IndividualRequestsXmlParser genericXmlParser = servicesConfig.individualRequestsXmlParser();

        assertNotNull(genericXmlParser);

        Map<String, String> replacementNamespaces =
            (Map<String, String>) getAccessibleField(IndividualRequestsXmlParser.class,
                                                     FIELD_REPLACEMENT_NAMESPACES,
                                                     Map.class,
                                                     genericXmlParser);

        assertNotNull(replacementNamespaces);
        assertEquals(1, replacementNamespaces.size());
        assertEquals(ServicesConfig.SDT_TARGET_APP_INDV_REQUEST_SCHEMA,
                     replacementNamespaces.get(ServicesConfig.SDT_BULK_REQUEST_SCHEMA));
    }
}
