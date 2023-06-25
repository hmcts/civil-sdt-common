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

import java.util.GregorianCalendar;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.consumers.api.IIndividualRequestConsumer;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.transformers.api.IConsumerTransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvrequestschema.IndividualRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvresponseschema.IndividualResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

/**
 * Consumer for the Individual Request processing.
 *
 * @author Manoj Kulkarni
 */
@Component("IndividualRequestConsumer")
public class IndividualRequestConsumer extends AbstractWsConsumer implements IIndividualRequestConsumer {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualRequestConsumer.class);

    /**
     * Consumer transformer for individual request.
     */
    private IConsumerTransformer<IndividualResponseType, IndividualRequestType, IIndividualRequest, IIndividualRequest> transformer;

    @Autowired
    public IndividualRequestConsumer(@Qualifier("IndividualRequestConsumerTransformer")
                                         IConsumerTransformer<IndividualResponseType, IndividualRequestType, IIndividualRequest, IIndividualRequest> transformer) {
        this.transformer = transformer;
    }

    /**
     * A boolean flag to indicate if the WebServiceException is to be thrown back to the client
     * when there is a failure to connect to the target application.
     * The default value is false i.e. the error will not be thrown to the client and the
     * consumer will keep trying to connect to the target application.
     */
    private boolean rethrowOnFailureToConnect = false;

    @Override
    public void processIndividualRequest(final IIndividualRequest individualRequest, final long connectionTimeOut,
                                         final long receiveTimeOut) throws OutageException, TimeoutException {
        // Transform domain object to web service object
        LOGGER.debug("Transform from IIndividualRequest to IndividualRequestType");
        final IndividualRequestType individualRequestType = this.transformer.transformDomainToJaxb(individualRequest);

        // Process and call the end point web service
        LOGGER.debug("Invoke target application service for individual request");
        final IndividualResponseType responseType =
                this.invokeTargetAppService(individualRequestType, individualRequest, connectionTimeOut,
                        receiveTimeOut);

        LOGGER.debug("Transform from IndividualResponseType to IIndividualRequest");
        this.transformer.transformJaxbToDomain(responseType, individualRequest);
    }

    /**
     * @param request           the individual request JAXB model
     * @param iRequest          the individual request domain object
     * @param connectionTimeOut the connection time out from the global parameter value
     * @param receiveTimeOut    the receive time out from the global parameter value.
     * @return the IndividualResponseType object after calling the web service of target app.
     * @throws OutageException  if there is a service outage
     * @throws TimeoutException if the read timed out within the time specified
     */
    private IndividualResponseType invokeTargetAppService(final IndividualRequestType request,
                                                          final IIndividualRequest iRequest,
                                                          final long connectionTimeOut, final long receiveTimeOut)
            throws OutageException, TimeoutException {
        final IServiceRouting serviceRouting =
                iRequest.getBulkSubmission().getTargetApplication()
                        .getServiceRouting(IServiceType.ServiceTypeName.SUBMIT_INDIVIDUAL);

        final String webServiceEndPoint = serviceRouting.getWebServiceEndpoint();
        final String targetAppCode = iRequest.getBulkSubmission().getTargetApplication().getTargetApplicationCode();

        // Get the client interface
        final ITargetAppInternalEndpointPortType client =
                getClient(targetAppCode, IServiceType.ServiceTypeName.SUBMIT_INDIVIDUAL.name(), webServiceEndPoint,
                        connectionTimeOut, receiveTimeOut);

        int attemptCount = 0;

        // Loop until the target application becomes available.
        while (true) {
            long startTime = 0;

            try {
                SdtMetricsMBean.getMetrics().upTargetAppCallCount();

                attemptCount++;

                LOGGER.debug("Submitting individual request[{}] to target application[{}], attempt[{}]",
                        iRequest.getSdtRequestReference(), targetAppCode, attemptCount);

                // Measure response time.
                startTime = new GregorianCalendar().getTimeInMillis();

                // Call the specific business method for this text - note that a single test can only use one web
                // service business method.
                final IndividualResponseType individualResponseType = client.submitIndividual(request);

                return individualResponseType;
            } catch (final WebServiceException f) {
                LOGGER.error("Target application [" +
                        iRequest.getBulkSubmission().getTargetApplication().getTargetApplicationName() +
                        "] error sending individual request [" + iRequest.getSdtRequestReference() + "]", f);

                // The following will throw a further exception unless we are here because the target application is
                // unavailable.
                super.handleClientErrors(getRethrowOnFailureToConnect(), f, iRequest.getSdtRequestReference());

                try {
                    // Delay before re-attempting to send to target application.
                    Thread.sleep(connectionTimeOut);
                } catch (final InterruptedException e) {
                    LOGGER.error("Sleep operation interrupted while re-attempting to send to target application", e);
                }
            } finally {
                // Measure total time spent in target application.
                final long endTime = new GregorianCalendar().getTimeInMillis();
                SdtMetricsMBean.getMetrics().addTargetAppResponseTime(endTime - startTime);
            }
        }
    }

    /**
     * @param transformer the transformer
     */
    public void
    setTransformer(final IConsumerTransformer<IndividualResponseType, IndividualRequestType, IIndividualRequest,
            IIndividualRequest> transformer) {
        this.transformer = transformer;
    }

    /**
     *
     * @return the transformer for IndividualRequestConsumer
     */
    public IConsumerTransformer<IndividualResponseType, IndividualRequestType, IIndividualRequest, IIndividualRequest>
            getTransformer ()
    {
        return this.transformer;
    }

    /**
     * Get the value for the rethrowOnFailureToConnect flag.
     *
     * @return rethrowOnFailureToConnect flag
     */
    public boolean getRethrowOnFailureToConnect() {
        return rethrowOnFailureToConnect;
    }

    /**
     * Set the value for the rethrowOnFailureToConnect.
     *
     * @param rethrowOnFailureToConnect - true will throw the connection fail error.
     */
    public void setRethrowOnFailureToConnect(final boolean rethrowOnFailureToConnect) {
        this.rethrowOnFailureToConnect = rethrowOnFailureToConnect;
    }
}
