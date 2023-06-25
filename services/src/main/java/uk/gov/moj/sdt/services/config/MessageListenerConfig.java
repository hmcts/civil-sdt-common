package uk.gov.moj.sdt.services.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import uk.gov.moj.sdt.services.messaging.api.IMessageDrivenBean;

@Configuration
public class MessageListenerConfig {

    @Bean
    @Scope("prototype")
    public MessageListenerAdapter messageListenerAdapter(@Qualifier("IndividualRequestMdb")
                                                         @Lazy IMessageDrivenBean individualRequestMdb) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(individualRequestMdb);
        messageListenerAdapter.setMessageConverter(null);
        messageListenerAdapter.setDefaultListenerMethod("readMessage");
        return messageListenerAdapter;
    }
}
