package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CMCUpdateRequestStatus {

    ACCEPTED("Accepted"),
    INITIALLY_ACCEPTED("Initially Accepted"),
    REJECTED("Rejected");

    private final String status;

    CMCUpdateRequestStatus(String status) {
        this.status = status;
    }

    @JsonValue
    String getStatus() {
        return status;
    }
}
