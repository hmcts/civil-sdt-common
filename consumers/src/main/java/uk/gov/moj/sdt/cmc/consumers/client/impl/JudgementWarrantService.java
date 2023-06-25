package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.api.IJudgementWarrantService;
import uk.gov.moj.sdt.cmc.consumers.request.JudgementWarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.response.JudgementWarrantResponse;

@Service("JudgementWarrantService")
public class JudgementWarrantService implements IJudgementWarrantService {

    private CMCApi cmcApi;

    @Autowired
    public JudgementWarrantService(CMCApi cmcApi) {
        this.cmcApi = cmcApi;
    }

    @Override
    public JudgementWarrantResponse judgementWarrantRequest(String authorization,
                                                            String serviceAuthorization,
                                                            String idamId,
                                                            String sdtRequestId,
                                                            JudgementWarrantRequest judgementWarrantRequest) {
        return cmcApi.judgementWarrantRequest(authorization,
                                              serviceAuthorization,
                                              idamId,
                                              sdtRequestId, judgementWarrantRequest
        );
    }
}
