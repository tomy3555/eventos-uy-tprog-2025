package logica.datatypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DTDatosUsuario {
    private String nickname;
    private String nombre;
    private String email;
    private String apellido;       // si es Asistente
    private LocalDate fechaNac;    // si es Asistente
    private String nombreInstitucion;
    private String desc;           // si es Organizador
    private String link;           // si es Organizador
    private Set<DTRegistro> registros;
    private Set<DTEdicion> ediciones;

    // NUEVO: imagen del usuario
    private String imagen;
    // NUEVO: seguidores y seguidos (nicknames)
    private Set<String> seguidores;
    private Set<String> seguidos;

    // Constructor básico para todos
    public DTDatosUsuario(String nickname, String nombre, String email) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.email = email;
        this.registros = new HashSet<>();
        this.ediciones = new HashSet<>();
        this.imagen = null; // por defecto
        this.seguidores = new HashSet<>();
        this.seguidos = new HashSet<>();
    }
    
    // Constructor conveniente con imagen (opcional)
    public DTDatosUsuario(String nickname, String nombre, String email, String imagen) {
        this(nickname, nombre, email);
        this.imagen = imagen;
    }

    // --- Getters ---
    public String getNickname() { return nickname; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getApellido() { return apellido; }
    public LocalDate getFechaNac() { return fechaNac; }
    public String getDesc() { return desc; }
    public String getLink() { return link; }
    public String getInstitucion() { return nombreInstitucion; }
    public Set<DTRegistro> getRegistros() { return registros; }
    public Set<DTEdicion> getEdiciones() { return ediciones; }

    // NUEVO: getter de imagen
    public String getImagen() { return imagen; }

    // NUEVO: seguidores/seguidos
    public Set<String> getSeguidores() { return seguidores; }
    public Set<String> getSeguidos() { return seguidos; }
    public void setSeguidores(Set<String> s) { this.seguidores = s == null ? new HashSet<>() : s; }
    public void setSeguidos(Set<String> s) { this.seguidos = s == null ? new HashSet<>() : s; }

    // --- Setters opcionales ---
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setFechaNac(LocalDate fechaNac) { this.fechaNac = fechaNac; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setLink(String link) { this.link = link; }
    public void setInstitucion(String inst) { this.nombreInstitucion = inst; }
    public void setRegistros(Set<DTRegistro> registros) { this.registros = registros; }
    public void setEdicion(Set<DTEdicion> edicion) { this.ediciones = edicion; }

    // NUEVO: setter de imagen
    public void setImagen(String imagen) { this.imagen = imagen; }
}