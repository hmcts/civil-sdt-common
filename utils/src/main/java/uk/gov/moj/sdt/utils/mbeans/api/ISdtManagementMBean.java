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

package uk.gov.moj.sdt.utils.mbeans.api;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Interface for all classes implementing {@link CopyOfISdtMetricsMBean}.
 *
 * @author Robin Compston
 */
public interface ISdtManagementMBean {
    /**
     * Get the current cache reset control.
     *
     * @return the cache reset number increments every time the cache is reset, tells caching classes to uncache.
     */
    int getCacheResetControl();

    /**
     * Allow Spring to wire up this bean.
     *
     * @param messageListenerContainer the value of the message listener container.
     */
    void setMessageListenerContainer(final DefaultMessageListenerContainer messageListenerContainer);

    /**
     * Mark all cache classes extending {@link AbstractCacheControl} as needing to uncache any cached content. It is the
     * responsibility of the implementation to notice this has been called and discard any cached items, forcing a
     * refresh from source.
     */
    void uncache();

    /**
     * Set the current size of the message driven beans pool.
     *
     * @param queueName the queue name for which the MDB pool size is to be set.
     * @param poolSize  new size of message driven bean pool
     * @return message indicating outcome.
     */
    String setMdbPoolSize(String queueName, int poolSize);

    /**
     * Requeue all individual requests that have not yet been sent to the target application and that were created
     * before a given time. These will have a status of RECEIVED (never sent to target application) or FORWARDED (sent
     * to target application but no response received).
     *
     * @param minimumAgeInMinutes minimum age in minutes of unforwarded messages to be requeued.
     */
    void requeueOldIndividualRequests(int minimumAgeInMinutes);

    /**
     * Performs action on the SDT Individual Request depending on the given request status.
     * If the given request status is FORWARDED, then re-sets the dead letter flag to false and leaves the request in
     * FORWARDED state. If the given request status is REJECTED, then re-sets the dead letter flag to false
     * and marks the request as REJECTED. An entry is made in the error log and performs an
     * check on the bulk submission record to check if all the individual requests are either
     * ACCEPTED or REJECTED and if so the bulk submission record is marked as COMPLETED.
     *
     * @param sdtRequestReference the SDT Individual Request Reference
     * @param requestStatus       the status to be updated for the request. Can be either FORWARDED or REJECTED.
     * @return error messages if there are any during the processing. If processing is successful, returns "OK"
     */
    String processDlqRequest(final String sdtRequestReference, final String requestStatus);
}
