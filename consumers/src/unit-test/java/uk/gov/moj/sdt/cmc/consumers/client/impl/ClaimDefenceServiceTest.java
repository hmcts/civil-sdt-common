package uk.gov.moj.sdt.cmc.consumers.client.impl;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResponse;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;
import uk.gov.moj.sdt.consumers.util.ClaimDefencesResultsUtil;
import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimDefenceServiceTest extends AbstractSdtUnitTestBase {

    @Mock
    private CMCApi cmcApi;

    private ClaimDefencesService claimDefencesService;

    @BeforeEach
    @Override
    public void setUp() {
        claimDefencesService = new ClaimDefencesService(cmcApi);
    }

    @Test
    void shouldReturnClaimDefencesResponse() {
        final ClaimDefencesResponse claimDefencesResponse = createClaimDefencesResponse();
        when(cmcApi.claimDefences(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(claimDefencesResponse);
        String fromDateTime = "2020-01-22T11:12:13";
        String toDateTime = "2020-12-01T13:14:15";
        ClaimDefencesResponse returnValue = claimDefencesService.claimDefences("", "", "",
                fromDateTime, toDateTime);
        assertNotNull(returnValue);
        assertEquals(claimDefencesResponse.getResults(), returnValue.getResults());
        assertEquals(claimDefencesResponse.getResultCount(), returnValue.getResultCount());
    }

    private SubmitQueryResponse createSubmitQueryResponse(ClaimDefencesResponse claimDefencesResponse) {
        SubmitQueryResponse submitQueryResponse = new SubmitQueryResponse();
        submitQueryResponse.setClaimDefencesResultsCount(claimDefencesResponse.getResultCount());
        submitQueryResponse.setClaimDefencesResults(claimDefencesResponse.getResults());
        return submitQueryResponse;
    }

    private ClaimDefencesResponse createClaimDefencesResponse() {
        ClaimDefencesResponse claimDefencesResponse = new ClaimDefencesResponse();

        ClaimDefencesResultsUtil resultsUtil = new ClaimDefencesResultsUtil();
        List<ClaimDefencesResult> results = resultsUtil.createClaimDefencesList();

        claimDefencesResponse.setResults(results);

        claimDefencesResponse.setResultCount(results.size());
        return claimDefencesResponse;
    }

}
