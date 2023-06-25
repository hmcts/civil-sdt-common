package uk.gov.moj.sdt.cmc.consumers.client.mock;

import java.util.Calendar;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IJudgementService;
import uk.gov.moj.sdt.cmc.consumers.request.judgement.JudgementRequest;
import uk.gov.moj.sdt.cmc.consumers.response.judgement.JudgementResponse;

@Service("MockJudgementRequestService")
public class MockJudgementRequestService implements IJudgementService {

    @Override
    public JudgementResponse requestJudgment(String idamId,
                                             String sdtRequestId,
                                             JudgementRequest judgementRequest) {
        JudgementResponse judgementResponse = new JudgementResponse();
        judgementResponse.setJudgmentEnteredDate(Calendar.getInstance().getTime());
        judgementResponse.setFirstPaymentDate(Calendar.getInstance().getTime());
        return judgementResponse;
    }
}
