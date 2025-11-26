
package publicadores;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DTNivel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="DTNivel">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="ORO"/>
 *     <enumeration value="PLATA"/>
 *     <enumeration value="BRONCE"/>
 *     <enumeration value="PLATINO"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "DTNivel")
@XmlEnum
public enum DTNivel {

    ORO,
    PLATA,
    BRONCE,
    PLATINO;

    public String value() {
        return name();
    }

    public static DTNivel fromValue(String v) {
        return valueOf(v);
    }

}
