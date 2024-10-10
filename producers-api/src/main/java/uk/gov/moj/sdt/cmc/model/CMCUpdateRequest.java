package uk.gov.moj.sdt.cmc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@XmlRootElement(name = "mcolResponseDetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class CMCUpdateRequest {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIMEZONE_EUROPE_LONDON = "Europe/London";

    @XmlTransient
    private Integer errorCode;

    @XmlTransient
    private String errorText;

    private String claimNumber;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = DATE_FORMAT, timezone = TIMEZONE_EUROPE_LONDON)
    private Date issueDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = DATE_FORMAT, timezone = TIMEZONE_EUROPE_LONDON)
    private Date serviceDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = DATE_FORMAT, timezone = TIMEZONE_EUROPE_LONDON)
    private Date judgmentEnteredDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = DATE_FORMAT, timezone = TIMEZONE_EUROPE_LONDON)
    private Date firstPaymentDate;

    private String warrantNumber;

    private String enforcingCourtCode;

    private String enforcingCourtName;

    private Long fee;

    private JudgmentWarrantStatus judgmentWarrantStatus;

    @XmlTransient
    private CMCUpdateRequestStatus requestStatus;
}
