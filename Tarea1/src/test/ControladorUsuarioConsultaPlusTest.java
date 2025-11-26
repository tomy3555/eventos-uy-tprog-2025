package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;            // ← ESTA es la clave

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@DisplayName("ControladorUsuario – ConsultaUsuario/obtenerDatos/updates encadenados")
class ControladorUsuarioConsultaPlusTest {

    private Object controladorUs;
    private String instNombre; // ← guardamos el nombre para reusar

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();

        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        Object fabrica = getter.invoke(null);

        controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});

        // nombre único por corrida (evita InstitucionYaExisteException)
        instNombre = "Inst_CUP_" + System.nanoTime();

        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion"}, instNombre, "d", "w");
    }

    @Test
    @DisplayName("ConsultaUsuario + obtenerDatosUsuario tras varias actualizaciones")
    void consultaYDatos() {
        // alta asistente (ajuste a 11 params con null, null)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                "cupi", "Cu Pi", "cupi@x", "d", "l", "Ap0",
                LocalDate.of(2000, 1, 1), instNombre, false, null, null);

        // actualizar asistente
        TestUtils.tryInvoke(controladorUs, new String[]{"actualizarAsistente"},
                "cupi", "Ap1", LocalDate.of(2001, 1, 1));

        // no debe lanzar
        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorUs, new String[]{"consultaUsuario"}, "cupi")
        );

        // obtener y verificar
        Object dto = TestUtils.tryInvoke(controladorUs, new String[]{"obtenerDatosUsuario"}, "cupi");
        assertNotNull(dto);

        var mAp = TestUtils.findMethod(dto, "getApellido", "apellido");
        var mFn = TestUtils.findMethod(dto, "getFechaNacimiento", "getNacimiento", "fechaNacimiento");

        if (mAp != null) assertEquals("Ap1", String.valueOf(assertDoesNotThrow(() -> mAp.invoke(dto))));
        if (mFn != null) assertEquals(LocalDate.of(2001, 1, 1),
                assertDoesNotThrow(() -> (LocalDate) mFn.invoke(dto)));
    }

    @Test
    @DisplayName("Cambiar a organizador y actualizarOrganizador")
    void cambioYUpdateOrganizador() {
        // alta organizador (11 params)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                "orgx", "Org X" , "orgx@x", "d0", "l0", "Ap",
                LocalDate.of(1990, 1, 1), instNombre, true, null, null);

        // actualizar
        TestUtils.tryInvoke(controladorUs, new String[]{"actualizarOrganizador"}, "orgx", "d1", "l1");

        Object dto = TestUtils.tryInvoke(controladorUs, new String[]{"obtenerDatosUsuario"}, "orgx");
        assertNotNull(dto);

        var mDesc = TestUtils.findMethod(dto, "getDescripcion", "descripcion");
        var mLink = TestUtils.findMethod(dto, "getLink", "link", "getWeb");

        if (mDesc != null) assertEquals("d1", String.valueOf(assertDoesNotThrow(() -> mDesc.invoke(dto))));
        if (mLink != null) assertEquals("l1", String.valueOf(assertDoesNotThrow(() -> mLink.invoke(dto))));
    }

}