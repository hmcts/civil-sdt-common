package uk.gov.moj.sdt.cmc.consumers.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@JsonPropertyOrder({
    "firstPaymentDate",
    "judgmentEnteredDate",
    "issueDate",
    "warrantNumber",
    "enforcingCourtCode",
    "enforcingCourtName",
    "fee",
    "judgmentWarrantStatus",
})
public class JudgementWarrantResponse {

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date firstPaymentDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date judgmentEnteredDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date issueDate;

    private String warrantNumber;

    private String enforcingCourtCode;

    private String enforcingCourtName;

    private Long fee;

    private JudgmentWarrantStatus judgmentWarrantStatus;

    public String getJudgmentWarrantStatus() {
        return judgmentWarrantStatus == null ? null : judgmentWarrantStatus.getMessage();
    }
}
