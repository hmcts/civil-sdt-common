package uk.gov.moj.sdt.cmc.consumers.api;

import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResponse;

public interface IClaimDefencesService {

    ClaimDefencesResponse claimDefences(String authorization, String serviceAuthorization, String idamId,
                                        String fromDate, String toDate);
}
