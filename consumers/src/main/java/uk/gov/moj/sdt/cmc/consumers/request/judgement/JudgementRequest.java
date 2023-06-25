package uk.gov.moj.sdt.cmc.consumers.request.judgement;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.moj.sdt.cmc.consumers.request.common.Address;
import uk.gov.moj.sdt.cmc.consumers.request.common.SotSignature;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "caseManRef",
    "claimAmountAdmitted",
    "courtFee",
    "deductedAmount",
    "interest",
    "jointJudgment",
    "judgmentType",
    "legalCosts",
    "payee",
    "paymentSchedule",
    "respondentId",
    "respondent1Address",
    "respondent1DOB",
    "respondent2Address",
    "respondent2DOB",
    "sentParticularsSeparately",
    "solicitorCost",
    "sotName"
})
public class JudgementRequest {

    private String claimNumber;

    private Boolean jointJudgment;

    private JudgmentType judgmentType;

    private Boolean sentParticularsSeparately;

    private String defendantId;

    private Address defendant1Address;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date defendant1DateOfBirth;

    private Address defendant2Address;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date defendant2DateOfBirth;

    private PaymentSchedule paymentSchedule;

    private Integer interest;

    private Long solicitorCost;

    private Long deductedAmount;

    private Long claimAmountAdmitted;

    private Long courtFee;

    private Long legalCosts;

    private Payee payee;

    private SotSignature sotSignature;

    public String getCaseManRef() {
        return claimNumber;
    }

    public Boolean isJointJudgment() {
        return jointJudgment;
    }

    public JudgmentType getJudgmentType() {
        return judgmentType;
    }

    public Boolean isSentParticularsSeparately() {
        return sentParticularsSeparately;
    }

    public String getRespondentId() {
        return defendantId;
    }

    public Address getRespondent1Address() {
        return defendant1Address;
    }

    public Date getRespondent1DOB() {
        return defendant1DateOfBirth;
    }

    public Address getRespondent2Address() {
        return defendant2Address;
    }

    public Date getRespondent2DOB() {
        return defendant2DateOfBirth;
    }

    public PaymentSchedule getPaymentSchedule() {
        return paymentSchedule;
    }

    public Integer getInterest() {
        return interest;
    }

    public Long getSolicitorCost() {
        return solicitorCost;
    }

    public Long getDeductedAmount() {
        return deductedAmount;
    }

    public Long getClaimAmountAdmitted() {
        return claimAmountAdmitted;
    }

    public Long getCourtFee() {
        return courtFee;
    }

    public Long getLegalCosts() {
        return legalCosts;
    }

    public Payee getPayee() {
        return payee;
    }

    public String getSotName() {
        return sotSignature != null ? sotSignature.getName() : "";
    }
}
