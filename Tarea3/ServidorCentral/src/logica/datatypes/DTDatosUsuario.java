package logica.datatypes;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import logica.utils.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTDatosUsuario implements Serializable {

    private String nickname;
    private String nombre;
    private String email;
    private String apellido;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaNac;
    private String nombreInstitucion;
    private String desc;
    private String link;

    @XmlElementWrapper(name = "registros")
    @XmlElement(name = "registro")
    private Set<DTRegistro> registros;

    @XmlElementWrapper(name = "ediciones")
    @XmlElement(name = "edicion")
    private Set<DTEdicion> ediciones;

    private String imagen;

    @XmlElementWrapper(name = "seguidores")
    @XmlElement(name = "seguidor")
    private Set<String> seguidores;

    @XmlElementWrapper(name = "seguidos")
    @XmlElement(name = "seguido")
    private Set<String> seguidos;
    
    @XmlElementWrapper(name = "asistencias")
    @XmlElement(name = "asistencia")
    private Set<DTRegistro> asistencias;

    public DTDatosUsuario() {
        this.registros = new HashSet<>();
        this.ediciones = new HashSet<>();
        this.seguidores = new HashSet<>();
        this.seguidos = new HashSet<>();
        this.imagen = null;
    }

    public DTDatosUsuario(String nickname, String nombre, String email) {
        this();
        this.nickname = nickname;
        this.nombre = nombre;
        this.email = email;
    }

    public DTDatosUsuario(String nickname, String nombre, String email, String imagen) {
        this(nickname, nombre, email);
        this.imagen = imagen;
    }

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
    public String getImagen() { return imagen; }
    public Set<String> getSeguidores() { return seguidores; }
    public Set<String> getSeguidos() { return seguidos; }
    public Set<DTRegistro> getAsistencias() { return asistencias; }


    public void setSeguidores(Set<String> s) { this.seguidores = s == null ? new HashSet<>() : s; }
    public void setSeguidos(Set<String> s) { this.seguidos = s == null ? new HashSet<>() : s; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setFechaNac(LocalDate fechaNac) { this.fechaNac = fechaNac; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setLink(String link) { this.link = link; }
    public void setInstitucion(String inst) { this.nombreInstitucion = inst; }
    public void setRegistros(Set<DTRegistro> registros) { this.registros = registros; }
    public void setEdicion(Set<DTEdicion> edicion) { this.ediciones = edicion; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setAsistencias(Set<DTRegistro> asistencias) { 
        this.asistencias = asistencias == null ? new HashSet<>() : asistencias; 
    }

    
}