package uk.gov.moj.sdt.cmc.consumers.client.mock;

import java.util.Calendar;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IJudgementWarrantService;
import uk.gov.moj.sdt.cmc.consumers.request.JudgementWarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.response.JudgementWarrantResponse;
import uk.gov.moj.sdt.cmc.consumers.response.JudgmentWarrantStatus;

@Service("MockJudgementWarrantService")
public class MockJudgementWarrantService implements IJudgementWarrantService {

    @Override
    public JudgementWarrantResponse judgementWarrantRequest(String authorization,
                                                            String serviceAuthorization,
                                                            String idamId,
                                                            String sdtRequestId,
                                                            JudgementWarrantRequest judgementWarrantRequest) {
        JudgementWarrantResponse response = new JudgementWarrantResponse();
        response.setWarrantNumber("123456");
        response.setFee(45678L);
        response.setJudgmentWarrantStatus(JudgmentWarrantStatus.JUDGMENT_ACCEPTED_WARRANT_ACCEPTED_BY_CCBC);
        response.setJudgmentEnteredDate(Calendar.getInstance().getTime());
        response.setEnforcingCourtCode("123");
        response.setEnforcingCourtName("Court Code");
        response.setFirstPaymentDate(Calendar.getInstance().getTime());
        return response;
    }
}
