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

    @XmlTransient
    private Integer errorCode;

    @XmlTransient
    private String errorText;

    private String claimNumber;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/London")
    private Date issueDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/London")
    private Date serviceDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/London")
    private Date judgmentEnteredDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/London")
    private Date firstPaymentDate;

    private String warrantNumber;

    private String enforcingCourtCode;

    private String enforcingCourtName;

    private Long fee;

    private JudgmentWarrantStatus judgmentWarrantStatus;

    @XmlTransient
    private CMCUpdateRequestStatus requestStatus;
}
