package test;

import logica.clases.*;
import logica.enumerados.DTEstado;
import logica.enumerados.DTNivel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EdicionesTest {

    // ===== Helpers =====
    private Eventos crearEvento() {
        return new Eventos(
                "Evento Test", "SIG", "Descripción del evento",
                LocalDate.of(2025, 1, 1),
                new HashMap<>(),           // categorías vacías
                "imagen.png"
        );
    }

    private TipoRegistro crearTipoRegistro(Ediciones e, String nombre, float costo, int cupo) {
        return new TipoRegistro(e, nombre, "desc", costo, cupo);
    }

    // ===== Constructores =====
    @Test
    void ctorBasico_asignaCamposYEstadoIngresada() {
        LocalDate fi = LocalDate.of(2025, 3, 1);
        LocalDate ff = LocalDate.of(2025, 3, 5);
        LocalDate fa = LocalDate.of(2025, 2, 20);

        Ediciones e = new Ediciones(
                crearEvento(), "Ed1", "ED1",
                fi, ff, fa, null,
                "Montevideo", "Uruguay"
        );

        assertEquals("Ed1", e.getNombre());
        assertEquals("ED1", e.getSigla());
        assertEquals(fi, e.getFechaInicio());
        assertEquals(ff, e.getFechaFin());
        assertEquals(fa, e.getFechaAlta());
        assertEquals("Montevideo", e.getCiudad());
        assertEquals("Uruguay", e.getPais());
        assertEquals(DTEstado.Ingresada, e.getEstado());
        assertTrue(e.estaIngresada());
        assertNull(e.getImagen());
        assertNull(e.getVideo());
    }

    @Test
    void ctorConEstadoExplícito() {
        Ediciones e = new Ediciones(
                crearEvento(), "Ed2", "ED2",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P", DTEstado.Aceptada
        );
        assertEquals(DTEstado.Aceptada, e.getEstado());
        assertFalse(e.estaIngresada());
    }

    @Test
    void ctorConImagen_yVideoNull() {
        Ediciones e = new Ediciones(
                crearEvento(), "Ed3", "ED3",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P", "foto.png"
        );
        assertEquals("foto.png", e.getImagen());
        assertNull(e.getVideo());
        assertEquals(DTEstado.Ingresada, e.getEstado());
    }

    @Test
    void ctorConImagenYVideo() {
        Ediciones e = new Ediciones(
                crearEvento(), "Ed4", "ED4",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P", "img.png", "video.mp4"
        );
        assertEquals("img.png", e.getImagen());
        assertEquals("video.mp4", e.getVideo());
    }

    // ===== Setters / Getters =====
    @Test
    void settersYGetters_funcionanCorrectamente() {
        Ediciones e = new Ediciones(
                crearEvento(), "E", "S",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "Ciudad", "Pais"
        );

        e.setNombre("Nuevo");
        e.setSigla("SIG2");
        e.setFechaInicio(LocalDate.of(2026, 1, 1));
        e.setFechaFin(LocalDate.of(2026, 1, 2));
        e.setFechaAlta(LocalDate.of(2025, 12, 31));
        e.setCiudad("NuevaCiudad");
        e.setPais("NuevoPais");
        e.setEstado(DTEstado.Rechazada);
        e.setImagen("foto.png");
        e.setVideo("video.mp4");

        assertEquals("Nuevo", e.getNombre());
        assertEquals("SIG2", e.getSigla());
        assertEquals(LocalDate.of(2026, 1, 1), e.getFechaInicio());
        assertEquals(LocalDate.of(2026, 1, 2), e.getFechaFin());
        assertEquals(LocalDate.of(2025, 12, 31), e.getFechaAlta());
        assertEquals("NuevaCiudad", e.getCiudad());
        assertEquals("NuevoPais", e.getPais());
        assertEquals(DTEstado.Rechazada, e.getEstado());
        assertEquals("foto.png", e.getImagen());
        assertEquals("video.mp4", e.getVideo());
    }

    // ===== Tipos de Registro =====
    @Test
    void agregar_y_obtenerTipoRegistro() {
        Ediciones e = new Ediciones(
                crearEvento(), "A", "B",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P"
        );

        // ¡Ojo! el constructor de TipoRegistro pide la Edición primero
        TipoRegistro tr1 = crearTipoRegistro(e, "General", 100f, 10);
        TipoRegistro tr2 = crearTipoRegistro(e, "VIP",     500f, 5);

        e.agregarTipoRegistro("General", tr1);
        e.agregarTipoRegistro("VIP", tr2);

        assertEquals(tr1, e.getTipoRegistro("General"));
        assertEquals(tr2, e.obtenerTipoRegistro("VIP"));
        assertNull(e.obtenerTipoRegistro("Inexistente"));
        assertEquals(2, e.getTiposRegistro().size());
        assertSame(e, tr1.getEdicion());
        assertSame(e, tr2.getEdicion());
    }

    // ===== Registros =====
    @Test
    void agregarRegistro_yVerificarMapa() {
        Ediciones e = new Ediciones(
                crearEvento(), "A", "B",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P"
        );

        TipoRegistro t1 = crearTipoRegistro(e, "General", 100f, 50);
        TipoRegistro t2 = crearTipoRegistro(e, "VIP",     300f, 20);

        Registro r1 = new Registro("R1", null, e, t1, LocalDate.now(), 100f, LocalDate.now(), crearEvento());
        Registro r2 = new Registro("R2", null, e, t2, LocalDate.now(), 200f, LocalDate.now(), crearEvento());

        e.agregarRegistro("R1", r1);
        e.agregarRegistro("R2", r2);

        assertEquals(r1, e.getRegistros().get("R1"));
        assertEquals(r2, e.getRegistros().get("R2"));
        assertEquals(2, e.getRegistros().size());
    }

    // ===== Patrocinios =====
    @Test
    void getPatrocinio_devuelveCorrecto() {
        Ediciones e = new Ediciones(
                crearEvento(), "A", "B",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P"
        );

        TipoRegistro tr = crearTipoRegistro(e, "General", 100f, 10);

        Patrocinio p1 = new Patrocinio(e, null, DTNivel.ORO,   tr, 10000, LocalDate.now(), 3, "COD1");
        Patrocinio p2 = new Patrocinio(e, null, DTNivel.PLATA, tr,  5000, LocalDate.now(), 2, "COD2");

        e.getPatrocinios().add(p1);
        e.getPatrocinios().add(p2);

        assertEquals(p1, e.getPatrocinio("COD1"));
        assertEquals(p2, e.getPatrocinio("COD2"));
        assertNull(e.getPatrocinio("NOEXISTE"));
    }

    // ===== Estado =====
    @Test
    void estaIngresada_dependeDelEstado() {
        Ediciones e = new Ediciones(
                crearEvento(), "A", "B",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P"
        );

        assertTrue(e.estaIngresada());
        e.setEstado(DTEstado.Aceptada);
        assertFalse(e.estaIngresada());
    }
    
    // ===== Organizador =====
    @Test
    void setOrganizador_aceptaNull_yQuedaNull() {
        Ediciones e = new Ediciones(
                crearEvento(), "OrgEd", "ORG",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "Montevideo", "Uruguay"
        );

        // pre: null
        assertNull(e.getOrganizador());

        // act: asigno null
        e.setOrganizador(null);

        // post: sigue null y la llamada cubre la línea del setter
        assertNull(e.getOrganizador());
    }

    @Test
    void setOrganizador_noLanza_explictamente() {
        Ediciones e = new Ediciones(
                crearEvento(), "OrgEd2", "ORG2",
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                null, "C", "P"
        );

        // simplemente verificamos que la invocación no explote (sin crear Usuario concreto)
        assertDoesNotThrow(() -> e.setOrganizador(null));
    }
}