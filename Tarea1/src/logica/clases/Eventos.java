package logica.clases;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;


public class Eventos{
	private String nombre;
	private String sigla;
	private String descripcion;
	private LocalDate fecha;
	private Map<String, Categoria> categorias;
	private Map<String, Ediciones> ediciones;
	private String imagen;
	private Boolean vigente;

	public Eventos(String nombre, String sigla, String descripcion, LocalDate fecha, Map<String, Categoria> categorias, String imagen) {
		this.nombre = nombre;
		this.sigla = sigla;
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.categorias = categorias;
        this.ediciones = new HashMap<>(); 
        this.imagen = imagen;
        this.vigente = true;
	} 
	
	//Definimos los getters
	public String getNombre() {
		return this.nombre;
	}
	
	 public void agregarEdicion(Ediciones edicion) {
	        ediciones.put(edicion.getNombre(), edicion); // clave = nombre de la edición
	    }
	
	public String getSigla() {
		return this.sigla;
	} 
	
	public String getDescripcion() {
		return this.descripcion;
	}
	
	public LocalDate getFecha(){
		return this.fecha;
	}
	
	public Map<String, Categoria> getCategorias() {
        return this.categorias;
    }
	
	public String getImagen() {
		return this.imagen;
	}
	
	//Definimos los setters
	/*public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setSigla(String sigla) {
		this.sigla = sigla;
	} 
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setFecha(LocalDate fecha){
		this.fecha = fecha;
	}
	*/
	public void setCategorias(Map<String, Categoria> categorias) {
        this.categorias = categorias;
    }

    public void setImagen(String imagen) {
		this.imagen = imagen;
	}

    public Map<String, Ediciones> getEdiciones() {
        return ediciones;
    }

    public Ediciones obtenerEdicion(String nombreEdicion) {
        return ediciones.get(nombreEdicion);
    }
    

    public Categoria obtenerCategoria(String nombreCategoria) {
        return categorias.get(nombreCategoria);
    }
    
    public Boolean getVigente() {
		return vigente;
	}
    
    public void setVigente(Boolean vigente) {
    	System.out.println("Cambiando vigente de evento " + this.nombre + " a " + vigente);
		this.vigente = vigente;
	}

}