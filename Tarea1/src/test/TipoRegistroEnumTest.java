package test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("TipoRegistro – existe y es usable (enum o clase)")
class TipoRegistroEnumTest {

    @Test
    void tipoRegistroExisteUsable() throws Exception {
        Class<?> clase = Class.forName("logica.Clases.TipoRegistro");
        assertNotNull(clase, "No existe logica.Clases.TipoRegistro");

        if (clase.isEnum()) {
            Object[] vals = clase.getEnumConstants();
            assertNotNull(vals);
            assertTrue(vals.length >= 1, "Enum sin constantes");
        } else {
            Object object = ReflectionPojoSupport.makeInstance("logica.Clases.TipoRegistro");
            assertNotNull(object, "No se pudo instanciar TipoRegistro (clase)");
            ReflectionPojoSupport.exercisePojo(object);
        }
    }
}