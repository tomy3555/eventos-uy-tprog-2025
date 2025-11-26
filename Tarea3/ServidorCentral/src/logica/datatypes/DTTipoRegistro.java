package logica.datatypes;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTTipoRegistro implements Serializable {

    private String nombre;
    private String descripcion;
    private float costo;
    private int cupo;

    public DTTipoRegistro() {
    }

    public DTTipoRegistro(String nombre, String descripcion, float costo, int cupo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.cupo = cupo;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public float getCosto() { return costo; }
    public int getCupo() { return cupo; }
}