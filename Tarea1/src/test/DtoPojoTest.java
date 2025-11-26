package test;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("DTOs – constructores y getters (reflexión)")
class DtoPojoTest {

    @Test 
    @DisplayName("DTCategorias")
    void dtCategorias() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTCategorias");
        assumeTrue(objeto != null, "No se pudo instanciar DTCategorias");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTDAtosUsuario / DTDatosUsuario")
    void dtDatosUsuario() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.DTDatosUsuario");
        assumeTrue(objeto != null, "No se pudo instanciar DTDatosUsuario");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTEdicion")
    void dtEdicion() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTEdicion");
        assumeTrue(objeto != null, "No se pudo instanciar DTEdicion");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTEvento")
    void dtEvento() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTEvento");
        assumeTrue(objeto != null, "No se pudo instanciar DTEvento");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTNivel")
    void dtNivel() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTNivel");
        assumeTrue(objeto != null, "No se pudo instanciar DTNivel");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTPatrocinio")
    void dtPatrocinio() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTPatrocinio");
        assumeTrue(objeto != null, "No se pudo instanciar DTPatrocinio");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTRegistro")
    void dtRegistro() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTRegistro");
        assumeTrue(objeto != null, "No se pudo instanciar DTRegistro");
        ReflectionPojoSupport.exercisePojo(objeto);
    }

    @Test 
    @DisplayName("DTTipoRegistro")
    void dtTipoRegistro() {
        Object objeto = ReflectionPojoSupport.makeInstance("logica.Datatypes.DTTipoRegistro");
        assumeTrue(objeto != null, "No se pudo instanciar DTTipoRegistro");
        ReflectionPojoSupport.exercisePojo(objeto);
    }
}
