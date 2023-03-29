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

/**
 * Interface for all classes implementing {@link CopyOfISdtMetricsMBean}.
 *
 * @author Robin Compston
 */
public interface ISdtMetricsMBean {
    /**
     * Get the current time.
     *
     * @return the formatted current time.
     */
    String getTime();

    /**
     * Get the current OS stats for CPU and memory.
     *
     * @return String containing OS stats.
     */
    String getOsStats();

    /**
     * Get the number of active customers using the system. Public because needed by handlers to reset unique customer
     * map.
     *
     * @return the number of active customers using the system.
     */
    long getActiveBulkCustomers();

    /**
     * Get summary of bulk submit statistics.
     *
     * @return summary of bulk submit statistics.
     */
    String getBulkSubmitStats();

    /**
     * Get summary of bulk feedback statistics.
     *
     * @return summary of bulk feedback statistics.
     */
    String getBulkFeedbackStats();

    /**
     * Get summary of submit query statistics.
     *
     * @return summary of submit query statistics.
     */
    String getSubmitQueryStats();

    /**
     * Get summary of status statistics.
     *
     * @return summary of status update statistics.
     */
    String getStatusUpdateStats();

    /**
     * Get the domain objects statistics.
     *
     * @return the domain objects statistics.
     */
    String getDomainObjectsStats();

    /**
     * Get the database call statistics.
     *
     * @return the database statistics.
     */
    String getDatabaseCallsStats();

    /**
     * Get the database read statistics.
     *
     * @return the database statistics.
     */
    String getDatabaseReadsStats();

    /**
     * Get the database write statistics.
     *
     * @return the database statistics.
     */
    String getDatabaseWritesStats();

    /**
     * Get the active customers statistics.
     *
     * @return the active customers statistics.
     */
    String getActiveCustomersStats();

    /**
     * Get the request queue statistics.
     *
     * @return the request queue statistics.
     */
    String getRequestQueueStats();

    /**
     * Get the target application response statistics.
     *
     * @return the target application response statistics.
     */
    String getTargetAppStats();

    /**
     * Get the error and exception statistics.
     *
     * @return the error and exception statistics.
     */
    String getErrorStats();

    /**
     * Get the last references assigned.
     *
     * @return the last references assigned.
     */
    String getLastRefStats();

    /**
     * Get the current active performance logging string.
     *
     * @return the current active performance logging string.
     */
    String getPerformanceLoggingString();

    /**
     * Get the current active performance logging flags.
     *
     * @return the current active performance logging flags.
     */
    short getPerformanceLoggingFlags();

    /**
     * Reset all metrics to initial value.
     */
    void reset();

    /**
     * Set the value of the performance logging flags.
     *
     * @param performanceLoggingFlags new value of performance logging flags.
     */
    void setPerformanceLoggingFlags(short performanceLoggingFlags);

    /**
     * Increment count of bulk submits.
     */
    void upBulkSubmitCount();

    /**
     * Add latest bulk submit time to total.
     *
     * @param bulkSubmitTime time to add to total bulk submit time.
     */
    void addBulkSubmitTime(final long bulkSubmitTime);

    /**
     * Increment count of bulk Feedbacks.
     */
    void upBulkFeedbackCount();

    /**
     * Add latest bulk Feedback time to total.
     *
     * @param bulkFeedbackTime time to add to total bulk feedback time.
     */
    void addBulkFeedbackTime(final long bulkFeedbackTime);

    /**
     * Increment count of submit querys.
     */
    void upSubmitQueryCount();

    /**
     * Add latest submit query time to total.
     *
     * @param submitQueryTime time to add to total submit query time.
     */
    void addSubmitQueryTime(final long submitQueryTime);

    /**
     * Increment count of status updates.
     */
    void upStatusUpdateCount();

    /**
     * Add latest status update time to total.
     *
     * @param statusUpdateTime time to add to total status update time.
     */
    void addStatusUpdateTime(final long statusUpdateTime);

    /**
     * Increment the domain objects count.
     */
    void upDomainObjectsCount();

    /**
     * Decrement the domain objects count.
     */
    void downDomainObjectsCount();

    /**
     * Increment the database reads count.
     */
    void upDatabaseReadsCount();

    /**
     * Add to the total database reads time.
     *
     * @param databaseReadsTime time to add to database reads time.
     */
    void addDatabaseReadsTime(final long databaseReadsTime);

    /**
     * Increment the database writes count.
     */
    void upDatabaseWritesCount();

    /**
     * Add to the total database writes time.
     *
     * @param databaseWritesTime time to add to database writes time.
     */
    void addDatabaseWritesTime(final long databaseWritesTime);

    /**
     * Increment the active bulk customers count.
     */
    void upActiveBulkCustomers();

    /**
     * Increment the request count.
     */
    void upRequestCount();

    /**
     * Increment the queued request count.
     */
    void upRequestQueueCount();

    /**
     * Add latest request queued time to total.
     *
     * @param requestQueuedTime latest request queued time to total.
     */
    void addRequestQueueTime(final long requestQueuedTime);

    /**
     * Increment request length.
     */
    void upRequestQueueLength();

    /**
     * Decrement request length.
     */
    void decrementRequestQueueLength();

    /**
     * Increment the request requeues count.
     */
    void upRequestRequeues();

    /**
     * Add latest target application response time to total.
     *
     * @param targetAppResponseTime latest target application response.
     */
    void addTargetAppResponseTime(final long targetAppResponseTime);

    /**
     * Increment the number of calls to target applications.
     */
    void upTargetAppCallCount();

    /**
     * Increment the number of times the target application was unavailable.
     */
    void upTargetAppUnavailable();

    /**
     * Increment the target application miscellaneous errors.
     */
    void upTargetAppMiscErrors();

    /**
     * Increment the target application SOAP errors.
     */
    void upTargetAppSoapErrors();

    /**
     * Increment the target application response timeout count.
     */
    void upTargetAppResponseTimeouts();

    /**
     * Increment the XML validation failure count.
     */
    void upXmlValidationFailureCount();

    /**
     * Increment the business exception count.
     */
    void upBusinessExceptionCount();

    /**
     * Set the last business exception encountered.
     *
     * @param lastBusinessException the last business exception.
     */
    void setLastBusinessException(final String lastBusinessException);

    /**
     * Set the last bulk submit reference assigned by the system.
     *
     * @param lastBulkSubmitRef the last bulk submit reference assigned.
     */
    void setLastBulkSubmitRef(final String lastBulkSubmitRef);

    /**
     * Set the last bulk request reference assigned by the system.
     *
     * @param lastBulkRequestRef the last bulk request reference assigned.
     */
    void setLastBulkRequestRef(final String lastBulkRequestRef);

    /**
     * Count the number of distinct web customers using the system since last reset.
     *
     * @param customer the unique identifier of the customer who called the web service.
     */
    void updateBulkCustomerCount(final String customer);

    /**
     * Write current value of metrics to a time-stamped file.
     */
    void dumpMetrics();
}
