package uk.gov.moj.sdt.cmc.consumers.response;

public enum JudgmentWarrantStatus {

    JUDGMENT_REQUEST_ERROR("Judgment Request error."),
    WARRANT_REQUEST_ERROR("Warrant Request error."),
    JUDGMENT_ACCEPTED_BY_CCBC("Judgment accepted by CCBC."),
    JUDGMENT_ACCEPTED_WARRANT_ACCEPTED_BY_CCBC("Judgment accepted by CCBC.  Warrant accepted by CCBC."),
    JUDGMENT_REJECTED_WARRANT_ACCEPTED_BY_CCBC("Judgment rejected by CCBC.  Warrant accepted by CCBC."),
    JUDGMENT_REJECTED_BY_CCBC("Judgment rejected by CCBC."),
    JUDGMENT_ACCEPTED_WARRANT_REJECTED_BY_CCBC("Judgment accepted by CCBC. Warrant rejected by CCBC."),
    JUDGMENT_REJECTED_WARRANT_REJECTED_BY_CCBC("Judgment rejected by CCBC.  Warrant rejected by CCBC.");

    private String message;

    JudgmentWarrantStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
