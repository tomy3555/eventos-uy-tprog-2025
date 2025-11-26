package logica.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "organizadores")
@PrimaryKeyJoinColumn(name = "nickname")
public class OrganizadorOO extends UsuarioOO {
	
	private static final long serialVersionUID = 1L;

    @Column(name = "descripcion", length = 500)
    @Size(max = 500)
    private String descripcion;

    @Column(name = "link", length = 255)
    @Size(max = 255)
    private String link;

    @OneToMany(mappedBy = "organizador", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EdicionOO> ediciones = new HashSet<>();

    protected OrganizadorOO() { super(); }

    public OrganizadorOO(String nickname, String nombre, String email, String contrasena, String imagen,
                         String descripcion, String link) {
        super(nickname, nombre, email, contrasena, imagen);
        this.descripcion = descripcion;
        this.link = link;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public Set<EdicionOO> getEdiciones() { return ediciones; }
    public void addEdicion(EdicionOO ed) {
        ediciones.add(ed);
        ed.setOrganizador(this);
    }
    public void removeEdicion(EdicionOO ed) {
        ediciones.remove(ed);
        ed.setOrganizador(null);
    }
}