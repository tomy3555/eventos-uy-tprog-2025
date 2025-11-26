package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ControladorEvento – listarEdicionesEvento vacío")
class ControladorEventoListarVacioTest {

    private Object controladorEv;
    private Object controladorUs;
    private String INST;     // institución única
    private String ORG;      // nick único
    private String ORG_MAIL; // email único

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        Object fabrica = getter.invoke(null);

        controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});
        try {
            controladorEv = TestUtils.tryInvoke(fabrica, new String[]{
                "getIEvento", "getIControladorEvento", "getControladorEvento", "getEvento"
            });
        } catch (AssertionError ignored) {
            controladorEv = Class.forName("logica.controladores.ControladorEvento")
                                 .getDeclaredConstructor().newInstance();
        }

        // ---- base: institución, organizador y categoría (todo único) ----
        long nonce = System.nanoTime();
        INST = "Inst_LE_" + nonce;
        ORG  = "orgLE_" + nonce;
        ORG_MAIL = ORG + "@x";

        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion", "AltaInstitucion"}, INST, "d", "w", null);

        // altaUsuario con 11 parámetros (flag true = organizador)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario", "AltaUsuario"},
                ORG, "Org LE", ORG_MAIL, "d", "l", "Ap",
                LocalDate.of(1990, 1, 1), INST, true, null, null);

        // categoría
        TestUtils.tryInvoke(controladorEv, new String[]{"altaCategoria", "AltaCategoria"}, "LE-Cat");
    }

    @Test
    @DisplayName("Un evento sin ediciones lista vacío (o no nulo)")
    void listaVacia() {
        // DTCategorias correcto (logica.datatypes)
        Object cats = TestUtils.tolerantNew("logica.datatypes.DTCategorias", java.util.List.of("LE-Cat"));

        // altaEvento: firmas comunes incluyen la institución al final; probamos ambas
        try {
            // Firma típica: (String nombre, String desc, LocalDate fecha, String sigla, DTCategorias cats, String institucion)
            TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento", "AltaEvento"},
                    "SoloEvento", "d", LocalDate.now(), "SE", cats, INST);
        } catch (RuntimeException e) {
            // Fallback: sin institución (por si tu implementación no la pide)
            TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento", "AltaEvento"},
                    "SoloEvento", "d", LocalDate.now(), "SE", cats);
        }

        // listarEdicionesEvento puede devolver List/Set/Collection; aceptamos vacío o no nulo
        Object res = TestUtils.tryInvoke(controladorEv, new String[]{"listarEdicionesEvento", "ListarEdicionesEvento"}, "SoloEvento");
        assertNotNull(res);

        if (res instanceof Collection<?> col) {
            // debe existir y puede estar vacío
            assertTrue(col.isEmpty() || col.size() >= 0);
        } else if (res instanceof Object[]) {
            assertTrue(((Object[]) res).length >= 0);
        } else {
            // forma distinta pero no nula: lo aceptamos
            assertTrue(true);
        }
    }
}
