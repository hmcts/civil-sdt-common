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
 * $Id: SdtInternalEndpointPortType.java manojk $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: manojk $ */
package uk.gov.moj.sdt.producers.sdtws;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.handlers.api.IWsUpdateItemHandler;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.UpdateRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.UpdateResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.sdtinternalendpoint.ISdtInternalEndpointPortType;

/**
 * Implementation of {@link ISdtInternalEndpointPortType}.
 *
 * @author Manoj Kulkarni
 */
// CHECKSTYLE:OFF
@Service("ISdtInternalEndpointPortType")
@WebService(serviceName = "SdtInternalEndpoint", portName = "SdtInternalEndpointPort", targetNamespace = "http://ws.sdt.moj.gov.uk/2013/sdt/SdtInternalEndpoint", wsdlLocation = "wsdl/SdtInternalEndpoint.wsdl", endpointInterface = "uk.gov.moj.sdt.ws._2013.sdt.sdtinternalendpoint.ISdtInternalEndpointPortType")
// CHECKSTYLE:ON
public class SdtInternalEndpointPortType implements ISdtInternalEndpointPortType {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SdtInternalEndpointPortType.class);

    /**
     * Update item handler for handling individual request update.
     */
    private IWsUpdateItemHandler updateItemHandler;

    @Autowired
    public SdtInternalEndpointPortType(@Qualifier("WsUpdateItemHandler") IWsUpdateItemHandler updateItemHandler) {
        setUpdateItemHandler(updateItemHandler);
    }

    public SdtInternalEndpointPortType() {}

    @Override
    public UpdateResponseType updateItem(final UpdateRequestType updateRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Endpoint called for update item for request reference [{}]",
                    updateRequest.getHeader().getSdtRequestId());
        }

        UpdateResponseType response = null;

        try {
            response = updateItemHandler.updateItem(updateRequest);
        }
        catch (Exception throwable)
        {
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
     * Sets the handler for handling update individual request.
     *
     * @param updateItemHandler the update item handler.
     */
    public void setUpdateItemHandler(final IWsUpdateItemHandler updateItemHandler) {
        this.updateItemHandler = updateItemHandler;
    }

}
