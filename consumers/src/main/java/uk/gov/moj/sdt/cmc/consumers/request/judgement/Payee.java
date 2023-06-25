package uk.gov.moj.sdt.cmc.consumers.request.judgement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.moj.sdt.cmc.consumers.request.common.Address;

@ToString
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "address",
    "apacsTransCode",
    "bankAccountHolder",
    "bankAccountNumber",
    "bankInfo1",
    "bankInfo2",
    "bankName",
    "bankSortCode",
    "dxNumber",
    "email",
    "faxNumber",
    "giroAccountNumber",
    "giroTransCode1",
    "giroTransCode2",
    "name",
    "pcm",
    "reference",
    "slipCodeline1",
    "slipCodeline2",
    "telephoneNumber"
})
public class Payee {

    private String name;

    private Address address;

    private String telephoneNumber;

    private String dxNumber;

    private String faxNumber;

    private String email;

    private String pcm;

    private String reference;

    private String bankAccountNumber;

    private String bankAccountHolder;

    private String bankSortCode;

    private String bankName;

    private String bankInfo1;

    private String bankInfo2;

    private String slipCodeline1;

    private String slipCodeline2;

    private String giroAccountNumber;

    private String giroTransCode1;

    private String giroTransCode2;

    private String apacsTransCode;

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getDxNumber() {
        return dxNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPcm() {
        return pcm;
    }

    public String getReference() {
        return reference;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getBankAccountHolder() {
        return bankAccountHolder;
    }

    public String getBankSortCode() {
        return bankSortCode;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankInfo1() {
        return bankInfo1;
    }

    public String getBankInfo2() {
        return bankInfo2;
    }

    public String getSlipCodeline1() {
        return slipCodeline1;
    }

    public String getSlipCodeline2() {
        return slipCodeline2;
    }

    public String getGiroAccountNumber() {
        return giroAccountNumber;
    }

    public String getGiroTransCode1() {
        return giroTransCode1;
    }

    public String getGiroTransCode2() {
        return giroTransCode2;
    }

    public String getApacsTransCode() {
        return apacsTransCode;
    }
}
