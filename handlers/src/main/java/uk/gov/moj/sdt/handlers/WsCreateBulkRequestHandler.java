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
package uk.gov.moj.sdt.handlers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.handlers.api.IWsCreateBulkRequestHandler;
import uk.gov.moj.sdt.services.api.IBulkSubmissionService;
import uk.gov.moj.sdt.transformers.AbstractTransformer;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.utils.concurrent.api.IInFlightMessage;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.VisitableTreeWalker;
import uk.gov.moj.sdt.validators.api.IBulkSubmissionValidator;
import uk.gov.moj.sdt.validators.exception.AbstractBusinessException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;

/**
 * Handles bulk submission request flow.
 *
 * @author d276205
 */
@Transactional(propagation = Propagation.REQUIRED)
@Component("WsCreateBulkRequestHandler")
public class WsCreateBulkRequestHandler extends AbstractWsHandler implements IWsCreateBulkRequestHandler {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WsCreateBulkRequestHandler.class);

    /**
     * Bulk Submission service.
     */
    private IBulkSubmissionService bulkSubmissionService;

    /**
     * Bulk Submission Validator.
     */
    private IBulkSubmissionValidator bulkSubmissionValidator;

    /**
     * The transformer associated with this handler.
     */
    private ITransformer<BulkRequestType, BulkResponseType, IBulkSubmission, IBulkSubmission> transformer;

    /**
     * The concurrencyMap to hold sdtCustId+custRef and BulkRef. This is used to prevent the customer sending two
     * requests close together which both get processed at the same time, causing duplicates. The normal check on a
     * duplicate does not work until the first bulk request has been persisted.
     */
    private Map<String, IInFlightMessage> concurrencyMap;

    @Autowired
    public WsCreateBulkRequestHandler(@Qualifier("BulkSubmissionService")
                                              IBulkSubmissionService bulkSubmissionService,
                                      @Qualifier("BulkSubmissionValidator")
                                          IBulkSubmissionValidator bulkSubmissionValidator,
                                      @Qualifier("BulkRequestTransformer")
                                              ITransformer<BulkRequestType, BulkResponseType, IBulkSubmission, IBulkSubmission> transformer,
                                      @Qualifier("concurrencyMap")
                                              Map concurrencyMap) {
        this.bulkSubmissionService = bulkSubmissionService;
        this.bulkSubmissionValidator = bulkSubmissionValidator;
        this.transformer = transformer;
        this.concurrencyMap = concurrencyMap;
    }

    @Override
    public BulkResponseType submitBulk(final BulkRequestType bulkRequestType) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Submit bulk started for customer[" + bulkRequestType.getHeader().getSdtCustomerId() +
                    "], customer reference[" + bulkRequestType.getHeader().getCustomerReference() + "]");
        }

        // Update mbean stats.
        SdtMetricsMBean.getMetrics().upBulkSubmitCount();

        // Measure response time.
        final long startTime = new GregorianCalendar().getTimeInMillis();

        // Update number of customers using the system.
        SdtMetricsMBean.getMetrics().updateBulkCustomerCount(
                Long.toString(bulkRequestType.getHeader().getSdtCustomerId()));

        // Initialise response;
        LOGGER.debug("Setup initial bulk submission response");
        BulkResponseType bulkResponseType = intialiseResponse(bulkRequestType);

        try {
            // Transform web service object to domain object(s)
            LOGGER.debug("Transform from BulkRequestType to IBulkSubmission");
            final IBulkSubmission bulkSubmission = getTransformer().transformJaxbToDomain(bulkRequestType);

            // Validate domain
            LOGGER.debug("Validate bulk submission");
            validateDomain(bulkSubmission);

            // Process validated request
            LOGGER.debug("Process bulk submission");
            processBulkSubmission(bulkSubmission);

            // Get the jaxb response object from the bulk submission domain object
            LOGGER.debug("Transform from IBulkSubmission to BulkResponseType");
            bulkResponseType = getTransformer().transformDomainToJaxb(bulkSubmission);
        } catch (final AbstractBusinessException be) {
            handleBusinessException(be, bulkResponseType);

        } finally {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Submit bulk completed for customer[" + bulkRequestType.getHeader().getSdtCustomerId() +
                        "], customer reference[" + bulkRequestType.getHeader().getCustomerReference() + "]");
            }

            // Measure total time spent in use case.
            final long endTime = new GregorianCalendar().getTimeInMillis();
            SdtMetricsMBean.getMetrics().addBulkSubmitTime(endTime - startTime);

            final String key =
                    bulkRequestType.getHeader().getSdtCustomerId() +
                            bulkRequestType.getHeader().getCustomerReference();

            // Clean up concurrency map for this thread.
            final IInFlightMessage inFlightMessage = concurrencyMap.get(key);

            // Did processing proceed far enough to create an in flight message?
            if (inFlightMessage != null) {
                // Yes. Has it registered in the thread list?
                final Map<Thread, Thread> competingThreads = inFlightMessage.getCompetingThreads();
                if (competingThreads != null) {
                    // Remove this thread from list.
                    inFlightMessage.getCompetingThreads().remove(Thread.currentThread());
                }

                // Clean up concurrency map for this customer/customer reference if no threads still using it. The
                // information is needed while there are still threads that are using it and have not yet reported a
                // duplicate concurrent error back to the client, even though the winning thread is now finished.
                if (inFlightMessage.getCompetingThreads() == null || inFlightMessage.getCompetingThreads().isEmpty()) {
                    concurrencyMap.remove(key);
                }
            }
        }

        return bulkResponseType;
    }

    /**
     * Process bulk submission instance.
     *
     * @param bulkSubmission bulk submission instance.
     */
    private void processBulkSubmission(final IBulkSubmission bulkSubmission) {
        bulkSubmissionService.saveBulkSubmission(bulkSubmission);
    }

    /**
     * Initialise response.
     *
     * @param bulkRequest request instance.
     * @return created BulkResponse instance.
     */
    private BulkResponseType intialiseResponse(final BulkRequestType bulkRequest) {
        final BulkResponseType response = new BulkResponseType();
        response.setSdtService(AbstractTransformer.SDT_SERVICE);
        response.setCustomerReference(bulkRequest.getHeader().getCustomerReference());
        response.setRequestCount(bulkRequest.getHeader().getRequestCount());
        response.setSubmittedDate(Calendar.getInstance());

        final StatusType status = new StatusType();
        response.setStatus(status);
        status.setCode(StatusCodeType.OK);

        return response;
    }

    /**
     * Validate domain object - {@link IBulkSubmission}.
     *
     * @param bulkSubmission domain instance to validate.
     * @throws AbstractBusinessException in case of any business rule validation failure.
     */
    private void validateDomain(final IBulkSubmission bulkSubmission) throws AbstractBusinessException {
        VisitableTreeWalker.walk(bulkSubmission, "Validator");

        // This validation needs to be done after the first lot of validation
        // because we need to check all individual requests
        bulkSubmissionValidator.checkIndividualRequests(bulkSubmission);

    }

    /**
     * Getter for transformer.
     *
     * @return the transformer associated with this class.
     */
    public ITransformer<BulkRequestType, BulkResponseType, IBulkSubmission, IBulkSubmission> getTransformer() {
        return transformer;
    }

    /**
     * Setter for transformer.
     *
     * @param transformer the transformer to be associated with this class.
     */
    // CHECKSTYLE:OFF
    public void
    setTransformer(final ITransformer<BulkRequestType, BulkResponseType, IBulkSubmission, IBulkSubmission> transformer)
    // CHECKSTYLE:ON
    {
        this.transformer = transformer;
    }

    /**
     * Setter for bulk submission service.
     *
     * @param bulkSubmissionService bulk submission service
     */
    public void setBulkSubmissionService(final IBulkSubmissionService bulkSubmissionService) {
        this.bulkSubmissionService = bulkSubmissionService;
    }

    /**
     * Setter for bulk submission validator.
     *
     * @param bulkSubmissionValidator bulk submission validator
     */
    public void setBulkSubmissionValidator(final IBulkSubmissionValidator bulkSubmissionValidator) {
        this.bulkSubmissionValidator = bulkSubmissionValidator;
    }

    /**
     * Set concurrency map.
     *
     * @param concurrencyMap map holding in flight bulk requests.
     */
    public void setConcurrencyMap(final Map<String, IInFlightMessage> concurrencyMap) {
        this.concurrencyMap = concurrencyMap;
    }

}
