package test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Manejadores – Singletons consistentes (tolerante mayúsc./minúsc.)")
class ManejadoresSingletonTest {

    @BeforeEach
    void reset() { TestUtils.resetAll(); }

    private static Method getGetter(Class<?> clazz) throws NoSuchMethodException {
        try { return clazz.getMethod("getInstancia"); } catch (NoSuchMethodException e) { return clazz.getMethod("getInstance"); }
    }

    @Test
    @DisplayName("ManejadorUsuario.getInstancia/getInstance → mismo objeto")
    void manejadorUsuarioSingleton() throws Exception {
        // Soporta logica.ManejadorUsuario y logica.manejadorUsuario
        Class<?> clazz = TestUtils.loadAny("logica.manejadores.ManejadorUsuario", "logica.manejadores.manejadorUsuario");
        Method getter = getGetter(clazz);
        Object objeto1 = getter.invoke(null);
        Object objeto2 = getter.invoke(null);
        assertNotNull(objeto1);
        assertSame(objeto1, objeto2, "ManejadorUsuario no es singleton (a != b)");
    }

    @Test
    @DisplayName("ManejadorEvento.getInstancia/getInstance → mismo objeto")
    void manejadorEventoSingleton() throws Exception {
        // Soporta logica.ManejadorEvento y logica.manejadorEvento
        Class<?> clazz = TestUtils.loadAny("logica.manejadores.ManejadorEvento", "logica.manejadores.manejadorEvento");
        Method getter = getGetter(clazz);
        Object objeto1 = getter.invoke(null);
        Object objeto2 = getter.invoke(null);
        assertNotNull(objeto1);
        assertSame(objeto1, objeto2, "ManejadorEvento no es singleton (a != b)");
    }
}
