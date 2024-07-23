package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CMCUpdateRequest {

    private int errorCode;

    private String errorText;

}
