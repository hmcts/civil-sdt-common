package uk.gov.moj.sdt.utils.cmc;

public enum RequestType {

    BREATHING_SPACE("mcolBreathingSpace"),
    CLAIM("mcolClaim"),
    CLAIM_STATUS_UPDATE("mcolClaimStatusUpdate"),
    DEFENCE_CRITERIA("mcolDefenceCriteria"),
    JUDGMENT("mcolJudgment"),
    JUDGMENT_WARRANT("mcolJudgmentWarrant"),
    WARRANT("mcolWarrant");

    private String type;

    RequestType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
