package logica.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "asistentes")
@PrimaryKeyJoinColumn(name = "nickname")
public class AsistenteOO extends UsuarioOO {
	
	private static final long serialVersionUID = 1L;

    @Column(name = "apellido", length = 100)
    @Size(max = 100)
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "institucion", length = 150)
    @Size(max = 150)
    private String institucion;

    protected AsistenteOO() {
        super();
    }

    public AsistenteOO(String nickname, String nombre, String email, String contrasena, String imagen,
                       String apellido, LocalDate fechaNacimiento, String institucion) {
        super(nickname, nombre, email, contrasena, imagen);
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.institucion = institucion;
    }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }
}