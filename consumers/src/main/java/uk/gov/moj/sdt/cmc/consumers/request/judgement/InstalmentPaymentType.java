package uk.gov.moj.sdt.cmc.consumers.request.judgement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "amount",
    "frequency"
})
public class InstalmentPaymentType {

    private Long amount;

    private InstalmentFrequencyType frequency;

    public Long getAmount() {
        return amount;
    }

    public InstalmentFrequencyType getFrequency() {
        return frequency;
    }
}
