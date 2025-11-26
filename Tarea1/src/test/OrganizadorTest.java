package test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Organizador – listarEdicionesAPartirDeOrganizador")
class OrganizadorTest {

    private Object fabrica;
    private Object controladorEv;
    private Object controladorUs;

    private String INST;
    private String ORG;
    private String MAIL;

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();

        // Fábrica (minúscula o mayúscula)
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); }
        catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        this.fabrica = getter.invoke(null);

        // Controlador Usuario
        controladorUs = TestUtils.tryInvoke(this.fabrica, new String[]{
            "getIUsuario", "getIControladorUsuario", "getControladorUsuario"
        });

        // Controlador Evento
        Object ceMaybe = null;
        try {
            ceMaybe = TestUtils.tryInvoke(this.fabrica, new String[]{
                "getIEvento", "getIControladorEvento", "getControladorEvento", "getEvento"
            });
        } catch (AssertionError ignored) {}
        if (ceMaybe == null) {
            Class<?> ceClazz = Class.forName("logica.controladores.ControladorEvento");
            Constructor<?> c = ceClazz.getDeclaredConstructor();
            c.setAccessible(true);
            controladorEv = c.newInstance();
        } else {
            controladorEv = ceMaybe;
        }

        // Base única por test
        long nonce = System.nanoTime();
        INST = "Inst_O_" + nonce;
        ORG  = "orgO_"  + nonce;
        MAIL = ORG + "@x";

        // Alta Institución y Organizador
        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion","AltaInstitucion"},
                INST, "desc", "web");
        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario","AltaUsuario"},
                ORG, "Organizador Nombre", MAIL, "desc", "link",
                "Ap", LocalDate.of(1990,1,1), INST, true, null, null);

        // Categoría base
        try { TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaCategoria","AltaCategoria"}, "Tec"); }
        catch (Throwable ignored) {}
    }

    private Object categoriasDTO(String... nombres) {
        try {
            Class<?> cls = Class.forName("logica.datatypes.DTCategorias");
            var ctor = cls.getDeclaredConstructor(java.util.List.class);
            ctor.setAccessible(true);
            java.util.List<String> lista = java.util.Arrays.asList(nombres);
            return ctor.newInstance(lista);
        } catch (ReflectiveOperationException e) {
            return TestUtils.tolerantNew("logica.datatypes.DTCategorias", java.util.Arrays.asList(nombres));
        }
    }

    @Test
    @DisplayName("Devuelve DTEdicion con DTEvento embebido y campos consistentes (1 edición)")
    void listarEdiciones_delOrganizador_unicaEdicion() throws Throwable {
        // Alta evento
        Object cats = categoriasDTO("Tec");
        String evName = "EV_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc EV", LocalDate.now(), "SIGEV", cats, INST);

        // Alta edición
        LocalDate hoy = LocalDate.now();
        String edName = "EdOrg1";
        String sigla  = "EDO1";
        String ciudad = "Montevideo";
        String pais   = "Uruguay";
        String img    = "ed.png";

        Object eventoObj  = DomainAccess.obtenerEvento(evName);
        Object usuarioOrg = DomainAccess.obtenerUsuario(ORG);
        assertNotNull(eventoObj);
        assertNotNull(usuarioOrg);

        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaEdicionEvento"},
                eventoObj, usuarioOrg,
                edName, sigla, "Desc Ed",
                hoy.plusDays(7), hoy.plusDays(9), hoy,
                ciudad, pais, img);

        Object orgEntidad = DomainAccess.obtenerUsuario(ORG);
        assertNotNull(orgEntidad);
        Method listar = TestUtils.findMethod(orgEntidad, "listarEdicionesAPartirDeOrganizador");
        assertNotNull(listar, "Organizador debe exponer listarEdicionesAPartirDeOrganizador()");
        @SuppressWarnings("unchecked")
        List<Object> lista = (List<Object>) listar.invoke(orgEntidad);

        assertNotNull(lista);
        assertEquals(1, lista.size(), "Debe haber exactamente una DTEdicion");

        Object dted = lista.get(0);
        // DTEdicion getters
        Method gNombre = TestUtils.findMethod(dted, "getNombre");
        Method gSigla  = TestUtils.findMethod(dted, "getSigla");
        Method gIni    = TestUtils.findMethod(dted, "getFechaInicio");
        Method gFin    = TestUtils.findMethod(dted, "getFechaFin");
        Method gAlta   = TestUtils.findMethod(dted, "getFechaAlta");
        Method gOrg    = TestUtils.findMethod(dted, "getOrganizador");
        Method gCiudad = TestUtils.findMethod(dted, "getCiudad");
        Method gPais   = TestUtils.findMethod(dted, "getPais");
        Method gImg    = TestUtils.findMethod(dted, "getImagen");
        Method gEvt    = TestUtils.findMethod(dted, "getEvento");

        assertEquals(edName, String.valueOf(gNombre.invoke(dted)));
        assertEquals(sigla,  String.valueOf(gSigla.invoke(dted)));
        assertEquals(hoy.plusDays(7), gIni.invoke(dted));
        assertEquals(hoy.plusDays(9), gFin.invoke(dted));
        assertEquals(hoy, gAlta.invoke(dted));
        assertEquals("Organizador Nombre", String.valueOf(gOrg.invoke(dted)));
        assertEquals(ciudad, String.valueOf(gCiudad.invoke(dted)));
        assertEquals(pais,   String.valueOf(gPais.invoke(dted)));
        assertEquals(img,    String.valueOf(gImg.invoke(dted)));

        Object dtevento = gEvt.invoke(dted);
        assertNotNull(dtevento, "Debe devolver un DTEvento embebido");

        Method gevNombre = TestUtils.findMethod(dtevento, "getNombre");
        Method gevSigla  = TestUtils.findMethod(dtevento, "getSigla");
        Method gevDesc   = TestUtils.findMethod(dtevento, "getDescripcion");
        Method gevFecha  = TestUtils.findMethod(dtevento, "getFecha");
        Method gevCats   = TestUtils.findMethod(dtevento, "getCategorias");
        Method gevEds    = TestUtils.findMethod(dtevento, "getEdiciones");
        Method gevImg    = TestUtils.findMethod(dtevento, "getImagen");

        assertEquals("Organizador Nombre", String.valueOf(gevNombre.invoke(dtevento)));
        assertEquals(sigla, String.valueOf(gevSigla.invoke(dtevento)));

        assertEquals("Desc EV", String.valueOf(gevDesc.invoke(dtevento)));
        assertEquals(LocalDate.now(), geVFechaDayOnly((LocalDate) gevFecha.invoke(dtevento)));

        @SuppressWarnings("unchecked")
        List<String> catsFromDTO = (List<String>) gevCats.invoke(dtevento);
        @SuppressWarnings("unchecked")
        List<String> edsFromDTO  = (List<String>) gevEds.invoke(dtevento);

        assertNotNull(catsFromDTO);
        assertTrue(catsFromDTO.contains("Tec"));
        assertNotNull(edsFromDTO);
        assertTrue(edsFromDTO.contains(edName));
        assertNull(gevImg.invoke(dtevento));
    }

    @Test
    @DisplayName("Dos ediciones agregadas al Organizador → se listan ambas como DTEdicion")
    void listarEdiciones_dosEdiciones() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String evName = "EV2_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "D", LocalDate.now(), "S2", cats, INST);

        LocalDate hoy = LocalDate.now();
        Object eventoObj  = DomainAccess.obtenerEvento(evName);
        Object usuarioOrg = DomainAccess.obtenerUsuario(ORG);

        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaEdicionEvento"},
                eventoObj, usuarioOrg,
                "Ed1", "E1", "x",
                hoy.plusDays(1), hoy.plusDays(2), hoy,
                "MVD", "UY", null);

        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaEdicionEvento"},
                eventoObj, usuarioOrg,
                "Ed2", "E2", "x",
                hoy.plusDays(3), hoy.plusDays(4), hoy,
                "MVD", "UY", null);

        Object orgEntidad = DomainAccess.obtenerUsuario(ORG);
        Method listar = TestUtils.findMethod(orgEntidad, "listarEdicionesAPartirDeOrganizador");
        @SuppressWarnings("unchecked")
        List<Object> lista = (List<Object>) listar.invoke(orgEntidad);

        assertNotNull(lista);
        Set<String> nombres = lista.stream().map(o -> {
            Method g = TestUtils.findMethod(o, "getNombre");
            try { return String.valueOf(g.invoke(o)); } catch (Exception e) { return ""; }
        }).collect(Collectors.toSet());

        assertTrue(nombres.contains("Ed1"));
        assertTrue(nombres.contains("Ed2"));
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private static LocalDate geVFechaDayOnly(LocalDate d) {
        return LocalDate.of(d.getYear(), d.getMonth(), d.getDayOfMonth());
    }
}
