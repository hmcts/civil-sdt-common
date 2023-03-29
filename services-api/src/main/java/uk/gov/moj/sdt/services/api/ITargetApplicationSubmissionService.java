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
package uk.gov.moj.sdt.services.api;

import uk.gov.moj.sdt.domain.api.IIndividualRequest;

/**
 * This interface is for target application prior-submission and post-submission operations on
 * the individual request.
 *
 * @author Manoj Kulkarni
 */
public interface ITargetApplicationSubmissionService {
    /**
     * Process the SDT Request reference associated with the Individual Request.
     * This method will read the individual request associated with the SDT request reference,
     * mark the request as forwarded, send it to target application for processing,
     * update the response from the target application and finally mark the request as complete.
     * Additional check is done to see if all the individual requests associated with
     * the bulk submission request are processed to their final state i.e. Accepted or Rejected.
     *
     * @param sdtRequestReference the unique SDT Request Reference associated with individual request
     */
    void processRequestToSubmit(final String sdtRequestReference);

    /**
     * Performs action on the SDT Individual Request depending on the given request status.
     * If the given request status is FORWARDED, then re-sets the dead letter flag to false and leaves the request in
     * FORWARDED state. If the given request status is REJECTED, then re-sets the dead letter flag to false
     * and marks the request as REJECTED. An entry is made in the error log and performs an
     * check on the bulk submission record to check if all the individual requests are either
     * ACCEPTED or REJECTED and if so the bulk submission record is marked as COMPLETED.
     *
     * @param individualRequest the SDT Individual Request object.
     * @param requestStatus     the status to be updated for the request. Can be either FORWARDED or REJECTED.
     */
    void processDLQRequest(final IIndividualRequest individualRequest, final String requestStatus);

}
