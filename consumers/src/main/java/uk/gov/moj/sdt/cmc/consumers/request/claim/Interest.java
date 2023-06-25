package uk.gov.moj.sdt.cmc.consumers.request.claim;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "dailyAmount",
    "owedDate",
    "claimDate",
    "claimAmountInterestBase"
})
public class Interest {

    private Double dailyAmount;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date owedDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date claimDate;

    private Double claimAmountInterestBase;
}
