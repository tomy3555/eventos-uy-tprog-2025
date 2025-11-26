package logica.interfaces;

import excepciones.UsuarioYaExisteException;
import excepciones.InstitucionYaExisteException;
import excepciones.UsuarioNoExisteException;
import excepciones.UsuarioTipoIncorrectoException;
import excepciones.CategoriaYaExisteException;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import logica.clases.Asistente;
import logica.clases.Institucion;
import logica.clases.Organizador;
import logica.clases.Usuario;
import logica.datatypes.DTDatosUsuario;
import logica.datatypes.DTRegistro;


public interface IControladorUsuario {

    // Crear usuarios
    public void altaUsuario(String nickname, String nombre, String correo, String descripcion, String link, String apellido, LocalDate fechaNacimiento, String institucion, boolean esOrganizador, String contrasena, String imagen) throws UsuarioYaExisteException;
    public Organizador ingresarOrganizador(String nickname, String nombre, String email, String contrasena, String imagen, String desc, String link);
    public Asistente ingresarAsistente(String nickname, String nombre, String email, String contrasena, String imagen, String apellido, LocalDate fechaDeNacimiento, Institucion institucion);
    
    // Crear institución
    
    public void altaInstitucion(String nombre, String descripcion, String link)throws InstitucionYaExisteException;
    // Overload: allow optional imagen filename for institution logo
    public void altaInstitucion(String nombre, String descripcion, String link, String imagen) throws InstitucionYaExisteException;

    // Listados
    public Map<String, Usuario> listarUsuarios();
    public Map<String, Asistente> listarAsistentes();
    public Map<String, Organizador> listarOrganizadores();
    public Set<String> getInstituciones();

    // Actualizaciones
    public void actualizarAsistente(String nickname, String apellido, LocalDate fechaNacimiento) throws UsuarioYaExisteException, UsuarioTipoIncorrectoException, UsuarioNoExisteException;

    public void actualizarOrganizador(String nickname, String desc, String link) throws UsuarioNoExisteException, UsuarioTipoIncorrectoException;

    // Consulta
    public Set<DTDatosUsuario> obtenerUsuariosDT() throws UsuarioNoExisteException;
    public DTDatosUsuario obtenerDatosUsuario(String nickname) throws UsuarioNoExisteException;
    public Set<DTRegistro> obtenerRegistrosAsistente(Asistente asist);
    public DTRegistro obtenerDatosRegistros(String identificador);
    public boolean esAsistente(String nickname);

    // Categorías
    public void altaCategoriaSinGUI(String nombre) throws CategoriaYaExisteException;
    

    public void seleccionarRegistro(String identificador);

    public String getRegistroSeleccionadoId();
    public DTRegistro obtenerRegistroSeleccionado();
    public String getUsuarioSeleccionadoNickname();
    public void modificarDatosUsuario(
            String nickname,
            String nombre,
            String descripcion,
            String link,
            String apellido,
            java.time.LocalDate fechaNacimiento,
            String institucion,
            String imagen
    ) throws excepciones.UsuarioNoExisteException, excepciones.UsuarioTipoIncorrectoException;

    // Inicio de sesión
    public boolean inicioSesion(String nickOrEmail, String contrasena);

    // Cierre de sesión
    public void cierreSesion();
    
    public boolean validarLogin(String nickOrEmail, String contrasena);
    public void modificarContrasenia(String nickname, String nuevaContrasena) throws UsuarioNoExisteException;

    // --- Seguimiento entre usuarios ---
    public boolean sigueA(String seguidor, String seguido);
    public void seguirUsuario(String seguidor, String seguido);
    public void dejarSeguirUsuario(String seguidor, String seguido);
    public int contarSeguidores(String nick);
    public int contarSeguidos(String nick);
    
    public void marcarAsistencia(String nickname, String registroId);
}