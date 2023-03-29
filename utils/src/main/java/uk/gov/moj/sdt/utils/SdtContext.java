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

package uk.gov.moj.sdt.utils;

import uk.gov.moj.sdt.utils.logging.LoggingContext;
import uk.gov.moj.sdt.utils.logging.api.ILoggingContext;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class holding all thread specific context that needs to be passed around in
 * SDT.
 *
 * @author Robin Compston.
 */
public final class SdtContext {

    /**
     * Thread local holder available throughout thread.
     */
    private static final ThreadLocal<SdtContext> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * The state of current performance logging options.
     */
    private ILoggingContext loggingContext;

    /**
     * The output stream created by CXF which must be stored until we are ready to write the message.
     */
    private OutputStream originalOutputStream;

    /**
     * The raw inbound XML handled by CXF and stored for application use in
     * thread local memory.
     */
    private String rawInXml;

    /**
     * The raw outbound XML handled by CXF and stored for application use in
     * thread local memory.
     */
    private String rawOutXml;

    /**
     * The id of the service request row used to log the contents of the inbound and outbound XML. The outbound
     * intercepter has to read the inbound service request in order to add the outbound xml and it uses this property to
     * locate it.
     */
    private Long serviceRequestId;

    /**
     * The unique id assigned by SDT to the submit bulk request.
     */
    private String submitBulkReference;

    /**
     * List to store the synchronisation tasks (commands) that are be executed
     * by the message synchroniser when the transaction is committed.
     */
    private List<Runnable> synchronisationTasks;

    /**
     * Map to store the raw XML associated with the status of each individual
     * request in a bulk submission. This XML is returned by the case management
     * system and is specific to the case management system. SDT is a generic
     * systems and must remain agnostic about the contents of this status and
     * therefore deliberately treats it as raw XML. As a result, there are no
     * JAXB classes corresponding to this status and the status must be received
     * from the case management system and passed through the the bulk customer.
     * This is done my extracting this XML from the notification received from
     * the case management system, and storing it in the database, then on
     * receiving a bulk feedback request, reading it out of the database,
     * storing it in this map and then inserting it into the outbound XML.
     */
    private Map<String, String> targetApplicationRespMap = new HashMap<>();

    /**
     * Constructor for {@link com.sun.jmx.snmp.ThreadContext}.
     */
    private SdtContext() {
    }

    /**
     * Adds an task for synchronisation to the synchronisation list.
     *
     * @param command the runnable task for synchronisation.
     * @return boolean - returns true if the synchronisation list is not already initialized.
     */
    public boolean addSynchronisationTask(final Runnable command) {
        boolean returnValue = false;
        if (this.synchronisationTasks == null) {
            this.synchronisationTasks = new ArrayList<>();
            returnValue = true;
        }
        this.synchronisationTasks.add(command);

        return returnValue;
    }

    /**
     * Clears the synchronisation task list after the thread no longer needs it.
     */
    public void clearSynchronisationTasks() {
        if (this.synchronisationTasks != null) {
            this.synchronisationTasks.clear();
            this.synchronisationTasks = null;
        }

    }

    /**
     * Get the {@link LoggingContext} object.
     *
     * @return logging context.
     */
    public ILoggingContext getLoggingContext() {
        return loggingContext;
    }

    /**
     * Get the original output stream.
     *
     * @return the original output stream.
     */
    public OutputStream getOriginalOutputStream() {
        return originalOutputStream;
    }

    /**
     * Retrieve the XML that makes up the inbound message.
     *
     * @return xmlMessage the inbound message intercepted by CXF.
     */
    public String getRawInXml() {
        return rawInXml;
    }

    /**
     * Retrieve the XML that makes up the outbound message.
     *
     * @return xmlMessage the outbound message intercepted by CXF.
     */
    public String getRawOutXml() {
        return rawOutXml;
    }

    /**
     * Get the service request id allocated by hibernate when storing the incoming message.
     *
     * @return the serviceRequestId
     */
    public Long getServiceRequestId() {
        return serviceRequestId;
    }

    /**
     * Get the submitBulkReference.
     *
     * @return the submitBulkReference.
     */
    public String getSubmitBulkReference() {
        return submitBulkReference;
    }

    /**
     * get synchronisation tasks.
     *
     * @return the list of the synchronisation tasks.
     */
    public List<Runnable> getSynchronisationTasks() {
        return this.synchronisationTasks;
    }

    /**
     * Get map containing raw XML returned by the case management system for each request.
     *
     * @return map containing raw XML returned by the case management system for each request.
     */
    public Map<String, String> getTargetApplicationRespMap() {
        return targetApplicationRespMap;
    }

    /**
     * Clean up thread local.
     */
    public void remove() {
        THREAD_LOCAL.remove();
    }

    /**
     * Set the {@link ILoggingContext} object.
     *
     * @param loggingContext the logging context.
     */
    public void setLoggingContext(final ILoggingContext loggingContext) {
        this.loggingContext = loggingContext;
    }

    /**
     * Save the output stream for later retrieval.
     *
     * @param originalOutputStream the original stream created by CXF.
     */
    public void setOriginalOutputStream(final OutputStream originalOutputStream) {
        this.originalOutputStream = originalOutputStream;
    }

    /**
     * Set the XML that makes up the inbound message.
     *
     * @param rawInXml the inbound message intercepted by CXF.
     */
    public void setRawInXml(final String rawInXml) {
        this.rawInXml = rawInXml;
    }

    /**
     * Set the XML that makes up the outbound message.
     *
     * @param rawOutXml the outbound message intercepted by CXF.
     */
    public void setRawOutXml(final String rawOutXml) {
        this.rawOutXml = rawOutXml;
    }

    /**
     * Set.
     *
     * @param serviceRequestId the serviceRequestId to set
     */
    public void setServiceRequestId(final Long serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    /**
     * Set new value of submitBulkReference.
     *
     * @param submitBulkReference new value of submitBulkReference.
     */
    public void setSubmitBulkReference(final String submitBulkReference) {
        this.submitBulkReference = submitBulkReference;
    }

    /**
     * Set map containing raw XML returned by the case management system for
     * each request (taken from the SDT database).
     *
     * @param targetApplicationRespMap value of map containing raw XML returned by the case
     *                                 management system for each request
     */
    public void setTargetApplicationRespMap(final Map<String, String> targetApplicationRespMap) {
        this.targetApplicationRespMap = targetApplicationRespMap;
    }

    /**
     * Get the SdtContext for this thread.
     *
     * @return the SdtContext for this thread.
     */
    public static SdtContext getContext() {
        // Look to see if context already setup for this thread.
        SdtContext sdtContext = THREAD_LOCAL.get();

        // If not create it and store it in thread local storage.
        if (sdtContext == null) {
            sdtContext = new SdtContext();

            final ILoggingContext loggingContext = new LoggingContext();
            sdtContext.setLoggingContext(loggingContext);

            THREAD_LOCAL.set(sdtContext);
        }

        return sdtContext;
    }
}
