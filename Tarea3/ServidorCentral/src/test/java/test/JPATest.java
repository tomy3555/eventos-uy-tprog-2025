package test;

import logica.modelo.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class JPATest {

    private OrganizadorOO org;
    private AsistenteOO asist;
    private EdicionOO ed;
    private RegistroOO reg;

    @BeforeEach
    void init() {
        org = new OrganizadorOO("org1", "Organizador Uno", "org@eventos.uy", "123", "imgOrg.png", "desc", "link");
        asist = new AsistenteOO("asis1", "Asistente Uno", "asis@eventos.uy", "abc", "imgA.png",
                "Apellido", LocalDate.of(2000, 1, 1), "UDELAR");
        ed = new EdicionOO("Ed1", "RockFest", "E1", LocalDate.now().minusDays(10), LocalDate.now().plusDays(2),
                LocalDate.now().minusDays(15), org, "Montevideo", "Uruguay",
                "Aceptada", "imgEd.png", "vid.mp4");
        reg = new RegistroOO("R1", 100.0, asist, ed, "General",
                LocalDate.now().minusDays(1), LocalDate.now(), "RockFest", false);
    }

    @Test
    void testUsuarioBasico() {
        assertEquals("org1", org.getNickname());
        assertEquals("Organizador Uno", org.getNombre());
        assertEquals("org@eventos.uy", org.getEmail());
        assertEquals("imgOrg.png", org.getImagen());
        assertEquals("desc", org.getDescripcion());
        assertEquals("link", org.getLink());
    }

    @Test
    void testAsistenteDatos() {
        assertEquals("Apellido", asist.getApellido());
        assertEquals("UDELAR", asist.getInstitucion());
        assertEquals(LocalDate.of(2000, 1, 1), asist.getFechaNacimiento());
    }

    @Test
    void testEdicionDatos() {
        assertEquals("Ed1", ed.getNombre());
        assertEquals("RockFest", ed.getEvento());
        assertEquals("E1", ed.getSigla());
        assertEquals("Montevideo", ed.getCiudad());
        assertEquals("Uruguay", ed.getPais());
        assertEquals("Aceptada", ed.getEstado());
        assertEquals("imgEd.png", ed.getImagen());
        assertEquals("vid.mp4", ed.getVideo());
        assertEquals(org, ed.getOrganizador());
    }

    @Test
    void testRelacionOrganizadorEdicion() {
        assertTrue(org.getEdiciones().isEmpty());
        org.addEdicion(ed);
        assertTrue(org.getEdiciones().contains(ed));
        assertEquals(org, ed.getOrganizador());
        org.removeEdicion(ed);
        assertTrue(org.getEdiciones().isEmpty());
        assertNull(ed.getOrganizador());
    }

    @Test
    void testRegistroDatos() {
        assertEquals("R1", reg.getIdentificador());
        assertEquals(100.0, reg.getCosto());
        assertEquals(asist, reg.getUsuario());
        assertEquals(ed, reg.getEdicion());
        assertEquals("General", reg.getTipoRegistro());
        assertEquals("RockFest", reg.getEvento());
        assertFalse(reg.isAsistenciaMarcada());
        reg.setAsistenciaMarcada(true);
        assertTrue(reg.isAsistenciaMarcada());
    }

    @Test
    void testEdicionAddRemoveRegistro() {
        assertTrue(ed.getRegistros().isEmpty());
        ed.addRegistro(reg);
        assertEquals(1, ed.getRegistros().size());
        assertEquals(ed, reg.getEdicion());
        ed.removeRegistro(reg);
        assertTrue(ed.getRegistros().isEmpty());
        assertNull(reg.getEdicion());
    }

    @Test
    void testEqualsYHash() {
        EdicionOO ed2 = new EdicionOO("Ed1", "RockFest", "E1", LocalDate.now(), LocalDate.now(),
                LocalDate.now(), org, "MVD", "UY", "Aceptada", "i.png", "v.mp4");
        assertEquals(ed, ed2);
        assertEquals(ed.hashCode(), ed2.hashCode());

        RegistroOO reg2 = new RegistroOO("R1", 200, asist, ed, "VIP", LocalDate.now(), LocalDate.now(), "RockFest", true);
        assertEquals(reg, reg2);
        assertEquals(reg.hashCode(), reg2.hashCode());
    }
}
