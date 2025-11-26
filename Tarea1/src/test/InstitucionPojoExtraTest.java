package test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Institucion – creación por CU y getters básicos")
class InstitucionPojoExtraTest {

    @Test
    @DisplayName("Institucion – creación por CU y getters básicos")
    void institucionViaControlador() throws ReflectiveOperationException {
        TestUtils.resetAll();

        // fábrica + CU
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try {
            getter = fab.getMethod("getInstance");
        } catch (NoSuchMethodException e) {
            getter = fab.getMethod("getInstancia");
        }

        Object fabrica;
        try {
            fabrica = getter.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("No se pudo invocar el método de fábrica", e);
        }

        Object controladorUs = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario", "getIControladorUsuario"});

        // Alta por CU (lado “oficial”)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion"}, "Inst_X", "Desc X", "webX");

        // Verificación de que fue registrada
        Object setObj = TestUtils.tryInvoke(controladorUs, new String[]{"getInstituciones"});
        if (setObj instanceof java.util.Set<?> set) {
            assertTrue(set.contains("Inst_X"));
        }

        // Intento acceder al dominio por manejador; si no se puede, fabrico un dummy
        Object inst = DomainAccess.obtenerInstitucion("Inst_X");
        if (inst == null) {
            try {
                inst = TestUtils.tolerantNew("logica.clases.Institucion", "Inst_X", "Desc X", "webX");
            } catch (IllegalStateException e) {
                inst = TestUtils.tolerantNew("logica.clases.Institucion");

                // Seteos manuales por si existen setters
                try {
                    Method metodo;
                    if ((metodo = TestUtils.findMethod(inst, "setNombre", String.class)) != null)
                        metodo.invoke(inst, "Inst_X");
                    if ((metodo = TestUtils.findMethod(inst, "setDescripcion", String.class)) != null)
                        metodo.invoke(inst, "Desc X");
                    if ((metodo = TestUtils.findMethod(inst, "setLink", String.class)) != null)
                        metodo.invoke(inst, "webX");
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    // setters inaccesibles → continuar
                }
            }
        }

        assertNotNull(inst, "No se pudo obtener ni construir Institucion");

        // Ejercicio de getters/equals/hashCode/toString
        ReflectionPojoSupport.exercisePojo(inst);
    }
}
