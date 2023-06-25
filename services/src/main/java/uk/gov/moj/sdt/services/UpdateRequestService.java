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
package uk.gov.moj.sdt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.services.api.IUpdateRequestService;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.services.utils.api.IMessagingUtility;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.ACCEPTED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.RESUBMIT_MESSAGE;

/**
 * Service class implementing the IUpdateRequestService.
 *
 * @author Manoj Kulkarni
 */
@Component("UpdateRequestService")
public class UpdateRequestService extends AbstractSdtService implements IUpdateRequestService {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRequestService.class);

    /**
     * Messaging utility for queueing messages in the messaging server.
     */
    private IMessagingUtility messagingUtility;

    @Autowired
    public UpdateRequestService(@Qualifier("IndividualRequestDao")
                                    IIndividualRequestDao individualRequestDao,
                                @Qualifier("IndividualResponseXmlParser")
                                    GenericXmlParser individualResponseXmlParser,
                                @Qualifier("MessagingUtility")
                                    IMessagingUtility messagingUtility,
                                RequestTypeXmlNodeValidator requestTypeXmlNodeValidator) {
        super(individualRequestDao, individualResponseXmlParser, requestTypeXmlNodeValidator);
        this.messagingUtility = messagingUtility;
    }

    @Override
    public void updateIndividualRequest(final IIndividualRequest individualRequestParam) {
        // Look for the individual request matching this unique request reference.
        final IIndividualRequest individualRequest =
                this.getIndRequestBySdtReference(individualRequestParam.getSdtRequestReference());

        // Proceed ahead if the Individual Request is found.
        if (individualRequest != null) {

            // Resubmit message to target application by enqueuing it.
            if (RESUBMIT_MESSAGE.getStatus().equals(
                    individualRequestParam.getRequestStatus())) {
                individualRequest.resetForwardingAttempts();
                getMessagingUtility().enqueueRequest(individualRequest);
            }

            // Refresh the individual request from the database with the status and the error code
            // from the target application.
            if (REJECTED.getStatus().equals(
                    individualRequestParam.getRequestStatus())) {
                individualRequest.markRequestAsRejected(individualRequestParam.getErrorLog());
            } else if (ACCEPTED.getStatus().equals(
                    individualRequestParam.getRequestStatus())) {
                individualRequest.markRequestAsAccepted();
            }

            // Mark the individual request as completed
            this.updateCompletedRequest(individualRequest);

        } else {
            LOGGER.warn("Individual Request with Sdt Request Reference [" +
                    individualRequestParam.getSdtRequestReference() + "] not found.");
        }

    }

    /**
     * Get the messaging utility class reference.
     *
     * @return the messaging utility
     */
    public IMessagingUtility getMessagingUtility() {
        return messagingUtility;
    }

    /**
     * @param messagingUtility the messaging utility class.
     */
    public void setMessagingUtility(final IMessagingUtility messagingUtility) {
        this.messagingUtility = messagingUtility;
    }
}
