package uk.gov.moj.sdt.cmc.consumers.model.claimdefences;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ClaimDefencesResult {
    private String caseManRef;
    private String respondentId;
    private LocalDate defendantResponseFiledDate;
    private LocalDateTime defendantResponseCreatedDate;
    private String responseType;
    private String defence;
}
