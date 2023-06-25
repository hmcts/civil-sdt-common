/* Copyrights and Licenses
 *
 * Copyright (c) 2010 by the Ministry of Justice. All rights reserved.
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
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$ */

package uk.gov.moj.sdt.consumers;

import java.util.GregorianCalendar;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.consumers.api.ISubmitQueryConsumer;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.transformers.api.IConsumerTransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

/**
 * @author D274994
 */
@Component("SubmitQueryConsumer")
public class SubmitQueryConsumer extends AbstractWsConsumer implements ISubmitQueryConsumer {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitQueryConsumer.class);

    /**
     * transformer for submit query request.
     */
    private IConsumerTransformer<SubmitQueryResponseType, SubmitQueryRequestType, ISubmitQueryRequest, ISubmitQueryRequest> transformer;

    @Autowired
    public SubmitQueryConsumer(@Qualifier("SubmitQueryConsumerTransformer")
                                   IConsumerTransformer<SubmitQueryResponseType, SubmitQueryRequestType, ISubmitQueryRequest, ISubmitQueryRequest> transformer) {
        this.transformer = transformer;
    }

    /* (non-Javadoc)
     *
     * @see
     * uk.gov.moj.sdt.consumers.api.ISubmitQueryConsumer#processSubmitQuery(
     * uk.gov.moj.sdt.domain.api.ISubmitQueryRequest) */
    @Override
    public SubmitQueryResponse processSubmitQuery(final ISubmitQueryRequest submitQueryRequest, final long connectionTimeOut,
                                                  final long receiveTimeOut) throws OutageException, TimeoutException {
        // Transform domain object to web service object
        final SubmitQueryRequestType submitQueryRequestType =
                this.transformer.transformDomainToJaxb(submitQueryRequest);

        // Process and call the end point web service
        LOGGER.debug("processSubmitQuery for {}:{}:{}", submitQueryRequest.getBulkCustomer(),
                submitQueryRequest.getTargetApplication(), submitQueryRequest.getQueryReference());
        SubmitQueryResponseType responseType = this.invokeTargetAppService(submitQueryRequestType, submitQueryRequest, connectionTimeOut,
                        receiveTimeOut);

        this.transformer.transformJaxbToDomain(responseType, submitQueryRequest);

        SubmitQueryResponse submitQueryResponse = new SubmitQueryResponse();
        submitQueryResponse.setResponseType(responseType);

        return submitQueryResponse;
    }

    /**
     * @param submitQueryRequestType request the individual request JAXB model
     * @param submitQueryRequest     the individual request domain object
     * @param connectionTimeOut      the connection time out from the global parameter value
     * @param receiveTimeOut         the receive time out from the global parameter value.
     * @return the SubmitQueryResponseType object after calling the web service
     * of target app.
     * @throws OutageException  if there is a service outage
     * @throws TimeoutException if the read timed out within the time specified
     */
    private SubmitQueryResponseType invokeTargetAppService(final SubmitQueryRequestType submitQueryRequestType,
                                                           final ISubmitQueryRequest submitQueryRequest,
                                                           final long connectionTimeOut, final long receiveTimeOut)
            throws OutageException, TimeoutException {
        final IServiceRouting serviceRouting =
                submitQueryRequest.getTargetApplication()
                        .getServiceRouting(IServiceType.ServiceTypeName.SUBMIT_QUERY);

        final String webServiceEndPoint = serviceRouting.getWebServiceEndpoint();
        final String targetAppCode = submitQueryRequest.getTargetApplication().getTargetApplicationCode();

        // Get the client interface
        final ITargetAppInternalEndpointPortType client =
                getClient(targetAppCode, IServiceType.ServiceTypeName.SUBMIT_QUERY.name(), webServiceEndPoint,
                        connectionTimeOut, receiveTimeOut);

        long startTime = 0;

        try {
            SdtMetricsMBean.getMetrics().upTargetAppCallCount();

            LOGGER.debug("Submitting query to target application[{}], for customer[{}]",
                    targetAppCode, submitQueryRequestType.getHeader().getTargetAppCustomerId());

            // Measure response time.
            startTime = new GregorianCalendar().getTimeInMillis();

            // Call the specific business method for this text - note that a single test can only use one web service
            // business method.
            final SubmitQueryResponseType submitQueryResponseType = client.submitQuery(submitQueryRequestType);

            return submitQueryResponseType;
        } catch (final WebServiceException f) {
            LOGGER.error("Target application [{}] error sending submit query request [{}]",
                    submitQueryRequest.getTargetApplication().getTargetApplicationCode(),
                    submitQueryRequest.getCriteriaType());
            super.handleClientErrors(true, f, submitQueryRequest.getCriteriaType());
        } finally {
            // Measure total time spent in target application.
            final long endTime = new GregorianCalendar().getTimeInMillis();
            SdtMetricsMBean.getMetrics().addTargetAppResponseTime(endTime - startTime);
        }

        return null;
    }

    /**
     *
     * @return the transformer for SubmitQueryConsumer
     */
    public IConsumerTransformer<SubmitQueryResponseType, SubmitQueryRequestType, ISubmitQueryRequest,
            ISubmitQueryRequest> getTransformer () {
        return this.transformer;
    }

    /**
     * Mutator method for transformer.
     *
     * @param transformer
     */
    public void setTransformer(IConsumerTransformer<SubmitQueryResponseType, SubmitQueryRequestType,
            ISubmitQueryRequest, ISubmitQueryRequest> transformer) {
        this.transformer = transformer;
    }

}
