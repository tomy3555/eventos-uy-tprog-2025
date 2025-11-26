package test;

import logica.clases.Institucion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstitucionTest {

    @Test
    void constructor_inicializa_campos_y_imagen_null() {
        Institucion inst = new Institucion(
                "Facultad de Ingeniería",
                "Facultad pública de ingeniería",
                "https://www.fing.edu.uy"
        );

        assertEquals("Facultad de Ingeniería", inst.getNombre());
        assertEquals("Facultad pública de ingeniería", inst.getDescripcion());
        assertEquals("https://www.fing.edu.uy", inst.getLink());
        assertNull(inst.getImagen(), "La imagen debe iniciar en null");
    }

    @Test
    void setters_actualizan_valores() {
        Institucion inst = new Institucion("A", "B", "C");

        inst.setNombre("NuevoNombre");
        inst.setDescripcion("NuevaDesc");
        inst.setLink("https://nuevo.link");
        inst.setImagen("logo.png");

        assertEquals("NuevoNombre", inst.getNombre());
        assertEquals("NuevaDesc", inst.getDescripcion());
        assertEquals("https://nuevo.link", inst.getLink());
        assertEquals("logo.png", inst.getImagen());
    }

    @Test
    void setters_aceptan_null_y_cadenas_vacias() {
        Institucion inst = new Institucion("A", "B", "C");

        inst.setNombre(null);
        inst.setDescripcion("");
        inst.setLink(null);
        inst.setImagen("");

        assertNull(inst.getNombre());
        assertEquals("", inst.getDescripcion());
        assertNull(inst.getLink());
        assertEquals("", inst.getImagen());
    }
}