package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IClaimStatusUpdateService;
import uk.gov.moj.sdt.cmc.consumers.request.ClaimStatusUpdateRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimStatusUpdateResponse;
import uk.gov.moj.sdt.cmc.consumers.response.ProcessingStatus;

@Service("MockClaimStatusUpdateService")
public class MockClaimStatusUpdateService implements IClaimStatusUpdateService {
    @Override
    public ClaimStatusUpdateResponse claimStatusUpdate(String idamId, String sdtRequestRef, ClaimStatusUpdateRequest claimStatusUpdateRequest) {
        ClaimStatusUpdateResponse claimStatusUpdateResponse = new ClaimStatusUpdateResponse();
        claimStatusUpdateResponse.setProcessingStatus(ProcessingStatus.QUEUED);
        return claimStatusUpdateResponse;
    }
}
