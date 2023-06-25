package uk.gov.moj.sdt.consumers.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ConsumersConfigTest {

    ConsumersConfig consumersConfig = new ConsumersConfig();

    @Test
    void testCreateTargetAppInternalEndpointPortType() {
        ITargetAppInternalEndpointPortType result = consumersConfig.createTargetAppInternalEndpointPortType();

        assertNotNull(result);
    }

}
