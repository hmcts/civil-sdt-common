package uk.gov.moj.sdt.cmc.consumers.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimResponse {

    private String claimNumber;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date issueDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date serviceDate;
}

