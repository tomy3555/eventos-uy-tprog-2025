package logica.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(
  name = "usuarios",
  uniqueConstraints = {
    @UniqueConstraint(name="uk_usuarios_email", columnNames = "email")
})
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.JOINED)
public class UsuarioOO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "nickname", nullable = false, length = 50)
  @NotBlank @Size(max = 50)
  private String nickname;

  @Column(name = "nombre", nullable = false, length = 100)
  @NotBlank @Size(max = 100)
  private String nombre;

  @Column(name = "email", nullable = false, length = 120)
  @NotBlank @Email @Size(max = 120)
  private String email;

  @Column(name = "contrasena", nullable = false, length = 255)
  @NotBlank @Size(max = 255)
  private String contrasena; // store hashed password

  @Column(name = "imagen", length = 255)
  @Size(max = 255)
  private String imagen;


  protected UsuarioOO() {}

  public UsuarioOO(String nickname, String nombre, String email,
                   String contrasena, String imagen) {
    this.nickname = nickname;
    this.nombre = nombre;
    this.email = email;
    this.contrasena = contrasena;
    this.imagen = imagen;
  }

  public String getNickname() { return nickname; }
  public void setNickname(String nickname) { this.nickname = nickname; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getContrasena() { return contrasena; }
  public void setContrasena(String contrasena) { this.contrasena = contrasena; }

  public String getImagen() { return imagen; }
  public void setImagen(String imagen) { this.imagen = imagen; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UsuarioOO)) return false;
    UsuarioOO u = (UsuarioOO) o;
    return Objects.equals(nickname, u.nickname);
  }

  @Override
  public int hashCode() { return Objects.hash(nickname); }

  @Override
  public String toString() {
    return "UsuarioOO{" +
           "nickname='" + nickname + '\'' +
           ", nombre='" + nombre + '\'' +
           ", email='" + email + '\'' +
           ", imagen='" + imagen + '\'' +
           '}';
  }
}