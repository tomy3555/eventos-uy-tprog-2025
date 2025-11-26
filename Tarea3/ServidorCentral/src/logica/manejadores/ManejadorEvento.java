package logica.manejadores;

//import usuario.Eventos;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import logica.clases.Ediciones;
import logica.clases.Eventos;
import logica.clases.Registro;
import logica.clases.TipoRegistro;


public class ManejadorEvento {
    private static ManejadorEvento instancia = null;
    private Map<String, Eventos> eventos;
    private Set<TipoRegistro> tiposRegistro;
    private Map<String, Ediciones> ediciones = new HashMap<>();
    private Map<String, Registro> registros = new HashMap<>();

    private ManejadorEvento() {
        eventos = new HashMap<>();
        tiposRegistro = new HashSet<>();
    }

    public static ManejadorEvento getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorEvento();
        }
        return instancia;
    }

    public boolean existeEvento(String nombre) {
        return eventos.containsKey(nombre);
    }

    public void agregarEvento(Eventos evento) {
        eventos.put(evento.getNombre(), evento);
    }

    public Eventos obtenerEvento(String nombre) {
        return eventos.get(nombre);
    }

    public Map<String, Eventos> obtenerEventos() {
        return eventos;
    }

    public Map<String, Ediciones> obtenerEdicionesEvento(String nombreEvento) {
        Eventos evento = eventos.get(nombreEvento);
        if (evento != null) {
            return evento.getEdiciones();
        }
        return null;
    }
    
    public boolean existeEdicion(String nombre){
		for (Map.Entry<String, Ediciones> edicionesIter : ediciones.entrySet()){
			Ediciones edicionIter = edicionesIter.getValue();
			//System.out.println(u.getEmail());
			if (edicionIter.getNombre().equals(nombre)) {
				return true;				
			}
		}
		return false;
	}
	

    public Ediciones obtenerEdicionEvento(String nombreEvento, String nombreEdicion) {
        Eventos evento = eventos.get(nombreEvento);
        if (evento != null) {
            return evento.obtenerEdicion(nombreEdicion);
        }
        return null;
    }

    public void agregarTipoRegistro(TipoRegistro tipo) {
        tiposRegistro.add(tipo);
    }

    public boolean existeTipoRegistroGlobal(String nombre) {
        for (TipoRegistro t : tiposRegistro) {
            if (t.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    public Set<TipoRegistro> obtenerTiposRegistro() {
        return tiposRegistro;
    }

    
    public void agregarEdicion(Ediciones edicion) {
        ediciones.put(edicion.getSigla(), edicion);
    }

    public Ediciones obtenerEdicion(String sigla) {
        return ediciones.get(sigla);
    }

    public void agregarRegistro(Registro registro) {
        registros.put(registro.getId(), registro);
    }

    public Registro obtenerRegistro(String identificador) {
        return registros.get(identificador);
    }
    public boolean existeRegistro(String identificador) {
    		if (obtenerRegistro(identificador) != null) {
    			return true;
    		}
    		
    	return false;
    }

    public Map<String, Registro> obtenerRegistros() {
        return registros;
    }
    
    
    public void eliminarEdicionDeMemoria(Ediciones ed) {
        if (ed == null) return;

        // 1) Quitar del mapa global de ediciones (sigla -> Ediciones)
        if (ed.getSigla() != null) {
            ediciones.remove(ed.getSigla());
        }

        // 2) Quitar del evento dueño (mapa nombreEdicion -> Ediciones)
        Eventos ev = ed.getEvento();
        if (ev != null && ev.getEdiciones() != null) {
            ev.getEdiciones().remove(ed.getNombre());
        }

        
    }
    
    private static void doReset() {
        if (instancia != null) {
            // Limpia TODAS tus colecciones internas
            instancia.eventos.clear();
            // Si ediciones/categorías/tipos no existen en tu clase, borra estas líneas:
            instancia.ediciones.clear();
            instancia.tiposRegistro.clear();
        }
    }
            public static void testReset() { doReset(); }
            public static void reset()       { doReset(); }
            public static void clear()       { doReset(); }

}