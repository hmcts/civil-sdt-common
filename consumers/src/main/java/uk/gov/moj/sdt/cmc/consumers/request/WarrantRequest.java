package uk.gov.moj.sdt.cmc.consumers.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.moj.sdt.cmc.consumers.request.common.Address;
import uk.gov.moj.sdt.cmc.consumers.request.common.SotSignature;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "caseManRef",
    "respondentId",
    "respondentAddress",
    "balanceOfDebt",
    "warrantAmount",
    "solicitorCost",
    "additionalNotes",
    "sotName"
})
public class WarrantRequest {

    private String claimNumber;

    private String defendantId;

    private Address defendantAddress;

    private Long balanceOfDebt;

    private Long warrantAmount;

    private Long solicitorCost;

    private String additionalNotes;

    private SotSignature sotSignature;

    public String getCaseManRef() {
        return claimNumber;
    }

    public String getRespondentId() {
        return defendantId;
    }

    @SuppressWarnings("java:S4144")
    public String getDefendantId() {
        return defendantId;
    }

    public Long getBalanceOfDebt() {
        return balanceOfDebt;
    }

    public Long getWarrantAmount() {
        return warrantAmount;
    }

    public Long getSolicitorCost() {
        return solicitorCost;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public Address getRespondentAddress() {
        return defendantAddress;
    }

    public String getSotName() {
        return sotSignature != null ? sotSignature.getName() : "";
    }
}
