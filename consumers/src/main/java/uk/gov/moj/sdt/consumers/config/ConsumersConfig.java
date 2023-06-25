package uk.gov.moj.sdt.consumers.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.moj.sdt.interceptors.in.SdtUnmarshallInterceptor;
import uk.gov.moj.sdt.interceptors.in.XmlInboundInterceptor;
import uk.gov.moj.sdt.interceptors.out.CacheEndOutboundInterceptor;
import uk.gov.moj.sdt.interceptors.out.CacheSetupOutboundInterceptor;
import uk.gov.moj.sdt.interceptors.out.XmlOutboundInterceptor;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

@ComponentScan("uk.gov.moj.sdt")
@Configuration
@EnableTransactionManagement
public class ConsumersConfig {

    public static final String SDT_TARGET_APP_SUBMIT_QUERY_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryRequestSchema";
    public static final String SDT_TARGET_APP_SUBMIT_QUERY_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/SubmitQueryResponseSchema";
    public static final String SDT_BASE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema";
    public static final String SDT_TARGET_APP_INDV_REQUEST_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvRequestSchema";
    public static final String SDT_TARGET_APP_INDV_RESPONSE_SCHEMA = "http://ws.sdt.moj.gov.uk/2013/sdt/targetApp/IndvResponseSchema";
    public static final String OVERRIDDEN_DYNAMICALLY = "http://dummhost/overridden/dynamically";
    public static final String SOAP_BINDINGS_HTTP = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

    @Bean
    @Scope("prototype")
    public ITargetAppInternalEndpointPortType createTargetAppInternalEndpointPortType() {


        XmlOutboundInterceptor xmlOutboundInterceptor = new XmlOutboundInterceptor();
        CacheSetupOutboundInterceptor cacheSetupOutboundInterceptor = new CacheSetupOutboundInterceptor();
        CacheEndOutboundInterceptor cacheEndOutboundInterceptor = new CacheEndOutboundInterceptor();
        XmlInboundInterceptor xmlInboundInterceptor = new XmlInboundInterceptor();
        SdtUnmarshallInterceptor sdtUnmarshallInterceptor = new SdtUnmarshallInterceptor();

        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(OVERRIDDEN_DYNAMICALLY);
        jaxWsProxyFactoryBean.setBindingId(SOAP_BINDINGS_HTTP);
        List<Interceptor<? extends Message>> outInterceptors = new ArrayList<>();
        outInterceptors.add(cacheSetupOutboundInterceptor);
        outInterceptors.add(xmlOutboundInterceptor);
        outInterceptors.add(cacheEndOutboundInterceptor);
        jaxWsProxyFactoryBean.setOutInterceptors(outInterceptors);

        List<Interceptor<? extends Message>> inInterceptors = new ArrayList<>();
        inInterceptors.add(xmlInboundInterceptor);
        inInterceptors.add(sdtUnmarshallInterceptor);
        jaxWsProxyFactoryBean.setInInterceptors(inInterceptors);

        List<LoggingFeature> features = new ArrayList<>();
        features.add(new LoggingFeature());
        jaxWsProxyFactoryBean.setFeatures(features);

        Map<String, Object> properties = new HashMap<>();
        properties.put("disable.outputstream.optimization", true);
        Map<String, String>  soapEnvNsMap = new HashMap<>();
        soapEnvNsMap.put("tsqreq", SDT_TARGET_APP_SUBMIT_QUERY_REQUEST_SCHEMA);
        soapEnvNsMap.put("tsqres", SDT_TARGET_APP_SUBMIT_QUERY_RESPONSE_SCHEMA);
        soapEnvNsMap.put("base", SDT_BASE_SCHEMA);
        soapEnvNsMap.put("tireq", SDT_TARGET_APP_INDV_REQUEST_SCHEMA);
        soapEnvNsMap.put("tires", SDT_TARGET_APP_INDV_RESPONSE_SCHEMA);
        properties.put("soap.env.ns.map", soapEnvNsMap);

        jaxWsProxyFactoryBean.setProperties(properties);

        return jaxWsProxyFactoryBean.create(ITargetAppInternalEndpointPortType.class);
    }
}
