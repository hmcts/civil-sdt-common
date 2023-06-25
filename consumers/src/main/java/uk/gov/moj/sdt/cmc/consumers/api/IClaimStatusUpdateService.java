package uk.gov.moj.sdt.cmc.consumers.api;

import uk.gov.moj.sdt.cmc.consumers.request.ClaimStatusUpdateRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimStatusUpdateResponse;

public interface IClaimStatusUpdateService {

    ClaimStatusUpdateResponse claimStatusUpdate(String idamId, String sdtRequestRef, ClaimStatusUpdateRequest claimStatusUpdateRequest);
}
