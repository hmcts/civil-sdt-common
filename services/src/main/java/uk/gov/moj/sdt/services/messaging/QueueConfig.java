package uk.gov.moj.sdt.services.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "spring.jms.servicebus.internal")
public class QueueConfig {

    private Map<String, String> targetAppQueue = new HashMap<>();

    public Map<String, String> getTargetAppQueue() {
        return targetAppQueue;
    }

    public void setTargetAppQueue(Map<String, String> targetAppQueue) {
         this.targetAppQueue = targetAppQueue;
    }
}
