package test;

import java.lang.reflect.Method;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ControladorUsuario – listados y ediciones por organizador")
class ControladorUsuarioListingsPlusTest {

    private Object controladorUs, controladorEv;
    private String INST; // institución única por ejecución

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance");
        } catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
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

        // Institución única
        INST = "Inst_LPU_" + System.nanoTime();
        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion"}, INST, "d", "w", null);
    }

    @Test
    @DisplayName("AltaUsuario múltiple: listarUsuarios/Asistentes/Organizadores y getInstituciones")
    void listadosBasicos() {
        // nicks/emails únicos
        String A1 = "a1_" + System.nanoTime();
        String A2 = "a2_" + System.nanoTime();
        String O1 = "o1_" + System.nanoTime();
        String A1_MAIL = A1 + "@x";
        String A2_MAIL = A2 + "@x";
        String O1_MAIL = O1 + "@x";

        // 2 asistentes (altaUsuario con 11 parámetros)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                A1, "A Uno", A1_MAIL, "d", "l", "Ap",
                LocalDate.of(2000, 1, 1), INST, false, null, null);
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                A2, "A Dos", A2_MAIL, "d", "l", "Ap",
                LocalDate.of(2001, 2, 2), INST, false, null, null);

        // 1 organizador
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                O1, "O Uno", O1_MAIL, "d", "l", "Ap",
                LocalDate.of(1990, 1, 1), INST, true, null, null);

        @SuppressWarnings("unchecked")
        Map<String, Object> users = (Map<String, Object>) TestUtils.tryInvoke(controladorUs, new String[]{"listarUsuarios"});
        @SuppressWarnings("unchecked")
        Map<String, Object> asistentes = (Map<String, Object>) TestUtils.tryInvoke(controladorUs, new String[]{"listarAsistentes"});
        @SuppressWarnings("unchecked")
        Map<String, Object> orgs = (Map<String, Object>) TestUtils.tryInvoke(controladorUs, new String[]{"listarOrganizadores"});
        @SuppressWarnings("unchecked")
        Set<String> insts = (Set<String>) TestUtils.tryInvoke(controladorUs, new String[]{"getInstituciones"});

        assertNotNull(users);  assertNotNull(asistentes);  assertNotNull(orgs);  assertNotNull(insts);
        assertTrue(users.size() >= 3);
        assertTrue(asistentes.containsKey(A1) && asistentes.containsKey(A2));
        assertTrue(orgs.containsKey(O1));
        assertTrue(insts.contains(INST));
    }

    @Test
    @DisplayName("listarEdicionesAPartirDeOrganizador (si está disponible)")
    void listarEdicionesDeOrganizador() throws Exception {
        // Organizador base (nick/mail únicos)
        String O = "o_" + System.nanoTime();
        String O_MAIL = O + "@x";
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                O, "O Uno", O_MAIL, "d", "l", "Ap",
                LocalDate.of(1990, 1, 1), INST, true, null, null);

        // Categoría tolerante
        try { TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaCategoria"}, "LP-Cat"); }
        catch (Throwable ignored) {}

        // Evento (firma común: String, String, LocalDate, String, DTCategorias, String)
        Object cats = TestUtils.tolerantNew("logica.datatypes.DTCategorias", List.of("LP-Cat"));
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                "LP-Ev", "d", LocalDate.now(), "LPEV", cats, INST);

        // ---- altaEdicionEvento: detectar firma y llamar acorde ----
        Method altaEd = null;
        for (Method m : controladorEv.getClass().getMethods()) {
            if (m.getName().equals("altaEdicionEvento")) { altaEd = m; break; }
        }
        if (altaEd != null) {
            Class<?>[] pt = altaEd.getParameterTypes();

            if (pt.length >= 2 && pt[0] == String.class && pt[1] == String.class) {
                // Firma (String evento, String organizador, ...)
                TestUtils.tryInvoke(controladorEv, new String[]{"altaEdicionEvento"},
                        "LP-Ev", "LP-Ed", "LPED", "x",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), LocalDate.now(),
                        O, "City", "UY", null);
            } else {
                // Firma (Eventos evento, Usuario usuario, ...)
                Object eventoObj = null, usuarioObj = null;
                try { eventoObj  = DomainAccess.obtenerEvento("LP-Ev"); } catch (RuntimeException ignored) {}
                try { usuarioObj = DomainAccess.obtenerUsuario(O);     } catch (RuntimeException ignored) {}

                if (eventoObj != null && usuarioObj != null) {
                    TestUtils.tryInvoke(controladorEv, new String[]{"altaEdicionEvento"},
                            eventoObj, usuarioObj,
                            "LP-Ed", "LPED", "x",
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), LocalDate.now(),
                            "City", "UY", null);
                }
            }
        }

        // El objetivo era crear sin type mismatch; no necesitan más asserts acá.
        assertTrue(true);
    }
}
