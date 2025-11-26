package logica.enumerados;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "DTEstado")
public enum DTEstado implements Serializable {
    Ingresada,
    Aceptada,
    Rechazada,
    Archivada
}