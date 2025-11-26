package logica.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

@Entity
@Table(name = "ediciones")
@Access(AccessType.FIELD)
public class EdicionOO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "nombre", nullable = false, length = 200)
    @NotBlank @Size(max = 200)
    private String nombre;

    @Column(name = "evento", length = 200)
    @NotBlank
    private String evento;

    @Column(name = "sigla", length = 50)
    private String sigla;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @ManyToOne
    @JoinColumn(name = "organizador_nick", referencedColumnName = "nickname")
    private OrganizadorOO organizador;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @Column(name = "video", length = 255)
    private String video;

    @OneToMany(mappedBy = "edicion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RegistroOO> registros = new HashSet<>();

    protected EdicionOO() {}

    public EdicionOO(String nombre, String evento, String sigla, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, OrganizadorOO organizador, String ciudad, String pais, String estado, String imagen, String video) {
        this.nombre = nombre;
        this.evento = evento;
        this.sigla = sigla;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.organizador = organizador;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = estado;
        this.imagen = imagen;
        this.video = video;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public OrganizadorOO getOrganizador() { return organizador; }
    public void setOrganizador(OrganizadorOO organizador) { this.organizador = organizador; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }

    public Set<RegistroOO> getRegistros() { return registros; }
    public void addRegistro(RegistroOO r) {
        registros.add(r);
        r.setEdicion(this);
    }
    public void removeRegistro(RegistroOO r) {
        registros.remove(r);
        r.setEdicion(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdicionOO)) return false;
        EdicionOO e = (EdicionOO) o;
        return Objects.equals(nombre, e.nombre);
    }

    @Override
    public int hashCode() { return Objects.hash(nombre); }
}