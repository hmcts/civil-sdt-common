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
package uk.gov.moj.sdt.consumers.api;

import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;

/**
 * Interface for the consumers implementing the consumer gateway.
 *
 * @author Manoj Kulkarni
 */
public interface IConsumerGateway {

    /**
     * @param individualRequest the Individual Request part of the submission request.
     * @param connectionTimeOut the connection timeout parameter value.
     * @param receiveTimeOut    the receive timeout parameter value.
     * @throws OutageException  if the target server is un-reachable.
     * @throws TimeoutException if the target server response cannot be obtained within the
     *                          timeout period.
     */
    void individualRequest(IIndividualRequest individualRequest,
                           final long connectionTimeOut, final long receiveTimeOut)
            throws OutageException, TimeoutException;

    /**
     * @param submitQueryRequest the submit query request coming from handler.
     * @param connectionTimeOut  the connection timeout parameter value.
     * @param receiveTimeOut     the receive timeout parameter value.
     * @throws OutageException  if the target server is un-reachable.
     * @throws TimeoutException if the target server response cannot be obtained within the
     *                          timeout period.
     */
    SubmitQueryResponse submitQuery(ISubmitQueryRequest submitQueryRequest,
                                    final long connectionTimeOut, final long receiveTimeOut)
            throws OutageException, TimeoutException;

}
