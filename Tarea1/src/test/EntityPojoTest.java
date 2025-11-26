package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("Entidades básicas – constructores/getters")
class EntityPojoTest {

    @Test
    @DisplayName("Usuario (vía controlador)")
    void usuario() throws Exception {
        TestUtils.resetAll();

        // Fábrica y CU
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        java.lang.reflect.Method get;
        try { get = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { get = fab.getMethod("getInstancia"); }
        Object fabrica = get.invoke(null);
        Object controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});

        // IDs únicos para evitar colisiones entre corridas
        String INST = "Inst_Pojos_" + System.nanoTime();
        String NICK = "u1";
        String MAIL = "u1_" + System.nanoTime() + "@x";

        // Institución + AltaUsuario (asistente, con 11 params)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion", "AltaInstitucion"}, INST, "d", "w");
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario", "AltaUsuario"},
                NICK, "U Uno", MAIL, "desc", "link",
                "Ap", LocalDate.of(1999, 1, 1), INST, false, null, null);

        // Obtenemos el dominio y lo ejercitamos
        Object usuario = DomainAccess.obtenerUsuario(NICK);
        assumeTrue(usuario != null, "No se pudo obtener Usuario dominio");
        ReflectionPojoSupport.exercisePojo(usuario);
    }

    @Test
    @DisplayName("Asistente (vía controlador)")
    void asistente() throws Exception {
        TestUtils.resetAll();

        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        java.lang.reflect.Method get;
        try { get = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { get = fab.getMethod("getInstancia"); }

        Object fabrica = get.invoke(null);
        Object controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});

        String INST = "Inst_Pojos_" + System.nanoTime();
        String NICK = "a1";
        String MAIL = "a1_" + System.nanoTime() + "@x";

        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion", "AltaInstitucion"}, INST, "d", "w");
        Object inst = DomainAccess.obtenerInstitucion(INST);

        Object asis = null;
        // Preferimos ingresarAsistente si está disponible (firma larga)
        try {
            asis = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                    NICK, "A Uno", MAIL,   // nickname, nombre, email
                    null, null,            // contrasena, imagen (opcionales en tu lógica)
                    "Ap",                  // apellido
                    LocalDate.of(2000, 1, 1),
                    inst                   // objeto Institucion
            );
        } catch (RuntimeException ignored) {
            // Fallback: crear como asistente por altaUsuario (11 params, flag false)
            asis = TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario", "AltaUsuario"},
                    NICK, "A Uno", MAIL, "desc", "link",
                    "Ap", LocalDate.of(2000, 1, 1), INST, false, null, null);
        }

        assertNotNull(asis);
        ReflectionPojoSupport.exercisePojo(asis);
    }

    @Test
    @DisplayName("Organizador (vía controlador)")
    void organizador() throws Exception {
        TestUtils.resetAll();

        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        java.lang.reflect.Method get;
        try { get = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { get = fab.getMethod("getInstancia"); }
        Object fabrica = get.invoke(null);
        Object controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});

        String INST = "Inst_Pojos_" + System.nanoTime();
        String NICK = "o1";
        String MAIL = "o1_" + System.nanoTime() + "@x";

        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion", "AltaInstitucion"}, INST, "d", "w");
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario", "AltaUsuario"},
                NICK, "O Uno", MAIL, "desc", "link",
                "Ap", LocalDate.of(1990, 1, 1), INST, true, null, null);

        Object org = DomainAccess.obtenerUsuario(NICK);
        assumeTrue(org != null, "No se pudo obtener Organizador dominio");
        ReflectionPojoSupport.exercisePojo(org);
    }
}
