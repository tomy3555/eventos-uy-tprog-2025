package logica.datatypes;
import java.time.LocalDate;

import logica.enumerados.DTNivel;
public class DTPatrocinio {
    private String codigo;
    private int monto;
    private LocalDate fecha;
    private DTNivel nivel;
    private int cantRegistrosGratuitos;
    private String institucion;
    private String siglaEdicion;
    private String tipoRegistro;

    public DTPatrocinio(String codigo, int monto, LocalDate fecha, DTNivel nivel, int cantRegistrosGratuitos, String institucion, String siglaEdicion, String tipoRegistro) {
        this.codigo = codigo;
        this.monto = monto;
        this.fecha = fecha;
        this.nivel = nivel;
        this.cantRegistrosGratuitos = cantRegistrosGratuitos;
        this.institucion = institucion;
        this.siglaEdicion = siglaEdicion;
        this.tipoRegistro = tipoRegistro;
    }

    public String getCodigo() { return codigo; }
    public int getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public DTNivel getNivel() { return nivel; }
    public int getCantRegistrosGratuitos() { return cantRegistrosGratuitos; }
    public String getInstitucion() { return institucion; }
    public String getSiglaEdicion() { return siglaEdicion; }
    public String getTipoRegistro() { return tipoRegistro; }
}