package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum JudgmentWarrantStatus {

    @XmlEnumValue("Judgment accepted by CCBC.")
    JUDGMENT_ACCEPTED_BY_CCBC("Judgment accepted by CCBC."),
    @XmlEnumValue("Judgment accepted by CCBC.  Warrant accepted by CCBC.")
    JUDGMENT_ACCEPTED_WARRANT_ACCEPTED_BY_CCBC("Judgment accepted by CCBC.  Warrant accepted by CCBC."),
    @XmlEnumValue("Judgment rejected by CCBC.  Warrant accepted by CCBC.")
    JUDGMENT_REJECTED_WARRANT_ACCEPTED_BY_CCBC("Judgment rejected by CCBC.  Warrant accepted by CCBC."),
    @XmlEnumValue("Judgment rejected by CCBC.")
    JUDGMENT_REJECTED_BY_CCBC("Judgment rejected by CCBC."),
    @XmlEnumValue("Judgment accepted by CCBC. Warrant rejected by CCBC.")
    JUDGMENT_ACCEPTED_WARRANT_REJECTED_BY_CCBC("Judgment accepted by CCBC. Warrant rejected by CCBC."),
    @XmlEnumValue("Judgment rejected by CCBC.  Warrant rejected by CCBC.")
    JUDGMENT_REJECTED_WARRANT_REJECTED_BY_CCBC("Judgment rejected by CCBC.  Warrant rejected by CCBC.");

    private final String message;

    JudgmentWarrantStatus(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }
}
