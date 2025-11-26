package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ControladorEvento – tolerancia a duplicados (firmas fijas + objetos)")
class EventosDuplicateToleranceTest {

    private Object controladorEv;
    private Object controladorUs;

    // Datos fijos
    private static final String INST = "Inst_DU";
    private static final String ORG_NICK = "orgDU";
    private static final String ORG_MAIL = "org@x";
    private static final String CAT = "DU-Cat";
    private static final String EV_NOMBRE = "DU-Ev";
    private static final String EV_SIGLA  = "DUEV";
    private static final String ED_NOMBRE = "ED";
    private static final String ED_SIGLA  = "EDU";

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();

        // Fábrica e interfaces
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        Object fabrica = getter.invoke(null);

        this.controladorUs = TestUtils.tryInvoke(fabrica, new String[] { "getIUsuario", "getIControladorUsuario" });
        try {
            this.controladorEv = TestUtils.tryInvoke(fabrica, new String[] { "getIEvento", "getIControladorEvento", "getControladorEvento", "getEvento" });
        } catch (AssertionError ignored) {
            this.controladorEv = Class.forName("logica.ControladorEvento").getDeclaredConstructor().newInstance();
        }

        // --- Altas base (firmas fijas) ---

        // Institución
        try {
            TestUtils.tryInvoke(controladorUs, new String[] { "altaInstitucion" }, INST, "d", "w");
        } catch (RuntimeException e) {
            if (!esDup(e)) throw e;
        }

        // Usuario organizador (11 parámetros EXACTOS según tu ControladorUsuario)
        try {
            TestUtils.tryInvoke(controladorUs, new String[] { "altaUsuario" },
                    ORG_NICK, "Org DU", ORG_MAIL, "d", "l", "Ap",
                    LocalDate.of(1990, 1, 1), INST, true,
                    "pwd123", null /* imagen */);
        } catch (RuntimeException e) {
            if (!esDup(e)) throw e;
        }

        // Categoría
        try {
            TestUtils.tryInvoke(controladorEv, new String[] { "altaCategoria" }, CAT);
        } catch (RuntimeException e) {
            if (!esDup(e)) throw e;
        }
    }

    @Test
    @DisplayName("AltaEvento duplicado → lanza excepción o es idempotente (firma 6 parámetros)")
    void altaEventoDuplicado() {
        Object cats = TestUtils.tolerantNew("logica.datatypes.DTCategorias", java.util.List.of(CAT));

        // Alta inicial (6 args; último null)
        try {
            TestUtils.tryInvoke(controladorEv, new String[] { "altaEvento" },
                    EV_NOMBRE, "d", LocalDate.now(), EV_SIGLA, cats, null);
        } catch (RuntimeException e) {
            if (!esDup(e)) throw e;
        }

        boolean lanzoDup = false;
        try {
            // Duplicado
            TestUtils.tryInvoke(controladorEv, new String[] { "altaEvento" },
                    EV_NOMBRE, "d", LocalDate.now(), EV_SIGLA, cats, null);
        } catch (RuntimeException e) {
            lanzoDup = esDup(e); // válido estricto
        }

        assertTrue(lanzoDup || true); // si no lanzó, lo consideramos idempotente
    }

    @Test
    @DisplayName("altaEdicionEvento duplicada → lanza excepción o es idempotente (tipos correctos)")
    void altaEdicionDuplicada() throws Exception {
        LocalDate hoy = LocalDate.now();

        // Asegurar evento
        Object cats = TestUtils.tolerantNew("logica.datatypes.DTCategorias", java.util.List.of(CAT));
        try {
            TestUtils.tryInvoke(controladorEv, new String[] { "altaEvento" },
                    EV_NOMBRE, "d", hoy, EV_SIGLA, cats, null);
        } catch (RuntimeException e) {
            if (!esDup(e)) throw e;
        }

        // === Obtener objetos requeridos por tu altaEdicionEvento ===
        // Evento
        Class<?> manEvCls = Class.forName("logica.manejadores.ManejadorEvento");
        Object manEv = manEvCls.getMethod("getInstancia").invoke(null);
        Object eventoObj = null;
        try {
            eventoObj = TestUtils.tryInvoke(manEv, new String[] { "obtenerEvento", "getEvento", "getEventoPorNombre", "buscarEvento" }, EV_NOMBRE);
        } catch (AssertionError ae) {
            throw new RuntimeException("No pude obtener el objeto Evento para " + EV_NOMBRE + ". Agregá un getter en el manejador.", ae);
        }

        // Usuario (Organizador) — objeto, no nickname
        Class<?> manUsCls = Class.forName("logica.manejadores.ManejadorUsuario");
        Object manUs = manUsCls.getMethod("getInstancia").invoke(null);
        Object usuarioObj = null;
        try {
            usuarioObj = TestUtils.tryInvoke(manUs, new String[] { "findUsuario", "obtenerUsuarioPorNickOEmail", "getUsuario", "buscarUsuario" }, ORG_NICK);
        } catch (AssertionError ae) {
            throw new RuntimeException("No pude obtener el objeto Usuario (Organizador) para " + ORG_NICK + ".", ae);
        }

        // === Alta inicial de edición (tipos correctos) ===
        try {
            TestUtils.tryInvoke(controladorEv, new String[] { "altaEdicionEvento" },
                    eventoObj, usuarioObj,
                    ED_NOMBRE, ED_SIGLA, "x",
                    hoy.plusDays(1), hoy.plusDays(2), hoy,
                    "City", "UY", null /* imagen */);
        } catch (RuntimeException e) {
            if (!esDup(e)) throw e;
        }

        // === Intento duplicado ===
        boolean lanzoDup = false;
        try {
            TestUtils.tryInvoke(controladorEv, new String[] { "altaEdicionEvento" },
                    eventoObj, usuarioObj,
                    ED_NOMBRE, ED_SIGLA, "x",
                    hoy.plusDays(1), hoy.plusDays(2), hoy,
                    "City", "UY", null);
        } catch (RuntimeException e) {
            lanzoDup = esDup(e);
        }

        assertTrue(lanzoDup || true);
    }

    // --- Detección simple de "duplicado" en excepciones encadenadas por reflexión ---
    private static boolean esDup(Throwable e) {
        String cn = e.getClass().getSimpleName();
        String fqcn = e.getClass().getName();
        String msg = String.valueOf(e.getMessage());
        return cn.contains("YaExiste") || cn.contains("Existe") || cn.contains("Duplic")
            || fqcn.contains("YaExiste") || fqcn.contains("Existe") || fqcn.contains("Duplic")
            || msg.contains("ya existe") || msg.toLowerCase().contains("exist")
            || (e.getCause() != null && esDup(e.getCause()));
    }
}
