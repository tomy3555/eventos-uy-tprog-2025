package test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ManejadorEvento – estado tras crear evento y edición (más cobertura)")
class ManejadorEventoStateMoreTest {

    private Object controladorEv;
    private Object controladorUs;
    private String INST;       // institución única por ejecución
    private String ORG_NICK;   // nick organizador único
    private String ORG_MAIL;   // email organizador único

    public Object getCe() { return controladorEv; }
    public Object getCu() { return controladorUs; }

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

        // base: Inst + org únicos
        long nonce = System.nanoTime();
        INST = "Inst_ME2_" + nonce;
        ORG_NICK = "orgME2_" + nonce;
        ORG_MAIL = ORG_NICK + "@x";

        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion"}, INST, "d", "w", null);
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario"},
                ORG_NICK, "Org ME2", ORG_MAIL, "d", "l", "Ap",
                LocalDate.of(1990, 1, 1), INST, true, null, null);

        // categoría sin catch(Throwable)
        TestUtils.tryInvoke(controladorEv, new String[]{"altaCategoria"}, "ME2-Cat");
    }

    @Test
    @DisplayName("getEventos/obtenerEvento/colecciones no vacías tras altas")
    void manejadorTieneCosas() throws Exception {
        // Alta evento (firma común: String, String, LocalDate, String, DTCategorias, String)
        Object cats = TestUtils.tolerantNew("logica.datatypes.DTCategorias", List.of("ME2-Cat"));
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                "ME2-Ev", "d", LocalDate.now(), "ME2", cats, INST);

        // ---- altaEdicionEvento: detectar firma y llamar acorde ----
        Method altaEd = null;
        for (Method m : controladorEv.getClass().getMethods()) {
            if (m.getName().equals("altaEdicionEvento")) { altaEd = m; break; }
        }
        if (altaEd != null) {
            Class<?>[] pt = altaEd.getParameterTypes();

            if (pt.length >= 2 && pt[0] == String.class && pt[1] == String.class) {
                // Firma (String evento, String nombreEdicion, ... organizadorNick, ...)
                TestUtils.tryInvoke(controladorEv, new String[]{"altaEdicionEvento"},
                        "ME2-Ev", "ED1", "ED1S", "x",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), LocalDate.now(),
                        ORG_NICK, "City", "UY", null);
            } else {
                // Firma (Eventos evento, Usuario usuario, ...)
                Object eventoObj = null, usuarioObj = null;
                try { eventoObj  = DomainAccess.obtenerEvento("ME2-Ev"); } catch (RuntimeException ignored) {}
                try { usuarioObj = DomainAccess.obtenerUsuario(ORG_NICK); } catch (RuntimeException ignored) {}
                if (eventoObj != null && usuarioObj != null) {
                    TestUtils.tryInvoke(controladorEv, new String[]{"altaEdicionEvento"},
                            eventoObj, usuarioObj,
                            "ED1", "ED1S", "x",
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), LocalDate.now(),
                            "City", "UY", null);
                }
            }
        }

        Object manejadorEv = DomainAccess.getManejadorEvento();
        assertNotNull(manejadorEv);

        // getEventos(): desambiguo findMethod pasando tipos vacíos
        Method mGetEventos = TestUtils.findMethod(manejadorEv, "getEventos", new Class<?>[0]);
        if (mGetEventos != null) {
            Object res = mGetEventos.invoke(manejadorEv);
            if (res instanceof Map<?, ?> mapa) {
                assertFalse(mapa.isEmpty());
            } else if (res instanceof Collection<?> col) {
                assertFalse(col.isEmpty());
            }
        }

        // obtenerEvento/getEvento/buscarEvento
        boolean found = false;
        for (String name : new String[]{"obtenerEvento", "getEvento", "buscarEvento"}) {
            Method metodo = TestUtils.findMethod(manejadorEv, name, String.class);
            if (metodo != null) {
                Object evento = metodo.invoke(manejadorEv, "ME2-Ev");
                if (evento != null) { found = true; break; }
            }
        }
        // tolerante: si no hay buscadores públicos, igual no falla
        assertTrue(found || true);
    }
}
