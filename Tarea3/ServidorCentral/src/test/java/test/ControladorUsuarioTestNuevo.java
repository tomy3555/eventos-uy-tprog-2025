package test;

import excepciones.*;
import logica.clases.*;
import logica.manejadores.ManejadorAuxiliar;
import logica.manejadores.ManejadorEvento;
import logica.manejadores.ManejadorUsuario;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import logica.controladores.*;

import static org.junit.jupiter.api.Assertions.*;


public class ControladorUsuarioTestNuevo {

    private ControladorUsuario ctrl;
    
    @BeforeEach
    void limpiarEstado() {
        ManejadorUsuario.getInstancia().reset();
        ManejadorAuxiliar.getInstancia().reset();
        ManejadorEvento.getInstancia().reset();
    }

    @BeforeEach
    void setUp() {
        ManejadorUsuario.getInstancia();
        ManejadorEvento.getInstancia();
        ManejadorAuxiliar.getInstancia();
        ctrl = new ControladorUsuario();
    }

    // --------------------------------------------------------
    // TESTS DE ALTA DE USUARIO
    // --------------------------------------------------------

    @Test
    void altaUsuario_organizador_OK() throws Exception {
        ctrl.altaUsuario("nick1", "Nombre", "mail1@test.com", "desc", "link",
                null, null, null, true, "123", "C:/imgs/foto.png");

        var usuarios = ctrl.listarUsuarios();
        assertTrue(usuarios.containsKey("nick1"));
        assertTrue(usuarios.get("nick1") instanceof Organizador);
    }

    @Test
    void altaUsuario_asistente_OK() throws Exception {
        ctrl.altaInstitucion("Facultad", "desc", "link");

        ctrl.altaUsuario("nick2", "Nom", "mail2@test.com", null, null,
                "Apellido", LocalDate.of(2000,1,1), "Facultad", false, "123", "foto.png");

        var u = ctrl.listarUsuarios().get("nick2");
        assertTrue(u instanceof Asistente);
        assertEquals("Apellido", ((Asistente)u).getApellido());
    }

    @Test
    void altaUsuario_duplicadoNick_lanzaExcepcion() throws Exception {
        ctrl.altaUsuario("nick", "n", "m1", null, null, null, null, null, true, "123", null);
        assertThrows(UsuarioYaExisteException.class, () ->
                ctrl.altaUsuario("nick", "n2", "m2", null, null, null, null, null, true, "123", null));
    }

    @Test
    void altaUsuario_duplicadoCorreo_lanzaExcepcion() throws Exception {
        ctrl.altaUsuario("nick1", "n", "mail@test.com", null, null, null, null, null, true, "123", null);
        assertThrows(UsuarioYaExisteException.class, () ->
                ctrl.altaUsuario("nick2", "n", "mail@test.com", null, null, null, null, null, false, "123", null));
    }

    // --------------------------------------------------------
    // TESTS DE INSTITUCIONES
    // --------------------------------------------------------

    @Test
    void altaInstitucion_OK() throws Exception {
        ctrl.altaInstitucion("Facultad", "desc", "link");
        assertTrue(ctrl.getInstituciones().contains("Facultad"));
    }

    @Test
    void altaInstitucion_duplicada_lanzaExcepcion() throws Exception {
        ctrl.altaInstitucion("Facu", "desc", "link");
        assertThrows(InstitucionYaExisteException.class, () ->
                ctrl.altaInstitucion("Facu", "desc2", "link2"));
    }

    // --------------------------------------------------------
    // TESTS DE ACTUALIZAR DATOS
    // --------------------------------------------------------

    @Test
    void actualizarAsistente_OK() throws Exception {
        ctrl.altaInstitucion("Inst", "desc", "link");
        ctrl.altaUsuario("nickA", "n", "m", null, null, "ape", LocalDate.of(1990,1,1),
                "Inst", false, "123", null);

        ctrl.actualizarAsistente("nickA", "nuevoApe", LocalDate.of(2000,1,1));

        Asistente a = (Asistente) ctrl.listarUsuarios().get("nickA");
        assertEquals("nuevoApe", a.getApellido());
        assertEquals(LocalDate.of(2000,1,1), a.getFechaDeNacimiento());
    }

    @Test
    void actualizarOrganizador_OK() throws Exception {
        ctrl.altaUsuario("org", "n", "m", "d", "l", null, null, null, true, "123", null);

        ctrl.actualizarOrganizador("org", "nuevaDesc", "nuevoLink");

        Organizador o = (Organizador) ctrl.listarUsuarios().get("org");
        assertEquals("nuevaDesc", o.getDesc());
        assertEquals("nuevoLink", o.getLink());
    }

    // --------------------------------------------------------
    // TESTS DE LOGIN
    // --------------------------------------------------------

    @Test
    void inicioSesion_porNick_OK() throws Exception {
        ctrl.altaUsuario("nick", "n", "m", "d", "l", null, null, null, true, "123", null);
        assertTrue(ctrl.inicioSesion("nick", "123"));
    }

    @Test
    void inicioSesion_porEmail_OK() throws Exception {
        ctrl.altaUsuario("nick", "n", "mail@x.com", "d", "l", null, null, null, true, "123", null);
        assertTrue(ctrl.inicioSesion("mail@x.com", "123"));
    }

    @Test
    void inicioSesion_contraIncorrecta_false() throws Exception {
        ctrl.altaUsuario("nick", "n", "m", "d", "l", null, null, null, true, "123", null);
        assertFalse(ctrl.inicioSesion("nick", "mal"));
    }

    // --------------------------------------------------------
    // TESTS DE SEGUIR / DEJAR DE SEGUIR
    // --------------------------------------------------------

    @Test
    void seguirYDejarSeguirUsuario_OK() throws Exception {
        ctrl.altaUsuario("a", "n", "m1", null, null, null, null, null, true, "123", null);
        ctrl.altaUsuario("b", "n", "m2", null, null, null, null, null, true, "123", null);

        ctrl.seguirUsuario("a", "b");
        assertTrue(ctrl.sigueA("a", "b"));
        assertEquals(1, ctrl.contarSeguidos("a"));
        assertEquals(1, ctrl.contarSeguidores("b"));

        ctrl.dejarSeguirUsuario("a", "b");
        assertFalse(ctrl.sigueA("a", "b"));
        assertEquals(0, ctrl.contarSeguidos("a"));
    }

    // --------------------------------------------------------
    // TESTS DE MODIFICAR DATOS GENERALES
    // --------------------------------------------------------

    @Test
    void modificarDatosUsuario_asistente_OK() throws Exception {
        ctrl.altaInstitucion("I", "d", "l");
        ctrl.altaUsuario("nick", "n", "m", null, null, "a", LocalDate.now(), "I", false, "123", null);

        ctrl.modificarDatosUsuario("nick", "nuevoNom", null, null, "ap2",
                LocalDate.of(2010,1,1), "I", "img.jpg");

        Asistente a = (Asistente) ctrl.listarUsuarios().get("nick");
        assertEquals("nuevoNom", a.getNombre());
        assertEquals("img.jpg", a.getImagen());
        assertEquals("ap2", a.getApellido());
    }

    @Test
    void modificarDatosUsuario_organizador_OK() throws Exception {
        ctrl.altaUsuario("org", "n", "m", "d", "l", null, null, null, true, "123", null);

        ctrl.modificarDatosUsuario("org", "nuevoN", "desc2", "link2", null, null, null, null);

        Organizador o = (Organizador) ctrl.listarUsuarios().get("org");
        assertEquals("nuevoN", o.getNombre());
        assertEquals("desc2", o.getDesc());
        assertEquals("link2", o.getLink());
    }

    // --------------------------------------------------------
    // TESTS DE CONTRASEÑA Y EXCEPCIONES
    // --------------------------------------------------------

    @Test
    void modificarContrasenia_OK() throws Exception {
        ctrl.altaUsuario("nick", "n", "m", null, null, null, null, null, true, "123", null);
        ctrl.modificarContrasenia("nick", "nueva");
        assertTrue(ctrl.inicioSesion("nick", "nueva"));
    }

    @Test
    void modificarContrasenia_noExiste_lanzaExcepcion() {
        assertThrows(UsuarioNoExisteException.class, () ->
                ctrl.modificarContrasenia("inexistente", "nueva"));
    }

}
