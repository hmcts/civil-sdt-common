package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CMCUpdateRequest {

    private int errorCode;

    private String errorText;

    private Date issueDate;

    private Date serviceDate;

    private Date judgmentEnteredDate;

    private Date firstPaymentDate;

    private String warrantNumber;

    private String enforcingCourtCode;

    private String enforcingCourtName;

    private long fee;

    private JudgmentWarrantStatus judgmentWarrantStatus;

    private ProcessingStatus processingStatus;
}
