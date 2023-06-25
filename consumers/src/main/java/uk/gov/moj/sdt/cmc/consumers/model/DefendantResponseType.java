
package uk.gov.moj.sdt.cmc.consumers.model;

import java.util.Calendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for defendantResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="defendantResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="eventCreatedDateOnMcol" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="raisedOnMcol" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="responseType" type="{http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema}responseType"/>
 *         &lt;element name="defence" type="{http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema}defenceType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="defendantId" use="required" type="{http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema}defendantIdType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "defendantResponseType", propOrder = {
        "filedDate",
        "eventCreatedDateOnMcol",
        "raisedOnMcol",
        "responseType",
        "defence"
})
public class DefendantResponseType {

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    protected Calendar filedDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    protected Calendar eventCreatedDateOnMcol;
    protected boolean raisedOnMcol;
    @XmlElement(required = true)
    protected ResponseType responseType;
    protected String defence;

    @XmlAttribute(name = "defendantId", required = true)
    protected String defendantId;

    /**
     * Gets the value of the filedDate property.
     *
     * @return possible object is
     * {@link String }
     */
    public Calendar getFiledDate() {
        return filedDate;
    }

    /**
     * Sets the value of the filedDate property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFiledDate(Calendar value) {
        this.filedDate = value;
    }

    /**
     * Gets the value of the eventCreatedDateOnMcol property.
     *
     * @return possible object is
     * {@link String }
     */
    public Calendar getEventCreatedDateOnMcol() {
        return eventCreatedDateOnMcol;
    }

    /**
     * Sets the value of the eventCreatedDateOnMcol property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEventCreatedDateOnMcol(Calendar value) {
        this.eventCreatedDateOnMcol = value;
    }

    /**
     * Gets the value of the raisedOnMcol property.
     */
    public boolean isRaisedOnMcol() {
        return raisedOnMcol;
    }

    /**
     * Sets the value of the raisedOnMcol property.
     */
    public void setRaisedOnMcol(boolean value) {
        this.raisedOnMcol = value;
    }

    /**
     * Gets the value of the responseType property.
     *
     * @return possible object is
     * {@link ResponseType }
     */
    public ResponseType getResponseType() {
        return responseType;
    }

    /**
     * Sets the value of the responseType property.
     *
     * @param value allowed object is
     *              {@link ResponseType }
     */
    public void setResponseType(ResponseType value) {
        this.responseType = value;
    }

    /**
     * Gets the value of the defence property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDefence() {
        return defence;
    }

    /**
     * Sets the value of the defence property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDefence(String value) {
        this.defence = value;
    }

    /**
     * Gets the value of the defendantId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDefendantId() {
        return defendantId;
    }

    /**
     * Sets the value of the defendantId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDefendantId(String value) {
        this.defendantId = value;
    }

}
