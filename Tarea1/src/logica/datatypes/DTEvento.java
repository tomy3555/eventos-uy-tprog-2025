package logica.datatypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import logica.enumerados.DTEstado;

public class DTEvento {
    private String nombre;
    private String sigla;
    private String descripcion;
    private LocalDate fecha;
    private List<String> categorias;
    private List<String> ediciones;

    private String imagen;

    public DTEvento(String nombre, String sigla, String descripcion, LocalDate fecha,
                    List<String> categorias, List<String> ediciones) {
        this.nombre = nombre;
        this.sigla = sigla;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.categorias = categorias;
        this.ediciones = ediciones;
        this.imagen = null; // por defecto
    }

    public DTEvento(String nombre, String sigla, String descripcion, LocalDate fecha,
                    List<String> categorias, List<String> ediciones, String imagen) {
        this(nombre, sigla, descripcion, fecha, categorias, ediciones);
        this.imagen = imagen;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getSigla() { return sigla; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFecha() { return fecha; }
    public List<String> getCategorias() { return categorias; }
    public List<String> getEdiciones() { return ediciones; }

    public String getImagen() { return imagen; }

    public void setImagen(String imagen) { this.imagen = imagen; }

    public List<String> getEdicionesAceptadas(List<DTEdicion> edicionesCompletas) {
        List<String> aceptadas = new ArrayList<>();
        for (DTEdicion ed : edicionesCompletas) {
            if (ed.getEstado() == DTEstado.Aceptada && this.ediciones.contains(ed.getNombre())) {
                aceptadas.add(ed.getNombre());
            }
        }
        return aceptadas;
    }
}