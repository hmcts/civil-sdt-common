package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.api.IClaimRequestService;
import uk.gov.moj.sdt.cmc.consumers.request.claim.ClaimRequest;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimResponse;

@Service("ClaimRequestService")
public class ClaimRequestService implements IClaimRequestService {

    private CMCApi cmcApi;

    @Autowired
    public ClaimRequestService(CMCApi cmcApi) {
        this.cmcApi = cmcApi;
    }

    @Override
    public ClaimResponse claimRequest(String idamId,
                                      String sdtRequestRef,
                                      ClaimRequest claimRequest) {
        return cmcApi.createSDTClaim(idamId, sdtRequestRef, claimRequest);
    }
}
