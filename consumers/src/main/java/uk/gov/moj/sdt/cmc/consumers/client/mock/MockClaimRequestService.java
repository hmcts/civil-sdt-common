package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IClaimRequestService;
import uk.gov.moj.sdt.cmc.consumers.request.claim.ClaimRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimResponse;

@Service("MockClaimRequestService")
public class MockClaimRequestService implements IClaimRequestService {

    @Override
    public ClaimResponse claimRequest(String idamId,
                                      String sdtRequestRef,
                                      ClaimRequest claimRequest) {
        return new ClaimResponse();
    }
}
