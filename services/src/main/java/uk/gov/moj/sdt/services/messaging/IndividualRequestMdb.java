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
package uk.gov.moj.sdt.services.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.services.api.ITargetApplicationSubmissionService;
import uk.gov.moj.sdt.services.messaging.api.IMessageDrivenBean;
import uk.gov.moj.sdt.services.messaging.api.ISdtMessage;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.logging.LoggingContext;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;

/**
 * Implementation of the IMessageReader interface.
 * This class is implemented using the Spring JMS 1.1 support and is invoked by the
 * MessageListenerAdapter class that is registered with the DefaultMessageListener.
 *
 * @author Manoj Kulkarni
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Component("IndividualRequestMdb")
public class IndividualRequestMdb implements IMessageDrivenBean {
    /**
     * Logger for logging messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualRequestMdb.class);

    /**
     * Target Application Submission Service.
     */
    private ITargetApplicationSubmissionService targetAppSubmissionService;

    @Autowired
    public IndividualRequestMdb(@Qualifier("TargetApplicationSubmissionService")
                                    ITargetApplicationSubmissionService targetAppSubmissionService) {
        this.targetAppSubmissionService = targetAppSubmissionService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void readMessage(final Message message) {
        // All expected messages are object messages written by the message writer.
        if (message instanceof ObjectMessage) {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            String sdtReference = null;
            Boolean caseOffLine = null;

            ISdtMessage sdtMessage = null;

            try {
                sdtMessage = (ISdtMessage) objectMessage.getObject();
                sdtReference = sdtMessage.getSdtRequestReference();
                caseOffLine = sdtMessage.isCaseOffLine();

                // Update statistics.
                SdtMetricsMBean.getMetrics().addRequestQueueTime(sdtMessage.getMessageSentTimestamp());
                SdtMetricsMBean.getMetrics().decrementRequestQueueLength();

            } catch (final JMSException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }

            LOGGER.debug("Received message, SDT reference [{}]",  sdtReference);
            // Assemble logging id - for dequeued messages we take the logging id of the original submit bulk thread as
            // the major portion to tie things together.
            SdtContext.getContext().getLoggingContext().setMajorLoggingId(sdtMessage.getEnqueueLoggingId());
            SdtContext.getContext().getLoggingContext().setMinorLoggingId(LoggingContext.getNextLoggingId());

            try {
                this.getTargetAppSubmissionService().processRequestToSubmit(sdtReference, caseOffLine);
            }
            // CHECKSTYLE:OFF
            catch (final RuntimeException e) {
                LOGGER.error("Fatal exception encountered in {}",
                        this.getTargetAppSubmissionService().getClass().getSimpleName());

                // We are about to abort the transaction; treat this message as never read and therefore reverse
                // decrement
                // of queue count done earlier.
                SdtMetricsMBean.getMetrics().upRequestQueueLength();

                // Rethrow it.
                throw e;
            }
        } else {
            LOGGER.error("Invalid message type [{}] read by MDB.", message.getClass().getCanonicalName());
        }
    }

    /**
     * @return the target application submission service.
     */
    private ITargetApplicationSubmissionService getTargetAppSubmissionService() {
        return targetAppSubmissionService;
    }

    /**
     * @param targetAppSubmissionService the target submission service
     */
    public void setTargetAppSubmissionService(final ITargetApplicationSubmissionService targetAppSubmissionService) {
        this.targetAppSubmissionService = targetAppSubmissionService;
    }
}
