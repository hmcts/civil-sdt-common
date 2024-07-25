package uk.gov.moj.sdt.cmc.model;

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
@XmlRootElement(name = "cmcResponseDetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class CMCUpdateRequest {

    @XmlTransient
    private Integer errorCode;

    @XmlTransient
    private String errorText;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    private Date issueDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    private Date serviceDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    private Date judgmentEnteredDate;

    @XmlJavaTypeAdapter(CMCUpdateRequestDateAdaptor.class)
    private Date firstPaymentDate;

    private String warrantNumber;

    private String enforcingCourtCode;

    private String enforcingCourtName;

    private Long fee;

    private JudgmentWarrantStatus judgmentWarrantStatus;

    @XmlTransient
    private ProcessingStatus processingStatus;
}
