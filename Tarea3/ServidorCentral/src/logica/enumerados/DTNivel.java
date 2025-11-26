package logica.enumerados;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "DTNivel")
public enum DTNivel implements Serializable {
    ORO,
    PLATA,
    BRONCE,
    PLATINO
}