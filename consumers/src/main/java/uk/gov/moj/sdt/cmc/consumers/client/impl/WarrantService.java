package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.api.IWarrantService;
import uk.gov.moj.sdt.cmc.consumers.request.WarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.response.WarrantResponse;

@Service("WarrantService")
public class WarrantService implements IWarrantService {

    private CMCApi cmcApi;

    @Autowired
    public WarrantService(CMCApi cmcApi) {
        this.cmcApi = cmcApi;
    }

    @Override
    public WarrantResponse warrantRequest(String authorization,
                                          String serviceAuthorization,
                                          String idamId,
                                          String sdtRequestId,
                                          WarrantRequest warrantRequest) {
        return cmcApi.warrantRequest(authorization,
                                     serviceAuthorization,
                                     idamId,
                                     sdtRequestId,
                                     warrantRequest);
    }
}
