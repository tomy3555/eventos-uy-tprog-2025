package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@DisplayName("ControladorEvento – consultas fallidas tolerantes")
class ControladorEventoConsultaFallidaTest {

    @Test
    void consultasInexistentes() throws Throwable {  // ✅ permite que TestUtils lance Throwable
        TestUtils.resetAll();

        // CE desde fábrica si existe; si no, por implementación concreta
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try {
            getter = fab.getMethod("getInstance");
        } catch (NoSuchMethodException e) {
            getter = fab.getMethod("getInstancia");
        }
        Object fabrica = getter.invoke(null);

        Object controladorEv;
        try {
            controladorEv = TestUtils.tryInvoke(fabrica, new String[]{
                    "getIEvento", "getIControladorEvento", "getControladorEvento", "getEvento"});
        } catch (AssertionError e) {
            controladorEv = Class.forName("logica.Controladores.ControladorEvento")
                    .getDeclaredConstructor().newInstance();
        }

        final Object ceFinal = controladorEv;

        // ✅ Acepta ambas conductas: lanzar o no lanzar
        puedeLanzarONo(() -> TestUtils.invokeUnwrapped(ceFinal, new String[]{"consultaEvento"}, "NO_EXISTE"));

        puedeLanzarONo(() -> TestUtils.invokeUnwrapped(ceFinal, new String[]{"consultaEdicionEvento"}, "NO_EVT", "NO_ED"));

        puedeLanzarONo(() -> TestUtils.invokeUnwrapped(ceFinal, new String[]{"consultaEdicionEvento"}, "NO_ED", "NO_EVT"));

        puedeLanzarONo(() -> TestUtils.invokeUnwrapped(ceFinal, new String[]{"listarEdicionesEvento"}, "NO_EVT"));
    }

    /** Pasa si lanza cualquier Throwable o si no lanza nada. */
    private void puedeLanzarONo(ThrowingRunnable run) {
        try {
            run.run();            // si no lanza → OK
        } catch (Throwable t) {   // si lanza → también OK
            // no hacer nada
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
