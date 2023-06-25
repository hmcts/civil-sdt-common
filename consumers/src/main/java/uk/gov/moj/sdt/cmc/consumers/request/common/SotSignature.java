package uk.gov.moj.sdt.cmc.consumers.request.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SotSignature {

    private String name;

    private Boolean flag;

    public String getName() {
        return name;
    }

    public Boolean getFlag() {
        return flag;
    }
}
