package uk.gov.moj.sdt.cmc.consumers.client.mock;

import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.cmc.consumers.api.IBreathingSpaceService;
import uk.gov.moj.sdt.cmc.consumers.request.BreathingSpaceRequest;
import uk.gov.moj.sdt.cmc.consumers.response.BreathingSpaceResponse;
import uk.gov.moj.sdt.cmc.consumers.response.ProcessingStatus;

@Service("MockBreathingSpaceService")
public class MockBreathingSpaceService implements IBreathingSpaceService {

    @Override
    public BreathingSpaceResponse breathingSpace(String idamId,
                                                 String sdtRequestId,
                                                 BreathingSpaceRequest breathingSpaceRequest) {
        BreathingSpaceResponse breathingSpaceResponse = new BreathingSpaceResponse();
        breathingSpaceResponse.setProcessingStatus(ProcessingStatus.QUEUED);
        return breathingSpaceResponse;
    }
}
