package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CMCUpdateRequest {

    private int errorCode;

    private String errorText;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
