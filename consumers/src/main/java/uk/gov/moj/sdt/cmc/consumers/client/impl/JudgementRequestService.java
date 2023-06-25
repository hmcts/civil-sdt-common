package uk.gov.moj.sdt.cmc.consumers.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.CMCApi;
import uk.gov.moj.sdt.cmc.consumers.api.IJudgementService;
import uk.gov.moj.sdt.cmc.consumers.request.judgement.JudgementRequest;
import uk.gov.moj.sdt.cmc.consumers.response.judgement.JudgementResponse;

@Service("JudgementRequestService")
public class JudgementRequestService implements IJudgementService {

    private CMCApi cmcApi;

    @Autowired
    public JudgementRequestService(CMCApi cmcApi) {
        this.cmcApi = cmcApi;
    }

    @Override
    public JudgementResponse requestJudgment(String idamId,
                                             String sdtRequestId,
                                             JudgementRequest judgementRequest) {
        return cmcApi.requestJudgment(idamId, sdtRequestId, judgementRequest);
    }
}
