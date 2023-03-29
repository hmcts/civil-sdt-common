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
package uk.gov.moj.sdt.dao.api;

import java.util.List;

import org.springframework.dao.DataAccessException;

import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;

/**
 * Interface for all classes implementing {@link IIndividualRequestDao}.
 *
 * @author d130680
 */
public interface IIndividualRequestDao extends IGenericDao {
    /**
     * Check the customer reference is unique across data retention period. Return the individual request if the check
     * fails or null if it succeeds.
     *
     * @param bulkCustomer      bulk customer
     * @param customerReference customer reference
     * @param dataRetention     the data retention period to use
     * @return null if the individual request is unique or the non unique individual request object
     * @throws DataAccessException JPA exception
     */
    IIndividualRequest getIndividualRequest(final IBulkCustomer bulkCustomer, final String customerReference,
                                            final int dataRetention) throws DataAccessException;

    /**
     * Returns the individual request object for the given Sdt Reference Id.
     *
     * @param sdtReferenceId the unique SDT reference Id
     * @return the Individual Request object associated with the Sdt reference Id.
     * @throws DataAccessException JPA exception
     */
    IIndividualRequest getRequestBySdtReference(final String sdtReferenceId) throws DataAccessException;

    /**
     * Returns a list of individual requests that are not yet processed by
     * the target application. The criteria used in this method is based on 3 conditions
     * a. the forwarding attempts greater than the max allowed (passed as parameter to the method)
     * b. the status of individual request is Forwarded.
     * c. the dead letter flag is false.
     *
     * @param maxAllowedAttempts - the maximum number of forwarding attempts allowed.
     * @return list of individual requests excluding any dead letter requests.
     * @throws DataAccessException JPA exception
     */
    List<IIndividualRequest> getPendingIndividualRequests(final int maxAllowedAttempts) throws DataAccessException;

    /**
     * Returns a list of individual requests that have been received and never forwarded with zero or more attempts to
     * forward them. This can happen if JMS goes down, in which case they may not have been successfully placed on the
     * queue. Since in normal processing only the MDB reattempts to forward requests, these are in effect lost to the
     * system. Manual intervention is needed to requeue them. Currently this method is called only by the mbean
     * responsible for manually requesting the forwarding of rejected messages.
     * Any requests that are flagged as dead letter are ignored.
     *
     * @param minimumAgeInMinutes - the minimum number of minutes since a message was updated before it will be
     *                            requeued.
     * @return list of individual requests excluding the dead letter requests.
     * @throws DataAccessException JPA exception
     */
    List<IIndividualRequest> getStaleIndividualRequests(final int minimumAgeInMinutes) throws DataAccessException;
}
