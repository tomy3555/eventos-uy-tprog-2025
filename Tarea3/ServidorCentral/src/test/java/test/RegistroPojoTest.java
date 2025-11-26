package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import logica.clases.Registro;

@DisplayName("Registro – constructor, getters y setters")
class RegistroPojoTest {

    @Test
    @DisplayName("Ctor básico con objetos null + getters")
    void ctorBasicoNulls() {
        LocalDate fechaReg = LocalDate.of(2025, 5, 20);
        LocalDate fechaIni = LocalDate.of(2025, 6, 1);

        Registro r = new Registro(
                "R-123",
                /* usuario    */ null,
                /* edicion    */ null,
                /* tipoReg    */ null,
                /* fechaReg   */ fechaReg,
                /* costo      */ 1500.50f,
                /* fechaIni   */ fechaIni,
                null
        );

        assertEquals("R-123", r.getId());
        assertNull(r.getUsuario());
        assertNull(r.getEdicion());
        assertNull(r.getTipoRegistro());
        assertEquals(fechaReg, r.getFechaRegistro());
        assertEquals(1500.50f, r.getCosto(), 0.0001f);
        assertEquals(fechaIni, r.getFechaInicio());
    }

    @Test
    @DisplayName("Setters de escalares actualizan correctamente")
    void settersEscalares() {
        Registro r = new Registro("X", null, null, null,
                LocalDate.of(2025, 1, 1), 1.0f, LocalDate.of(2025, 1, 2),null);

        r.setId("R-999");
        r.setCosto(999.99f);
        LocalDate nuevaFechaReg = LocalDate.of(2025, 7, 7);
        LocalDate nuevaFechaIni = LocalDate.of(2025, 7, 10);
        r.setFechaRegistro(nuevaFechaReg);
        r.setFechaInicio(nuevaFechaIni);

        assertEquals("R-999", r.getId());
        assertEquals(999.99f, r.getCosto(), 0.0001f);
        assertEquals(nuevaFechaReg, r.getFechaRegistro());
        assertEquals(nuevaFechaIni, r.getFechaInicio());
    }

    @Test
    @DisplayName("Setters de asociaciones guardan la MISMA referencia (si se pueden instanciar por reflexión)")
    void settersAsociaciones_mismaReferencia_siDisponibles() throws Exception {
        // Intentamos crear instancias reflejando cualquier constructor disponible con valores por defecto.
        Object usuario   = tryMake("logica.clases.Usuario");
        Object edicion   = tryMake("logica.clases.Ediciones");
        Object tipoReg   = tryMake("logica.clases.TipoRegistro");

        // Si no podemos crear alguno, omitimos este test (no falla la suite por dependencias).
        assumeTrue(usuario != null && edicion != null && tipoReg != null,
                "No se pudieron instanciar Usuario/Ediciones/TipoRegistro por reflexión");

        Registro r = new Registro("R", null, null, null,
                LocalDate.of(2025, 1, 1), 0f, LocalDate.of(2025, 1, 2),null);

        // Usamos los setters públicos reales (tipados) vía reflexión
        Method setUsuario = Registro.class.getMethod("setUsuario", usuario.getClass().getSuperclass() == null
                ? usuario.getClass() : usuario.getClass().getSuperclass());
        Method setEdicion = Registro.class.getMethod("setEdicion", edicion.getClass().getSuperclass() == null
                ? edicion.getClass() : edicion.getClass().getSuperclass());
        Method setTipoReg = Registro.class.getMethod("setTipoRegistro", tipoReg.getClass().getSuperclass() == null
                ? tipoReg.getClass() : tipoReg.getClass().getSuperclass());

        // OJO: los setters están tipados con las clases exactas del paquete,
        // así que mejor obtenemos los métodos por nombre y parámetro exacto:
        setUsuario = Registro.class.getMethod("setUsuario", Class.forName("logica.clases.Usuario"));
        setEdicion = Registro.class.getMethod("setEdicion", Class.forName("logica.clases.Ediciones"));
        setTipoReg = Registro.class.getMethod("setTipoRegistro", Class.forName("logica.clases.TipoRegistro"));

        setUsuario.invoke(r, usuario);
        setEdicion.invoke(r, edicion);
        setTipoReg.invoke(r, tipoReg);

        // Ahora pedimos los getters también por reflexión (para no depender de tipos en compile-time)
        Method getUsuario = Registro.class.getMethod("getUsuario");
        Method getEdicion = Registro.class.getMethod("getEdicion");
        Method getTipoReg = Registro.class.getMethod("getTipoRegistro");

        assertSame(usuario, getUsuario.invoke(r), "Debe conservar la misma instancia de Usuario");
        assertSame(edicion, getEdicion.invoke(r), "Debe conservar la misma instancia de Ediciones");
        assertSame(tipoReg, getTipoReg.invoke(r), "Debe conservar la misma instancia de TipoRegistro");
    }

    // ───────────────────────── helpers ─────────────────────────

    /**
     * Intenta instanciar una clase por reflexión usando el primer constructor disponible,
     * rellenando cada parámetro con un valor por defecto razonable.
     * Si falla, retorna null.
     */
    private static Object tryMake(String fqcn) {
        try {
            Class<?> c = Class.forName(fqcn);
            Constructor<?>[] ctors = c.getDeclaredConstructors();
            if (ctors.length == 0) return null;

            // Tomamos el primer constructor accesible
            Constructor<?> ctor = ctors[0];
            ctor.setAccessible(true);

            Class<?>[] params = ctor.getParameterTypes();
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                Class<?> p = params[i];
                args[i] = defaultFor(p);
            }
            return ctor.newInstance(args);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Valores por defecto simples para tipos comunes. */
    private static Object defaultFor(Class<?> p) {
        if (!p.isPrimitive()) {
            if (p == String.class) return "";
            if (p == LocalDate.class) return LocalDate.now();
            // Para cualquier otro objeto, probamos con null (evita acoplar dependencias)
            return null;
        }
        if (p == boolean.class) return false;
        if (p == byte.class) return (byte) 0;
        if (p == short.class) return (short) 0;
        if (p == int.class) return 0;
        if (p == long.class) return 0L;
        if (p == float.class) return 0f;
        if (p == double.class) return 0d;
        if (p == char.class) return '\0';
        return null;
    }
}
