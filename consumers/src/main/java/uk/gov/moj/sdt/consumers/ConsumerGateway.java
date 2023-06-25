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
package uk.gov.moj.sdt.consumers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.consumers.api.IConsumerGateway;
import uk.gov.moj.sdt.consumers.api.IIndividualRequestConsumer;
import uk.gov.moj.sdt.consumers.api.ISubmitQueryConsumer;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;

/**
 * Implementation of the ConsumerGateway interface.
 *
 * @author Manoj Kulkarni
 */
@Component("ConsumerGateway")
public class ConsumerGateway implements IConsumerGateway {

    /**
     * The consumer interface for the IndividualRequest.
     */
    private IIndividualRequestConsumer individualRequestConsumer;

    /**
     * The consumer interface for the SubmitQuery.
     */
    private ISubmitQueryConsumer submitQueryConsumer;

    @Autowired
    public ConsumerGateway(@Qualifier("IndividualRequestConsumer") IIndividualRequestConsumer individualRequestConsumer,
                           @Qualifier("SubmitQueryConsumer") ISubmitQueryConsumer submitQueryConsumer) {
        this.individualRequestConsumer = individualRequestConsumer;
        this.submitQueryConsumer = submitQueryConsumer;
    }

    @Override
    public void individualRequest(final IIndividualRequest individualRequest,
                                  final long connectionTimeOut, final long receiveTimeOut)
            throws OutageException, TimeoutException {
        this.getIndividualRequestConsumer().processIndividualRequest(
                individualRequest, connectionTimeOut, receiveTimeOut);
    }

    @Override
    public SubmitQueryResponse submitQuery(final ISubmitQueryRequest submitQueryRequest,
                                           final long connectionTimeOut, final long receiveTimeOut)
            throws OutageException, TimeoutException {

        return this.getSubmitQueryConsumer().processSubmitQuery(submitQueryRequest,
                connectionTimeOut, receiveTimeOut);
    }

    /**
     * @return individual request consumer.
     */
    public IIndividualRequestConsumer getIndividualRequestConsumer() {
        return individualRequestConsumer;
    }

    /**
     * @param individualRequestConsumer the individual request consumer.
     */
    public void setIndividualRequestConsumer(
            final IIndividualRequestConsumer individualRequestConsumer) {
        this.individualRequestConsumer = individualRequestConsumer;
    }

    /**
     * @return submit query consumer instance.
     */
    public ISubmitQueryConsumer getSubmitQueryConsumer() {
        return submitQueryConsumer;
    }

    /**
     * @param submitQueryConsumer - set submit query consumer instance.
     */
    public void setSubmitQueryConsumer(
            final ISubmitQueryConsumer submitQueryConsumer) {
        this.submitQueryConsumer = submitQueryConsumer;
    }
}
