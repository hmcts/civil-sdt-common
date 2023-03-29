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

package uk.gov.moj.sdt.utils.mbeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.utils.logging.PerformanceLogger;
import uk.gov.moj.sdt.utils.mbeans.api.ICustomerCounter;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtMetricsMBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A class to gather online metrics for SDT. This POJO is configured by Spring to be available as an MBean visible via
 * jconsole in the application server JVM.
 * <p>The following metrics are available:
 * service calls counts of each type,
 * - last web service time
 * - rate
 * - times,
 * - avg,
 * - max,
 * - min,
 * domain objects count,
 * database calls count,
 * - times,
 * - avg,
 * - min,
 * - max,
 * customer count,
 * bulk count,
 * request count,
 * numbers of complete bulks (all requests) sent,
 * request queue lengths,
 * requests on queue,
 * - times,
 * - avg,
 * - max,
 * - min,
 * timeouts,
 * target application unavailable,
 * requeues,
 * status updates
 * on bulk
 * on requests,
 * XML validation failure count,
 * validation errors,
 * business exceptions,
 * last business exception,
 * reset stats to zero on demand.
 * function to reload database global data on demand.
 * function to reload log4j config on demand.</p>
 *
 * @author Robin Compston
 */

@Component("SdtMetricsMBean")
public final class SdtMetricsMBean implements ISdtMetricsMBean {
    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SdtMetricsMBean.class);

    /**
     * The singleton instance of this class created by Spring.
     */
    private static SdtMetricsMBean thisBean;

    /**
     * The singleton instance of this class created by Spring.
     */
    private static final double MILLISECONDS = 1000;

    /**
     * Constant to convert nanoseconds to milliseconds.
     */
    private static final long NANO_TO_MILLI = 1000000;

    /**
     * Current active value of performance logging flags which control what performance logging points are active.
     */
    private short performanceLoggingFlags;

    /**
     * Count of all bulk submits.
     */
    private long bulkSubmitCount;

    /**
     * Time of last bulk submits web service call.
     */
    private long bulkSubmitLastTime;

    /**
     * Total processing time of all bulk submits.
     */
    private long bulkSubmitTime;

    /**
     * Minimum processing time of all bulk submits.
     */
    private long bulkSubmitTimeMin = Long.MAX_VALUE;

    /**
     * Maximum processing time of all bulk submits.
     */
    private long bulkSubmitTimeMax;

    /**
     * Count of all bulk feedback.
     */
    private long bulkFeedbackCount;

    /**
     * Time of last bulk Feedback web service call.
     */
    private long bulkFeedbackLastTime;

    /**
     * Total processing time of all bulk feedbacks.
     */
    private long bulkFeedbackTime;

    /**
     * Minimum processing time of all bulk feedbacks.
     */
    private long bulkFeedbackTimeMin = Long.MAX_VALUE;

    /**
     * Maximum processing time of all bulk feedbacks.
     */
    private long bulkFeedbackTimeMax;

    /**
     * Count of all submit query.
     */
    private long submitQueryCount;

    /**
     * Time of last submit query web service call.
     */
    private long submitQueryLastTime;

    /**
     * Total processing time of all submit querys.
     */
    private long submitQueryTime;

    /**
     * Minimum processing time of all submit querys.
     */
    private long submitQueryTimeMin = Long.MAX_VALUE;

    /**
     * Maximum processing time of all submit querys.
     */
    private long submitQueryTimeMax;

    /**
     * Count of all status updates.
     */
    private long statusUpdateCount;

    /**
     * Time of last status update web service call.
     */
    private long statusUpdateLastTime;

    /**
     * Total processing time of all status updates.
     */
    private long statusUpdateTime;

    /**
     * Minimum processing time of all status updates.
     */
    private long statusUpdateTimeMin = Long.MAX_VALUE;

    /**
     * Maximum processing time of all status updates.
     */
    private long statusUpdateTimeMax;

    /**
     * Number of requests in bulk submits.
     */
    private long requestCount;

    /**
     * Time of start or last reset.
     */
    private long resetTime;

    /**
     * Number of domain objects in existence.
     */
    private long domainObjectsCount;

    /**
     * Number of DAO calls.
     */
    private long databaseCallsCount;

    /**
     * Time calling database.
     */
    private long databaseCallsTime;

    /**
     * Maximum time calling database.
     */
    private long databaseCallsTimeMin = Long.MAX_VALUE;

    /**
     * Maximum time calling database.
     */
    private long databaseCallsTimeMax;

    /**
     * Number of DAO reads.
     */
    private long databaseReadsCount;

    /**
     * Time reading database.
     */
    private long databaseReadsTime;

    /**
     * Maximum time reading database.
     */
    private long databaseReadsTimeMin = Long.MAX_VALUE;

    /**
     * Maximum time reading database.
     */
    private long databaseReadsTimeMax;

    /**
     * Number of DAO writes.
     */
    private long databaseWritesCount;

    /**
     * Time writing database.
     */
    private long databaseWritesTime;

    /**
     * Maximum writing reading database.
     */
    private long databaseWritesTimeMin = Long.MAX_VALUE;

    /**
     * Maximum writing reading database.
     */
    private long databaseWritesTimeMax;

    /**
     * Number of active bulk customers.
     */
    private long activeBulkCustomers;

    /**
     * Number of requests queue count to target application.
     */
    private long requestQueueCount;

    /**
     * Time on queue waiting for request to be processed.
     */
    private long requestQueueTime;

    /**
     * Maximum time on queue waiting for request to be processed.
     */
    private long requestQueueTimeMin = Long.MAX_VALUE;

    /**
     * Maximum time on queue waiting for request to be processed.
     */
    private long requestQueueTimeMax;

    /**
     * Current request queue length.
     */
    private long requestQueueLength;

    /**
     * Maximum request queue length.
     */
    private long requestQueueLengthMax;

    /**
     * Number of request requeues following timeout or during recovery.
     */
    private long requestRequeues;

    /**
     * Number of calls to target applications.
     */
    private long targetAppCallCount;

    /**
     * Time for target application to respond.
     */
    private long targetAppResponseTime;

    /**
     * Minimum time for target application to respond.
     */
    private long targetAppResponseTimeMin = Long.MAX_VALUE;

    /**
     * Maximum time for target application to respond.
     */
    private long targetAppResponseTimeMax;

    /**
     * Number of times the target application was unavailable.
     */
    private long targetAppUnavailable;

    /**
     * Number of times the target application returned a miscellaneous error.
     */
    private long targetAppMiscErrors;

    /**
     * Number of times the target application returned a SOAP error.
     */
    private long targetAppSoapErrors;

    /**
     * Number of timeouts waiting for target application to respond.
     */
    private long targetAppResponseTimeouts;

    /**
     * Number of XML validation errors of incoming messages.
     */
    private long xmlValidationFailureCount;

    /**
     * Number of business exceptions.
     */
    private long businessExceptionCount;

    /**
     * Last business exceptions code.
     */
    private String lastBusinessException;

    /**
     * The last bulk submit reference assigned.
     */
    private String lastBulkSubmitRef;

    /**
     * The last bulk request reference assigned.
     */
    private String lastBulkRequestRef;

    /**
     * Date formatter for all dates.
     */
    private DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss.SSS");

    /**
     * Utility class for counting unique customers.
     */
    private ICustomerCounter customerCounter;

    /**
     * Constructor for {@link SdtMetricsMBean}. This called by Spring and should become the bean that all subsequent
     * calls to metrics use, hence is stores in thisBean, which is obtained by getMetrics ().
     */
    public SdtMetricsMBean() {
        SdtMetricsMBean.thisBean = this;

        // Set start time.
        this.resetTime = new GregorianCalendar().getTimeInMillis();
    }

    /**
     * Constructor for {@link SdtMetricsMBean} with discriminator parameter.
     *
     * @param doNotRegister discriminator parameter to identify construct which does not register bean in static. Only
     *                      Spring must do this so that mbean uses same bean that we use. Parameter is a marker and is
     *                      not used.
     */
    public SdtMetricsMBean(final boolean doNotRegister) {
    }

    // BULK SUBMISSION

    /**
     * Get current count of bulks submits since last reset.
     *
     * @return count of bulk submits.
     */
    private long getBulkSubmitCount() {
        return this.bulkSubmitCount;
    }

    @Override
    public void upBulkSubmitCount() {
        this.bulkSubmitCount += 1;

        this.bulkSubmitLastTime = new GregorianCalendar().getTimeInMillis();
    }

    /**
     * Get the number of individual requests.
     *
     * @return the number of individual requests.
     */
    private long getRequestCount() {
        return this.requestCount;
    }

    /**
     * Get total time for bulks submits since last reset.
     *
     * @return total time (milliseconds) for bulk submits.
     */
    private long getBulkSubmitTime() {
        return this.bulkSubmitTime;
    }

    /* (non-Javadoc)
     *
     * @see uk.gov.moj.sdt.utils.mbeans.IFred#addBulkSubmitTime(long) */
    @Override
    public void addBulkSubmitTime(final long bulkSubmitTime) {
        this.bulkSubmitTime += bulkSubmitTime;

        // Update the minimum if needed.
        if (bulkSubmitTime < this.bulkSubmitTimeMin) {
            this.bulkSubmitTimeMin = bulkSubmitTime;
        }

        // Update the maximum if needed.
        if (bulkSubmitTime > this.bulkSubmitTimeMax) {
            this.bulkSubmitTimeMax = bulkSubmitTime;
        }
    }

    /**
     * Get average time for bulks submits since last reset.
     *
     * @return average time (milliseconds) for bulk submits.
     */
    private long getBulkSubmitTimeAvg() {
        return (this.bulkSubmitCount == 0) ? 0 : this.bulkSubmitTime / this.bulkSubmitCount;
    }

    /**
     * Get minimum time for bulks submits since last reset.
     *
     * @return minimum time (milliseconds) for bulk submits.
     */
    private long getBulkSubmitTimeMin() {
        return this.bulkSubmitTimeMin;
    }

    /**
     * Get maximum time for bulks submits since last reset.
     *
     * @return maximum time (milliseconds) for bulk submits.
     */
    private long getBulkSubmitTimeMax() {
        return this.bulkSubmitTimeMax;
    }

    // BULK FEEDBACK

    /**
     * Get current count of bulks Feedbacks since last reset.
     *
     * @return count of bulk Feedbacks.
     */
    private long getBulkFeedbackCount() {
        return this.bulkFeedbackCount;
    }

    @Override
    public void upBulkFeedbackCount() {
        this.bulkFeedbackCount += 1;

        this.bulkFeedbackLastTime = new GregorianCalendar().getTimeInMillis();
    }

    /**
     * Get total time for bulks Feedbacks since last reset.
     *
     * @return total time (milliseconds) for bulk Feedbacks.
     */
    private long getBulkFeedbackTime() {
        return this.bulkFeedbackTime;
    }

    @Override
    public void addBulkFeedbackTime(final long bulkFeedbackTime) {
        this.bulkFeedbackTime += bulkFeedbackTime;

        // Update the minimum if needed.
        if (bulkFeedbackTime < this.bulkFeedbackTimeMin) {
            this.bulkFeedbackTimeMin = bulkFeedbackTime;
        }

        // Update the maximum if needed.
        if (bulkFeedbackTime > this.bulkFeedbackTimeMax) {
            this.bulkFeedbackTimeMax = bulkFeedbackTime;
        }
    }

    /**
     * Get average time for bulks Feedbacks since last reset.
     *
     * @return average time (milliseconds) for bulk Feedbacks.
     */
    private long getBulkFeedbackTimeAvg() {
        return (this.bulkFeedbackCount == 0) ? 0 : this.bulkFeedbackTime / this.bulkFeedbackCount;
    }

    /**
     * Get minimum time for bulks Feedbacks since last reset.
     *
     * @return minimum time (milliseconds) for bulk Feedbacks.
     */
    private long getBulkFeedbackTimeMin() {
        return this.bulkFeedbackTimeMin;
    }

    /**
     * Get maximum time for bulks Feedbacks since last reset.
     *
     * @return maximum time (milliseconds) for bulk Feedbacks.
     */
    private long getBulkFeedbackTimeMax() {
        return this.bulkFeedbackTimeMax;
    }

    // SUBMIT QUERY

    /**
     * Get current count of submit queries since last reset.
     *
     * @return count of submit querys.
     */
    private long getSubmitQueryCount() {
        return this.submitQueryCount;
    }

    @Override
    public void upSubmitQueryCount() {
        this.submitQueryCount += 1;

        this.submitQueryLastTime = new GregorianCalendar().getTimeInMillis();
    }

    /**
     * Get total time for submit queries since last reset.
     *
     * @return total time (milliseconds) for submit querys.
     */
    private long getSubmitQueryTime() {
        return this.submitQueryTime;
    }

    @Override
    public void addSubmitQueryTime(final long submitQueryTime) {
        this.submitQueryTime += submitQueryTime;

        // Update the minimum if needed.
        if (submitQueryTime < this.submitQueryTimeMin) {
            this.submitQueryTimeMin = submitQueryTime;
        }

        // Update the maximum if needed.
        if (submitQueryTime > this.submitQueryTimeMax) {
            this.submitQueryTimeMax = submitQueryTime;
        }
    }

    /**
     * Get average time for submit query feedbacks since last reset.
     *
     * @return average time (milliseconds) for submit querys.
     */
    private long getSubmitQueryTimeAvg() {
        return (this.submitQueryCount == 0) ? 0 : this.submitQueryTime / this.submitQueryCount;
    }

    /**
     * Get minimum time for submit query feedbacks since last reset.
     *
     * @return minimum time (milliseconds) for submit querys.
     */
    private long getSubmitQueryTimeMin() {
        return this.submitQueryTimeMin;
    }

    /**
     * Get maximum time for submit query feedbacks since last reset.
     *
     * @return maximum time (milliseconds) for submit querys.
     */
    private long getSubmitQueryTimeMax() {
        return this.submitQueryTimeMax;
    }

    // UPDATE REQUEST

    /**
     * Get current count of status updates since last reset.
     *
     * @return count of status updates.
     */
    private long getStatusUpdateCount() {
        return this.statusUpdateCount;
    }

    @Override
    public void upStatusUpdateCount() {
        this.statusUpdateCount += 1;

        this.statusUpdateLastTime = new GregorianCalendar().getTimeInMillis();
    }

    /**
     * Get total time for status updates since last reset.
     *
     * @return total time (milliseconds) for status updates.
     */
    private long getStatusUpdateTime() {
        return this.statusUpdateTime;
    }

    @Override
    public void addStatusUpdateTime(final long statusUpdateTime) {
        this.statusUpdateTime += statusUpdateTime;

        // Update the minimum if needed.
        if (statusUpdateTime < this.statusUpdateTimeMin) {
            this.statusUpdateTimeMin = statusUpdateTime;
        }

        // Update the maximum if needed.
        if (statusUpdateTime > this.statusUpdateTimeMax) {
            this.statusUpdateTimeMax = statusUpdateTime;
        }
    }

    /**
     * Get average time for status updates since last reset.
     *
     * @return average time (milliseconds) for status updates.
     */
    private long getStatusUpdateTimeAvg() {
        return (this.statusUpdateCount == 0) ? 0 : this.statusUpdateTime / this.statusUpdateCount;
    }

    /**
     * Get minimum time for status updates since last reset.
     *
     * @return minimum time (milliseconds) for status updates.
     */
    private long getStatusUpdateTimeMin() {
        return this.statusUpdateTimeMin;
    }

    /**
     * Get maximum time for status updates since last reset.
     *
     * @return maximum time (milliseconds) for status updates.
     */
    private long getStatusUpdateTimeMax() {
        return this.statusUpdateTimeMax;
    }

    /**
     * Get the number of domain objects.
     *
     * @return the number of domain objects.
     */
    private long getDomainObjectsCount() {
        return this.domainObjectsCount;
    }

    /**
     * Get the number of database (Hibernate) calls.
     *
     * @return the number of database (Hibernate) calls.
     */
    private long getDatabaseCallsCount() {
        return this.databaseCallsCount;
    }

    /**
     * Get the time spent calling the database (Hibernate).
     *
     * @return the time spent calling the database (Hibernate).
     */
    private long getDatabaseCallsTime() {
        return this.databaseCallsTime;
    }

    /**
     * Get average time for submit queries since last reset.
     *
     * @return average time (milliseconds) for submit querys.
     */
    private long getDatabaseCallsTimeAvg() {
        return (this.databaseCallsCount == 0) ? 0 : this.databaseCallsTime / this.databaseCallsCount;
    }

    /**
     * Get the minimum time spent calling the database (Hibernate).
     *
     * @return the minimum time spent calling the database (Hibernate).
     */
    private long getDatabaseCallsTimeMin() {
        return this.databaseCallsTimeMin;
    }

    /**
     * Get the maximum time spent calling the database (Hibernate).
     *
     * @return the maximum time spent calling the database (Hibernate).
     */
    private long getDatabaseCallsTimeMax() {
        return this.databaseCallsTimeMax;
    }

    /**
     * Get the number of database (Hibernate) Reads.
     *
     * @return the number of database (Hibernate) Reads.
     */
    private long getDatabaseReadsCount() {
        return this.databaseReadsCount;
    }

    /**
     * Get the time spent reading the database (Hibernate).
     *
     * @return the time spent reading the database (Hibernate).
     */
    private long getDatabaseReadsTime() {
        return this.databaseReadsTime;
    }

    /**
     * Get average time for submit queries since last reset.
     *
     * @return average time (milliseconds) for submit querys.
     */
    private long getDatabaseReadsTimeAvg() {
        return (this.databaseReadsCount == 0) ? 0 : this.databaseReadsTime / this.databaseReadsCount;
    }

    /**
     * Get the minimum time spent reading the database (Hibernate).
     *
     * @return the minimum time spent reading the database (Hibernate).
     */
    private long getDatabaseReadsTimeMin() {
        return this.databaseReadsTimeMin;
    }

    /**
     * Get the maximum time spent reading the database (Hibernate).
     *
     * @return the maximum time spent reading the database (Hibernate).
     */
    private long getDatabaseReadsTimeMax() {
        return this.databaseReadsTimeMax;
    }

    /**
     * Get the number of database (Hibernate) Writes.
     *
     * @return the number of database (Hibernate) Writes.
     */
    private long getDatabaseWritesCount() {
        return this.databaseWritesCount;
    }

    /**
     * Get the time spent Writeing the database (Hibernate).
     *
     * @return the time spent Writeing the database (Hibernate).
     */
    private long getDatabaseWritesTime() {
        return this.databaseWritesTime;
    }

    /**
     * Get average time for submit queries since last reset.
     *
     * @return average time (milliseconds) for submit querys.
     */
    private long getDatabaseWritesTimeAvg() {
        return (this.databaseWritesCount == 0) ? 0 : this.databaseWritesTime / this.databaseWritesCount;
    }

    /**
     * Get the minimum time spent Writeing the database (Hibernate).
     *
     * @return the minimum time spent Writeing the database (Hibernate).
     */
    private long getDatabaseWritesTimeMin() {
        return this.databaseWritesTimeMin;
    }

    /**
     * Get the maximum time spent Writeing the database (Hibernate).
     *
     * @return the maximum time spent Writeing the database (Hibernate).
     */
    private long getDatabaseWritesTimeMax() {
        return this.databaseWritesTimeMax;
    }

    /**
     * Get the request queue count.
     *
     * @return the request queue count.
     */
    private long getRequestQueueCount() {
        return this.requestQueueCount;
    }

    /**
     * Get the total time spent on request queue.
     *
     * @return the total time spent on request queue.
     */
    private long getRequestQueueTime() {
        return this.requestQueueTime;
    }

    /**
     * Get average time for submit queries since last reset.
     *
     * @return average time (milliseconds) for submit querys.
     */
    private long getRequestQueueTimeAvg() {
        return (this.requestQueueCount == 0) ? 0 : this.requestQueueTime / this.requestQueueCount;
    }

    /**
     * Get the minimum time spent on request queue.
     *
     * @return the minimum time spent on request queue.
     */
    private long getRequestQueueTimeMin() {
        return this.requestQueueTimeMin;
    }

    /**
     * Get the maximum time spent on request queue.
     *
     * @return the maximum time spent on request queue.
     */
    private long getRequestQueueTimeMax() {
        return this.requestQueueTimeMax;
    }

    /**
     * Get the current request queue length.
     *
     * @return the current request queue length.
     */
    private long getRequestQueueLength() {
        return this.requestQueueLength;
    }

    /**
     * Get the maximum request queue length.
     *
     * @return the maximum request queue length.
     */
    private long getRequestQueueLengthMax() {
        return this.requestQueueLengthMax;
    }

    /**
     * Get the number of requests requeues.
     *
     * @return the number of requests requeues.
     */
    private long getRequestRequeues() {
        return this.requestRequeues;
    }

    /**
     * Get the number of calls to target applications.
     *
     * @return the number of calls to target applications.
     */
    private long getTargetAppCallCount() {
        return this.targetAppCallCount;
    }

    /**
     * Get the total response time for the target application.
     *
     * @return the total response time for the target application.
     */
    private long getTargetAppResponseTime() {
        return this.targetAppResponseTime;
    }

    /**
     * Get average time for target application response since last reset.
     *
     * @return average time for target application response.
     */
    private long getTargetAppResponseTimeAvg() {
        return (this.targetAppCallCount == 0) ? 0 : this.targetAppResponseTime / this.targetAppCallCount;
    }

    /**
     * Get the minimum response time for the target application.
     *
     * @return the minimum response time for the target application.
     */
    private long getTartegAppResponseTimeMin() {
        return this.targetAppResponseTimeMin;
    }

    /**
     * Get the maximum response time for the target application.
     *
     * @return the maximum response time for the target application.
     */
    private long getTargetAppResponseTimeMax() {
        return this.targetAppResponseTimeMax;
    }

    /**
     * Get the number of times the target application was unavailable.
     *
     * @return the number of times the target application was unavailable.
     */
    private long getTargetAppUnavailable() {
        return this.targetAppUnavailable;
    }

    /**
     * Get the number of times the target application errored.
     *
     * @return the number of times the target application errored.
     */
    private long getTargetAppMiscErrors() {
        return this.targetAppMiscErrors;
    }

    /**
     * Get the number of times the target application encountered SOAP error.
     *
     * @return the number of times the target application encountered SOAP error.
     */
    private long getTargetAppSoapErrors() {
        return this.targetAppSoapErrors;
    }

    /**
     * Get the number of target application response timeouts.
     *
     * @return the number of target application response timeouts.
     */
    private long getTargetAppResponseTimeouts() {
        return this.targetAppResponseTimeouts;
    }

    /**
     * Get the number of XML validation errors.
     *
     * @return the number of XML validation errors.
     */
    private long getXmlValidationFailureCount() {
        return this.xmlValidationFailureCount;
    }

    /**
     * Get the number of business exceptions.
     *
     * @return the number of business exceptions.
     */
    private long getBusinessExceptionCount() {
        return this.businessExceptionCount;
    }

    /**
     * Get the number of seconds since start or the last reset.
     *
     * @return the number of business exceptions.
     */
    private long getElapsedTime() {
        // Get current time
        final long now = new GregorianCalendar().getTimeInMillis();

        return now - this.getResetTime();
    }

    /**
     * Get the number of seconds since start or the last reset.
     *
     * @return the number of business exceptions.
     */
    private long getResetTime() {
        return this.resetTime;
    }

    /**
     * Get the formatted rate for the count over the elapsed time.
     *
     * @param count       the count during the the elapsed time.
     * @param elapsedTime the period in which to measure the rate.
     * @return the formatted rate for the count over the elapsed time.
     */
    private String getRate(final long count, final long elapsedTime) {
        final DecimalFormat df = new DecimalFormat("#0.00000");

        return df.format(count / (elapsedTime / MILLISECONDS));
    }

    /**
     * Get the number of business exceptions.
     *
     * @return the number of business exceptions.
     */
    private String getLastBusinessException() {
        return this.lastBusinessException;
    }

    /**
     * Get the unique customer counter.
     *
     * @return customer counter reference.
     */
    private ICustomerCounter getCustomerCounter() {
        return customerCounter;
    }

    /**
     * Set the unique customer counter.
     *
     * @param customerCounter new value of customer counter.
     */
    public void setCustomerCounter(final ICustomerCounter customerCounter) {
        this.customerCounter = customerCounter;
    }

    @Override
    public void upDomainObjectsCount() {
        this.domainObjectsCount += 1;
    }

    @Override
    public void downDomainObjectsCount() {
        this.domainObjectsCount -= 1;

        // Time problems means that this can go negative since beans created before Spring had finished initialising are
        // never recorded. Avoid this.
        if (this.domainObjectsCount < 0) {
            this.domainObjectsCount = 0;
        }
    }

    @Override
    public void upDatabaseReadsCount() {
        this.databaseCallsCount += 1;
        this.databaseReadsCount += 1;
    }

    @Override
    public void addDatabaseReadsTime(final long databaseReadsTime) {
        this.databaseReadsTime += databaseReadsTime;

        // Update the minimum if needed.
        if (databaseReadsTime < this.databaseReadsTimeMin) {
            this.databaseReadsTimeMin = databaseReadsTime;
        }

        // Update the maximum if needed.
        if (databaseReadsTime > this.databaseReadsTimeMax) {
            this.databaseReadsTimeMax = databaseReadsTime;
        }

        this.databaseCallsTime += databaseReadsTime;

        // Update the minimum if needed.
        if (databaseReadsTime < this.databaseCallsTimeMin) {
            this.databaseCallsTimeMin = databaseReadsTime;
        }

        // Update the maximum if needed.
        if (databaseReadsTime > this.databaseCallsTimeMax) {
            this.databaseCallsTimeMax = databaseReadsTime;
        }
    }

    @Override
    public void upDatabaseWritesCount() {
        this.databaseCallsCount += 1;
        this.databaseWritesCount += 1;
    }

    @Override
    public void addDatabaseWritesTime(final long databaseWritesTime) {
        this.databaseWritesTime += databaseWritesTime;

        // Update the minimum if needed.
        if (databaseWritesTime < this.databaseWritesTimeMin) {
            this.databaseWritesTimeMin = databaseWritesTime;
        }

        // Update the maximum if needed.
        if (databaseWritesTime > this.databaseWritesTimeMax) {
            this.databaseWritesTimeMax = databaseWritesTime;
        }

        this.databaseCallsTime += databaseWritesTime;

        // Update the minimum if needed.
        if (databaseWritesTime < this.databaseCallsTimeMin) {
            this.databaseCallsTimeMin = databaseWritesTime;
        }

        // Update the maximum if needed.
        if (databaseWritesTime > this.databaseCallsTimeMax) {
            this.databaseCallsTimeMax = databaseWritesTime;
        }
    }

    @Override
    public void upActiveBulkCustomers() {
        this.activeBulkCustomers += 1;
    }

    @Override
    public void upRequestCount() {
        this.requestCount += 1;
    }

    @Override
    public void upRequestQueueCount() {
        this.requestQueueCount += 1;
    }

    @Override
    public void addRequestQueueTime(final long requestQueueWriteTime) {
        // Find how long the message has been on the queue.
        final long requestQueueReadTime = new GregorianCalendar().getTimeInMillis();
        final long elapsedTime = requestQueueReadTime - requestQueueWriteTime;

        // Add to total time on queue of all messages.
        this.requestQueueTime += elapsedTime;

        // Update the minimum if needed.
        if (elapsedTime < this.requestQueueTimeMin) {
            this.requestQueueTimeMin = elapsedTime;
        }

        // Update the maximum if needed.
        if (elapsedTime > this.requestQueueTimeMax) {
            this.requestQueueTimeMax = elapsedTime;
        }
    }

    @Override
    public void upRequestQueueLength() {
        this.requestQueueLength += 1;
        LOGGER.debug("Upping queue length to {}", this.requestQueueLength);

        if (this.requestQueueLength > this.requestQueueLengthMax) {
            this.requestQueueLengthMax = this.requestQueueLength;
        }
    }

    @Override
    public void decrementRequestQueueLength() {
        this.requestQueueLength -= 1;
        LOGGER.debug("Decrementing queue length to {}", this.requestQueueLength);
    }

    @Override
    public void upRequestRequeues() {
        this.requestRequeues += 1;
    }

    @Override
    public void addTargetAppResponseTime(final long targetAppResponseTime) {
        this.targetAppResponseTime += targetAppResponseTime;

        // Update the minimum if needed.
        if (targetAppResponseTime < this.targetAppResponseTimeMin) {
            this.targetAppResponseTimeMin = targetAppResponseTime;
        }

        // Update the maximum if needed.
        if (targetAppResponseTime > this.targetAppResponseTimeMax) {
            this.targetAppResponseTimeMax = targetAppResponseTime;
        }
    }

    @Override
    public void upTargetAppCallCount() {
        this.targetAppCallCount++;
    }

    @Override
    public void upTargetAppUnavailable() {
        this.targetAppUnavailable += 1;
    }

    @Override
    public void upTargetAppMiscErrors() {
        this.targetAppMiscErrors += 1;
    }

    @Override
    public void upTargetAppSoapErrors() {
        this.targetAppSoapErrors += 1;
    }

    @Override
    public void upTargetAppResponseTimeouts() {
        this.targetAppResponseTimeouts += 1;
    }

    @Override
    public void upXmlValidationFailureCount() {
        this.xmlValidationFailureCount += 1;
    }

    @Override
    public void upBusinessExceptionCount() {
        this.businessExceptionCount += 1;
    }

    @Override
    public void setLastBusinessException(final String lastBusinessException) {
        this.lastBusinessException = lastBusinessException;
    }

    @Override
    public void setLastBulkSubmitRef(final String lastBulkSubmitRef) {
        this.lastBulkSubmitRef = lastBulkSubmitRef;
    }

    @Override
    public void setLastBulkRequestRef(final String lastBulkRequestRef) {
        this.lastBulkRequestRef = lastBulkRequestRef;
    }

    @Override
    public void reset() {
        LOGGER.debug("Resetting SDT metrics");

        this.bulkSubmitCount = 0;
        this.bulkSubmitLastTime = 0;
        this.bulkSubmitTime = 0;
        this.bulkSubmitTimeMin = Long.MAX_VALUE;
        this.bulkSubmitTimeMax = 0;
        this.bulkFeedbackCount = 0;
        this.bulkFeedbackLastTime = 0;
        this.bulkFeedbackTime = 0;
        this.bulkFeedbackTimeMin = Long.MAX_VALUE;
        this.bulkFeedbackTimeMax = 0;
        this.submitQueryCount = 0;
        this.submitQueryLastTime = 0;
        this.submitQueryTime = 0;
        this.submitQueryTimeMin = Long.MAX_VALUE;
        this.submitQueryTimeMax = 0;
        this.statusUpdateCount = 0;
        this.statusUpdateLastTime = 0;
        this.statusUpdateTime = 0;
        this.statusUpdateTimeMin = Long.MAX_VALUE;
        this.statusUpdateTimeMax = 0;
        this.requestCount = 0;
        this.resetTime = 0;
        this.domainObjectsCount = 0;
        this.databaseCallsCount = 0;
        this.databaseCallsTime = 0;
        this.databaseCallsTimeMin = Long.MAX_VALUE;
        this.databaseCallsTimeMax = 0;
        this.databaseReadsCount = 0;
        this.databaseReadsTime = 0;
        this.databaseReadsTimeMin = Long.MAX_VALUE;
        this.databaseReadsTimeMax = 0;
        this.databaseWritesCount = 0;
        this.databaseWritesTime = 0;
        this.databaseWritesTimeMin = Long.MAX_VALUE;
        this.databaseWritesTimeMax = 0;
        this.activeBulkCustomers = 0;
        this.requestQueueCount = 0;
        this.requestQueueTime = 0;
        this.requestQueueTimeMin = Long.MAX_VALUE;
        this.requestQueueTimeMax = 0;
        // Do not reset this - this.requestQueueLength = 0;
        this.requestQueueLengthMax = 0;
        this.requestRequeues = 0;
        this.targetAppCallCount = 0;
        this.targetAppResponseTime = 0;
        this.targetAppResponseTimeMin = Long.MAX_VALUE;
        this.targetAppResponseTimeMax = 0;
        this.targetAppUnavailable = 0;
        this.targetAppMiscErrors = 0;
        this.targetAppSoapErrors = 0;
        this.targetAppResponseTimeouts = 0;
        this.xmlValidationFailureCount = 0;
        this.businessExceptionCount = 0;
        this.lastBusinessException = "";
        // Do not reset this - this.lastBulkSubmitRef = "";
        // Do not reset this - this.lastBulkRequestRef = "";
        this.resetTime = new GregorianCalendar().getTimeInMillis();
    }

    @Override
    public long getActiveBulkCustomers() {
        return this.activeBulkCustomers;
    }

    @Override
    public String getBulkSubmitStats() {

        return "Bulk submits: count[" + this.getBulkSubmitCount() + "], last["
                + this.formatter.format(bulkSubmitLastTime) + "], rate["
                + this.getRate(this.getBulkSubmitCount(), this.getElapsedTime()) + "], requests["
                + this.getRequestCount() + "], rate[" + this.getRate(this.getRequestCount(), this.getElapsedTime())
                + "], time[" + this.getBulkSubmitTime() + "], average[" + this.getBulkSubmitTimeAvg() + "], minimum["
                + this.getBulkSubmitTimeMin() + "], maximum[" + this.getBulkSubmitTimeMax() + "]";
    }

    @Override
    public String getTime() {
        // Format time.
        final Date date = new Date();
        return "Current time[" + this.formatter.format(date) + "], last reset time["
                + this.formatter.format(this.getResetTime()) + "]";
    }

    @Override
    public String getOsStats() {
        final java.lang.management.OperatingSystemMXBean o = ManagementFactory.getOperatingSystemMXBean();
        if (!(o instanceof com.sun.management.OperatingSystemMXBean)) {
            return "";
        }

        final com.sun.management.OperatingSystemMXBean osMxBean = (com.sun.management.OperatingSystemMXBean) o;
        final RuntimeMXBean rtMxBean = ManagementFactory.getRuntimeMXBean();

        // Format CPU time.
        String cpuTime = "";
        if (osMxBean != null) {
            cpuTime = formatTime(osMxBean.getProcessCpuTime() / NANO_TO_MILLI);
        }

        // Format up time.
        String upTime = "";
        if (rtMxBean != null) {
            upTime = formatTime(rtMxBean.getUptime());
        }

        return "Operating system: number of processors[" + osMxBean.getAvailableProcessors() + "], process cpu time["
                + cpuTime + "], up time[" + upTime + "], total physical memory["
                + osMxBean.getTotalPhysicalMemorySize() + "], free physical memory["
                + osMxBean.getFreePhysicalMemorySize() + "]";
    }

    @Override
    public String getBulkFeedbackStats() {
        return "Bulk feedbacks: count[" + this.getBulkFeedbackCount() + "], last["
                + this.formatter.format(bulkFeedbackLastTime) + "], rate["
                + this.getRate(this.getBulkFeedbackCount(), this.getElapsedTime()) + "], time["
                + this.getBulkFeedbackTime() + "], average[" + this.getBulkFeedbackTimeAvg() + "], minimum["
                + this.getBulkFeedbackTimeMin() + "], maximum[" + this.getBulkFeedbackTimeMax() + "]";
    }

    @Override
    public String getSubmitQueryStats() {
        return "Submit queries: count[" + this.getSubmitQueryCount() + "], last["
                + this.formatter.format(submitQueryLastTime) + "], rate["
                + this.getRate(this.getSubmitQueryCount(), this.getElapsedTime()) + "], time["
                + this.getSubmitQueryTime() + "], average[" + this.getSubmitQueryTimeAvg() + "], minimum["
                + this.getSubmitQueryTimeMin() + "], maximum[" + this.getSubmitQueryTimeMax() + "]";
    }

    @Override
    public String getStatusUpdateStats() {
        return "Status updates: count[" + this.getStatusUpdateCount() + "], last["
                + this.formatter.format(statusUpdateLastTime) + "], rate["
                + this.getRate(this.getStatusUpdateCount(), this.getElapsedTime()) + "], time["
                + this.getStatusUpdateTime() + "], average[" + this.getStatusUpdateTimeAvg() + "], minimum["
                + this.getStatusUpdateTimeMin() + "], maximum[" + this.getStatusUpdateTimeMax() + "]";
    }

    @Override
    public String getDomainObjectsStats() {
        return "Domain objects: count[" + this.getDomainObjectsCount() + "]";
    }

    @Override
    public String getDatabaseCallsStats() {
        return "Database calls: count[" + this.getDatabaseCallsCount() + "], rate["
                + this.getRate(this.getDatabaseCallsCount(), this.getElapsedTime()) + "], time["
                + this.getDatabaseCallsTime() + "], average[" + this.getDatabaseCallsTimeAvg() + "], minimum["
                + this.getDatabaseCallsTimeMin() + "], maximum[" + this.getDatabaseCallsTimeMax() + "]";
    }

    @Override
    public String getDatabaseReadsStats() {
        return "Database reads: count[" + this.getDatabaseReadsCount() + "], rate["
                + this.getRate(this.getDatabaseReadsCount(), this.getElapsedTime()) + "], time["
                + this.getDatabaseReadsTime() + "], average[" + this.getDatabaseReadsTimeAvg() + "], minimum["
                + this.getDatabaseReadsTimeMin() + "], maximum[" + this.getDatabaseReadsTimeMax() + "]";
    }

    @Override
    public String getDatabaseWritesStats() {
        return "Database writes: count[" + this.getDatabaseWritesCount() + "], rate["
                + this.getRate(this.getDatabaseWritesCount(), this.getElapsedTime()) + "], time["
                + this.getDatabaseWritesTime() + "], average[" + this.getDatabaseWritesTimeAvg() + "], minimum["
                + this.getDatabaseWritesTimeMin() + "], maximum[" + this.getDatabaseWritesTimeMax() + "]";
    }

    @Override
    public String getActiveCustomersStats() {
        return "Active bulk customers: count[" + this.getActiveBulkCustomers() + "]";
    }

    @Override
    public String getRequestQueueStats() {
        return "Request queue: count[" + this.getRequestQueueCount() + "], rate["
                + this.getRate(this.getRequestQueueCount(), this.getElapsedTime()) + "], time["
                + this.getRequestQueueTime() + "], average[" + this.getRequestQueueTimeAvg() + "], minimum["
                + this.getRequestQueueTimeMin() + "], maximum[" + this.getRequestQueueTimeMax()
                + "], current queue length[" + this.getRequestQueueLength() + "], max queue length["
                + this.getRequestQueueLengthMax() + "], requeues[" + this.getRequestRequeues() + "]";
    }

    @Override
    public String getTargetAppStats() {
        return "Target App Calls: count[" + this.getTargetAppCallCount() + "], rate["
                + this.getRate(this.getTargetAppCallCount(), this.getElapsedTime()) + "], time["
                + this.getTargetAppResponseTime() + "], average[" + this.getTargetAppResponseTimeAvg() + "], minimum["
                + this.getTartegAppResponseTimeMin() + "], maximum[" + this.getTargetAppResponseTimeMax()
                + "], timeouts[" + this.getTargetAppResponseTimeouts() + "], unavailable["
                + this.getTargetAppUnavailable() + "], misc errors[" + this.getTargetAppMiscErrors()
                + "], soap errors[" + this.getTargetAppSoapErrors() + "]";
    }

    @Override
    public String getErrorStats() {
        return "Errors and exceptions: xml validation errors[" + this.getXmlValidationFailureCount()
                + "], business exceptions[" + this.getBusinessExceptionCount() + "], last business exception["
                + this.getLastBusinessException() + "]";
    }

    /**
     * Get the last bulk submission reference assigned.
     *
     * @return the last bulk submission reference.
     */
    private String getLastBulkSubmitRef() {
        return this.lastBulkSubmitRef;
    }

    /**
     * Get the last bulk request reference assigned.
     *
     * @return the last bulk request reference.
     */
    private String getLastBulkRequestRef() {
        return this.lastBulkRequestRef;
    }

    @Override
    public String getLastRefStats() {
        return "References: last bulk submit reference[" + this.getLastBulkSubmitRef()
                + "], last bulk request reference[" + this.getLastBulkRequestRef() + "]";
    }

    @Override
    public String getPerformanceLoggingString() {
        return "Performance logging flags: "
                + PerformanceLogger.pad(Integer.toBinaryString(this.getPerformanceLoggingFlags()), true,
                "0", PerformanceLogger.FLAG_BIT_LENGTH);
    }

    @Override
    public short getPerformanceLoggingFlags() {
        return this.performanceLoggingFlags;
    }

    @Override
    public void setPerformanceLoggingFlags(final short performanceLoggingFlags) {
        this.performanceLoggingFlags = performanceLoggingFlags;
    }

    @Override
    public void updateBulkCustomerCount(final String customer) {
        getCustomerCounter().updateBulkCustomerCount(customer);
    }

    /**
     * Get singleton instance of this bean - used by callers to update statistics. Note that if Spring initialisation is
     * not complete the version of the constructor that sets up McolMetricsMBean.thisBean will not have been called and
     * this method will return a temporary mbean which will satisfy caller but will result in lost early statistics -
     * this is acceptable.
     *
     * @return singleton instance of this bean.
     */
    public static SdtMetricsMBean getMetrics() {
        if (SdtMetricsMBean.thisBean == null) {
            // Keep caller happy with throw away metrics - these stats will be lost - Spring not yet inititalised.
            final SdtMetricsMBean sdtMetricsMBean = new SdtMetricsMBean(true);
            sdtMetricsMBean.setCustomerCounter(new CustomerCounter());
            SdtMetricsMBean.thisBean = sdtMetricsMBean;
        }

        return SdtMetricsMBean.thisBean;
    }

    @Override
    public void dumpMetrics() {
        final Date date = new Date();

        try {
            String filename = "sdt.metrics." + this.formatter.format(date) + ".txt";
            filename = filename.replace(':', '.');
            final File file = new File(filename);
            final BufferedWriter output = new BufferedWriter(new FileWriter(file));

            // Dump the various metrics to the file.
            output.write(getTime() + "\n");
            output.write(getOsStats() + "\n");
            output.write(getActiveCustomersStats() + "\n");
            output.write(getBulkSubmitStats() + "\n");
            output.write(getRequestQueueStats() + "\n");
            output.write(getLastRefStats() + "\n");
            output.write(getTargetAppStats() + "\n");
            output.write(getBulkFeedbackStats() + "\n");
            output.write(getSubmitQueryStats() + "\n");
            output.write(getStatusUpdateStats() + "\n");
            output.write(getDomainObjectsStats() + "\n");
            output.write(getDatabaseCallsStats() + "\n");
            output.write(getDatabaseReadsStats() + "\n");
            output.write(getDatabaseWritesStats() + "\n");
            output.write(getErrorStats() + "\n");
            output.write(getPerformanceLoggingString() + "\n");
            output.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Format milliseconds into hours, minutes, seconds and milliseconds.
     *
     * @param timeInMilliseconds time in milliseconds to be formatted.
     * @return formatted time.
     */
    private String formatTime(final long timeInMilliseconds) {
        // CHECKSTYLE:OFF
        long hours = timeInMilliseconds / 3600000;
        long minutes = timeInMilliseconds / 60000 % 60;
        long seconds = timeInMilliseconds / 1000 % 60;
        long milliseconds = timeInMilliseconds % 1000;

        return pad(hours, 2) + ":" + pad(minutes, 2) + ":" + pad(seconds, 2) + "."
                + pad(milliseconds, 3);
        // CHECKSTYLE:ON
    }

    /**
     * Pad a number to a given number of characters with leading zeros.
     *
     * @param number number of be padded.
     * @param size   resulting size of padded number.
     * @return padded number.
     */
    private String pad(final long number, final int size) {
        StringBuilder result = new StringBuilder(Long.toString(number));
        for (int i = result.length(); i < size; i++) {
            result = new StringBuilder("0").append(result);
        }
        return result.toString();
    }
}
