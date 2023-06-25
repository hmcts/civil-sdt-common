
package uk.gov.moj.sdt.cmc.consumers.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for responseType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="responseType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AS"/>
 *     &lt;enumeration value="DE"/>
 *     &lt;enumeration value="DC"/>
 *     &lt;enumeration value="PA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "responseType")
@XmlEnum
public enum ResponseType {

    AS,
    DE,
    DC,
    PA;

    public String value() {
        return name();
    }

    public static ResponseType fromValue(String v) {
        return valueOf(v);
    }

}
