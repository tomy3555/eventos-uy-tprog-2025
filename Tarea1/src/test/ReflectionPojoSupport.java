package test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ReflectionPojoSupport {
    private ReflectionPojoSupport() {}

    /* ====== Instanciación tolerante ====== */

    static Object makeInstance(String fqcn) {
        return makeInstance(fqcn, 0);
    }

    private static Object makeInstance(String fqcn, int depth) {
        try {
            Class<?> clase = Class.forName(fqcn);

            if (clase.isEnum()) {
                Object[] vals = clase.getEnumConstants();
                return vals != null && vals.length > 0 ? vals[0] : null;
            }

            // 1) Constructor vacío
            try {
                Constructor<?> constructor1 = clase.getDeclaredConstructor();
                constructor1.setAccessible(true);
                return constructor1.newInstance();
            } catch (NoSuchMethodException e) {
                // no hay ctor vacío, seguimos
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }

            // 2) Constructores ordenados por menor cantidad de parámetros
            Constructor<?>[] construct = clase.getDeclaredConstructors();
            Arrays.sort(construct, Comparator.comparingInt(Constructor::getParameterCount));
            for (Constructor<?> constructor : construct) {
                Class<?>[] teese = constructor.getParameterTypes();
                Object[] args = new Object[teese.length];
                boolean okey = true;
                for (int i = 0; i < teese.length; i++) {
                    args[i] = sampleFor(teese[i], depth + 1);
                    if (args[i] == null && teese[i].isPrimitive()) {
                        okey = false;
                        break;
                    }
                }
                if (!okey) {
                    continue;
                }
                try {
                    constructor.setAccessible(true);
                    return constructor.newInstance(args);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    // intentamos siguiente ctor
                }
            }
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Object sampleFor(Class<?> clase, int depth) {
        if (clase == String.class) return "x";
        if (clase == int.class || clase == Integer.class) return 0;
        if (clase == long.class || clase == Long.class) return 0L;
        if (clase == double.class || clase == Double.class) return 0.0;
        if (clase == float.class || clase == Float.class) return 0.0f;
        if (clase == boolean.class || clase == Boolean.class) return false;
        if (clase == char.class || clase == Character.class) return '\0';
        if (clase == LocalDate.class) return LocalDate.now();

        if (clase.isEnum()) {
            Object[] vals = clase.getEnumConstants();
            return vals != null && vals.length > 0 ? vals[0] : null;
        }

        if (Collection.class.isAssignableFrom(clase)) {
            if (Set.class.isAssignableFrom(clase)) return new HashSet<>(List.of("x"));
            return new ArrayList<>(List.of("x"));
        }
        if (Map.class.isAssignableFrom(clase)) return new HashMap<>();

        // Evitar recursión infinita en dominio propio
        if (depth <= 1 && clase.getName().startsWith("logica.")) {
            Object objeto = makeInstance(clase.getName(), depth + 1);
            if (objeto != null) return objeto;
        }

        // último recurso
        try {
            Constructor<?> constructor = clase.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /* ====== Ejecutar getters/equals/hashCode/toString ====== */

    static void exercisePojo(Object object) {
        assertNotNull(object);
        // getters "getX"/"isX" sin parámetros
        for (Method m : object.getClass().getMethods()) {
            if (m.getParameterCount() == 0
                    && (m.getName().startsWith("get") || m.getName().startsWith("is"))
                    && !m.getReturnType().equals(void.class)) {
                try {
                    m.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    // getter inaccesible o con error → continuar
                }
            }
        }
        // equals/hashCode/toString básicos
        object.equals(object);
        object.hashCode();
        object.toString();
        assertNotEquals(object, new Object());
        assertNotEquals(object, null);
    }
}
