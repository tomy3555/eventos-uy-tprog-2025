package logica.datatypes;
import java.time.LocalDate;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import logica.utils.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
  name = "DTArchEdicion",
  propOrder = {
    "nombreEvento",
    "siglaEdicion",
    "fechaInicio",
    "fechaFin",
    "fechaArchivado",
    "organizadorNick"
  }
)
public class DTArchEdicion {

  private String nombreEvento;        
  private String siglaEdicion;         

  @XmlJavaTypeAdapter(LocalDateAdapter.class)
  private LocalDate fechaInicio;

  @XmlJavaTypeAdapter(LocalDateAdapter.class)
  private LocalDate fechaFin;

  @XmlJavaTypeAdapter(LocalDateAdapter.class)
  private LocalDate fechaArchivado;   // puede ser null

  private String organizadorNick;

  // === Constructor vacío requerido por JAXB
  public DTArchEdicion() {}

  // === ctor SIN fechaArchivado
  public DTArchEdicion(String nombreEvento, String siglaEdicion,
                       LocalDate fechaInicio, LocalDate fechaFin,
                       String organizadorNick) {
    this(nombreEvento, siglaEdicion, fechaInicio, fechaFin, null, organizadorNick);
  }

  // === ctor completo
  public DTArchEdicion(String nombreEvento, String siglaEdicion,
                       LocalDate fechaInicio, LocalDate fechaFin,
                       LocalDate fechaArchivado, String organizadorNick) {
    this.nombreEvento = nombreEvento;
    this.siglaEdicion = siglaEdicion;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.fechaArchivado = fechaArchivado;
    this.organizadorNick = organizadorNick;
  }

  // === Getters/Setters bean-style
  public String getNombreEvento() { return nombreEvento; }
  public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }

  public String getSiglaEdicion() { return siglaEdicion; }
  public void setSiglaEdicion(String siglaEdicion) { this.siglaEdicion = siglaEdicion; }

  public LocalDate getFechaInicio() { return fechaInicio; }
  public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

  public LocalDate getFechaFin() { return fechaFin; }
  public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

  public LocalDate getFechaArchivado() { return fechaArchivado; }
  public void setFechaArchivado(LocalDate fechaArchivado) { this.fechaArchivado = fechaArchivado; }

  public String getOrganizadorNick() { return organizadorNick; }
  public void setOrganizadorNick(String organizadorNick) { this.organizadorNick = organizadorNick; }
}