package logica.datatypes;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import logica.enumerados.DTEstado;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import logica.utils.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTEdicion implements Serializable {

    private String nombre;
    private String sigla;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaInicio;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaFin;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaAlta;
    private String organizador;
    private String ciudad;
    private String pais;
    private String imagen;
    private String video;
    private DTEstado estado;

    @XmlElementWrapper(name = "tiposRegistro")
    @XmlElement(name = "tipoRegistro")
    private List<DTTipoRegistro> tiposRegistro = new ArrayList<>();

    @XmlElementWrapper(name = "patrocinios")
    @XmlElement(name = "patrocinio")
    private List<DTPatrocinio> patrocinios = new ArrayList<>();

    @XmlElementWrapper(name = "registros")
    @XmlElement(name = "registro")
    private List<DTRegistro> registros = new ArrayList<>();

    private DTEvento evento;

    public DTEdicion() {
        this.tiposRegistro = new ArrayList<>();
        this.patrocinios = new ArrayList<>();
        this.registros = new ArrayList<>();
    }

    public DTEdicion(String nombre, String sigla, LocalDate fechaInicio, LocalDate fechaFin,
                     LocalDate fechaAlta, String organizador, String ciudad, String pais, String imagen, DTEvento evento) {
        this();
        this.nombre = nombre;
        this.sigla = sigla;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.organizador = organizador;
        this.ciudad = ciudad;
        this.pais = pais;
        this.imagen = imagen;
        this.video = null;
        this.evento = evento;
    }

    public DTEdicion(String nombre, String sigla, LocalDate fechaInicio, LocalDate fechaFin,
                     LocalDate fechaAlta, String organizador, String ciudad, String pais, String imagen, DTEstado estado, DTEvento evento) {
        this(nombre, sigla, fechaInicio, fechaFin, fechaAlta, organizador, ciudad, pais, imagen, evento);
        this.estado = estado;
    }

    public DTEdicion(String nombre, String sigla, LocalDate fechaInicio, LocalDate fechaFin,
                     LocalDate fechaAlta, String organizador, String ciudad, String pais, String imagen, DTEstado estado,
                     List<DTTipoRegistro> tiposRegistro, List<DTPatrocinio> patrocinios, DTEvento evento) {
        this(nombre, sigla, fechaInicio, fechaFin, fechaAlta, organizador, ciudad, pais, imagen, estado, evento);
        if (tiposRegistro != null) this.tiposRegistro = tiposRegistro;
        if (patrocinios != null) this.patrocinios = patrocinios;
    }

    public DTEdicion(String nombre, String sigla, LocalDate fechaInicio, LocalDate fechaFin,
                     LocalDate fechaAlta, String organizador, String ciudad, String pais, String imagen, DTEstado estado,
                     List<DTTipoRegistro> tiposRegistro, List<DTPatrocinio> patrocinios, List<DTRegistro> registros, DTEvento evento) {
        this(nombre, sigla, fechaInicio, fechaFin, fechaAlta, organizador, ciudad, pais, imagen, estado, evento);
        if (tiposRegistro != null) this.tiposRegistro = tiposRegistro;
        if (patrocinios != null) this.patrocinios = patrocinios;
        if (registros != null) this.registros = registros;
    }

    public String getNombre() { return nombre; }
    public String getSigla() { return sigla; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public String getOrganizador() { return organizador; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }
    public String getImagen() { return imagen; }
    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
    public DTEstado getEstado() { return estado; }
    public DTEvento getEvento() { return evento; }
    public List<DTTipoRegistro> getTiposRegistro() { return tiposRegistro; }
    public List<DTPatrocinio> getPatrocinios() { return patrocinios; }
    public List<DTRegistro> getRegistros() { return registros; }
}