package logica.datatypes;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import logica.utils.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTRegistro implements Serializable {

    private String identificador;
    private String usuario;
    private String edicion;
    private String tipoRegistro;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaRegistro;
    private float costo;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaInicio;
    private String evento;
    private Boolean asistio;

    public DTRegistro() {
    }

    public DTRegistro(String identificador, String usuario, String edicion, String tipoRegistro, LocalDate fechaRegistro, float costo, LocalDate fechaInicio, String evento, Boolean asistio) {
        this.identificador = identificador;
        this.usuario = usuario;
        this.edicion = edicion;
        this.tipoRegistro = tipoRegistro;
        this.fechaRegistro = fechaRegistro;
        this.costo = costo;
        this.fechaInicio = fechaInicio;
        this.evento = evento;
        this.asistio = asistio;
    }

    public String getId() { return identificador; }
    public String getUsuario() { return usuario; }
    public String getEdicion() { return edicion; }
    public String getTipoRegistro() { return tipoRegistro; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public float getCosto() { return costo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public String getEvento() {return evento;}
    public Boolean getAsistencia() {
    	return asistio;
    }
}