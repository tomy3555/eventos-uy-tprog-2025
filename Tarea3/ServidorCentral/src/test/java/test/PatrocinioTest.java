package test;

import logica.clases.*;
import logica.enumerados.DTNivel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PatrocinioTest {

    // ===== Helpers con Categoria incluida =====
    private Eventos crearEvento() {
        Map<String, Categoria> cats = new HashMap<>();
        cats.put("Tecnología", new Categoria("Tecnología"));
        cats.put("Cultura",    new Categoria("Cultura"));

        // ctor: Eventos(String nombre, String sigla, String descripcion, LocalDate fecha, Map<String, Categoria> categorias, String imagen)
        return new Eventos(
                "Evento Test", "SIG", "Descripción",
                LocalDate.of(2025, 1, 1),
                cats,
                "evento.png"
        );
    }

    private Ediciones crearEdicion() {
        // organizador = null (no lo necesitamos en este test)
        return new Ediciones(
                crearEvento(), "Edición Test", "EDT",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 12),
                LocalDate.of(2025, 2, 1),
                null, "Montevideo", "Uruguay"
        );
    }

    private TipoRegistro crearTipoRegistro(Ediciones e, String nombre, float costo, int cupo) {
        // ¡Ojo! tu firma pide Ediciones como 1er parámetro
        return new TipoRegistro(e, nombre, "desc", costo, cupo);
    }

    private Institucion crearInstitucion() {
        return new Institucion("Facultad de Ingeniería", "Facultad pública", "https://www.fing.edu.uy");
    }

    // ===== Tests =====

    @Test
    void constructor_asigna_todos_los_campos_correctamente() {
        Ediciones ed = crearEdicion();
        Institucion inst = crearInstitucion();
        TipoRegistro tr = crearTipoRegistro(ed, "General", 1500f, 100);
        LocalDate fecha = LocalDate.of(2025, 2, 15);

        Patrocinio p = new Patrocinio(
                ed,                     // edicion
                inst,                   // institucion
                DTNivel.ORO,            // nivel
                tr,                     // tipoRegistro
                20000,                  // aporte
                fecha,                  // fechaPatrocinio
                7,                      // cantidadRegistros
                "COD-ORO-001"           // codigoPatrocinio
        );

        assertSame(ed, p.getEdicion());
        assertSame(inst, p.getInstitucion());
        assertEquals(DTNivel.ORO, p.getNivel());
        assertSame(tr, p.getTipoRegistro());
        assertEquals(20000, p.getAporte());
        assertEquals(fecha, p.getFechaPatrocinio());
        assertEquals(7, p.getCantidadRegistros());
        assertEquals("COD-ORO-001", p.getCodigoPatrocinio());

        // De yapa: el evento tiene categorías cargadas con Categoria
        assertEquals(2, ed.getEvento().getCategorias().size());
        assertNotNull(ed.getEvento().obtenerCategoria("Tecnología"));
        assertEquals("Cultura", ed.getEvento().obtenerCategoria("Cultura").getNombre());
    }

    @Test
    void relaciones_coherentes_con_edicion_y_tipoRegistro() {
        Ediciones ed = crearEdicion();
        TipoRegistro tr = crearTipoRegistro(ed, "VIP", 3000f, 20);

        Patrocinio p = new Patrocinio(
                ed, null, DTNivel.PLATA, tr,
                12000, LocalDate.now(), 3, "COD-PLATA-01"
        );

        // El TipoRegistro referencia a la misma edición
        assertSame(ed, tr.getEdicion());
        // El patrocinio referencia esa edición y tipo
        assertSame(ed, p.getEdicion());
        assertSame(tr, p.getTipoRegistro());

        // Podemos asociarlo con la edición (set expuesto)
        ed.getPatrocinios().add(p);
        assertTrue(ed.getPatrocinios().contains(p));
    }
}