package test;

import logica.clases.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    @Test
    void constructor_y_get_set_funcionan() {
        Categoria c = new Categoria("Tecnología");
        assertEquals("Tecnología", c.getNombre());

        c.setNombre("Cultura");
        assertEquals("Cultura", c.getNombre());
    }
}