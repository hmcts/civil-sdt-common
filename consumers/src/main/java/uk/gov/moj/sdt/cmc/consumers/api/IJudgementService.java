package uk.gov.moj.sdt.cmc.consumers.api;

import uk.gov.moj.sdt.cmc.consumers.request.judgement.JudgementRequest;
import uk.gov.moj.sdt.cmc.consumers.response.judgement.JudgementResponse;

public interface IJudgementService {

    JudgementResponse requestJudgment(String idamId,
                                      String sdtRequestId,
                                      JudgementRequest judgementRequest);

}
