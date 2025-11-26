package logica.manejadores;


import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import logica.clases.Asistente;
import logica.clases.Institucion;
import logica.clases.Organizador;
import logica.clases.Usuario;

public class ManejadorUsuario {
	private static ManejadorUsuario instancia; //singleton
	private Map<String, Usuario> usuarios = new HashMap<String, Usuario>();
	private Map<String, Asistente> asistentes = new HashMap<String, Asistente>();
	private Map<String, Organizador> organizadores = new HashMap<String, Organizador>();
	private Map<String, Institucion> institucionesMap = new HashMap<>();
	private Set<String> instituciones = new HashSet<String>();
	
	//// instancia de manejador singleton (no se si esta del todo bien)
	private ManejadorUsuario() {
		 usuarios = new HashMap<>();
		 asistentes = new HashMap<>();
		 organizadores = new HashMap<>();
		 instituciones = new HashSet<>();
	}
	
	public static ManejadorUsuario getInstancia() {
		if (instancia == null) {
			instancia = new ManejadorUsuario();
		}
		return instancia;
	}
	
	public Map<String, Usuario> getUsuarios() {
		return this.usuarios;
	}
	
	public Map<String, Asistente> getAsistentes() {
		return this.asistentes;
	}
	
	public Map<String, Organizador> getOrganizadores() {
		return this.organizadores;
	}

	public Set<String> getInstituciones() {
		return this.instituciones;
	}
	public void addUsuario(Usuario user) {
		this.usuarios.put(user.getNickname(), user);
		if (user instanceof Asistente) {
			Asistente ast = findAsistente(user.getNickname());
			this.asistentes.put(ast.getNickname(), ast);
		}else {
			Organizador org = findOrganizador(user.getNickname());
			this.organizadores.put(org.getNickname(), org);
		}
	}
	public void addInstitucion(Institucion insti) {
		this.instituciones.add(insti.getNombre());
		this.institucionesMap.put(insti.getNombre(), insti);
	}
	public Usuario findUsuario(String nickname) {
		return usuarios.get(nickname);
	}
	
	public Organizador findOrganizador(String nickname) {
	    Usuario user = usuarios.get(nickname);
	    if (user instanceof Organizador) {
	        return (Organizador) user;
	    }
	    return null; // o podés tirar una excepción si preferís
	}
	public Asistente findAsistente(String nickname) {
	    Usuario user = usuarios.get(nickname);
	    if (user instanceof Asistente) {
	        return (Asistente) user;
	    }
	    return null; // o podés tirar una excepción si preferís
	}
	
	public Usuario obtenerUsuarioPorNickOEmail(String valor) {
	    // Buscar por nick
	    Usuario usuariosIter = usuarios.get(valor);
	    if (usuariosIter != null) return usuariosIter;

	    // Buscar por email
	    for (Usuario user : usuarios.values()) {
	        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(valor)) {
	            return user;
	        }
	    }
	    return null;
	}

	
	public Boolean findCorreo(String correo) {
		for (Map.Entry<String, Usuario> insti : usuarios.entrySet()){
			Usuario user = insti.getValue();
			if (user.getEmail().equals(correo)) {
				return true;
				
			}
		}
		return false;
	}
	
	public Institucion findInstitucion(String nombre) {
		return institucionesMap.get(nombre);
	}
	
	public boolean existeInstitucion(String nombre) {
		return instituciones.contains(nombre);
	}
	private static void doReset() {
        if (instancia != null) {
            instancia.usuarios.clear();
            instancia.organizadores.clear();
            instancia.asistentes.clear();
            instancia.instituciones.clear();
        }
        instancia = null;
    }
    public static void testReset() { doReset(); }
    public static void reset()       { doReset(); }
    public static void clear()       { doReset(); }
	
}