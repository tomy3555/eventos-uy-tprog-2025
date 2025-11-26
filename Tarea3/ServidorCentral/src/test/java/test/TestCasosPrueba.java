package test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import excepciones.UsuarioYaExisteException;
import logica.CargaDatosPrueba;
import logica.fabrica;
import logica.controladores.ControladorEvento;
import logica.interfaces.IControladorUsuario;
import logica.manejadores.ManejadorAuxiliar;
import logica.manejadores.ManejadorEvento;
import logica.manejadores.ManejadorUsuario;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestCasosPrueba {

    private static IControladorUsuario cUsuario;
    private static ManejadorAuxiliar mAux;
    private static ManejadorUsuario mUsr;
    private static ManejadorEvento mEv;

    @BeforeAll
    static void cargarDatosPrueba() throws Exception {
        ManejadorUsuario.reset();
        ManejadorEvento.reset();
        ManejadorAuxiliar.reset();
        
        // Instancias
        cUsuario = fabrica.getInstance().getIControladorUsuario();
        mAux     = ManejadorAuxiliar.getInstancia();
        mUsr     = ManejadorUsuario.getInstancia();
        mEv      = ManejadorEvento.getInstancia();

        // Semillas
        CargaDatosPrueba.cargarInstitucionesEjemplo();
        CargaDatosPrueba.cargarCategorias();
        CargaDatosPrueba.cargarEventosEjemplo();
        CargaDatosPrueba.cargarUsuariosEjemplo();
        CargaDatosPrueba.cargarEdicionesEjemplo();
        CargaDatosPrueba.cargarTipoRegistroEjemplo();
        CargaDatosPrueba.cargarPatrociniosEjemplo();
        CargaDatosPrueba.cargarRegistrosEjemplo();
        CargaDatosPrueba.logResumenDatos();
    }

    @Test @Order(1)
    void testCategoriaYaExiste() {
        assertTrue(mAux.existeCategoria("Tecnología"));
        assertFalse(mAux.existeCategoria("NoExiste"));
    }

    @Test @Order(2)
    void testUsuarioYaExisteNickname() {
        assertThrows(
            UsuarioYaExisteException.class,
            () -> cUsuario.altaUsuario(
                    "atorres",
                    "Ana",
                    "atorres@gmail.com",
                    null,
                    null,
                    "Torres",
                    LocalDate.of(1990, 5, 12),
                    "Facultad de Ingeniería",
                    false,
                    "contrasena123",
                    "imagen.jpg"
            )
        );
    }

    @Test @Order(3)
    void testUsuarioYaExisteEmail() {
        UsuarioYaExisteException ex = assertThrows(
            UsuarioYaExisteException.class,
            () -> cUsuario.altaUsuario(
                    "paniTorres",
                    "pani",
                    "atorres@gmail.com",
                    null,
                    null,
                    "Torres",
                    LocalDate.of(1990, 5, 12),
                    "Facultad de Ingeniería",
                    false,
                    "contrasena123",
                    "imagen.jpg"
            )
        );
        assertEquals("El usuario con correo atorres@gmail.com ya esta registrado", ex.getMessage());
    }

    @Test @Order(4)
    void testInstitucionYaExistente() {
        assertTrue(mUsr.existeInstitucion("ORT Uruguay"));
    }

    @Test @Order(5)
    void testEventoYaExiste() {
        assertTrue(mEv.existeEvento("Montevideo Comics"));
    }

    @Test @Order(6)
    void testEventoNoExiste() {
        assertFalse(mEv.existeEvento("eventoPrueba"));
    }

    @Test @Order(7)
    void testConsultaUsuarioNoExistente() {
        // Versión sin excepciones: verifico que no esté en el manejador
        assertFalse(mUsr.getUsuarios().containsKey("usuarioQueNoExiste"));
        // Si tuvieras un método que lanza excepción, podrías usar:
        // assertThrows(UsuarioNoExisteException.class, () -> cUsuario.obtenerDatosUsuario("usuarioQueNoExiste"));
    }
}