package uk.gov.moj.sdt.domain.api;

import java.time.LocalDateTime;

/**
 * Interface for all classes implementing {@link IIndividualRequest}.
 *
 * @author Manoj Kulkarni
 */
public interface IIndividualRequest extends IDomainObject {

    /**
     * Get the Bulk Submission.
     *
     * @return bulk submission
     */
    IBulkSubmission getBulkSubmission();

    /**
     * Set the Bulk Submission.
     *
     * @param bulkSubmission bulk submission
     */
    void setBulkSubmission(final IBulkSubmission bulkSubmission);

    /**
     * Get customer request reference.
     *
     * @return customer request reference
     */
    String getCustomerRequestReference();

    /**
     * Set customer request reference.
     *
     * @param customerRequestReference customer request reference
     */
    void setCustomerRequestReference(final String customerRequestReference);

    /**
     * Get request status.
     *
     * @return request status
     */
    String getRequestStatus();

    /**
     * Set request status.
     *
     * @param requestStatus request status
     */
    void setRequestStatus(final String requestStatus);

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
     * Get line number.
     *
     * @return line number
     */
    Integer getLineNumber();

    /**
     * Set line number.
     *
     * @param lineNumber line number
     */
    void setLineNumber(final Integer lineNumber);

    /**
     * Get SDT request reference.
     *
     * @return SDT request reference
     */
    String getSdtRequestReference();

    /**
     * Set SDT request reference.
     *
     * @param sdtRequestReference SDT request reference
     */
    void setSdtRequestReference(final String sdtRequestReference);

    /**
     * Get created date.
     *
     * @return created date
     */
    LocalDateTime getCreatedDate();

    /**
     * Set created date.
     *
     * @param createdDate created dated
     */
    void setCreatedDate(final LocalDateTime createdDate);

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
     * Get the request XML payload.
     *
     * @return xml payload
     */
    byte[] getRequestPayload();

    /**
     * Set the request XML payload.
     *
     * @param requestPayload xml payload
     */
    void setRequestPayload(final byte[] requestPayload);

    /**
     * Get forwarding attempts.
     *
     * @return forwarding attempts
     */
    int getForwardingAttempts();

    /**
     * Set forwarding attempts.
     *
     * @param forwardingAttempts forwarding attempts
     */
    void setForwardingAttempts(final int forwardingAttempts);

    /**
     * Get target application response.
     *
     * @return target application response
     */
    byte[] getTargetApplicationResponse();

    /**
     * Set target application response.
     *
     * @param targetApplicationResponse target application response
     */
    void setTargetApplicationResponse(final byte[] targetApplicationResponse);

    /**
     * Get internal system error.
     *
     * @return internal system error
     */
    String getInternalSystemError();

    /**
     * Set internal system error.
     *
     * @param internalSystemError internal system error
     */
    void setInternalSystemError(final String internalSystemError);

    /**
     * Get error log.
     *
     * @return error log
     */
    IErrorLog getErrorLog();

    /**
     * Set error log.
     *
     * @param errorLog error log
     */
    void setErrorLog(final IErrorLog errorLog);

    /**
     * Get request type.
     *
     * @return request type
     */
    String getRequestType();

    /**
     * Set request type.
     *
     * @param requestType request type
     */
    void setRequestType(final String requestType);

    /**
     * This method will increment the forwarding attempts, set the updated date to current date and the status to
     * FORWARDED.
     */
    void incrementForwardingAttempts();

    /**
     * This method will mark the individual request object with Accepted status
     * and set the completed and updated dates.
     */
    void markRequestAsAccepted();

    /**
     * This method will mark the individual request object with "Rejected" status
     * and set the completed and updated dates. Any errors will be set in the error log.
     *
     * @param errorLog error log
     */
    void markRequestAsRejected(final IErrorLog errorLog);

    /**
     * This method will mark the individual request object with "Initially Accepted" status
     * and set the updated date.
     */
    void markRequestAsInitiallyAccepted();

    /**
     * This method will mark the individual request object with "Awaiting Data" status
     * and set the updated date.
     */
    void markRequestAsAwaitingData();

    /**
     * is Enqueueable?.
     *
     * @return true if request can be enqueued else false.
     */
    boolean isEnqueueable();

    /**
     * Populate reference related fields.
     */
    void populateReferences();

    /**
     * This method will re-set the forwarding attempts to zero and will
     * also change the status to the initial value of Received.
     */
    void resetForwardingAttempts();

    /**
     * is DeadLetter?.
     *
     * @return true if this request is a dead letter i.e. cannot be processed due to unknown problem and requires
     * further investigation.
     */
    boolean isDeadLetter();

    /**
     * Sets deadLetter property.
     *
     * @param deadLetter indicates if request is dead letter.
     */
    void setDeadLetter(final boolean deadLetter);

    /**
     * The status of the Individual Request - one of "Forwarded", "Received", "Rejected", "Initially Accepted" or
     * "Accepted".
     *
     * @author d130680
     */
    public enum IndividualRequestStatus {

        /**
         * Received.
         */
        RECEIVED("Received"),

        /**
         * Forwarded.
         */
        FORWARDED("Forwarded"),

        /**
         * Awaiting Data.
         */
        AWAITING_DATA("Awaiting Data"),

        /**
         * Rejected.
         */
        REJECTED("Rejected"),

        /**
         * Resubmit Message.
         */
        RESUBMIT_MESSAGE("Resubmit Message"),

        /**
         * Initially Accepted.
         */
        INITIALLY_ACCEPTED("Initially Accepted"),

        /**
         * Accepted.
         */
        ACCEPTED("Accepted"),

        /**
         * Queued.
         */
        QUEUED("Queued"),

        /**
         * Queued.
         */
        FAILED_QUEUE("Failed Queue");

        /**
         * Individual request status.
         */
        private String status;

        /**
         * Constructor.
         *
         * @param s status
         */
        private IndividualRequestStatus(final String s) {

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
