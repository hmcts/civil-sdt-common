package uk.gov.moj.sdt.cmc.consumers.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.moj.sdt.cmc.consumers.request.judgement.JudgementRequest;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "judgmentRequest",
    "warrantRequest"
})
public class JudgementWarrantRequest {

    @JsonProperty("mcolJudgment")
    private JudgementRequest judgmentRequest;
    @JsonProperty("mcolWarrant")
    private WarrantRequest warrantRequest;
}
