package test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import logica.fabrica;
import logica.interfaces.IControladorUsuario;

class SanityTest {
    @Test
    void fabricaDisponible() {
        IControladorUsuario cu = fabrica.getInstance().getIControladorUsuario();
        assertNotNull(cu);
    }
}