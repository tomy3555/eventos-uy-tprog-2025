package logica.clases;

import java.util.Map;

import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Organizador extends Usuario {
    private String desc;
    private String link;
    private Map<String, Ediciones> ediciones;

    public Organizador(String nickname, String nombre, String email, String contrasena, String imagen, String desc, String link) {
    	super(nickname, nombre, email, contrasena);
    	this.desc = desc;
    	this.link = link;
        this.ediciones = new HashMap<>();

    }
    public String getDesc() {
        return desc;
    }
    public Map<String, Ediciones> getEdiciones() {
        return ediciones;
    }

    public List<DTEdicion> listarEdicionesAPartirDeOrganizador() {
        List<DTEdicion> lista = new ArrayList<>();
        for (Ediciones e : ediciones.values()) {
        	DTEvento dtEvento = new DTEvento(
				this.getNombre(),
				e.getSigla(),
				e.getEvento().getDescripcion(),
				e.getEvento().getFecha(),
				new ArrayList<>(e.getEvento().getCategorias().keySet()),
				new ArrayList<>(e.getEvento().getEdiciones().keySet()),
				e.getEvento().getImagen()
			);
        	 // Asegura que el evento esté cargado
            lista.add(new DTEdicion(
                e.getNombre(),
                e.getSigla(),
                e.getFechaInicio(),
                e.getFechaFin(),
                e.getFechaAlta(),
                this.getNombre(), 
                e.getCiudad(),
                e.getPais(),
                e.getImagen(),
                dtEvento
            ));
        }
        return lista;
    }

    public void agregarEdicion(Ediciones edic) {
    	ediciones.put(edic.getNombre(), edic);
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
    @Override
    public String getTipoUsuario() {
        return "Organizador";
    }
}