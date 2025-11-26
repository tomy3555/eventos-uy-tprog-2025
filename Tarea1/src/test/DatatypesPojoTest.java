package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import logica.datatypes.DTPatrocinio;
import logica.datatypes.DTRegistro;
import logica.datatypes.DTTipoRegistro;
import logica.enumerados.DTEstado;
import logica.enumerados.DTNivel;

@DisplayName("Datatypes – Constructores y getters")
class DatatypesPojoTest {

    @Test
    @DisplayName("DTEdicion: ctor básico + getters (con imagen y evento)")
    void dtEdicionCtorBasico() {
        LocalDate ini  = LocalDate.of(2025, 1, 10);
        LocalDate fin  = LocalDate.of(2025, 1, 12);
        LocalDate alta = LocalDate.of(2024, 12, 1);

        DTEdicion dto = new DTEdicion(
                "Ed2025", "ED25",
                ini, fin, alta,
                "org1", "Montevideo", "Uruguay",
                "ed2025.png",
                (DTEvento) null
        );

        assertEquals("Ed2025", dto.getNombre());
        assertEquals("ED25", dto.getSigla());
        assertEquals(ini, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFin());
        assertEquals(alta, dto.getFechaAlta());
        assertEquals("org1", dto.getOrganizador());
        assertEquals("Montevideo", dto.getCiudad());
        assertEquals("Uruguay", dto.getPais());
        assertEquals("ed2025.png", dto.getImagen());
        assertNull(dto.getEstado(), "El ctor básico no debe setear estado");
        assertNull(dto.getEvento(), "Para no depender de DTEvento, pasamos null");
        assertNotNull(dto.getTiposRegistro());
        assertNotNull(dto.getPatrocinios());
        assertNotNull(dto.getRegistros());
        assertTrue(dto.getTiposRegistro().isEmpty());
        assertTrue(dto.getPatrocinios().isEmpty());
        assertTrue(dto.getRegistros().isEmpty());
    }

    @Test
    @DisplayName("DTEdicion: ctor con estado + getters (con imagen y evento)")
    void dtEdicionCtorConEstado() {
        LocalDate ini  = LocalDate.of(2025, 2, 1);
        LocalDate fin  = LocalDate.of(2025, 2, 2);
        LocalDate alta = LocalDate.of(2025, 1, 1);

        DTEdicion dto = new DTEdicion(
                "EdInvierno", "EDINV",
                ini, fin, alta,
                "orgX", "Salto", "Uruguay",
                "invierno.png",
                DTEstado.Ingresada,   
                (DTEvento) null
        );

        assertEquals("EdInvierno", dto.getNombre());
        assertEquals("EDINV", dto.getSigla());
        assertEquals(ini, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFin());
        assertEquals(alta, dto.getFechaAlta());
        assertEquals("orgX", dto.getOrganizador());
        assertEquals("Salto", dto.getCiudad());
        assertEquals("Uruguay", dto.getPais());
        assertEquals("invierno.png", dto.getImagen());
        assertEquals(DTEstado.Ingresada, dto.getEstado());
        assertNull(dto.getEvento());
    }

    @Test
    @DisplayName("DTEdicion: ctor con listas (usa la misma referencia, sin copia defensiva)")
    void dtEdicionCtorConListas() {
        LocalDate ini  = LocalDate.of(2025, 5, 1);
        LocalDate fin  = LocalDate.of(2025, 5, 3);
        LocalDate alta = LocalDate.of(2025, 4, 1);

        List<DTTipoRegistro> tipos = new ArrayList<>();
        List<DTPatrocinio>   pats  = new ArrayList<>();
        List<DTRegistro>     regs  = new ArrayList<>();

        DTEdicion dto = new DTEdicion(
                "EdOtoño", "EDOT",
                ini, fin, alta,
                "orgY", "Colonia", "Uruguay",
                "otono.png",
                DTEstado.Aceptada,
                tipos, pats, regs,
                (DTEvento) null
        );

        assertSame(tipos, dto.getTiposRegistro());
        assertSame(pats,  dto.getPatrocinios());
        assertSame(regs,  dto.getRegistros());

        tipos.add(null);
        pats.add(null);
        regs.add(null);
        assertEquals(1, dto.getTiposRegistro().size());
        assertEquals(1, dto.getPatrocinios().size());
        assertEquals(1, dto.getRegistros().size());
    }

    @Test
    @DisplayName("DTPatrocinio: ctor + getters")
    void dtPatrocinio() {
        LocalDate fecha = LocalDate.of(2025, 3, 15);

        DTPatrocinio dto = new DTPatrocinio(
                "PAT-001",
                5000,
                fecha,
                DTNivel.ORO,
                10,
                "FING",
                "ED25",
                "SPONSOR"
        );

        assertEquals("PAT-001", dto.getCodigo());
        assertEquals(5000, dto.getMonto());
        assertEquals(fecha, dto.getFecha());
        assertEquals(DTNivel.ORO, dto.getNivel());
        assertEquals(10, dto.getCantRegistrosGratuitos());
        assertEquals("FING", dto.getInstitucion());
        assertEquals("ED25", dto.getSiglaEdicion());
        assertEquals("SPONSOR", dto.getTipoRegistro());
    }

    @Test
    @DisplayName("DTRegistro: ctor + getters")
    void dtRegistro() {
        LocalDate reg    = LocalDate.of(2025, 4, 1);
        LocalDate inicio = LocalDate.of(2025, 4, 10);

        DTRegistro dto = new DTRegistro(
                "R-001",
                "ana",
                "Ed2025",
                "GENERAL",
                reg,
                1000.0f,
                inicio
        );

        assertEquals("R-001", dto.getId());
        assertEquals("ana", dto.getUsuario());
        assertEquals("Ed2025", dto.getEdicion());
        assertEquals("GENERAL", dto.getTipoRegistro());
        assertEquals(reg, dto.getFechaRegistro());
        assertEquals(1000.0f, dto.getCosto(), 0.0001f);
        assertEquals(inicio, dto.getFechaInicio());
    }

    @Test
    @DisplayName("DTTipoRegistro: ctor + getters")
    void dtTipoRegistro() {
        DTTipoRegistro dto = new DTTipoRegistro(
                "VIP",
                "Acceso con beneficios",
                1999.99f,
                200
        );

        assertEquals("VIP", dto.getNombre());
        assertEquals("Acceso con beneficios", dto.getDescripcion());
        assertEquals(1999.99f, dto.getCosto(), 0.0001f);
        assertEquals(200, dto.getCupo());
    }
}
