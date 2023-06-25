package uk.gov.moj.sdt.cmc.consumers.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "bsType",
    "caseManRef",
    "respondentId"
})
public class BreathingSpaceRequest {

    private String claimNumber;

    private String defendantId;

    private String breathingSpaceNotificationType;

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public void setDefendantId(String defendantId) {
        this.defendantId = defendantId;
    }

    public void setBreathingSpaceNotificationType(String breathingSpaceNotificationType) {
        this.breathingSpaceNotificationType = breathingSpaceNotificationType;
    }

    public String getCaseManRef() {
        return claimNumber;
    }

    public String getRespondentId() {
        return defendantId;
    }

    public String getBsType() {
        return breathingSpaceNotificationType;
    }
}
