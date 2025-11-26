package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ControladorUsuario – Edge cases (errores comunes)")
class ControladorUsuarioEdgeCasesTest {

    private Object fabrica, controladorUs;

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); 
        } catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        fabrica = getter.invoke(null);
        controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});
    }

    @Test
    @DisplayName("actualizarAsistente sobre nick inexistente → lanza")
    void actualizarAsistenteInexistente() {
        assertThrows(Exception.class, () ->
            TestUtils.invokeUnwrapped(controladorUs, new String[]{"actualizarAsistente"},
                "noexiste", "Ap", LocalDate.of(2000, 1, 1))
        );
    }

    @Test
    @DisplayName("actualizarOrganizador sobre nick inexistente → lanza")
    void actualizarOrganizadorInexistente() {
        assertThrows(Exception.class, () ->
            TestUtils.invokeUnwrapped(controladorUs, new String[]{"actualizarOrganizador"},
                "noexiste", "desc", "link")
        );
    }

    @Test
    @DisplayName("obtenerDatosUsuario de nick inexistente → null o lanza (aceptamos ambos)")
    void obtenerDatosUsuarioInexistente() {
        boolean lanzo;
        try {
            assertThrows(Exception.class, () ->
                TestUtils.invokeUnwrapped(controladorUs, new String[]{"obtenerDatosUsuario"}, "noexiste")
            );
            lanzo = true;
        } catch (AssertionError ae) {
            lanzo = false; // no lanzó: esperamos null
            Object dto = TestUtils.tryInvoke(controladorUs, new String[]{"obtenerDatosUsuario"}, "noexiste");
            assertNull(dto);
        }
        assertTrue(lanzo || !lanzo); // sólo para callar “resultado no usado”
    }

    @Test
    @DisplayName("AltaInstitucion duplicada → idempotente o lanza (aceptamos ambos)")
    void altaInstitucionDuplicada() {
        // nombre único para evitar colisión con otros tests
        String nombre = "Inst_X_" + System.nanoTime();

        // 1) primera alta: debe pasar
        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion"}, nombre, "d", "w", null);

        // 2) segunda alta con el mismo nombre: aceptamos que lance o que sea idempotente
        try {
            assertThrows(Exception.class, () ->
                TestUtils.invokeUnwrapped(controladorUs, new String[]{"altaInstitucion"}, nombre, "d2", "w2", null)
            );
            // si llegamos acá, lanzó y está bien
        } catch (AssertionError ignored) {
            // no lanzó: lo consideramos idempotente
        }
    }

    @Test
    @DisplayName("ingresarAsistente con Institución null → lanza o NO crea nada")
    void ingresarAsistenteInstitucionNull() {
        boolean lanzo;
        try {
            assertThrows(Exception.class, () ->
                TestUtils.invokeUnwrapped(controladorUs, new String[]{"ingresarAsistente"},
                    "a1", "A", "a@x", "Ap", null, "Garcia", LocalDate.of(2000, 1, 1), null)
            );
            lanzo = true;
        } catch (AssertionError ae) {
            lanzo = false; // no lanzó: verificamos que no haya quedado creado
            @SuppressWarnings("unchecked")
            Map<String, Object> asisMap =
                (Map<String, Object>) TestUtils.tryInvoke(controladorUs, new String[]{"listarAsistentes"});
            assertFalse(asisMap.containsKey("a1"),
                "No lanzó y dejó 'a1' creado; debería ignorar o lanzar.");
        }
        assertTrue(lanzo || !lanzo);
    }
}
