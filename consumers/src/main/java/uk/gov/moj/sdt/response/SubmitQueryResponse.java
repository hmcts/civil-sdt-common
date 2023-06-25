package uk.gov.moj.sdt.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQueryResponse {
    private String fromDate;
    private String toDate;
    private Integer claimDefencesResultsCount;
    private List<ClaimDefencesResult> claimDefencesResults;
    private SubmitQueryResponseType responseType;
}
