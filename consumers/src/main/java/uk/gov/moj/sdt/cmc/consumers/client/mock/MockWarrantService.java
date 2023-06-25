package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IWarrantService;
import uk.gov.moj.sdt.cmc.consumers.request.WarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.response.WarrantResponse;

@Service("MockWarrantService")
public class MockWarrantService implements IWarrantService {


    @Override
    public WarrantResponse warrantRequest(String authorization,
                                          String serviceAuthorization,
                                          String idamId,
                                          String sdtRequestId,
                                          WarrantRequest warrantRequest) {
        WarrantResponse response = new WarrantResponse();
        response.setWarrantNumber("123456");
        response.setFee(45678L);
        return response;
    }
}
