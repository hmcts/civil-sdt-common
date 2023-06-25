package uk.gov.moj.sdt.cmc.consumers.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ClaimStatusUpdateResponse {

    private ProcessingStatus processingStatus;
}

