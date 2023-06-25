package uk.gov.moj.sdt.producers.config;

import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import uk.gov.moj.sdt.producers.sdtws.config.ProducersConfig;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;
import uk.gov.moj.sdt.ws._2013.sdt.sdtinternalendpoint.ISdtInternalEndpointPortType;

@ComponentScan("uk.gov.moj.sdt")
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableCaching(proxyTargetClass = true)
@Import(ProducersConfig.class)
public class EndToEndTestConfig {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EndToEndTestConfig.class);

    @Value("${server.portnumber}")
    private int serverPortNumber = 8080;

    @Value("${server.hostname}")
    private String serverHostName = "localhost";

    private static final String DISABLE_OUTPUTSTREAM_OPTIMIZATION = "disable.outputstream.optimization";
    private static final String SOAP_ENV_NS_MAP = "soap.env.ns.map";

    @Bean("ISdtEndpointPortType")
    @Scope("prototype")
    public ISdtEndpointPortType createSdtEndpointPortType() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        String address =  "http://"+ serverHostName + ":" + serverPortNumber + "/producers/service/sdtapi";
        jaxWsProxyFactoryBean.setAddress(address);
        jaxWsProxyFactoryBean.setBindingId(SOAPBinding.SOAP11HTTP_BINDING);
        Map<String, Object> properties = new HashMap<>();
        properties.put(DISABLE_OUTPUTSTREAM_OPTIMIZATION, true);
        Map<String, String> soapEnvNsMap = new HashMap<>();
        properties.put(SOAP_ENV_NS_MAP, soapEnvNsMap);
        jaxWsProxyFactoryBean.setProperties(properties);
        LOGGER.info("createSdtEndpointPortType ... done");
        return jaxWsProxyFactoryBean.create(ISdtEndpointPortType.class);
    }

    @Bean("ISdtInternalEndpointPortType")
    @Scope("prototype")
    public ISdtInternalEndpointPortType createSdtInternalEndpointPortType() {
        JaxWsProxyFactoryBean jaxWsInternalProxyFactoryBean = new JaxWsProxyFactoryBean();
        String address =  "http://"+ serverHostName + ":" + serverPortNumber + "/producers/service/sdtinternalapi";
        jaxWsInternalProxyFactoryBean.setAddress(address);
        jaxWsInternalProxyFactoryBean.setBindingId(SOAPBinding.SOAP11HTTP_BINDING);
        Map<String, Object> properties = new HashMap<>();
        properties.put(DISABLE_OUTPUTSTREAM_OPTIMIZATION, true);

        jaxWsInternalProxyFactoryBean.setProperties(properties);
        LOGGER.info("createSdtInternalEndpointPortType ... done");
        return jaxWsInternalProxyFactoryBean.create(ISdtInternalEndpointPortType.class);
    }
}
