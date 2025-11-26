package publicadores;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;

import logica.datatypes.DTDatosUsuario;
import logica.datatypes.DTRegistro;
import logica.fabrica;
import logica.interfaces.IControladorUsuario;
import excepciones.UsuarioYaExisteException;
import excepciones.UsuarioNoExisteException;
import excepciones.UsuarioTipoIncorrectoException;
import excepciones.InstitucionYaExisteException;
import excepciones.CategoriaYaExisteException;
import util.ConfigLoader;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class PublicadorUsuario {

    private Endpoint endpoint = null;
    private IControladorUsuario icu = fabrica.getInstance().getIControladorUsuario();

    @WebMethod(exclude = true)
    public void publicar() {
        String ip = ConfigLoader.get("ipServidor");
        String puerto = ConfigLoader.get("puerto");
        String address = "http://" + ip + ":" + puerto + "/publicadorUsuario";

        endpoint = Endpoint.publish(address, this);
        System.out.println("Servicio PublicadorUsuario publicado en: " + address);
        System.out.println("WSDL disponible en: " + address + "?wsdl");
    }


    /* =============================
       Métodos del servicio (sin cambios)
       ============================= */

    @WebMethod
    public void altaUsuario(
        @WebParam(name = "nickname") String nickname,
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "correo") String correo,
        @WebParam(name = "descripcion") String descripcion,
        @WebParam(name = "link") String link,
        @WebParam(name = "apellido") String apellido,
        @WebParam(name = "fechaNacimientoISO") String fechaNacimientoISO, 
        @WebParam(name = "institucion") String institucion,
        @WebParam(name = "esOrganizador") boolean esOrganizador,
        @WebParam(name = "contrasena") String contrasena,
        @WebParam(name = "imagen") String imagen
    ) throws UsuarioYaExisteException {

        java.time.LocalDate ld = null;
        if (fechaNacimientoISO != null && !fechaNacimientoISO.isBlank()) {
            ld = java.time.LocalDate.parse(fechaNacimientoISO); // "yyyy-MM-dd"
        }

        icu.altaUsuario(nickname, nombre, correo, descripcion, link,
                apellido, ld, institucion, esOrganizador, contrasena, imagen);
    }

    @WebMethod
    public DTDatosUsuario obtenerDatosUsuario(
        @WebParam(name = "nickname") String nickname
    ) throws UsuarioNoExisteException {
        return icu.obtenerDatosUsuario(nickname);
    }

    @WebMethod
    public boolean validarLogin(
        @WebParam(name = "nickOrEmail") String nickOrEmail,
        @WebParam(name = "contrasena") String contrasena
    ) {
        return icu.validarLogin(nickOrEmail, contrasena);
    }

    @WebMethod
    public void altaInstitucion(
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "descripcion") String descripcion,
        @WebParam(name = "link") String link
    ) throws InstitucionYaExisteException {
        icu.altaInstitucion(nombre, descripcion, link);
    }

    @WebMethod
    public void altaCategoriaSinGUI(
        @WebParam(name = "nombre") String nombre
    ) throws CategoriaYaExisteException {
        icu.altaCategoriaSinGUI(nombre);
    }

    @WebMethod
    public void modificarDatosUsuario(
        @WebParam(name = "nickname") String nickname,
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "descripcion") String descripcion,
        @WebParam(name = "link") String link,
        @WebParam(name = "apellido") String apellido,
        @WebParam(name = "fechaNacimiento") String fechaNacimiento,
        @WebParam(name = "institucion") String institucion,
        @WebParam(name = "imagen") String imagen
    ) throws UsuarioNoExisteException, UsuarioTipoIncorrectoException {
        LocalDate ld = null;
        if (fechaNacimiento != null && !fechaNacimiento.isBlank()) {
            ld = LocalDate.parse(fechaNacimiento);
        }
        icu.modificarDatosUsuario(nickname, nombre, descripcion, link, apellido, ld, institucion, imagen);
    }
    
    @WebMethod
    public DTDatosUsuario[] obtenerUsuariosDT() throws UsuarioNoExisteException {
        java.util.Set<DTDatosUsuario> usuarios = icu.obtenerUsuariosDT();
        return usuarios.toArray(new DTDatosUsuario[0]);
    }

    @WebMethod
    public void seguirUsuario(
        @WebParam(name = "seguidor") String seguidor,
        @WebParam(name = "seguido") String seguido
    ) {
        icu.seguirUsuario(seguidor, seguido);
    }

    @WebMethod
    public void dejarSeguirUsuario(
        @WebParam(name = "seguidor") String seguidor,
        @WebParam(name = "seguido") String seguido
    ) {
        icu.dejarSeguirUsuario(seguidor, seguido);
    }

    @WebMethod
    public String[] listarInstituciones() {
        java.util.Set<String> instituciones = icu.getInstituciones();
        return instituciones.toArray(new String[0]);
    }

    @WebMethod
    public String[] getInstituciones() {
        Set<String> instituciones = icu.getInstituciones();
        return instituciones.toArray(new String[0]);
    }

    @WebMethod
    public boolean esAsistente(
        @WebParam(name = "nickname") String nickname
    ) {
        return icu.esAsistente(nickname);
    }

    @WebMethod
    public boolean sigueA(
        @WebParam(name = "seguidor") String seguidor,
        @WebParam(name = "seguido") String seguido
    ) {
        return icu.sigueA(seguidor, seguido);
    }

    @WebMethod
    public DTRegistro obtenerDatosRegistros(
        @WebParam(name = "identificador") String identificador
    ) {
        return icu.obtenerDatosRegistros(identificador);
    }

    @WebMethod
    public void marcarAsistencia(
        @WebParam(name = "nickname") String nickname,
        @WebParam(name = "registroId") String registroId
    ) throws UsuarioNoExisteException, UsuarioTipoIncorrectoException, excepciones.RegistroNoExiste {
        icu.marcarAsistencia(nickname, registroId);
    }
    
    @WebMethod
    public void modificarContrasenia(
        @WebParam(name = "nickname") String nickname,
        @WebParam(name = "nuevaContrasenia") String nuevaContrasenia
    ) throws UsuarioNoExisteException {
        icu.modificarContrasenia(nickname, nuevaContrasenia);
    }

    @WebMethod(exclude = true)
    public Endpoint getEndpoint() {
        return endpoint;
    }
}