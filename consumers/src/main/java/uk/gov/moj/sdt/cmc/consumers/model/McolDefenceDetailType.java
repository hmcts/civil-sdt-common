
package uk.gov.moj.sdt.cmc.consumers.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for mcolDefenceDetailType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="mcolDefenceDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="claimNumber" type="{http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema}claimNumberType"/>
 *         &lt;element name="defendantResponse" type="{http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema}defendantResponseType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "mcolDefenceDetail")
@XmlType(propOrder = {
        "claimNumber",
        "defendantResponse"
})
public class McolDefenceDetailType {
    protected String claimNumber;
    protected DefendantResponseType defendantResponse;

    /**
     * Gets the value of the claimNumber property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getClaimNumber() {
        return claimNumber;
    }

    /**
     * Sets the value of the claimNumber property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setClaimNumber(String value) {
        this.claimNumber = value;
    }

    /**
     * Gets the value of the defendantResponse property.
     *
     * @return possible object is
     * {@link DefendantResponseType }
     */
    public DefendantResponseType getDefendantResponse() {
        return defendantResponse;
    }

    /**
     * Sets the value of the defendantResponse property.
     *
     * @param value allowed object is
     *              {@link DefendantResponseType }
     */
    public void setDefendantResponse(DefendantResponseType value) {
        this.defendantResponse = value;
    }

}
