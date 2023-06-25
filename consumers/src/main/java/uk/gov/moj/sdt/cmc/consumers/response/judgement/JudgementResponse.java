package uk.gov.moj.sdt.cmc.consumers.response.judgement;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class JudgementResponse {

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date firstPaymentDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date judgmentEnteredDate;

}
