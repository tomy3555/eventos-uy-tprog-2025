package logica.datatypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import logica.enumerados.DTEstado;

public class DTEdicion {
    private String nombre;
    private String sigla;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaAlta;
    private String organizador;
    private String ciudad;
    private String pais;
    private String imagen;
    private String video;
    private DTEstado estado;
    private List<DTTipoRegistro> tiposRegistro = new ArrayList<>();
    private List<DTPatrocinio> patrocinios = new ArrayList<>();
    private List<DTRegistro> registros = new ArrayList<>();
    private DTEvento evento;

    public DTEdicion(String nombre, String sigla, LocalDate fechaInicio, LocalDate fechaFin,
                     LocalDate fechaAlta, String organizador, String ciudad, String pais, String imagen, DTEvento evento) {
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
    public List<DTRegistro> getRegistros() {
        return registros;
    }
}