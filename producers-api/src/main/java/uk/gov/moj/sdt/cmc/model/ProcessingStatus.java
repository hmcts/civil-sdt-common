package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessingStatus {

    QUEUED("queued"),
    PROCESSED("processed");

    private final String status;

    ProcessingStatus(String status) {
        this.status = status;
    }

    @JsonValue
    String getStatus() {
        return status;
    }
}
