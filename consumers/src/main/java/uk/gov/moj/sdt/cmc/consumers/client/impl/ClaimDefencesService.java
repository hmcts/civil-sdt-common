package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.api.IClaimDefencesService;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResponse;

@Service("ClaimDefencesService")
public class ClaimDefencesService implements IClaimDefencesService {

    private final CMCApi cmcApi;

    @Autowired
    public ClaimDefencesService(CMCApi cmcApi) {
        this.cmcApi = cmcApi;
    }

    @Override
    public ClaimDefencesResponse claimDefences(String authorization, String serviceAuthorization, String idamId, String fromDate,
                                               String toDate) {
        return cmcApi.claimDefences(authorization, serviceAuthorization, idamId, fromDate, toDate);
    }
}
