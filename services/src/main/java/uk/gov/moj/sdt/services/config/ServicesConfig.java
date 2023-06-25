package uk.gov.moj.sdt.services.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.moj.sdt.services.messaging.QueueConfig;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.services.utils.IndividualRequestsXmlParser;
import uk.gov.moj.sdt.utils.concurrent.api.IInFlightMessage;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ComponentScan("uk.gov.moj.sdt")
@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
@EnableScheduling
public class ServicesConfig {

    private static final String TARGET_APP_SUBMIT_QUERY_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryResponseSchema";
    public static final String SUBMIT_QUERY_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema";
    public static final String SDT_SUBMIT_QUERY_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema";
    public static final String TARGET_APP_SUBMIT_QUERY_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryRequestSchema";
    public static final String SDT_TARGET_APP_INDV_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvResponseSchema";
    public static final String SDT_BULK_FEEDBACK_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema";
    public static final String SDT_INDIVIDUAL_UPDATE_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/IndividualUpdateRequestSchema";
    public static final String SDT_BULK_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema";
    public static final String SDT_TARGET_APP_INDV_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema";

    @Value("${sdt.service.config.concurrentConsumers}")
    private int concurrentConsumers = 1;
    @Value("${sdt.service.config.maxConcurrentConsumers}")
    private int maxConcurrentConsumers = 5;
    @Value("${sdt.service.config.receiveTimeout}")
    private int receiveTimeout = 30000;
    @Value("${sdt.service.config.idleTaskExecutionLimit}")
    private int idleTaskExecutionLimit = 10;

    @Value("${sdt.service.config.idleConsumerLimit}")
    private int idleConsumerLimit = 5;

    @Bean
    @Lazy
    @Qualifier("messageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(ConnectionFactory jmsConnectionFactory,
                                                                    PlatformTransactionManager transactionManager,
                                                                    QueueConfig queueConfig,
                                                                    @Lazy MessageListenerAdapter messageListenerAdapter) {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(jmsConnectionFactory);
        defaultMessageListenerContainer.setDestinationName(queueConfig.getTargetAppQueue().get("MCOL"));
        defaultMessageListenerContainer.setMessageListener(messageListenerAdapter);
        defaultMessageListenerContainer.setTransactionManager(transactionManager);
        defaultMessageListenerContainer.setConcurrentConsumers(concurrentConsumers);
        defaultMessageListenerContainer.setMaxConcurrentConsumers(maxConcurrentConsumers);
        defaultMessageListenerContainer.setReceiveTimeout(receiveTimeout);
        defaultMessageListenerContainer.setIdleTaskExecutionLimit(idleTaskExecutionLimit);
        defaultMessageListenerContainer.setIdleConsumerLimit(idleConsumerLimit);
        return defaultMessageListenerContainer;
    }

    @Bean
    @Qualifier("SubmitQueryResponseXmlParser")
    public GenericXmlParser submitQueryResponseXmlParser() {
        GenericXmlParser genericXmlParser = new GenericXmlParser();
        genericXmlParser.setEnclosingTag("targetAppDetail");
        Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(
            TARGET_APP_SUBMIT_QUERY_RESPONSE_SCHEMA,
            SUBMIT_QUERY_RESPONSE_SCHEMA
        );
        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        return genericXmlParser;
    }

    @Bean
    @Qualifier("SubmitQueryRequestXmlParser")
    public GenericXmlParser submitQueryRequestXmlParser() {
        GenericXmlParser genericXmlParser = new GenericXmlParser();
        genericXmlParser.setEnclosingTag("criterion");
        Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(
            SDT_SUBMIT_QUERY_REQUEST_SCHEMA,
            TARGET_APP_SUBMIT_QUERY_REQUEST_SCHEMA
        );
        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        return genericXmlParser;
    }

    @Bean
    @Qualifier("IndividualResponseXmlParser")
    public GenericXmlParser individualResponseXmlParser() {
        GenericXmlParser genericXmlParser = new GenericXmlParser();
        genericXmlParser.setEnclosingTag("targetAppDetail");
        Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(
            SDT_TARGET_APP_INDV_RESPONSE_SCHEMA,
            SDT_BULK_FEEDBACK_RESPONSE_SCHEMA
        );
        replacementNamespaces.put(
            SDT_INDIVIDUAL_UPDATE_REQUEST_SCHEMA,
            SDT_BULK_FEEDBACK_RESPONSE_SCHEMA
        );
        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        return genericXmlParser;
    }

    @Bean
    @Qualifier("IndividualRequestsXmlParser")
    public IndividualRequestsXmlParser individualRequestsXmlParser() {
        IndividualRequestsXmlParser genericXmlParser = new IndividualRequestsXmlParser();
        Map<String, String> replacementNamespaces = new HashMap<>();
        replacementNamespaces.put(
            SDT_BULK_REQUEST_SCHEMA,
            SDT_TARGET_APP_INDV_REQUEST_SCHEMA
        );
        genericXmlParser.setReplacementNamespaces(replacementNamespaces);
        return genericXmlParser;
    }

    @Bean
    @Qualifier("concurrentMap")
    public Map<String, IInFlightMessage> concurrentMap() {
        return new ConcurrentHashMap<>();
    }
}
