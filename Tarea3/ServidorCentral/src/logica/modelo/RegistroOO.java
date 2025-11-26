package logica.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "registros")
@Access(AccessType.FIELD)
public class RegistroOO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "identificador", nullable = false, length = 100)
    @NotBlank
    private String identificador;

    @Column(name = "costo")
    private double costo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_nick", referencedColumnName = "nickname")
    private UsuarioOO usuario;

    @ManyToOne
    @JoinColumn(name = "edicion_nombre", referencedColumnName = "nombre")
    private EdicionOO edicion;

    @Column(name = "tipo_registro", length = 100)
    private String tipoRegistro;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "evento", length = 120)
    private String evento; // event identifier (kept simple)

    @Column(name = "asistencia_marcada")
    private boolean asistenciaMarcada;

    protected RegistroOO() {}

    public RegistroOO(String identificador, double costo, UsuarioOO usuario, EdicionOO edicion, String tipoRegistro, LocalDate fechaRegistro, LocalDate fechaInicio, String evento, boolean asistenciaMarcada) {
        this.identificador = identificador;
        this.costo = costo;
        this.usuario = usuario;
        this.edicion = edicion;
        this.tipoRegistro = tipoRegistro;
        this.fechaRegistro = fechaRegistro;
        this.fechaInicio = fechaInicio;
        this.evento = evento;
        this.asistenciaMarcada = asistenciaMarcada;
    }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public UsuarioOO getUsuario() { return usuario; }
    public void setUsuario(UsuarioOO usuario) { this.usuario = usuario; }

    public EdicionOO getEdicion() { return edicion; }
    public void setEdicion(EdicionOO edicion) { this.edicion = edicion; }

    public String getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(String tipoRegistro) { this.tipoRegistro = tipoRegistro; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public boolean isAsistenciaMarcada() { return asistenciaMarcada; }
    public void setAsistenciaMarcada(boolean asistenciaMarcada) { this.asistenciaMarcada = asistenciaMarcada; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistroOO)) return false;
        RegistroOO r = (RegistroOO) o;
        return Objects.equals(identificador, r.identificador);
    }

    @Override
    public int hashCode() { return Objects.hash(identificador); }
}