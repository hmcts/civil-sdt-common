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
 * $Id: WsCreateBulkRequestHandler.java 16487 2013-06-11 12:54:20Z compstonr $
 * $LastChangedRevision: 16487 $
 * $LastChangedDate: 2013-06-11 13:54:20 +0100 (Tue, 11 Jun 2013) $
 * $LastChangedBy: compstonr $ */
package uk.gov.moj.sdt.handlers;

import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.handlers.api.IWsReadBulkRequestHandler;
import uk.gov.moj.sdt.services.api.IBulkFeedbackService;
import uk.gov.moj.sdt.transformers.AbstractTransformer;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.exception.AbstractBusinessException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkRequestStatusType;

/**
 * Handles bulk feedback submission request flow.
 *
 * @author d301488
 */
@Transactional(propagation = Propagation.REQUIRED)
@Component("WsReadBulkFeedbackRequestHandler")
public class WsReadBulkFeedbackRequestHandler extends AbstractWsHandler implements IWsReadBulkRequestHandler {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WsReadBulkFeedbackRequestHandler.class);

    /**
     * Injected Get Bulk Feedback service.
     */
    private IBulkFeedbackService bulkFeedbackService;

    /**
     * The transformer associated with this handler.
     */
    private ITransformer<BulkFeedbackRequestType, BulkFeedbackResponseType,
            IBulkFeedbackRequest, IBulkSubmission> transformer;

    @Autowired
    public WsReadBulkFeedbackRequestHandler(@Qualifier("BulkFeedbackService")
                                                    IBulkFeedbackService bulkFeedbackService,
                                            @Qualifier("BulkFeedbackTransformer")
                                                ITransformer<BulkFeedbackRequestType, BulkFeedbackResponseType, IBulkFeedbackRequest, IBulkSubmission> transformer) {
        this.bulkFeedbackService = bulkFeedbackService;
        this.transformer = transformer;
    }

    @Override
    public BulkFeedbackResponseType getBulkFeedback(final BulkFeedbackRequestType bulkFeedbackRequest) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Bulk feedback started for customer[{}], customer reference[{}]",
                    bulkFeedbackRequest.getHeader().getSdtCustomerId(),
                    bulkFeedbackRequest.getHeader().getSdtBulkReference());
        }

        // Update mbean stats.
        SdtMetricsMBean.getMetrics().upBulkFeedbackCount();

        // Measure response time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        // Update number of customers using the system.
        SdtMetricsMBean.getMetrics().updateBulkCustomerCount(
                Long.toString(bulkFeedbackRequest.getHeader().getSdtCustomerId()));

        // Initialise response;
        LOGGER.debug("Setup initial bulk feedback response");
        BulkFeedbackResponseType response = createResponse(bulkFeedbackRequest);

        try {
            // Transform Web service object to Domain object.
            LOGGER.debug("Transform from BulkFeedbackRequestType to IBulkFeedbackRequest");
            final IBulkFeedbackRequest bulkFeedbackRequestDomain =
                    getTransformer().transformJaxbToDomain(bulkFeedbackRequest);

            // Validate the domain object
            LOGGER.debug("Validate bulk feedback");
            validateDomain(bulkFeedbackRequestDomain);

            // Process validated request
            LOGGER.debug("Process bulk feedback");
            final IBulkSubmission bulkSubmission = bulkFeedbackService.getBulkFeedback(bulkFeedbackRequestDomain);

            // Get the jaxb response object from the bulk submission domain object
            LOGGER.debug("Transform from IBulkFeedbackRequest to BulkFeedbackResponseType");
            response = getTransformer().transformDomainToJaxb(bulkSubmission);
        } catch (final AbstractBusinessException be) {
            handleBusinessException(be, response.getBulkRequestStatus());
        } finally {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Bulk feedback completed for customer[{}], customer reference[{}]",
                        bulkFeedbackRequest.getHeader().getSdtCustomerId(),
                        bulkFeedbackRequest.getHeader().getSdtBulkReference());
            }

            // Measure total time spent in use case.
            final long endTime = new GregorianCalendar().getTimeInMillis();
            SdtMetricsMBean.getMetrics().addBulkFeedbackTime(endTime - startTime);
        }

        return response;
    }

    /**
     * Create Response and initialise status.
     *
     * @param bulkFeedbackRequest bulk feedback request.
     * @return instance of BulkFeedbackResponseType
     */
    private BulkFeedbackResponseType createResponse(final BulkFeedbackRequestType bulkFeedbackRequest) {
        final BulkFeedbackResponseType response = new BulkFeedbackResponseType();
        final BulkRequestStatusType bulkRequestStatusType = new BulkRequestStatusType();
        bulkRequestStatusType.setSdtService(AbstractTransformer.SDT_SERVICE);

        final StatusType statusType = new StatusType();
        bulkRequestStatusType.setStatus(statusType);
        bulkRequestStatusType.setSdtBulkReference(bulkFeedbackRequest.getHeader().getSdtBulkReference());

        response.setBulkRequestStatus(bulkRequestStatusType);
        return response;
    }

    /**
     * Validate to ensure integrity of bulk feedback request.
     *
     * @param bulkFeedbackRequest bulk feedback request criteria
     * @throws AbstractBusinessException business exception
     */
    private void validateDomain(final IBulkFeedbackRequest bulkFeedbackRequest) throws AbstractBusinessException {
        VisitableTreeWalker.walk(bulkFeedbackRequest, "Validator");
    }

    /**
     * Set bulk feedback service implementation.
     *
     * @param bulkFeedbackService bulk feedback service
     */
    public void setBulkFeedbackService(final IBulkFeedbackService bulkFeedbackService) {
        this.bulkFeedbackService = bulkFeedbackService;
    }

    /**
     * Getter for transformer.
     *
     * @return the transformer associated with this class.
     */
    public ITransformer<BulkFeedbackRequestType, BulkFeedbackResponseType, IBulkFeedbackRequest, IBulkSubmission>
    getTransformer() {
        return transformer;
    }

    /**
     * Setter for transformer.
     *
     * @param transformer the transformer to be associated with this class.
     */
    public void setTransformer(final ITransformer<BulkFeedbackRequestType, BulkFeedbackResponseType,
            IBulkFeedbackRequest, IBulkSubmission> transformer) {
        this.transformer = transformer;
    }
}
