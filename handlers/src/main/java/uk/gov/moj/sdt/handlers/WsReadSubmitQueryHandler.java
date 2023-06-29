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

import java.math.BigInteger;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.handlers.api.IWsReadSubmitQueryHandler;
import uk.gov.moj.sdt.services.api.ISubmitQueryService;
import uk.gov.moj.sdt.transformers.AbstractTransformer;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.exception.AbstractBusinessException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.ResultsType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

/**
 * Implementation for handling submit query flow.
 *
 * @author d130680
 */
@Transactional(propagation = Propagation.REQUIRED)
@Component("WsReadSubmitQueryHandler")
public class WsReadSubmitQueryHandler extends AbstractWsHandler implements IWsReadSubmitQueryHandler {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WsReadSubmitQueryHandler.class);

    /**
     * Submit Query service to return response.
     */
    private ISubmitQueryService submitQueryService;

    /**
     * The transformer associated with this handler.
     */
    // CHECKSTYLE:OFF
    private ITransformer<SubmitQueryRequestType, SubmitQueryResponseType, ISubmitQueryRequest, ISubmitQueryRequest> transformer;

    // CHECKSTYLE:ON

    @Autowired
    public WsReadSubmitQueryHandler(@Qualifier("SubmitQueryService")
                                                ISubmitQueryService submitQueryService,
                                    @Qualifier("SubmitQueryTransformer")
                                        ITransformer<SubmitQueryRequestType, SubmitQueryResponseType, ISubmitQueryRequest, ISubmitQueryRequest> transformer) {
        this.submitQueryService = submitQueryService;
        this.transformer = transformer;
    }

    @Override
    public SubmitQueryResponseType submitQuery(final SubmitQueryRequestType submitQueryRequestType) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Submit query started for customer[" +
                    submitQueryRequestType.getHeader().getSdtCustomerId() + "], query reference[" +
                    submitQueryRequestType.getHeader().getQueryReference() + "]");
        }

        // Update mbean stats.
        SdtMetricsMBean.getMetrics().upSubmitQueryCount();

        // Measure response time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        // Update number of customers using the system.
        SdtMetricsMBean.getMetrics().updateBulkCustomerCount(
                Long.toString(submitQueryRequestType.getHeader().getSdtCustomerId()));

        // Initialise response;
        LOGGER.debug("Setup initial submit query response");
        SubmitQueryResponseType submitQueryResponseType = createResponse(submitQueryRequestType);

        try {
            // Transform web service object to domain object(s)
            LOGGER.debug("Transform from SubmitQueryRequestType to ISubmitQueryRequest");
            final ISubmitQueryRequest submitQueryRequest =
                    getTransformer().transformJaxbToDomain(submitQueryRequestType);

            // Validate domain.
            LOGGER.debug("Validate submit query");
            validateDomain(submitQueryRequest);

            // Process validated request.
            LOGGER.debug("Process submit query");
            processSubmitQuery(submitQueryRequest);

            LOGGER.debug("Transform from ISubmitQueryRequest to SubmitQueryResponseType");
            submitQueryResponseType = getTransformer().transformDomainToJaxb(submitQueryRequest);
        } catch (final AbstractBusinessException be) {
            handleBusinessException(be, submitQueryResponseType);
        } finally {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Submit query completed for customer[" +
                        submitQueryRequestType.getHeader().getSdtCustomerId() + "], query reference[" +
                        submitQueryRequestType.getHeader().getQueryReference() + "]");
            }

            // Measure total time spent in use case.
            final long endTime = new GregorianCalendar().getTimeInMillis();
            SdtMetricsMBean.getMetrics().addSubmitQueryTime(endTime - startTime);
        }

        return submitQueryResponseType;

    }

    /**
     * Create and initialise Response.
     *
     * @param submitQueryRequestType request
     * @return SubmitQueryResponse
     */
    private SubmitQueryResponseType createResponse(final SubmitQueryRequestType submitQueryRequestType) {
        final SubmitQueryResponseType submitQueryResponseType = new SubmitQueryResponseType();
        submitQueryResponseType.setSdtService(AbstractTransformer.SDT_SERVICE);
        submitQueryResponseType.setSdtCustomerId(submitQueryRequestType.getHeader().getSdtCustomerId());
        submitQueryResponseType.setStatus(new StatusType());
        submitQueryResponseType.setResultCount(BigInteger.valueOf(0));
        submitQueryResponseType.setResults(new ResultsType());

        return submitQueryResponseType;
    }

    /**
     * Validate to ensure integrity of submit query.
     *
     * @param submitQueryRequest submit query criteria
     * @throws AbstractBusinessException business exception
     */
    private void validateDomain(final ISubmitQueryRequest submitQueryRequest) throws AbstractBusinessException {
        VisitableTreeWalker.walk(submitQueryRequest, "Validator");
    }

    /**
     * Process submit query.
     *
     * @param request submit query request
     */
    private void processSubmitQuery(final ISubmitQueryRequest request) {
        submitQueryService.submitQuery(request);
    }

    /**
     * Set the Submit Query Service.
     *
     * @param submitQueryService submit query service
     */
    public void setSubmitQueryService(final ISubmitQueryService submitQueryService) {
        this.submitQueryService = submitQueryService;
    }

    /**
     * Getter for transformer.
     *
     * @return the transformer associated with this class.
     */
    public ITransformer<SubmitQueryRequestType, SubmitQueryResponseType, ISubmitQueryRequest, ISubmitQueryRequest>
    getTransformer() {
        return transformer;
    }

    /**
     * Setter for transformer.
     *
     * @param transformer the transformer to be associated with this class.
     */
    // CHECKSTYLE:OFF
    public void
    setTransformer(final ITransformer<SubmitQueryRequestType, SubmitQueryResponseType, ISubmitQueryRequest, ISubmitQueryRequest> transformer) {
        this.transformer = transformer;
    }
    // CHECKSYTLE:ON
}
