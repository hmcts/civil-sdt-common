package uk.gov.moj.sdt.services.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.moj.sdt.services.messaging.MessageWriter;
import uk.gov.moj.sdt.services.messaging.QueueConfig;

import javax.jms.ConnectionFactory;

@Configuration
@ComponentScan("uk.gov.moj.sdt")
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableTransactionManagement
public class ServicesTestConfig {

    @Autowired
    private ConnectionFactory jmsConnectionFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Lazy
    private MessageListenerAdapter messageListenerAdapter;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private QueueConfig queueConfig;

    @Bean
    @Qualifier("messageListenerContainerMCol")
    public DefaultMessageListenerContainer messageListenerContainerMCol() {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(jmsConnectionFactory);
        defaultMessageListenerContainer.setDestinationName(queueConfig.getTargetAppQueue().get("MCOLS"));
        defaultMessageListenerContainer.setMessageListener(messageListenerAdapter);
        defaultMessageListenerContainer.setTransactionManager(transactionManager);
        defaultMessageListenerContainer.setConcurrentConsumers(1);
        defaultMessageListenerContainer.setMaxConcurrentConsumers(5);
        defaultMessageListenerContainer.setReceiveTimeout(30000);
        defaultMessageListenerContainer.setIdleTaskExecutionLimit(10);
        defaultMessageListenerContainer.setIdleConsumerLimit(5);
        return defaultMessageListenerContainer;
    }

    @Bean
    @Lazy
    @Qualifier("IMessageWriterBad")
    public MessageWriter IMessageWriterBad() {
        MessageWriter messageWriter = new MessageWriter(jmsTemplate,
                                                        queueConfig);
        return messageWriter;
    }
}
