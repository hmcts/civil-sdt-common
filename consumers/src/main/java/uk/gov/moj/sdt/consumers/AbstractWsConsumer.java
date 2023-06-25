/* Copyrights and Licenses
 *
 * Copyright (c) 2013 by the Ministry of Justice. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * - All advertising materials mentioning features or use of this software must display the
 * following acknowledgment: "This product includes Money Claims OnLine."
 * - Products derived from this software may not be called "Money Claims OnLine" nor may
 * "Money Claims OnLine" appear in their names without prior written permission of the
 * Ministry of Justice.
 * - Redistributions of any form whatsoever must retain the following acknowledgment: "This
 * product includes Money Claims OnLine."
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */
package uk.gov.moj.sdt.consumers;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Lookup;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.SoapFaultException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

/**
 * @author Manoj Kulkarni
 */
public abstract class AbstractWsConsumer {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWsConsumer.class);

    /**
     * The map to hold the TargetApp Internal End Point with the key
     * as string concatenation of the target application and service type.
     */
    private Map<String, ITargetAppInternalEndpointPortType> clientCache =
            new ConcurrentHashMap<>();

    /**
     * Get the client for the specified target application. If the client is not cached already, a new client
     * connection is created otherwise the already cached client is returned.
     *
     * @param targetApplicationCode the target application code
     * @param serviceType           the service type associated with the target application code
     * @param webServiceEndPoint    the Web Service End Point URL
     * @param connectionTimeOut     the connection time out value
     * @param receiveTimeOut        the acknowledgement time out value
     * @return the target application end point port bean i.e. the client interface.
     */
    public ITargetAppInternalEndpointPortType getClient(final String targetApplicationCode, final String serviceType,
                                                        final String webServiceEndPoint, final long connectionTimeOut,
                                                        final long receiveTimeOut) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get client for target [{}] service [{}] endpoint [{}] conn timeout [{}] recv timeout [{}]",
                    targetApplicationCode, serviceType, webServiceEndPoint, connectionTimeOut, receiveTimeOut);
        }

        final String clientCacheKey = targetApplicationCode + serviceType;
        ITargetAppInternalEndpointPortType client = null;

        if (this.clientCache.containsKey(clientCacheKey)) {
            client = this.clientCache.get(clientCacheKey);
        } else {
            client = this.createClient();
            this.clientCache.put(clientCacheKey, client);
        }

        final Client clientProxy = ClientProxy.getClient(client);

        // Set endpoint address
        final BindingProvider provider = (BindingProvider) client;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webServiceEndPoint);
        provider.getRequestContext().put(org.apache.cxf.message.Message.ENDPOINT_ADDRESS, webServiceEndPoint);

        final HTTPConduit httpConduit = (HTTPConduit) clientProxy.getConduit();
        final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        // Specifies the amount of time, in milliseconds, that the client will attempt to establish a connection before
        // it times out
        httpClientPolicy.setConnectionTimeout(connectionTimeOut);
        // Specifies the amount of time, in milliseconds, that the client will wait for a response before it times out.
        httpClientPolicy.setReceiveTimeout(receiveTimeOut);
        httpConduit.setClient(httpClientPolicy);

        return client;
    }

    /**
     * Calls the create target app end point to create the client.
     *
     * @return the target application end point port bean i.e. the client interface.
     */
    private ITargetAppInternalEndpointPortType createClient() {
        // Get the SOAP proxy client.
        return this.createTargetAppEndPoint();
    }

    /**
     * Method injection method that will be invoked by Spring container
     * to give a new instance of the bean every time.
     *
     * @return ITargetAppInternalEndpointPortType the end point interface.
     */
    @Lookup
    public ITargetAppInternalEndpointPortType createTargetAppEndPoint() {
        // IMPORTANT: Please do not add any implementation here. The method intentionally returns null. At run-time,
        // Spring will
        // override this method with
        // implementation code that will return an new instance of the end point bean when this method is called.

        return null;
    }

    /**
     * This method does the exception handling process after a client submits the
     * soap request.
     *
     * @param rethrowOnFailureToConnect - true if the WebServiceException should be re-thrown on a connect failure i.e.
     *                                  when the server is down.
     * @param wsException               - the WebServiceException caught by the client submit call.
     * @param errorReferenceContext     - optional parameter indicating the unique reference to identify the request.
     * @throws WebServiceException - if passed in exception cannot be handled.
     * @throws OutageException     - if there is a connection failure, the web service exception may be re-thrown.
     * @throws TimeoutException    - if there is a read timeout issue, throw the time out exception.
     * @throws SoapFaultException  - if the soap request resulted in an soap fault.
     */
    protected void handleClientErrors(final boolean rethrowOnFailureToConnect, final WebServiceException wsException,
                                      final String errorReferenceContext)
            throws WebServiceException, OutageException, TimeoutException, SoapFaultException {
        // If the target application is unavailable but caller does not wish to keep trying then re-throw exception.
        if ((wsException.getCause() instanceof ConnectException) && rethrowOnFailureToConnect) {
            // Target application must be unavailable - update stats.
            SdtMetricsMBean.getMetrics().upTargetAppUnavailable();

            throw new OutageException("OUTAGE_ERROR", wsException.getMessage());
        }
        // If the target application is unavailable continue trying to send message indefinitely.
        else if (wsException.getCause() instanceof ConnectException)
        {
            // Target application must be unavailable - update stats.
            SdtMetricsMBean.getMetrics().upTargetAppUnavailable();

            // Swallow exception - we want to carry on trying to connect.
        }
        // Timeout waiting for target application to respond.
        else if (wsException.getCause() instanceof SocketTimeoutException) {
            // Target application must be unavailable - update stats.
            SdtMetricsMBean.getMetrics().upTargetAppResponseTimeouts();

            throw new TimeoutException("TIMEOUT_ERROR", "Read time out error sending [" + errorReferenceContext + "]");
        }
        // Request was badly formatted.
        else if (wsException.getCause() instanceof org.apache.cxf.binding.soap.SoapFault ||
                wsException.getCause() instanceof javax.xml.ws.soap.SOAPFaultException) {
            // Target application must be unavailable - update stats.
            SdtMetricsMBean.getMetrics().upTargetAppSoapErrors();

            throw new SoapFaultException("SOAP_FAULT", wsException.getMessage());
        } else {
            // Target application must be unavailable - update stats.
            SdtMetricsMBean.getMetrics().upTargetAppMiscErrors();

            // If is is any other exception, we can't handle it cleanly. So throw it back
            throw wsException;

        }
    }
}
