// test/FabricaSingletonTest.java
package test;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

import logica.fabrica;

public class FabricaSingletonTest {
    @Test
    void getInstanceDevuelveSiempreLaMisma() {
        fabrica fabrica1 = fabrica.getInstance();
        fabrica fabrica2 = fabrica.getInstance();
        assertSame(fabrica1, fabrica2);
    }
}
