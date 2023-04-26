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
@Table(name = "PURGE_JOB_AUDIT_MESSAGES")
@Entity
public class PurgeJobAuditMessage extends AbstractDomainObject {

    @Id
    @Column(name = "PURGE_JOB_MESSAGE_ID")
    private Long id;

    @Column(name = "PURGE_JOB_ID")
    private Long purgeJobId;

    @Column(name = "MESSAGE_DATE")
    private LocalDateTime jobStartDate;

    @Column(name = "LOG_MESSAGE")
    private String logMessage;

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

    public Long getPurgeJobId() {
        return purgeJobId;
    }

    public void setPurgeJobId(Long purgeJobId) {
        this.purgeJobId = purgeJobId;
    }

    public LocalDateTime getJobStartDate() {
        return jobStartDate;
    }

    public void setJobStartDate(LocalDateTime jobStartDate) {
        this.jobStartDate = jobStartDate;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
