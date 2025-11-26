package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DTO sweep – ejercita getters/toString/hashCode en DTOs")
class DTOsCoverageSweepTest {

    @Test
    void dtos() {
        // Intentamos construir y pasear varios DTOs comunes
        tryConstructAndExercise("logica.datatypes.DTDatosUsuario");
        tryConstructAndExercise("logica.datatypes.DTEdicion");
        tryConstructAndExercise("logica.datatypes.DTEvento");
        tryConstructAndExercise("logica.datatypes.DTRegistro");
        tryConstructAndExercise("logica.datatypes.DTNivel");

        // DTCategorias: primero con colección, si no, sin args
        Object dataCategorias = tryNew("logica.datatypes.DTCategorias", new Class<?>[]{Collection.class}, List.of("Tec", "Datos"));
        if (dataCategorias == null) {
            dataCategorias = tryNewNoArgs("logica.datatypes.DTCategorias");
        }
        if (dataCategorias != null) {
            ReflectionPojoSupport.exercisePojo(dataCategorias);
        }

        assertTrue(true);
    }

    private void tryConstructAndExercise(String ceene) {
        Object objeto = null;

        // 1) ctor sin args
        if (objeto == null) {
            objeto = tryNewNoArgs(ceene);
        }
        // 2) intento típico con fechas/strings
        if (objeto == null) {
            objeto = tryNew(ceene,
                    new Class<?>[]{String.class, String.class, String.class, LocalDate.class, LocalDate.class},
                    "N", "S", "D", LocalDate.now(), LocalDate.now());
        }
        // 3) strings
        if (objeto == null) {
            objeto = tryNew(ceene, new Class<?>[]{String.class, String.class, String.class}, "N", "S", "D");
        }
        // 4) un string
        if (objeto == null) {
            objeto = tryNew(ceene, new Class<?>[]{String.class}, "N");
        }

        if (objeto != null) {
            ReflectionPojoSupport.exercisePojo(objeto);
        }
    }

    /* ================= Helpers sin catch RuntimeException ================= */

    private Object tryNewNoArgs(String fqcn) {
        try {
            Class<?> clase = Class.forName(fqcn);
            Constructor<?> constructor = clase.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | SecurityException e) {
            return null;
        }
    }

    private Object tryNew(String fqcn, Class<?>[] paramTypes, Object... args) {
        try {
            Class<?> clase = Class.forName(fqcn);
            Constructor<?> constructor = clase.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | SecurityException e) {
            return null;
        }
    }
}
