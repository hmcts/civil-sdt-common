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
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$ */

package uk.gov.moj.sdt.producers.sdtws;

import javax.jws.WebService;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.handlers.api.IWsCreateBulkRequestHandler;
import uk.gov.moj.sdt.handlers.api.IWsReadBulkRequestHandler;
import uk.gov.moj.sdt.handlers.api.IWsReadSubmitQueryHandler;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

/**
 * Implementation of {@link ISdtEndpointPortType}.
 *
 * @author Saurabh Agarwal
 */

// CHECKSTYLE:OFF
@NoArgsConstructor
@Service("ISdtEndpointPortType")
@WebService(serviceName = "SdtEndpoint", portName = "SdtEndpointPort", targetNamespace = "http://ws.sdt.moj.gov.uk/2013/sdt/SdtEndpoint", wsdlLocation = "wsdl/SdtEndpoint.wsdl", endpointInterface = "uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType")
// CHECKSTYLE:ON
public class SdtEndpointPortType implements ISdtEndpointPortType {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SdtEndpointPortType.class);

    /**
     * Handles bulk submission request.
     */
    private IWsCreateBulkRequestHandler wsCreateBulkRequestHandler;

    /**
     * Handles bulk feedback submission request.
     */
    private IWsReadBulkRequestHandler wsReadBulkRequestHandler;

    /**
     * Handles submit query details.
     */
    private IWsReadSubmitQueryHandler wsReadSubmitQueryHandler;

    @Autowired
    public SdtEndpointPortType(@Qualifier("WsCreateBulkRequestHandler") IWsCreateBulkRequestHandler wsCreateBulkRequestHandler,
                               @Qualifier("WsReadBulkFeedbackRequestHandler") IWsReadBulkRequestHandler wsReadBulkRequestHandler,
                               @Qualifier("WsReadSubmitQueryHandler") IWsReadSubmitQueryHandler wsReadSubmitQueryHandler) {
        setWsCreateBulkRequestHandler(wsCreateBulkRequestHandler);
        setWsReadBulkRequestHandler(wsReadBulkRequestHandler);
        setWsReadSubmitQueryHandler(wsReadSubmitQueryHandler);
    }

    @Override
    public BulkResponseType submitBulk(final BulkRequestType bulkRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Endpoint called for submit bulk by customer [{}]",
                    bulkRequest.getHeader().getSdtCustomerId());
        }

        BulkResponseType response = null;
        try {
            response = wsCreateBulkRequestHandler.submitBulk(bulkRequest);
        } catch (Exception throwable) {
            handleException(throwable);
        }

        return response;
    }

    @Override
    public BulkFeedbackResponseType getBulkFeedback(final BulkFeedbackRequestType bulkFeedbackRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Endpoint called for bulk feedback by customer [{}]",
                    bulkFeedbackRequest.getHeader().getSdtCustomerId());
        }

        BulkFeedbackResponseType response = null;
        try {
            response = wsReadBulkRequestHandler.getBulkFeedback(bulkFeedbackRequest);
        } catch (Exception throwable) {
            handleException(throwable);
        }

        return response;
    }

    @Override
    public SubmitQueryResponseType submitQuery(final SubmitQueryRequestType submitQueryRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Endpoint called for submit query by customer [{}]",
                    submitQueryRequest.getHeader().getSdtCustomerId());
        }

        SubmitQueryResponseType response = null;

        try {
            response = wsReadSubmitQueryHandler.submitQuery(submitQueryRequest);
        } catch (Exception throwable) {
            handleException(throwable);
        }

        return response;

    }

    /**
     * Handles throwable and re-throws runtime exception.
     *
     * @param throwable exception to be handled
     */
    private void handleException(final Throwable throwable) {
        LOGGER.error("Unexpected error - ", throwable);
        throw new RuntimeException(
                "A SDT system component error has occurred. Please contact the SDT support team for assistance");
    }

    /**
     * @param wsCreateBulkRequestHandler the wsCreateBulkRequestHandler to set
     */
    public void setWsCreateBulkRequestHandler(final IWsCreateBulkRequestHandler wsCreateBulkRequestHandler) {
        this.wsCreateBulkRequestHandler = wsCreateBulkRequestHandler;
    }

    /**
     * @param wsReadBulkRequestHandler the wsReadBulkRequestHandler to set
     */
    public void setWsReadBulkRequestHandler(final IWsReadBulkRequestHandler wsReadBulkRequestHandler) {
        this.wsReadBulkRequestHandler = wsReadBulkRequestHandler;
    }

    /**
     * @param wsReadSubmitQueryHandler the wsReadSubmitQueryHandler to set
     */
    public void setWsReadSubmitQueryHandler(final IWsReadSubmitQueryHandler wsReadSubmitQueryHandler) {
        this.wsReadSubmitQueryHandler = wsReadSubmitQueryHandler;
    }

}
