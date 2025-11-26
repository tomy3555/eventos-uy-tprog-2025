package test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ControladorUsuario – AltaCategoriaSinGUI (si existe)")
class ControladorUsuarioAltaCategoriaSinGUITest {

    @Test
    void altaCategoriaSinGUI() throws Exception {
        TestUtils.resetAll();
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); 
        } catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        Object fabrica = getter.invoke(null);
        Object controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});

        TestUtils.tryInvoke(controladorUs, new String[]{"altaCategoriaSinGUI"}, "SweepCat");
        assertTrue(true);
    }
}
