package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.api.IClaimStatusUpdateService;
import uk.gov.moj.sdt.cmc.consumers.request.ClaimStatusUpdateRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimStatusUpdateResponse;

@Service("ClaimStatusUpdateService")
public class ClaimStatusUpdateService implements IClaimStatusUpdateService {

    private CMCApi cmcApi;

    @Autowired
    public ClaimStatusUpdateService(CMCApi cmcApi) {
        this.cmcApi = cmcApi;
    }

    @Override
    public ClaimStatusUpdateResponse claimStatusUpdate(String idamId,
                                                       String sdtRequestRef,
                                                       ClaimStatusUpdateRequest claimStatusUpdateRequest) {
        return cmcApi.claimStatusUpdate(idamId, sdtRequestRef, claimStatusUpdateRequest);
    }
}
