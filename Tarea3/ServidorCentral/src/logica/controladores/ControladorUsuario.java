package logica.controladores;

import excepciones.UsuarioYaExisteException;
import excepciones.InstitucionYaExisteException;
import excepciones.UsuarioNoExisteException;
import excepciones.UsuarioTipoIncorrectoException;
import excepciones.CategoriaYaExisteException;
import excepciones.RegistroNoExiste;

import java.util.Map;
import java.util.Set;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import logica.clases.Asistente;
import logica.clases.Categoria;
import logica.clases.Ediciones;
import logica.clases.Institucion;
import logica.clases.Organizador;
import logica.clases.Registro;
import logica.clases.Usuario;
import logica.datatypes.DTDatosUsuario;
import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import logica.datatypes.DTRegistro;
import logica.interfaces.IControladorUsuario;
import logica.manejadores.ManejadorEvento;
import logica.manejadores.ManejadorAuxiliar;
import logica.manejadores.ManejadorUsuario;

public class ControladorUsuario implements IControladorUsuario {
	
    private ManejadorUsuario manejador = ManejadorUsuario.getInstancia();
    private ManejadorEvento manejadorEv = ManejadorEvento.getInstancia();
    private ManejadorAuxiliar manejadorAux = ManejadorAuxiliar.getInstancia();

    // --- estado de selección para consultas ---
    private String usuarioSeleccionadoNickname = null;
    private String registroSeleccionadoId = null;

    public Organizador ingresarOrganizador(String nickname, String nombre, String email, String contrasena, String imagen,  String desc, String link) {
        return new Organizador(nickname, nombre, email, contrasena, imagen, desc, link);
    }

    public Asistente ingresarAsistente(String nickname, String nombre, String email, String contrasena, String imagen, String apellido,
                                       LocalDate fechaDeNacimiento, Institucion institucion) {
        return new Asistente(nickname, nombre, email, contrasena, imagen, apellido, fechaDeNacimiento, institucion);
    }

    public void altaUsuario(String nickname, String nombre, String correo, String descripcion, String link,
            String apellido, LocalDate fechaNacimiento, String institucion,
            boolean esOrganizador, String contrasena, String imagen)
throws UsuarioYaExisteException {

// verificar unicidad de nickname y correo
if (manejador.findUsuario(nickname) != null) {
throw new UsuarioYaExisteException("El usuario con nickname " + nickname + " ya esta registrado");
}
if (manejador.findCorreo(correo)) {
throw new UsuarioYaExisteException("El usuario con correo " + correo + " ya esta registrado");
}

// === Normalizar imagen a "solo nombre de archivo" o null ===
String img = (imagen == null) ? null : imagen.trim();
if (img != null && img.isEmpty()) img = null;
if (img != null) {
img = img.replace("\\", "/");
int slash = img.lastIndexOf('/');
if (slash >= 0 && slash < img.length() - 1) {
img = img.substring(slash + 1); // nos quedamos con el nombre (p.ej. IMG-US01.jpg)
}
}

Usuario nuevoUsuario;
if (esOrganizador) {
// Asegurate que este ctor realmente setea la imagen interna; por las dudas la seteamos luego.
nuevoUsuario = new Organizador(nickname, nombre, correo, contrasena, img, descripcion, link);
} else {
Institucion inst = manejador.findInstitucion(institucion);
nuevoUsuario = new Asistente(nickname, nombre, correo, contrasena, img, apellido, fechaNacimiento, inst);
}

// === Por si el constructor no guardó la imagen, la forzamos ===
if (img != null && (nuevoUsuario.getImagen() == null || nuevoUsuario.getImagen().isBlank())) {
nuevoUsuario.setImagen(img);
}

manejador.addUsuario(nuevoUsuario);
}

    @Override
    public void altaInstitucion(String nombre, String descripcion, String link) throws InstitucionYaExisteException {
        // Delegate to new overload without image
        altaInstitucion(nombre, descripcion, link, null);
    }
    
    // New overload that accepts an optional imagen filename
    @Override
    public void altaInstitucion(String nombre, String descripcion, String link, String imagen) throws InstitucionYaExisteException {
        if (manejador.findInstitucion(nombre) != null) {
            throw new InstitucionYaExisteException("La institución " + nombre + " ya existe");        
        }

        Institucion nuevaInstitucion = new Institucion(nombre, descripcion, link);
        if (imagen != null) {
            imagen = imagen.trim();
            if (imagen.isEmpty()) imagen = null;
        }
        if (imagen != null) {
            // store only filename portion
            imagen = imagen.replace("\\", "/");
            int last = imagen.lastIndexOf('/');
            if (last >= 0 && last < imagen.length() - 1) imagen = imagen.substring(last + 1);
            nuevaInstitucion.setImagen(imagen);
        }
        manejador.addInstitucion(nuevaInstitucion);
    }

    public Map<String, Usuario> listarUsuarios() {
        return manejador.getUsuarios();
    }

    public Map<String, Asistente> listarAsistentes() {
        return manejador.getAsistentes();
    }

    public Map<String, Organizador> listarOrganizadores() {
        return manejador.getOrganizadores();
    }
    
    public Set<String> getInstituciones(){
        return manejador.getInstituciones();
    }

    public void actualizarAsistente(String nickname, String apellido, LocalDate fechaNacimiento) throws UsuarioYaExisteException, UsuarioTipoIncorrectoException, UsuarioNoExisteException {
        Usuario user = manejador.findUsuario(nickname);
        if (user == null) {
            throw new UsuarioNoExisteException(nickname);
        }
        if (!(user instanceof Asistente)) {
            throw new UsuarioTipoIncorrectoException(nickname);
        }
        Asistente asistUser = (Asistente) user;
        asistUser.setApellido(apellido);
        asistUser.setFechaDeNacimiento(fechaNacimiento);
    }

    public void actualizarOrganizador(String nickname, String desc, String link) throws UsuarioNoExisteException, UsuarioTipoIncorrectoException {
        Usuario user = manejador.findUsuario(nickname);
        if (user == null) {
            throw new UsuarioNoExisteException(nickname);
        }
        if (!(user instanceof Organizador)) {
            throw new UsuarioTipoIncorrectoException(nickname);
        }
        Organizador orgUser = (Organizador) user;
        orgUser.setDesc(desc);
        orgUser.setLink(link);
    }

    public Set<DTDatosUsuario> obtenerUsuariosDT() throws UsuarioNoExisteException {
        Map<String, Usuario> usuarios = manejador.getUsuarios();
        Set<DTDatosUsuario> datosUsuarios = new HashSet<>();
        
        for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
            String nickname = entry.getKey();
            DTDatosUsuario datos = obtenerDatosUsuario(nickname);
            if (datos != null) {
                datosUsuarios.add(datos);
            }
        }
        
        return datosUsuarios;
    }

    
    public DTDatosUsuario obtenerDatosUsuario(String nickname) throws UsuarioNoExisteException {
        Usuario user = manejador.findUsuario(nickname);
        if (user == null) {
            throw new UsuarioNoExisteException(nickname);
        }

        // guardamos el usuario consultado
        this.usuarioSeleccionadoNickname = nickname;

        DTDatosUsuario dto = new DTDatosUsuario(user.getNickname(), user.getNombre(), user.getEmail());

        // === NUEVO: pasar imagen al DTO (normalizada) ===
        String img = user.getImagen();
        if (img != null) {
            img = img.trim();
            if (img.isEmpty()) img = null;
            // si por error viniera con ruta tipo "/img/usuarios/archivo.jpg", nos quedamos con el nombre
            if (img != null && (img.contains("/") || img.contains("\\"))) {
                img = img.replace("\\", "/");
                int last = img.lastIndexOf('/');
                if (last >= 0 && last < img.length() - 1) {
                    img = img.substring(last + 1);
                }
            }
        }
        dto.setImagen(img);

        // populate seguidores/seguidos into DTO
        try {
            dto.setSeguidores(new java.util.HashSet<>(user.getSeguidores()));
            dto.setSeguidos(new java.util.HashSet<>(user.getSeguidos()));
        } catch (Exception ignored) {
            // in case user's follower sets are not initialized
            dto.setSeguidores(new java.util.HashSet<>());
            dto.setSeguidos(new java.util.HashSet<>());
        }

         if (user instanceof Asistente) {
            Asistente asisUser = (Asistente) user;
            dto.setApellido(asisUser.getApellido());
            dto.setFechaNac(asisUser.getFechaDeNacimiento());
            dto.setRegistros(obtenerRegistrosAsistente(asisUser));
            dto.setInstitucion(obtenerInstitucion(asisUser));
            // Poblar asistencias en el DTO
            dto.setAsistencias(obtenerAsistenciasAsistente(asisUser));
        } else if (user instanceof Organizador) {
            Organizador orgUser = (Organizador) user;
            dto.setDesc(orgUser.getDesc());
            dto.setLink(orgUser.getLink());
            dto.setEdicion(listarEdicionesAPartirDeOrganizador(orgUser));
        }
        return dto;
    }
    
    public String obtenerInstitucion(Asistente asist) {
    	Institucion inst = asist.getInstitucion();
    	if (inst != null) {
    		String nombreInstitucion = inst.getNombre();
    		return nombreInstitucion;
    	}
    	return null;
    }
    
    public Set<DTRegistro> obtenerRegistrosAsistente(Asistente asist){
        Set<DTRegistro> dtr = new HashSet<>();
        Map<String, Registro> registros = asist.getRegistros();
        for (Registro reg : registros.values()) {
            DTRegistro detalle = obtenerDatosRegistros(reg.getId());
            dtr.add(detalle);
        }
        return dtr;
    }

    // Devuelve el set de DTRegistro de asistencias del Asistente
    public Set<DTRegistro> obtenerAsistenciasAsistente(Asistente asist) {
        Set<DTRegistro> dtr = new HashSet<>();
        Map<String, Registro> asistencias = asist.getAsistencias();
        for (Registro reg : asistencias.values()) {
            DTRegistro detalle = obtenerDatosRegistros(reg.getId());
            dtr.add(detalle);
        }
        return dtr;
    }

    public static Set<DTEdicion> listarEdicionesAPartirDeOrganizador(Organizador orgUser) {
        Set<DTEdicion> lista = new HashSet<>();
        for (Ediciones edicionIter : orgUser.getEdiciones().values()) {
        	DTEvento dtEvento = new DTEvento(edicionIter.getEvento().getNombre(), edicionIter.getSigla(),
        			edicionIter.getEvento().getDescripcion(),
        			edicionIter.getEvento().getFecha(),
    				new ArrayList<>(edicionIter.getEvento().getCategorias().keySet()),
    				new ArrayList<>(edicionIter.getEvento().getEdiciones().keySet()),
    				edicionIter.getEvento().getImagen()
    			);
            lista.add(new DTEdicion(
                edicionIter.getNombre(),
                edicionIter.getSigla(),
                edicionIter.getFechaInicio(),
                edicionIter.getFechaFin(),
                edicionIter.getFechaAlta(),
                orgUser.getNombre(),
                edicionIter.getCiudad(),
                edicionIter.getPais(),
                edicionIter.getImagen(),
                edicionIter.getEstado(),
                dtEvento
            ));
        }
        return lista;
    }
    
    public void consultaUsuario(String nickname) throws UsuarioNoExisteException {
        Usuario user = manejador.findUsuario(nickname);
        if (user == null) {
            throw new UsuarioNoExisteException(nickname);
        }
        this.usuarioSeleccionadoNickname = nickname;
        // Lógica adicional según caso de uso
    }

    public DTRegistro obtenerDatosRegistros(String identificador) {
        DTRegistro dto = null;
        if (manejadorEv.existeRegistro(identificador)) {
            Registro reg = manejadorEv.obtenerRegistro(identificador);
            dto = new DTRegistro(
                reg.getId(),
                reg.getUsuario().getNickname(),
                reg.getEdicion().getNombre(),
                reg.getTipoRegistro().getNombre(),
                reg.getFechaRegistro(),
                reg.getCosto(),
                reg.getFechaInicio(),
                reg.getEdicion().getEvento().getNombre(),
                reg.getAsistencia()
            );
        }
        return dto;
    }

    public boolean esAsistente(String nickname) {
        return listarAsistentes() != null && listarAsistentes().containsKey(nickname);
    }

    // --- manejo de selección de Registro ---
    public void seleccionarRegistro(String ident) {
        if (!manejadorEv.existeRegistro(ident)) {
            throw new RegistroNoExiste(ident);
        }
        this.registroSeleccionadoId = ident;
    }

    public String getRegistroSeleccionadoId() {
        return registroSeleccionadoId;
    }

    // --- manejo de selección de Usuario ---
    public String getUsuarioSeleccionadoNickname() {
        return usuarioSeleccionadoNickname;
    }

    public void altaCategoriaSinGUI(String nombre) throws CategoriaYaExisteException {
        if (manejadorAux.existeCategoria(nombre)) {
            throw new CategoriaYaExisteException(nombre);
        }
        Categoria catIter = new Categoria(nombre);
        manejadorAux.agregarCategoria(catIter.getNombre(), catIter);
    }

    public Institucion getInstitucionPorNombre(String nombre) {
        return manejador.findInstitucion(nombre);
    }
    
    @Override
    public DTRegistro obtenerRegistroSeleccionado() {
        if (registroSeleccionadoId == null) {
            return null;
        }
        return obtenerDatosRegistros(registroSeleccionadoId);
    }

    @Override
    public void modificarDatosUsuario(String nickname, String nombre, String descripcion, String link,
                                      String apellido, LocalDate fechaNacimiento, String institucion,
                                      String imagen)
            throws UsuarioNoExisteException, UsuarioTipoIncorrectoException {

        Usuario user = manejador.findUsuario(nickname);
        if (user == null) {
            throw new UsuarioNoExisteException(nickname);
        }

        // nombre aplica a ambos roles
        user.setNombre(nombre);

        // actualizar imagen si viene no vacía
        if (imagen != null && !imagen.isBlank()) {
            user.setImagen(imagen);
        }

        if (user instanceof Organizador) {
            Organizador orgUser = (Organizador) user;
            orgUser.setDesc(descripcion);
            orgUser.setLink(link);

        } else if (user instanceof Asistente) {
            Asistente asisUser = (Asistente) user;
            asisUser.setApellido(apellido);
            asisUser.setFechaDeNacimiento(fechaNacimiento);

            if (institucion != null && !institucion.isBlank()) {
                Institucion inst = manejador.findInstitucion(institucion);
                asisUser.setInstitucion(inst);
            }
        } else {
            throw new UsuarioTipoIncorrectoException(nickname);
        }
    }
    public void modificarContrasenia(String nickname, String nuevaContrasena) throws UsuarioNoExisteException {
		Usuario user = manejador.findUsuario(nickname);
		if (user == null) {
			throw new UsuarioNoExisteException(nickname);
		}
		user.setContrasena(nuevaContrasena);
	}
    

    public boolean inicioSesion(String nickOrEmail, String contrasena) {
        Usuario usuario = manejador.findUsuario(nickOrEmail);
        if (usuario == null) {
            for (Usuario user : manejador.getUsuarios().values()) {
                if (user.getEmail().equals(nickOrEmail)) {
                    usuario = user;
                    break;
                }
            }
        }
        if (usuario == null) {
            return false;
        }
        if (usuario.getContrasena() == null || !usuario.getContrasena().equals(contrasena)) {
            return false;
        }
        return true;
    }

    /**
     * Caso de uso: cierre de sesión. Por ahora no realiza ninguna acción.
     */
    public void cierreSesion() {
        // No hace nada por ahora
    }
    
    @Override
    public boolean validarLogin(String nickOrEmail, String contrasena) {
        ManejadorUsuario manejadorUser = ManejadorUsuario.getInstancia();

        // Buscar por nickname o por correo electrónico
        Usuario user = manejadorUser.obtenerUsuarioPorNickOEmail(nickOrEmail);

        // Si no existe, devolvemos false directamente
        if (user == null) {
            return false;
        }

        // Si la contraseña está vacía o no coincide → false
        if (user.getContrasena() == null || !user.getContrasena().equals(contrasena)) {
            return false;
        }

        // Si llegó acá → es válido
        return true;
    }
	
	// --- Seguimiento entre usuarios ---
    @Override
    public boolean sigueA(String seguidor, String seguido) {
        if (seguidor == null || seguido == null) return false;
        Usuario u = manejador.findUsuario(seguidor);
        if (u == null) return false;
        return u.sigueA(seguido);
    }

    @Override
    public void seguirUsuario(String seguidor, String seguido) {
        if (seguidor == null || seguido == null) throw new IllegalArgumentException("Nick inválido");
        if (seguidor.equals(seguido)) return; // no follow self
        Usuario s = manejador.findUsuario(seguidor);
        Usuario t = manejador.findUsuario(seguido);
        if (s == null || t == null) throw new IllegalArgumentException("Usuario inexistente");
        if (s.sigueA(seguido)) return; // ya sigue
        s.addSeguido(seguido);
        t.addSeguidor(seguidor);
    }

    @Override
    public void dejarSeguirUsuario(String seguidor, String seguido) {
        if (seguidor == null || seguido == null) throw new IllegalArgumentException("Nick inválido");
        Usuario s = manejador.findUsuario(seguidor);
        Usuario t = manejador.findUsuario(seguido);
        if (s == null || t == null) throw new IllegalArgumentException("Usuario inexistente");
        if (!s.sigueA(seguido)) return;
        s.removeSeguido(seguido);
        t.removeSeguidor(seguidor);
    }

    @Override
    public int contarSeguidores(String nick) {
        if (nick == null) return 0;
        Usuario u = manejador.findUsuario(nick);
        if (u == null) return 0;
        return u.contarSeguidores();
    }

    @Override
    public int contarSeguidos(String nick) {
        if (nick == null) return 0;
        Usuario u = manejador.findUsuario(nick);
        if (u == null) return 0;
        return u.contarSeguidos();
    }

    public void marcarAsistencia(String nickname, String registroId) {
        Usuario user = manejador.findUsuario(nickname);
        Asistente asistente = (Asistente) user;
        Registro registro = asistente.getRegistros().get(registroId);
        asistente.addAsistencia(registroId, registro);
    }
}