package test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ControladorEvento – Categorías: listar por categoría / categorías con eventos / categorías globales")
class ControladorEventoCategoriasTest {

    private Object fabrica;
    private Object cu; // ControladorUsuario
    private Object ce; // ControladorEvento

    private String INST;
    private String ORG;
    private String MAIL;

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetAll();

        // Fábrica
        Class<?> fab = TestUtils.loadAny("logica.Fabrica", "logica.fabrica");
        Method getter;
        try { getter = fab.getMethod("getInstance"); } catch (NoSuchMethodException e) { getter = fab.getMethod("getInstancia"); }
        this.fabrica = getter.invoke(null);

        // CU y CE
        cu = TestUtils.tryInvoke(fabrica, new String[]{"getIUsuario","getIControladorUsuario","getControladorUsuario"});
        Object maybeCE = null;
        try {
            maybeCE = TestUtils.tryInvoke(fabrica, new String[]{"getIEvento","getIControladorEvento","getControladorEvento","getEvento"});
        } catch (AssertionError ignored) {}
        if (maybeCE == null) {
            Class<?> ceClazz = Class.forName("logica.controladores.ControladorEvento");
            Constructor<?> c0 = ceClazz.getDeclaredConstructor();
            c0.setAccessible(true);
            ce = c0.newInstance();
        } else {
            ce = maybeCE;
        }

        // Base única por corrida
        long nonce = System.nanoTime();
        INST = "Inst_Cat_" + nonce;
        ORG  = "orgCat_" + nonce;
        MAIL = ORG + "@x";

        TestUtils.tryInvoke(cu, new String[]{"altaInstitucion","AltaInstitucion"}, INST, "desc", "web");
        TestUtils.tryInvoke(cu, new String[]{"altaUsuario","AltaUsuario"},
                ORG, "Org Cat", MAIL, "desc","link",
                "Ap", LocalDate.of(1990,1,1), INST, true, null, null);

        // Categorías base (idempotente)
        altaCategoriaIdempotente(ce, "Tecnologia");
        altaCategoriaIdempotente(ce, "Deportes");
    }

    /* ================= Helpers ================= */

    private Object categoriasDTO(String... nombres) {
        try {
            Class<?> cls = Class.forName("logica.datatypes.DTCategorias");
            var ctor = cls.getDeclaredConstructor(java.util.List.class);
            ctor.setAccessible(true);
            return ctor.newInstance(java.util.Arrays.asList(nombres));
        } catch (ReflectiveOperationException e) {
            return TestUtils.tolerantNew("logica.datatypes.DTCategorias", java.util.Arrays.asList(nombres));
        }
    }

    private void altaCategoriaIdempotente(Object ceRef, String nombre) {
        try { TestUtils.invokeUnwrapped(ceRef, new String[]{"altaCategoria","AltaCategoria"}, nombre); }
        catch (Throwable ignored) { /* ya existe o tu impl lanza; ok */ }
    }

    /** SIEMPRE usa la firma con OBJETOS (Eventos, Usuario, …) en el ORDEN exacto. */
    private void crearEdicion(Object ceRef,
                              String nombreEvento, String nickOrg,
                              String nombreEd, String siglaEd, String descEd,
                              LocalDate ini, LocalDate fin, LocalDate alta,
                              String ciudad, String pais, String imagen) throws Throwable {
        Object eventoObj  = DomainAccess.obtenerEvento(nombreEvento);
        Object usuarioObj = DomainAccess.obtenerUsuario(nickOrg);
        assertNotNull(eventoObj,  "No pude resolver Eventos (" + nombreEvento + ")");
        assertNotNull(usuarioObj, "No pude resolver Usuario (" + nickOrg + ")");

        TestUtils.invokeUnwrapped(ceRef, new String[]{"altaEdicionEvento"},
                eventoObj, usuarioObj,
                nombreEd, siglaEd, descEd,
                ini, fin, alta,
                ciudad, pais, imagen);
    }

    private String getNombre(Object dtoLike) {
        Method m = TestUtils.findMethod(dtoLike, "getNombre", "nombre");
        if (m == null) return String.valueOf(dtoLike);
        try { return String.valueOf(m.invoke(dtoLike)); } catch (Exception e) { return String.valueOf(dtoLike); }
    }

    /* ================= Tests ================= */

    @Test
    @DisplayName("listarEventosPorCategoria devuelve solo los eventos de esa categoría (con normalización)")
    void listarEventosPorCategoria_ok() throws Throwable {
        // Evento 1 -> Tecnologia
        String evTec = "EV_TEC_" + System.nanoTime();
        Object dtTec = categoriasDTO("Tecnologia");
        TestUtils.tryInvoke(ce, new String[]{"altaEvento"}, evTec, "d", LocalDate.now(), "TEC", dtTec, INST);
        crearEdicion(ce, evTec, ORG, "EdTec", "ET1", "ed tec",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), LocalDate.now(),
                "Montevideo","UY", null);

        // Evento 2 -> Deportes
        String evDep = "EV_DEP_" + System.nanoTime();
        Object dtDep = categoriasDTO("Deportes");
        TestUtils.tryInvoke(ce, new String[]{"altaEvento"}, evDep, "d", LocalDate.now(), "DEP", dtDep, INST);
        crearEdicion(ce, evDep, ORG, "EdDep", "ED1", "ed dep",
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), LocalDate.now(),
                "Montevideo","UY", null);

        @SuppressWarnings("unchecked")
        List<Object> dtevs = (List<Object>) TestUtils.tryInvoke(ce, new String[]{"listarEventosPorCategoria"}, "tecnología"); // con tilde/minúsculas
        assertNotNull(dtevs);

        var nombres = dtevs.stream().map(this::getNombre).collect(Collectors.toSet());
        assertTrue(nombres.contains(evTec));
        assertFalse(nombres.contains(evDep));
    }

    @Test
    @DisplayName("listarCategoriasConEventos devuelve únicas y no vacías (según eventos cargados)")
    void listarCategoriasConEventos_ok() {
        @SuppressWarnings("unchecked")
        List<String> cats = (List<String>) TestUtils.tryInvoke(ce, new String[]{"listarCategoriasConEventos"});
        assertNotNull(cats);
        // Deben estar al menos las que creamos en eventos previos (si no se cargaron, este test por sí solo crea luego)
        // Como los tests son independientes, aseguramos creando un evento rápido:
        if (cats.isEmpty() || (!cats.contains("Tecnologia") && !cats.contains("Deportes"))) {
            Object dtTec = categoriasDTO("Tecnologia");
            String ev = "EV_T_" + System.nanoTime();
            TestUtils.tryInvoke(ce, new String[]{"altaEvento"}, ev, "d", LocalDate.now(), "T", dtTec, INST);

            cats = (List<String>) TestUtils.tryInvoke(ce, new String[]{"listarCategoriasConEventos"});
            assertNotNull(cats);
        }
        // Normal: únicas, no vacías
        Set<String> unico = new java.util.LinkedHashSet<>(cats);
        assertEquals(unico.size(), cats.size());
        assertTrue(cats.stream().allMatch(s -> s != null && !s.isBlank()));
    }

    @Test
    @DisplayName("listarCategorias (estático) devuelve el set de categorías del sistema (limpio y no nulo)")
    void listarCategoriasStatic_ok() throws Exception {
        // forzamos al menos una categoría real cargada por CE
        Object dtTec = categoriasDTO("Tecnologia");
        String ev = "EV_T2_" + System.nanoTime();
        TestUtils.tryInvoke(ce, new String[]{"altaEvento"}, ev, "d", LocalDate.now(), "T2", dtTec, INST);

        // Llamada estática (resolvemos clase por nombre)
        Method m = null;
        try {
            Class<?> C = Class.forName("logica.controladores.ControladorEvento");
            m = C.getDeclaredMethod("listarCategorias");
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            try {
                Class<?> C = Class.forName("logica.ControladorEvento");
                m = C.getDeclaredMethod("listarCategorias");
            } catch (ClassNotFoundException | NoSuchMethodException ignored2) { /* sin método */ }
        }
        assertNotNull(m, "No se encontró método estático listarCategorias()");
        @SuppressWarnings("unchecked")
        List<String> cats = (List<String>) m.invoke(null);
        assertNotNull(cats);
        assertTrue(cats.stream().allMatch(s -> s != null && !s.isBlank()));
    }
}
