package uk.gov.moj.sdt.domain;

import org.apache.commons.lang3.StringUtils;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.AWAITING_DATA;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.FORWARDED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.INITIALLY_ACCEPTED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

/**
 * When processing an Individual Request within the Bulk Request, the SDT application records
 * the following information as part of the Individual Request details.
 *
 * @author d130680
 */
@Table(name = "INDIVIDUAL_REQUESTS")
@Entity
public class IndividualRequest extends AbstractDomainObject implements IIndividualRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ind_req_seq")
    @Column(name = "INDIVIDUAL_REQUEST_ID")
    private long id;

    @Column(name = "VERSION_NUMBER")
    private int version;

    /**
     * Bulk submission.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = BulkSubmission.class)
    @JoinColumn(name="BULK_SUBMISSION_ID")
    private IBulkSubmission bulkSubmission;

    /**
     * Identifier defined by End User to uniquely (unique for the End User within
     * the SDT Data Retention Period) identify individual requests within a Bulk Request.
     */
    @Column(name = "CUSTOMER_REQUEST_REF")
    private String customerRequestReference;

    /**
     * The status of the Individual Request - one of "Forwarded", "Received", "Rejected", "Initially Accepted" or
     * "Accepted".
     */
    @Column(name = "REQUEST_STATUS")
    private String requestStatus;

    /**
     * For claim requests this will be the date of issue. For judgment requests this will be the date of judgment.
     * For paid/written-off/discontinued/withdrawn requests this will be the date the relevant action was taken.
     * For warrant requests this will be the date of issue.
     * If the request was not successfully processed this field will be blank. Formatted as DDMMYYYY.
     */
    @Column(name = "ISSUED_DATE")
    private LocalDateTime issuedDate;

    /**
     * SDT bulk reference.
     */
    @Column(name = "SDT_BULK_REFERENCE")
    private String sdtBulkReference;

    /**
     * Line number.
     */
    @Column(name = "LINE_NUMBER")
    private Integer lineNumber;

    /**
     * SDT request reference.
     */
    @Column(name = "SDT_REQUEST_REFERENCE")
    private String sdtRequestReference;

    /**
     * Date record was created.
     */
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    /**
     * Date record was updated.
     */
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    /**
     * Date/Time the Individual Request processing was completed. This is the Date/Time that the final outcome
     * was received from the Court Case Management system following full processing
     */
    @Column(name = "COMPLETED_DATE")
    private LocalDateTime completedDate;

    /**
     * An Individual Request can be forwarded for up to the defined maximum forwarding attempts.
     * This value indicates the number of attempts to forward the Individual Request to date.
     */
    @Column(name = "FORWARDING_ATTEMPTS")
    private int forwardingAttempts;

    /**
     * Target Application Response for Individual Request processing.
     */
    @Column(name = "TARGET_APPLICATION_RESPONSE")
    private byte[] targetApplicationResponse;

    /**
     * Error log.
     */
    @OneToOne(cascade = CascadeType.ALL, targetEntity = ErrorLog.class, mappedBy = "individualRequest")
    private IErrorLog errorLog;

    /**
     * XML request payload.
     */
    @Column(name = "INDIVIDUAL_PAYLOAD")
    private byte[] requestPayload;

    /**
     * Internal system error.
     */
    @Column(name = "INTERNAL_SYSTEM_ERROR")
    private String internalSystemError;

    /**
     * Request type like mcolClaim, mcolJudgment etc.
     */
    @Column(name = "REQUEST_TYPE")
    private String requestType;

    /**
     * Flags whether message is dead letter i.e. cannot be processed due to unknown problem and requires further
     * investigation.
     */
    @Column(name = "DEAD_LETTER")
    private boolean deadLetter;

    @Override
    public IBulkSubmission getBulkSubmission() {
        return bulkSubmission;
    }

    @Override
    public void setBulkSubmission(final IBulkSubmission bulkSubmission) {
        this.bulkSubmission = bulkSubmission;
    }

    @Override
    public String getCustomerRequestReference() {
        return customerRequestReference;
    }

    @Override
    public void setCustomerRequestReference(final String customerRequestReference) {
        this.customerRequestReference = customerRequestReference;
    }

    @Override
    public String getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(final String requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String getSdtBulkReference() {
        return sdtBulkReference;
    }

    @Override
    public void setSdtBulkReference(final String sdtBulkReference) {
        this.sdtBulkReference = sdtBulkReference;
    }

    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(final Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String getSdtRequestReference() {
        return sdtRequestReference;
    }

    @Override
    public void setSdtRequestReference(final String sdtRequestReference) {
        this.sdtRequestReference = sdtRequestReference;
    }

    @Override
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public void setUpdatedDate(final LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    @Override
    public void setCompletedDate(final LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    @Override
    public byte[] getRequestPayload() {
        return requestPayload;
    }

    @Override
    public void setRequestPayload(final byte[] requestPayload) {
        this.requestPayload = requestPayload;
    }

    @Override
    public int getForwardingAttempts() {
        return forwardingAttempts;
    }

    @Override
    public void setForwardingAttempts(final int forwardingAttempts) {
        this.forwardingAttempts = forwardingAttempts;
    }

    @Override
    public byte[] getTargetApplicationResponse() {
        return targetApplicationResponse;
    }

    @Override
    public void setTargetApplicationResponse(final byte[] targetApplicationResponse) {
        this.targetApplicationResponse = targetApplicationResponse;
    }

    @Override
    public String getInternalSystemError() {
        return internalSystemError;
    }

    @Override
    public void setInternalSystemError(final String internalSystemError) {
        this.internalSystemError = internalSystemError;
    }

    @Override
    public IErrorLog getErrorLog() {
        return errorLog;
    }

    @Override
    public void setErrorLog(final IErrorLog errorLog) {
        this.errorLog = errorLog;
    }

    @Override
    public String getRequestType() {
        return requestType;
    }

    @Override
    public void setRequestType(final String requestType) {
        this.requestType = requestType;

    }

    @Override
    public void incrementForwardingAttempts() {
        this.setRequestStatus(FORWARDED.getStatus());
        this.setForwardingAttempts(this.getForwardingAttempts() + 1);
        this.setUpdatedDate(LocalDateTime.now());
    }

    @Override
    public void markRequestAsAccepted() {
        this.setRequestStatus(IndividualRequestStatus.ACCEPTED.getStatus());

        // Reset forward attempts
        this.setForwardingAttempts(0);

        // Set the completed date only if the status is rejected or accepted.
        this.setCompletedDate(LocalDateTime.now());

        // Set the updated date
        this.setUpdatedDate(LocalDateTime.now());

    }

    @Override
    public void markRequestAsRejected(final IErrorLog errorLog) {
        this.setRequestStatus(REJECTED.getStatus());

        // Reset forward attempts
        this.setForwardingAttempts(0);

        // Set the error log if there is one
        if (errorLog != null) {
            this.setErrorLog(errorLog);
            errorLog.setIndividualRequest(this);
        }

        // Set the completed date only if the status is rejected or accepted.
        this.setCompletedDate(LocalDateTime.now());

        // Set the updated date
        this.setUpdatedDate(LocalDateTime.now());

    }

    @Override
    public void markRequestAsInitiallyAccepted() {
        this.setRequestStatus(INITIALLY_ACCEPTED.getStatus());
        // Reset forward attempts
        this.setForwardingAttempts(0);

        // Set the updated date
        this.setUpdatedDate(LocalDateTime.now());

    }

    @Override
    public void markRequestAsAwaitingData() {
        this.setRequestStatus(AWAITING_DATA.getStatus());
        // Reset forward attempts
        this.setForwardingAttempts(0);

        // Set the updated date
        this.setUpdatedDate(LocalDateTime.now());

    }

    @Override
    public boolean isEnqueueable() {
        return IIndividualRequest.IndividualRequestStatus.RECEIVED.getStatus().equals(requestStatus);
    }

    @Override
    public void populateReferences() {
        // Set the SDT Bulk Reference
        sdtBulkReference = bulkSubmission.getSdtBulkReference();
        populateSdtRequestReference();
    }

    @Override
    public void resetForwardingAttempts() {
        this.setForwardingAttempts(0);
        this.setUpdatedDate(LocalDateTime.now());
        this.setRequestStatus(IndividualRequestStatus.RECEIVED.getStatus());
    }

    /**
     * Populate SDT Request Reference from the SDT Bulk Reference.
     */
    private void populateSdtRequestReference() {

        // Left padd the line number with 0 to a maximum of 7 characters
        final String paddedLineNumber = StringUtils.leftPad(String.valueOf(lineNumber), 7, "0");

        // Count requests.
        SdtMetricsMBean.getMetrics().upRequestCount();

        // SDT Request Reference consists of <SDT Bulk Reference>-<zero padded line number>
        sdtRequestReference = sdtBulkReference + "-" + paddedLineNumber;

        // Update last seen bulk reference.
        SdtMetricsMBean.getMetrics().setLastBulkRequestRef(sdtRequestReference);
    }

    @Override
    public boolean isDeadLetter() {
        return deadLetter;
    }

    @Override
    public void setDeadLetter(final boolean deadLetter) {
        this.deadLetter = deadLetter;

    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getHashId(this) + "[");
        sb.append(super.toString());
        sb.append(", bulkSubmission=").append(getHashId(this.getBulkSubmission()));
        sb.append(", customerRequestReference=").append(this.getCustomerRequestReference());
        sb.append(", requestStatus=").append(this.getRequestStatus());
        sb.append(", sdtBulkReference=").append(this.getSdtBulkReference());
        sb.append(", lineNumber=").append(this.getLineNumber());
        sb.append(", sdtRequestReference=").append(this.getSdtRequestReference());
        sb.append(", createdDate=").append(this.getCreatedDate());
        sb.append(", updatedDate=").append(this.getUpdatedDate());
        sb.append(", completedDate=").append(this.getCompletedDate());
        sb.append(", forwardingAttempts=").append(this.getForwardingAttempts());
        sb.append(", targetApplicationResponse=").append(
                null == this.getTargetApplicationResponse() ? "" :
                new String(this.getTargetApplicationResponse(), StandardCharsets.UTF_8));
        sb.append(", errorLog=").append(this.getErrorLog());
        sb.append(", requestPayload=").append(
                null == this.getRequestPayload() ? "" :
                new String(this.getRequestPayload(), StandardCharsets.UTF_8));
        sb.append(", internalSystemError=").append(this.getInternalSystemError());
        sb.append(", requestType=").append(this.getRequestType());
        sb.append(", deadLetter=").append(this.isDeadLetter());
        sb.append("]");
        return sb.toString();
    }
}
