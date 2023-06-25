package uk.gov.moj.sdt.consumers.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.moj.sdt.consumers.IndividualRequestConsumer;
import uk.gov.moj.sdt.consumers.api.IIndividualRequestConsumer;
import uk.gov.moj.sdt.interceptors.in.XmlInboundInterceptor;
import uk.gov.moj.sdt.interceptors.out.CacheEndOutboundInterceptor;
import uk.gov.moj.sdt.interceptors.out.CacheSetupOutboundInterceptor;
import uk.gov.moj.sdt.interceptors.out.XmlOutboundInterceptor;
import uk.gov.moj.sdt.transformers.IndividualRequestConsumerTransformer;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

@ComponentScan("uk.gov.moj.sdt")
@EnableAutoConfiguration
@EnableTransactionManagement
@Configuration
public class ConsumersTestConfig {


    private XmlOutboundInterceptor xmlOutboundInterceptor = new XmlOutboundInterceptor();

    private CacheSetupOutboundInterceptor cacheSetupOutboundInterceptor = new CacheSetupOutboundInterceptor();

    private CacheEndOutboundInterceptor cacheEndOutboundInterceptor = new CacheEndOutboundInterceptor();

    private XmlInboundInterceptor xmlInboundInterceptor = new XmlInboundInterceptor();

    private IndividualRequestConsumerTransformer transformer = new IndividualRequestConsumerTransformer();

    @Bean
    @Scope("prototype")
    public ITargetAppInternalEndpointPortType createTargetAppInternalEndpointPortType() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress("http://dummhost/overridden/dynamically");
        jaxWsProxyFactoryBean.setBindingId("http://www.w3.org/2003/05/soap/bindings/HTTP/");
        List<Interceptor<? extends Message>> outInterceptors = new ArrayList<>();
        outInterceptors.add(cacheSetupOutboundInterceptor);
        outInterceptors.add(xmlOutboundInterceptor);
        outInterceptors.add(cacheEndOutboundInterceptor);
        jaxWsProxyFactoryBean.setOutInterceptors(outInterceptors);

        List<Interceptor<? extends Message>> inInterceptors = new ArrayList<>();
        inInterceptors.add(xmlInboundInterceptor);
        jaxWsProxyFactoryBean.setInInterceptors(inInterceptors);

        List<LoggingFeature> features = new ArrayList<>();
        features.add(new LoggingFeature());
        jaxWsProxyFactoryBean.setFeatures(features);

        Map<String, Object> properties = new HashMap<>();
        properties.put("disable.outputstream.optimization", true);
        Map<String, String>  soapEnvNsMap = new HashMap<>();
        soapEnvNsMap.put("tsqreq", "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryRequestSchema");
        soapEnvNsMap.put("tsqres", "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryResponseSchema");
        soapEnvNsMap.put("base", "http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema");
        soapEnvNsMap.put("tireq", "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema");
        soapEnvNsMap.put("tires", "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvResponseSchema");
        properties.put("soap.env.ns.map", soapEnvNsMap);

        jaxWsProxyFactoryBean.setProperties(properties);

        return jaxWsProxyFactoryBean.create(ITargetAppInternalEndpointPortType.class);
    }

    @Bean
    @Qualifier("IndividualRequestConsumer")
    @Primary
    public IIndividualRequestConsumer individualRequestConsumer() {
        IndividualRequestConsumer individualRequestConsumer = new IndividualRequestConsumer(transformer);
        individualRequestConsumer.setRethrowOnFailureToConnect(true);
        return individualRequestConsumer;
    }
}
