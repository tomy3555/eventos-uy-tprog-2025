package logica.datatypes;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTTopEvento implements Serializable {

    private String nombreEvento;
    private int visitas;

    public DTTopEvento() { }

    public DTTopEvento(String nombreEvento, int visitas) {
        this.nombreEvento = nombreEvento;
        this.visitas = visitas;
    }

    public String getNombreEvento() { return nombreEvento; }
    public int getVisitas() { return visitas; }
}
