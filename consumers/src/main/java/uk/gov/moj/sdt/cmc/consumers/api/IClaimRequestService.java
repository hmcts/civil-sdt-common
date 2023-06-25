package uk.gov.moj.sdt.cmc.consumers.api;

import uk.gov.moj.sdt.cmc.consumers.request.claim.ClaimRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimResponse;

public interface IClaimRequestService {

    ClaimResponse claimRequest(String idamId,
                               String sdtRequestRef,
                               ClaimRequest claimRequest);
}
