package uk.gov.moj.sdt.cmc.consumers.request.claim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.moj.sdt.cmc.consumers.request.common.Address;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "name",
    "address"
})
public class CorrespondenceDetailType {

    private String name;

    private Address address;
}
