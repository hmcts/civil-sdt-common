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

package uk.gov.moj.sdt.domain.api;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for all classes implementing {@link IBulkSubmission}.
 *
 * @author Manoj Kulkarni
 */
public interface IBulkSubmission extends IDomainObject {

    /**
     * Get the Bulk Customer.
     *
     * @return bulk customer
     */
    IBulkCustomer getBulkCustomer();

    /**
     * Set the Bulk Customer.
     *
     * @param bulkCustomer bulk customer
     */
    void setBulkCustomer(final IBulkCustomer bulkCustomer);

    /**
     * Get the Target Application.
     *
     * @return target application
     */
    ITargetApplication getTargetApplication();

    /**
     * Set the Target Application.
     *
     * @param targetApplication target application
     */
    void setTargetApplication(final ITargetApplication targetApplication);

    /**
     * Get SDT bulk reference.
     *
     * @return SDT bulk reference
     */
    String getSdtBulkReference();

    /**
     * Set SDT bulk reference.
     *
     * @param sdtBulkReference SDT bulk reference
     */
    void setSdtBulkReference(final String sdtBulkReference);

    /**
     * Get customer reference.
     *
     * @return customer reference
     */
    String getCustomerReference();

    /**
     * Set customer reference.
     *
     * @param customerReference customer reference
     */
    void setCustomerReference(final String customerReference);

    /**
     * Get created date.
     *
     * @return created date
     */
    LocalDateTime getCreatedDate();

    /**
     * Set created date.
     *
     * @param localDateTime created dated
     */
    void setCreatedDate(final LocalDateTime localDateTime);

    /**
     * Get number of request.
     *
     * @return number of request
     */
    long getNumberOfRequest();

    /**
     * Set number of request.
     *
     * @param numberOfRequest number of request
     */
    void setNumberOfRequest(final long numberOfRequest);

    /**
     * Get submission status.
     *
     * @return submission status
     */
    String getSubmissionStatus();

    /**
     * Set submission status.
     *
     * @param submissionStatus submission status
     */
    void setSubmissionStatus(final String submissionStatus);

    /**
     * Get completed date.
     *
     * @return completed date
     */
    LocalDateTime getCompletedDate();

    /**
     * Set completed date.
     *
     * @param completedDate completed date
     */
    void setCompletedDate(final LocalDateTime completedDate);

    /**
     * Get updated date.
     *
     * @return updated date
     */
    LocalDateTime getUpdatedDate();

    /**
     * Set updated date.
     *
     * @param updatedDate updated date
     */
    void setUpdatedDate(final LocalDateTime updatedDate);

    /**
     * Get the XML payload.
     *
     * @return xml payload
     */
    byte[] getPayload();

    /**
     * Set the XML payload.
     *
     * @param bs xml payload
     */
    void setPayload(final byte[] bs);

    /**
     * Get list of individual request.
     *
     * @return list of individual requests
     */
    List<IIndividualRequest> getIndividualRequests();

    /**
     * Set list of individual request.
     *
     * @param individualRequests list of individual request
     */
    void setIndividualRequests(final List<IIndividualRequest> individualRequests);

    /**
     * Add given individual request.
     *
     * @param individualRequest individual request
     */
    void addIndividualRequest(final IIndividualRequest individualRequest);

    /**
     * Get service request.
     *
     * @return service request
     */
    IServiceRequest getServiceRequest();

    /**
     * Set service request.
     *
     * @param serviceRequest service request
     */
    void setServiceRequest(final IServiceRequest serviceRequest);

    /**
     * Get error text.
     *
     * @return error text
     */
    String getErrorText();

    /**
     * Set error text.
     *
     * @param errorText error text
     */
    void setErrorText(final String errorText);

    /**
     * Get error code.
     *
     * @return error code
     */
    String getErrorCode();

    /**
     * Set error code.
     *
     * @param errorCode error code
     */
    void setErrorCode(final String errorCode);

    /**
     * Error status.
     *
     * @return true if there are errors else false.
     */
    boolean hasError();

    /**
     * Marks the bulk submission as Validated unless the submission is in "Completed" status.
     */
    void markAsValidated();

    /**
     * Marks the bulk submission as completed.
     */
    void markAsCompleted();

    /**
     * The status of the Bulk Request - one of "Uploaded", "Failed", "
     * Validated", or "Completed" to reflect the current status of SDT processing. Maintained by SDT.
     *
     * @author d130680
     */
    public enum BulkRequestStatus {

        /**
         * Uploaded.
         */
        UPLOADED("Uploaded"),

        /**
         * Failed.
         */
        FAILED("Failed"),

        /**
         * Validated.
         */
        VALIDATED("Validated"),

        /**
         * Completed.
         */
        COMPLETED("Completed");

        /**
         * Bulk request status.
         */
        private String status;

        /**
         * Constructor.
         *
         * @param s status
         */
        private BulkRequestStatus(final String s) {

            this.status = s;
        }

        /**
         * Get the bulk request status.
         *
         * @return bulk request status
         */
        public String getStatus() {
            return status;
        }
    }

}
