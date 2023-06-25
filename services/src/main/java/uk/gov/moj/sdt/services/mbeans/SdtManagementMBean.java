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
package uk.gov.moj.sdt.services.mbeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.services.api.ITargetApplicationSubmissionService;
import uk.gov.moj.sdt.services.messaging.api.IMessageWriter;
import uk.gov.moj.sdt.services.utils.api.IMessagingUtility;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to manage the SDT application via mbean commands. Implements the following commands:
 * <p>
 * 1. Uncache all cacheable domain objects and force reload from database.
 * 2. Set message driven bean pool size for individual request JMS queue.
 * 3. Requeue old individual requests which have not yet been sent to target application.
 *
 * @author Robin Compston
 */
@Component("SdtManagementMBean")
public class SdtManagementMBean implements ISdtManagementMBean {
    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SdtManagementMBean.class);

    /**
     * Success message to return after successfully processing the SDT individual request.
     */
    private static final String OK_MESSAGE = "OK";

    /**
     * Error message to return if the mandatory parameters are missing.
     */
    private static final String MANDATORY_PARAMETERS_ERR_MSG =
            "SDT Request Reference and the Request Status should be supplied.";

    /**
     * Error message to return if the invalid request status values are supplied.
     */
    private static final String INVALID_PARAM_VALUES_MSG =
            "Invalid Request Status supplied. The Request Status should be either REJECTED or FORWARDED";

    /**
     * Expected Parameter values for the request status parameter of processDLQRequest method.
     */
    private static final String[] REQ_STATUS_PARAMVAL = {"FORWARDED", "REJECTED"};

    /**
     * Error message to return if the Sdt Request Reference parameter does not correspond
     * to an Individual Request record in the database.
     */
    private static final String INVALID_SDT_REQUEST_REF_MSG = "SDT Request Reference supplied does not exist.";

    /**
     * Error message to return if the SDT Request Reference parameter is not marked as Dead Letter
     * Queue flag.
     */
    private static final String SDT_REQUEST_NOT_ON_DLQ_MSG =
            "SDT Request Reference supplied is not marked as dead letter and cannot be processed.";

    /**
     * Maximum value to which MDB pool size can be set.
     */
    private static final int MAX_POOL_SIZE = 50;
    /**
     * The current value of a flag which controls whether individual
     * {@link uk.gov.moj.sdt.domain.cache.AbstractCacheControl} instances need to be
     * uncached. This number is incremented on each uncache, telling caching objects that their cache is stale and needs
     * to be discarded and reloaded. They look at this value and compare it with their own copy of it each time the
     * cache is accessed.
     */
    private int cacheResetControl;

    /**
     * Map of all message driven bean message listener containers, defining the MDB pool size.
     */
    private Map<String, DefaultMessageListenerContainer> containerMap = new HashMap<>();

    /**
     * Individual Request Dao to perform operations on the individual request object.
     */
    private IIndividualRequestDao individualRequestDao;

    /**
     * This variable holding the messaging utility reference.
     */
    private IMessagingUtility messagingUtility;

    /**
     * This variable holding the target application submission service.
     */
    private ITargetApplicationSubmissionService targetAppSubmissionService;

    @Autowired
    public SdtManagementMBean(@Qualifier("messageListenerContainer")
                                  DefaultMessageListenerContainer messageListenerContainer,
                              @Qualifier("IndividualRequestDao")
                                  IIndividualRequestDao individualRequestDao,
                              @Qualifier("MessagingUtility")
                                  IMessagingUtility messagingUtility,
                              @Qualifier("TargetApplicationSubmissionService")
                                  ITargetApplicationSubmissionService targetAppSubmissionService) {
        this.individualRequestDao = individualRequestDao;
        this.messagingUtility = messagingUtility;
        this.targetAppSubmissionService = targetAppSubmissionService;
        setMessageListenerContainer(messageListenerContainer);
    }

    @Override
    public int getCacheResetControl() {
        return this.cacheResetControl;
    }

    @Override
    public void setMessageListenerContainer(final DefaultMessageListenerContainer messageListenerContainer) {
        containerMap.put(messageListenerContainer.getDestinationName(), messageListenerContainer);
    }

    @Override
    public void uncache() {
        this.cacheResetControl++;

        LOGGER.info("Uncaching all cacheable items, cacheResetControl=" + cacheResetControl);
    }

    @Override
    public String setMdbPoolSize(final String queueName, final int poolSize) {
        if (!containerMap.containsKey(queueName)) {
            LOGGER.error("mdb pool [" + queueName + "] not found.");
            return "mdb pool [" + queueName + "] not found";
        }

        // Validate new pool size.
        if (poolSize < 1 || poolSize > MAX_POOL_SIZE) {
            LOGGER.error("MDB pool size can only be set between 1 and " + MAX_POOL_SIZE + ".");
            return "MDB pool size can only be set between 1 and " + MAX_POOL_SIZE + ".";
        }

        // Get the message listener container registered earlier.
        final DefaultMessageListenerContainer messageListenerContainer = containerMap.get(queueName);

        // Set new maximum pool size for named queue.
        final int oldPoolSize = messageListenerContainer.getMaxConcurrentConsumers();
        messageListenerContainer.setMaxConcurrentConsumers(poolSize);

        LOGGER.info("mdb pool [" + queueName + "] size changed from " + oldPoolSize + " to " + poolSize);
        return "mdb pool [" + queueName + "] size changed from " + oldPoolSize + " to " + poolSize;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void requeueOldIndividualRequests(final int minimumAgeInMinutes) {
        // Get list of pending individual requests.
        final List<IIndividualRequest> individualRequests =
                this.individualRequestDao.getStaleIndividualRequests(minimumAgeInMinutes);

        LOGGER.info("Requeue " + individualRequests.size() + " rejected messages older than " + minimumAgeInMinutes +
                " minutes");

        // Loop through the list of the individual requests found.
        if (!individualRequests.isEmpty()) {
            for (IIndividualRequest individualRequest : individualRequests) {
                this.messagingUtility.enqueueRequest(individualRequest);

                // Re-set the forwarding attempts on the individual request.
                individualRequest.resetForwardingAttempts();

                LOGGER.debug("Now re-queued pending individual request [" +
                        individualRequest.getSdtRequestReference() + "]");
            }

            // Persist the list of individual requests.
            this.individualRequestDao.persistBulk(individualRequests);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String processDlqRequest(final String sdtRequestReference, final String requestStatus) {
        IIndividualRequest individualRequest = null;

        // Parameters validation.
        if (sdtRequestReference == null || requestStatus == null) {
            return MANDATORY_PARAMETERS_ERR_MSG;
        } else {
            // Check the request status is either REJECTED or FORWARDED.
            if (!(REQ_STATUS_PARAMVAL[0].equals(requestStatus.toUpperCase()) || REQ_STATUS_PARAMVAL[1]
                    .equals(requestStatus.toUpperCase()))) {
                return INVALID_PARAM_VALUES_MSG;
            }

            // Check the SDT request reference is valid.
            individualRequest = this.individualRequestDao.getRequestBySdtReference(sdtRequestReference);
            if (individualRequest == null) {
                return INVALID_SDT_REQUEST_REF_MSG;
            }

            // Check that the DLQ Request flag is true.
            if (!individualRequest.isDeadLetter()) {
                return SDT_REQUEST_NOT_ON_DLQ_MSG;
            }

        }

        // Now that we have found the individual request record, perform the actions required.
        if (individualRequest != null) {
            this.targetAppSubmissionService.processDLQRequest(individualRequest, requestStatus);
        }

        return OK_MESSAGE;
    }

    /**
     * @param individualRequestDao the individual request dao object.
     */
    public void setIndividualRequestDao(final IIndividualRequestDao individualRequestDao) {
        this.individualRequestDao = individualRequestDao;
    }

    /**
     * @param messageWriter the message writer instance.
     */
    public void setMessageWriter(final IMessageWriter messageWriter) {
    }

    /**
     * @param messagingUtility the messagingUtility instance.
     */
    public void setMessagingUtility(final IMessagingUtility messagingUtility) {
        this.messagingUtility = messagingUtility;
    }

    /**
     * @param targetAppSubmissionService the targetApplicationSubmissionService instance.
     */
    public void setTargetAppSubmissionService(final ITargetApplicationSubmissionService targetAppSubmissionService) {
        this.targetAppSubmissionService = targetAppSubmissionService;
    }

}
