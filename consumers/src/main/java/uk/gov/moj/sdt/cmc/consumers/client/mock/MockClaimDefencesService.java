package uk.gov.moj.sdt.cmc.consumers.client.mock;

import java.util.List;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IClaimDefencesService;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResponse;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;
import uk.gov.moj.sdt.consumers.util.ClaimDefencesResultsUtil;

@Service("MockClaimDefencesService")
public class MockClaimDefencesService implements IClaimDefencesService {

    @Override
    public ClaimDefencesResponse claimDefences(String authorization, String serviceAuthorization, String idamId,
                                               String fromDate, String toDate) {
        return createClaimDefencesResponse();
    }

    private ClaimDefencesResponse createClaimDefencesResponse() {
        ClaimDefencesResultsUtil resultsUtil = new ClaimDefencesResultsUtil();
        List<ClaimDefencesResult> results = resultsUtil.createClaimDefencesList();
        ClaimDefencesResponse response = new ClaimDefencesResponse();
        response.setResults(results);
        response.setResultCount(results.size());
        return response;
    }

}
