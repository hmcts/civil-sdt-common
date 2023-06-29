/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2013 by the Ministry of Justice. All rights reserved.
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
package uk.gov.moj.sdt.handlers;

import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.handlers.api.IWsUpdateItemHandler;
import uk.gov.moj.sdt.services.api.IUpdateRequestService;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.UpdateRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.UpdateResponseType;

/**
 * Handler for the update item web service.
 *
 * @author Manoj Kulkarni
 */
@Transactional(propagation = Propagation.REQUIRED)
@Component("WsUpdateItemHandler")
public class WsUpdateItemHandler extends AbstractWsHandler implements IWsUpdateItemHandler {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WsUpdateItemHandler.class);

    /**
     * Service class to process the update item.
     */
    private IUpdateRequestService updateRequestService;

    /**
     * The transformer associated with this handler.
     */
    // CHECKSTYLE:OFF
    private ITransformer<UpdateRequestType, UpdateResponseType, IIndividualRequest, IIndividualRequest> transformer;

    // CHECKSTYLE:ON

    @Autowired
    public WsUpdateItemHandler(@Qualifier("UpdateRequestService")
                                       IUpdateRequestService updateRequestService,
                               @Qualifier("UpdateItemTransformer")
                                   ITransformer<UpdateRequestType, UpdateResponseType, IIndividualRequest, IIndividualRequest> transformer) {
        this.updateRequestService = updateRequestService;
        this.transformer = transformer;
    }

    @Override
    public UpdateResponseType updateItem(final UpdateRequestType updateRequestType) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Update item started for sdt request id[" + updateRequestType.getHeader().getSdtRequestId() +
                    "]");
        }

        // Update mbean stats.
        SdtMetricsMBean.getMetrics().upStatusUpdateCount();

        // Measure response time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        // Initialise response.
        LOGGER.debug("Setup initial update request response");
        UpdateResponseType updateResponseType = new UpdateResponseType();

        try {
            updateResponseType.setStatus(new StatusType());

            // Transform to domain object.
            LOGGER.debug("Transform from UpdateRequestType to IIndividualRequest");
            final IIndividualRequest individualRequest = getTransformer().transformJaxbToDomain(updateRequestType);

            // No need to validate, call the service layer to process the object
            LOGGER.debug("Process individual request");
            this.getUpdateRequestService().updateIndividualRequest(individualRequest);

            // Transform domain to Jaxb
            LOGGER.debug("Transform from IIndividualRequest to UpdateResponseType");
            updateResponseType = getTransformer().transformDomainToJaxb(individualRequest);
        } finally {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Update item completed for sdt request id[" +
                        updateRequestType.getHeader().getSdtRequestId() + "]");
            }

            // Measure total time spent in use case.
            final long endTime = new GregorianCalendar().getTimeInMillis();
            SdtMetricsMBean.getMetrics().addStatusUpdateTime(endTime - startTime);
        }

        return updateResponseType;
    }

    /**
     * @return the transformer for the update item
     */
    public ITransformer<UpdateRequestType, UpdateResponseType, IIndividualRequest, IIndividualRequest>
    getTransformer() {
        return transformer;
    }

    /**
     * @param transformer the Update Item transformer.
     */
    // CHECKSTYLE:OFF
    public void
    setTransformer(final ITransformer<UpdateRequestType, UpdateResponseType, IIndividualRequest, IIndividualRequest> transformer)
    // CHECKSTYLE:ON
    {
        this.transformer = transformer;
    }

    /**
     * @return the update request service.
     */
    public IUpdateRequestService getUpdateRequestService() {
        return updateRequestService;
    }

    /**
     * @param updateRequestService the Update Request Service for the update item processing.
     */
    public void setUpdateRequestService(final IUpdateRequestService updateRequestService) {
        this.updateRequestService = updateRequestService;
    }
}
