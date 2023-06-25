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

import java.io.Serializable;

import uk.gov.moj.sdt.services.messaging.api.ISdtMessage;

/**
 * Message class that holds the actual message text and additional information
 * to be put on the messaging queue.
 *
 * @author Manoj Kulkarni
 */
public class SdtMessage implements Serializable, ISdtMessage {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * This variable holds the sdtRequestReference of the individual request
     * that is to be queued on the message queue for further processing.
     */
    private String sdtRequestReference;

    /**
     * This variable holds the date and time that the message is put on the queue.
     * It is used for determining the rate at which messages are read back from
     * the queue.
     */
    private long messageSentTimestamp;

    /**
     * This variable holds performance logging id of the enqueuing thread.
     */
    private long enqueueLoggingId;

    private Boolean caseOffLine;

    @Override
    public String getSdtRequestReference() {
        return sdtRequestReference;
    }

    @Override
    public void setSdtRequestReference(final String sdtRequestReference) {
        this.sdtRequestReference = sdtRequestReference;
    }

    @Override
    public long getMessageSentTimestamp() {
        return messageSentTimestamp;
    }

    @Override
    public void setMessageSentTimestamp(final long messageSentTimestamp) {
        this.messageSentTimestamp = messageSentTimestamp;
    }

    @Override
    public long getEnqueueLoggingId() {
        return enqueueLoggingId;
    }

    @Override
    public void setEnqueueLoggingId(final long enqueueLoggingId) {
        this.enqueueLoggingId = enqueueLoggingId;
    }

    @Override
    public Boolean isCaseOffLine() {
        return caseOffLine;
    }

    @Override
    public void setCaseOffLine(Boolean caseOffLine) {
        this.caseOffLine = caseOffLine;
    }

    @Override
    public String toString() {
        return "SdtMessage [sdtRequestReference=" + sdtRequestReference + ", messageSentTimestamp=" +
                messageSentTimestamp + ", enqueueLoggingId=" + enqueueLoggingId + ", caseOffLine=" + caseOffLine + "]";
    }
}
