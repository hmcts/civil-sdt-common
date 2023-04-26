package uk.gov.moj.sdt.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Purge Job Audit.
 *
 * @author Mark Dathorne
 */
@Table(name = "PURGE_JOB_AUDIT")
@Entity
public class PurgeJobAudit extends AbstractDomainObject {

    @Id
    @Column(name = "PURGE_JOB_ID")
    private Long id;

    @Column(name = "JOB_START_DATE")
    private LocalDateTime jobStartDate;

    @Column(name = "JOB_END_DATE")
    private LocalDateTime jobEndDate;

    @Column(name = "RETENTION_DATE")
    private String retentionDate;

    @Column(name = "COUNT_OF_ERROR_LOGS_DELETED")
    private Long countOfErrorLogsDeleted;

    @Column(name = "COUNT_OF_INDIVIDUAL_REQUESTS_DELETED")
    private Long countOfIndividualRequestsDeleted;

    @Column(name = "COUNT_OF_BULK_SUBMISSIONS_DELETED")
    private Long countOfBulkSubmissionsDeleted;

    @Column(name = "COUNT_OF_SERVICE_REQUESTS_DELETED")
    private Long countOfServiceRequestsDeleted;

    @Column(name = "SUCCESS")
    private Boolean success;

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
        return 0;
    }

    public LocalDateTime getJobStartDate() {
        return jobStartDate;
    }

    public void setJobStartDate(LocalDateTime jobStartDate) {
        this.jobStartDate = jobStartDate;
    }

    public LocalDateTime getJobEndDate() {
        return jobEndDate;
    }

    public void setJobEndDate(LocalDateTime jobEndDate) {
        this.jobEndDate = jobEndDate;
    }

    public String getRetentionDate() {
        return retentionDate;
    }

    public void setRetentionDate(String retentionDate) {
        this.retentionDate = retentionDate;
    }

    public Long getCountOfErrorLogsDeleted() {
        return countOfErrorLogsDeleted;
    }

    public void setCountOfErrorLogsDeleted(Long countOfErrorLogsDeleted) {
        this.countOfErrorLogsDeleted = countOfErrorLogsDeleted;
    }

    public Long getCountOfIndividualRequestsDeleted() {
        return countOfIndividualRequestsDeleted;
    }

    public void setCountOfIndividualRequestsDeleted(Long countOfIndividualRequestsDeleted) {
        this.countOfIndividualRequestsDeleted = countOfIndividualRequestsDeleted;
    }

    public Long getCountOfBulkSubmissionsDeleted() {
        return countOfBulkSubmissionsDeleted;
    }

    public void setCountOfBulkSubmissionsDeleted(Long countOfBulkSubmissionsDeleted) {
        this.countOfBulkSubmissionsDeleted = countOfBulkSubmissionsDeleted;
    }

    public Long getCountOfServiceRequestsDeleted() {
        return countOfServiceRequestsDeleted;
    }

    public void setCountOfServiceRequestsDeleted(Long countOfServiceRequestsDeleted) {
        this.countOfServiceRequestsDeleted = countOfServiceRequestsDeleted;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
