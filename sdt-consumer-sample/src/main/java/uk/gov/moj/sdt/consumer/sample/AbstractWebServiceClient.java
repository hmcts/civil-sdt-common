package uk.gov.moj.sdt.consumer.sample;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;

/**
 * Base class for commonality across web service clients.
 *
 * @author Saurabh
 */
public abstract class AbstractWebServiceClient {
    /**
     * Constant to define location of Spring context file.
     */
    public static final String APP_CONTEXT_LOCATION = "classpath*:/uk/gov/moj/sdt/consumer/sample/spring.context.xml";

    /**
     * SDT Endpoint to be called.
     */
    private ISdtEndpointPortType sdtEndpoint;

    /**
     * Setter for SDT endpoint.
     *
     * @param sdtEndpoint SDT endpoint.
     */
    public void setSdtEndpoint(ISdtEndpointPortType sdtEndpoint) {
        this.sdtEndpoint = sdtEndpoint;
    }

    /**
     * Return a client to call SDT's external endpoint. The client is customised with timeout values.
     *
     * @return client for SDT's external endpoint.
     */
    protected ISdtEndpointPortType getSdtEndpointClient() {
        return getSdtEndpointClient(5000, 100000);
    }

    /**
     * Return a client to call SDT's external endpoint. The client is customised with timeout values.
     *
     * @param connTimeout     connection timeout.
     * @param responseTimeout response timeout.
     * @return client for SDT's external endpoint.
     */
    protected ISdtEndpointPortType getSdtEndpointClient(final long connTimeout, final long responseTimeout) {

        Client clientProxy = ClientProxy.getClient(sdtEndpoint);

        HTTPConduit httpConduit = (HTTPConduit) clientProxy.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        // Specifies the amount of time, in milliseconds, that the client will attempt to establish a connection before
        // it times out
        httpClientPolicy.setConnectionTimeout(connTimeout);
        // Specifies the amount of time, in milliseconds, that the client will wait for a response before it times out.
        httpClientPolicy.setReceiveTimeout(responseTimeout);
        httpConduit.setClient(httpClientPolicy);

        return sdtEndpoint;

    }

}
