package test;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class TestUtils {
    private TestUtils() {}

    /* ---------- Invocación flexible ---------- */

    /**
     * Flexible finder that supports two common usages from tests:
     *  - findMethod(target, "name1", "name2", ...)
     *  - findMethod(target, "methodName", Class<?>... paramTypes) or
     *    findMethod(target, "methodName", new Class<?>[0])
     */
    static Method findMethod(Object target, Object... namesOrTypes) {
        if (target == null || namesOrTypes == null || namesOrTypes.length == 0) return null;

        // Case A: (target, methodName, Class<?>... paramTypes) OR (target, methodName, Class<?>[])
        if (namesOrTypes.length >= 2) {
            // If the second argument is a Class array (common call: new Class<?>[0])
            if (namesOrTypes[1] instanceof Class<?>[]) {
                String name = String.valueOf(namesOrTypes[0]);
                Class<?>[] params = (Class<?>[]) namesOrTypes[1];
                try {
                    Method m = target.getClass().getMethod(name, params);
                    m.setAccessible(true);
                    return m;
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }

            // Or if all remaining args are Class objects (varargs style)
            boolean allClasses = true;
            for (int i = 1; i < namesOrTypes.length; i++) {
                if (!(namesOrTypes[i] instanceof Class<?>)) { allClasses = false; break; }
            }
            if (allClasses) {
                String name = String.valueOf(namesOrTypes[0]);
                Class<?>[] params = new Class<?>[namesOrTypes.length - 1];
                for (int i = 1; i < namesOrTypes.length; i++) params[i - 1] = (Class<?>) namesOrTypes[i];
                try {
                    Method m = target.getClass().getMethod(name, params);
                    m.setAccessible(true);
                    return m;
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        }

        // Case B: treat all arguments as alternative method NAMES (strings)
        for (Object o : namesOrTypes) {
            String name = String.valueOf(o);
            for (Method method : target.getClass().getMethods()) {
                if (method.getName().equals(name)) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    static Object tryInvoke(Object target, String[] names, Object... args) {
        Method method = findMethod(target, names);
        if (method == null) {
            String all = Arrays.stream(target.getClass().getMethods()).map(Method::getName).toList().toString();
            fail("No se encontró ninguno de: " + String.join(", ", names) +
                 " en " + target.getClass().getName() + ". Públicos: " + all);
        }
        try { return method.invoke(target, args); 
        } catch (InvocationTargetException e) { throw new RuntimeException(e.getTargetException()); 
        } catch (IllegalAccessException | IllegalArgumentException e) { throw new RuntimeException(e); }
    }

    /* ---------- Carga tolerante de clases (soporta fabrica minúscula) ---------- */

    static Class<?> loadAny(String... names) {
        for (String n : names) {
            try { 
            	return Class.forName(n); 
            	} catch (ClassNotFoundException e) { continue; 
            	} catch (LinkageError e) { continue; } // clase encontrada pero no cargable: probamos siguiente
        }
        throw new RuntimeException("No pude cargar ninguna clase: " + Arrays.toString(names));
    }

    /* ---------- Reset SOLO de singletons reales (Fábrica y Manejadores) ---------- */

    static void resetSingleton(Class<?> clase) {
        for (var fname : new String[] { "instancia", "instance" }) {
            try {
                Field field = clase.getDeclaredField(fname);
                field.setAccessible(true);
                field.set(null, null);
                return;
            } catch (NoSuchFieldException e) {
                continue; // probamos el siguiente nombre de campo
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void resetAll() {
        for (String cn : List.of(
                "logica.Fabrica",
                "logica.fabrica",
                "logica.ManejadorUsuario",
                "logica.manejadorUsuario",
                "logica.ManejadorEvento",
                "logica.manejadorEvento"
        )) {
            try { resetSingleton(Class.forName(cn)); 
            } catch (ClassNotFoundException e) { continue; 
            } catch (LinkageError e) { continue; }
        }
    }

    /* ---------- Constructores tolerantes (por si hace falta crear DTOs) ---------- */

    static Object tolerantNew(String fqcn, Object... args) {
        try {
            Class<?> clase = Class.forName(fqcn);
            outer:
            for (Constructor<?> construct : clase.getDeclaredConstructors()) {
                if (construct.getParameterCount() != args.length) { continue; }
                Class<?>[] teese = construct.getParameterTypes();
                for (int i = 0; i < teese.length; i++) {
                    if (args[i] == null) { continue; }
                    if (!teese[i].isAssignableFrom(args[i].getClass())) { continue outer; }
                }
                construct.setAccessible(true);
                return construct.newInstance(args);
            }
            Constructor<?> construct1 = clase.getDeclaredConstructor();
            construct1.setAccessible(true);
            return construct1.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Clase no encontrada: " + fqcn, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No hay constructor compatible en " + fqcn, e);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static Object getFromPrivateMaps(Object holder, String key, String... fields) {
        Class<?> clase = holder.getClass();
        for (String fname : fields) {
            try {
                Field field = clase.getDeclaredField(fname);
                field.setAccessible(true);
                Object objeto = field.get(holder);
                if (objeto instanceof Map<?, ?> mapa) {
                    return ((Map<String, Object>) mapa).get(key);
                }
            } catch (NoSuchFieldException e) {
                continue; // probamos siguiente campo
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    // Lanza la excepción original (sin envolver) para poder usar assertThrows con su tipo real.
    static Object invokeUnwrapped(Object target, String[] names, Object... args) throws Exception {
        Method method = findMethod(target, names);
        if (method == null) {
            String all = Arrays.stream(target.getClass().getMethods()).map(Method::getName).toList().toString();
            throw new AssertionError("No se encontró ninguno de: " + String.join(", ", names) +
                                     " en " + target.getClass().getName() + ". Públicos: " + all);
        }
        try { return method.invoke(target, args); 
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Exception) throw (Exception) t;
            if (t instanceof Error) throw (Error) t;
            throw new RuntimeException(t);
        } catch (IllegalAccessException | IllegalArgumentException e) { throw new RuntimeException(e); }
    }
}