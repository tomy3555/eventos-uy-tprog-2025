package logica.clases;

import java.time.LocalDate;

import logica.enumerados.DTNivel;

public class Patrocinio {
    private Ediciones edicion;
    private Institucion institucion;
    private DTNivel nivel;
    private TipoRegistro tipoRegistro;
    private int aporte;
    private LocalDate fechaPatrocinio;
    private int cantidadRegistros;
    private String codigoPatrocinio;

    public Patrocinio(Ediciones edicion, Institucion institucion, DTNivel nivel, TipoRegistro tipoRegistro, int aporte, LocalDate fechaPatrocinio, int cantidadRegistros, String codigoPatrocinio) {
        this.edicion = edicion;
        this.institucion = institucion;
        this.nivel = nivel;
        this.tipoRegistro = tipoRegistro;
        this.aporte = aporte;
        this.fechaPatrocinio = fechaPatrocinio;
        this.cantidadRegistros = cantidadRegistros;
        this.codigoPatrocinio = codigoPatrocinio;
    }

    public Ediciones getEdicion() { return edicion; }
    public Institucion getInstitucion() { return institucion; }
    public DTNivel getNivel() { return nivel; }
    public TipoRegistro getTipoRegistro() { return tipoRegistro; }
    public int getAporte() { return aporte; }
    public LocalDate getFechaPatrocinio() { return fechaPatrocinio; }
    public int getCantidadRegistros() { return cantidadRegistros; }
    public String getCodigoPatrocinio() { return codigoPatrocinio; }

    //public void setEdicion(Ediciones edicion) { this.edicion = edicion; }
    //public void setInstitucion(Institucion institucion) { this.institucion = institucion; }
    //public void setNivel(DTNivel nivel) { this.nivel = nivel; }
    //public void setTipoRegistro(TipoRegistro tipoRegistro) { this.tipoRegistro = tipoRegistro; }
    //public void setAporte(int aporte) { this.aporte = aporte; }
    //public void setFechaPatrocinio(LocalDate fechaPatrocinio) { this.fechaPatrocinio = fechaPatrocinio; }
    //public void setCantidadRegistros(int cantidadRegistros) { this.cantidadRegistros = cantidadRegistros; }
    //public void setCodigoPatrocinio(String codigoPatrocinio) { this.codigoPatrocinio = codigoPatrocinio; }
}