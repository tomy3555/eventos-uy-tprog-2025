package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import logica.datatypes.DTPatrocinio;
import logica.datatypes.DTRegistro;
import logica.datatypes.DTTipoRegistro;
import logica.enumerados.DTEstado;

@DisplayName("DTEvento – Constructores, getters y lógica de aceptadas")
class DTEventoTest {

    private DTEvento newEventoSinImagen(List<String> cats, List<String> eds) {
        return new DTEvento(
            "Evento Tech", "EVT",
            "Desc", LocalDate.of(2025, 1, 1),
            cats, eds
        );
    }

    private DTEvento newEventoConImagen(List<String> cats, List<String> eds, String img) {
        return new DTEvento(
            "Feria Libro", "FLB",
            "Desc", LocalDate.of(2025, 6, 10),
            cats, eds, img
        );
    }

    private DTEdicion ed(String nombre, DTEstado estado) {
        return new DTEdicion(
            nombre, "SIG",
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 2),
            LocalDate.of(2025, 9, 1),
            "org1", "Montevideo", "Uruguay",
            "img.png",
            estado,
            null 
        );
    }


    @Test
    @DisplayName("Ctor sin imagen: imagen=null por defecto y getters básicos")
    void ctorSinImagen() {
        List<String> cats = new ArrayList<>(Arrays.asList("Tecnología", "Innovación"));
        List<String> eds  = new ArrayList<>(Arrays.asList("Ed2024", "Ed2025"));

        DTEvento ev = newEventoSinImagen(cats, eds);

        assertEquals("Evento Tech", ev.getNombre());
        assertEquals("EVT", ev.getSigla());
        assertEquals("Desc", ev.getDescripcion());
        assertEquals(LocalDate.of(2025, 1, 1), ev.getFecha());
        assertSame(cats, ev.getCategorias(), "No debe copiar la lista de categorías");
        assertSame(eds,  ev.getEdiciones(),  "No debe copiar la lista de ediciones");
        assertNull(ev.getImagen(), "Imagen debe iniciar en null en el ctor básico");
    }

    @Test
    @DisplayName("Ctor con imagen: setea imagen; setImagen() la cambia")
    void ctorConImagenYSetter() {
        DTEvento ev = newEventoConImagen(
            new ArrayList<>(List.of("Cultura")), 
            new ArrayList<>(List.of("EdA", "EdB")),
            "portada.png"
        );

        assertEquals("portada.png", ev.getImagen());
        ev.setImagen("nueva.png");
        assertEquals("nueva.png", ev.getImagen(), "setImagen debe actualizar el campo");
    }


    @Test
    @DisplayName("Mutabilidad: cambios externos en categorías/ediciones se reflejan en el DTO")
    void mutabilidadListas() {
        List<String> cats = new ArrayList<>(List.of("Arte"));
        List<String> eds  = new ArrayList<>(List.of("Ed1"));

        DTEvento ev = newEventoSinImagen(cats, eds);

        cats.add("Literatura");
        eds.add("Ed2");

        assertEquals(List.of("Arte", "Literatura"), ev.getCategorias());
        assertEquals(List.of("Ed1", "Ed2"), ev.getEdiciones());
    }


    @Test
    @DisplayName("getEdicionesAceptadas: incluye solo (estado=Aceptada) ∧ (nombre ∈ this.ediciones)")
    void getEdicionesAceptadas_basico() {
        List<String> eds = new ArrayList<>(List.of("EdOK", "EdOtra"));
        DTEvento ev = newEventoSinImagen(new ArrayList<>(List.of("Tech")), eds);

        // Candidatas completas
        DTEdicion a1 = ed("EdOK",   DTEstado.Aceptada);
        DTEdicion a2 = ed("NoList", DTEstado.Aceptada);   // aceptada pero NO está en this.ediciones
        DTEdicion n1 = ed("EdOtra", DTEstado.Ingresada);     // está en this.ediciones pero NO aceptada
        DTEdicion a3 = ed("EdOK",   DTEstado.Aceptada);   // mismo nombre repetido -> se incluye igual

        List<String> res = ev.getEdicionesAceptadas(List.of(a1, a2, n1, a3));

        assertEquals(List.of("EdOK", "EdOK"), res);
    }

    @Test
    @DisplayName("getEdicionesAceptadas: sin intersección -> vacío")
    void getEdicionesAceptadas_vacio() {
        DTEvento ev = newEventoSinImagen(
            new ArrayList<>(List.of("Tech")),
            new ArrayList<>(List.of("X", "Y"))
        );

        List<String> res = ev.getEdicionesAceptadas(List.of(
            ed("A", DTEstado.Aceptada),
            ed("B", DTEstado.Ingresada)
        ));

        assertTrue(res.isEmpty(), "No hay aceptadas cuyos nombres estén en this.ediciones");
    }

    @Test
    @DisplayName("getEdicionesAceptadas: si this.ediciones es null -> NullPointerException")
    void getEdicionesAceptadas_edicionesNull_lanzaNPE() {
        DTEvento ev = new DTEvento(
            "E", "S", "D", LocalDate.now(),
            new ArrayList<>(), null // ediciones = null
        );

        assertThrows(NullPointerException.class, () ->
            ev.getEdicionesAceptadas(List.of(ed("Ed", DTEstado.Aceptada)))
        );
    }

    @Test
    @DisplayName("getEdicionesAceptadas: lista de entrada vacía -> resultado vacío")
    void getEdicionesAceptadas_entradaVacia() {
        DTEvento ev = newEventoSinImagen(
            new ArrayList<>(List.of("Ed1")),
            new ArrayList<>(List.of("Ed1"))
        );

        assertTrue(ev.getEdicionesAceptadas(List.of()).isEmpty());
    }
}
