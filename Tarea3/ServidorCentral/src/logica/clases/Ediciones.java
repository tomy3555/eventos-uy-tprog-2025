package logica.clases;

import java.time.LocalDate;
import java.util.Set;

import logica.enumerados.DTEstado;

import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;


public class Ediciones {
    private Eventos evento;
    private String nombre;
    private String sigla;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaAlta;
    private Usuario organizador;
    private String ciudad;
    private String pais;
    private DTEstado estado;
    private Map<String, TipoRegistro> tiposRegistro = new HashMap<>();
    private Set<Patrocinio> patrocinios = new HashSet<>();
    private Map<String, Registro> registros = new HashMap<>();
    private String imagen;
    private String video;

    public Ediciones(Eventos evento, String nombre, String sigla,
                     LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta,
                     Usuario organizador, String ciudad, String pais) {
        this.evento = evento;
        this.nombre = nombre;
        this.sigla = sigla;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.organizador = organizador;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = DTEstado.Ingresada;
        this.video = null;
    }
    public Ediciones(Eventos evento, String nombre, String sigla,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta,
            Usuario organizador, String ciudad, String pais, DTEstado estadoEdicion) {
     this.evento = evento;
     this.nombre = nombre;
     this.sigla = sigla;
     this.fechaInicio = fechaInicio;
     this.fechaFin = fechaFin;
     this.fechaAlta = fechaAlta;
     this.organizador = organizador;
     this.ciudad = ciudad;
     this.pais = pais;
     this.estado = estadoEdicion;
     this.video = null;
    }
    
    public Ediciones(Eventos evento, String nombre, String sigla,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta,
            Usuario organizador, String ciudad, String pais, String imagen) {
        this.evento = evento;
        this.nombre = nombre;
        this.sigla = sigla;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.organizador = organizador;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = DTEstado.Ingresada;
        this.imagen = imagen;
        this.video = null;
    }

    // New constructor accepting imagen and video
    public Ediciones(Eventos evento, String nombre, String sigla,
            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta,
            Usuario organizador, String ciudad, String pais, String imagen, String video) {
        this.evento = evento;
        this.nombre = nombre;
        this.sigla = sigla;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.organizador = organizador;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = DTEstado.Ingresada;
        this.imagen = imagen;
        this.video = video;
    }

    public Eventos getEvento() {
        return evento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

	public void agregarTipoRegistro(String ident, TipoRegistro tipo) {
		this.tiposRegistro.put(ident, tipo);
		
	}

    public java.util.Collection<TipoRegistro> getTiposRegistro() {
        return tiposRegistro.values();
    }

    public TipoRegistro getTipoRegistro(String nombre) {
        return tiposRegistro.get(nombre);
    }

    public java.util.Collection<Patrocinio> getPatrocinios() {
        return patrocinios;
    }

    public Patrocinio getPatrocinio(String codigo) {
        for (Patrocinio p : patrocinios) {
            if (p.getCodigoPatrocinio().equals(codigo)) return p;
        }
        return null;
    }

    public Map<String, Registro> getRegistros() {
        return registros;
    }

    public TipoRegistro obtenerTipoRegistro(String nombre) {
        TipoRegistro tipo = tiposRegistro.get(nombre);
        if (tipo != null) return tipo;
        return null;
    }

    public void agregarRegistro(String ident, Registro registro) {
        this.registros.put(ident, registro);
    }
    
    public DTEstado getEstado() {
        return estado;
    }

    public void setEstado(DTEstado estado) {
        this.estado = estado;
    }

    public boolean estaIngresada() {
        return estado == DTEstado.Ingresada;
    }

    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

}
