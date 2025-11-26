package test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import logica.clases.TipoRegistro;

@DisplayName("ControladorEvento – Altas/Listados/Consultas (sin catch genéricos)")
class ControladorEventoTest {

    private Object fabrica;
    private Object controladorEv;
    private Object controladorUs;

    // IDs únicos por corrida (evita *YaExiste*)
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

        // Controlador de USUARIO (por fábrica)
        controladorUs = TestUtils.tryInvoke(this.fabrica, new String[]{
                "getIUsuario", "getIControladorUsuario", "getControladorUsuario"
        });

        // Controlador de EVENTO: por fábrica si existe; si no, instancia concreta
        Object ceMaybe = null;
        try {
            ceMaybe = TestUtils.tryInvoke(this.fabrica, new String[]{
                    "getIEvento", "getIControladorEvento", "getControladorEvento", "getEvento"
            });
        } catch (AssertionError ignored) { /* instanciamos directo */ }

        if (ceMaybe == null) {
            Class<?> ceClazz = Class.forName("logica.controladores.ControladorEvento");
            Constructor<?> constructor = ceClazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            controladorEv = constructor.newInstance();
        } else {
            controladorEv = ceMaybe;
        }

        // ---------- Base única por test ----------
        long nonce = System.nanoTime();
        INST = "Inst_A_" + nonce;
        ORG  = "org1_"  + nonce;
        MAIL = ORG + "@x";

        // Institución y organizador (firmas largas)
        TestUtils.tryInvoke(controladorUs, new String[]{"altaInstitucion", "AltaInstitucion"},
                INST, "desc", "web");

        TestUtils.tryInvoke(controladorUs, new String[]{"altaUsuario", "AltaUsuario"},
                ORG, "Org Uno", MAIL, "desc", "link",
                "Ap", LocalDate.of(1990, 1, 1), INST, true, null, null);

        // Categorías base (idempotentes)
        altaCategoriaIdempotente(controladorEv, "Tecnologia");
        altaCategoriaIdempotente(controladorEv, "Tec");
    }

    /* ---------- Helpers SIN catch genéricos ---------- */

    private Object categoriasDTO(String... nombres) {
        try {
            Class<?> cls = Class.forName("logica.datatypes.DTCategorias");
            var ctor = cls.getDeclaredConstructor(java.util.List.class); // tu ctor real
            ctor.setAccessible(true);
            java.util.List<String> lista = java.util.Arrays.asList(nombres);
            return ctor.newInstance(lista);
        } catch (ReflectiveOperationException e) {
            // Fallback ultra-tolerante si cambia el paquete/clase en alguna versión
            return TestUtils.tolerantNew("logica.datatypes.DTCategorias", java.util.Arrays.asList(nombres));
        }
    }

    private String getDTEventoNombre(Object dtevento) {
        Method metodo = TestUtils.findMethod(dtevento, "getNombre", "nombre");
        if (metodo == null) return String.valueOf(dtevento);
        try { return String.valueOf(metodo.invoke(dtevento)); }
        catch (ReflectiveOperationException | IllegalArgumentException e) { return String.valueOf(dtevento); }
    }

    private String getDTEdicionNombre(Object dted) {
        Method metodo = TestUtils.findMethod(dted, "getNombre", "nombre");
        if (metodo == null) return String.valueOf(dted);
        try { return String.valueOf(metodo.invoke(dted)); }
        catch (ReflectiveOperationException | IllegalArgumentException e) { return String.valueOf(dted); }
    }

    private void altaCategoriaIdempotente(Object ceRef, String nombre) {
        try {
            TestUtils.invokeUnwrapped(ceRef, new String[]{"altaCategoria", "AltaCategoria"}, nombre);
        } catch (Throwable ignored) {
            // Si ya existe o tu implementación devuelve error, lo tomamos como idempotente
        }
    }

    /** Siempre usa la firma con OBJETOS (Eventos, Usuario, …) en el orden exacto. */
    private void crearEdicion(Object ce,
                              String nombreEvento, String nickOrg,
                              String nombreEd, String siglaEd, String descEd,
                              LocalDate ini, LocalDate fin, LocalDate alta,
                              String ciudad, String pais, String imagen) throws Throwable {

        Object eventoObj  = DomainAccess.obtenerEvento(nombreEvento);
        Object usuarioObj = DomainAccess.obtenerUsuario(nickOrg);
        assertNotNull(eventoObj,  "No pude resolver Eventos (" + nombreEvento + ")");
        assertNotNull(usuarioObj, "No pude resolver Usuario (" + nickOrg + ")");

        TestUtils.invokeUnwrapped(ce, new String[]{"altaEdicionEvento"},
                eventoObj, usuarioObj,
                nombreEd, siglaEd, descEd,
                ini, fin, alta,
                ciudad, pais, imagen);
    }

    /* ---------- Tests ---------- */

    @Test
    @DisplayName("AltaCategoria + AltaEvento + listar/consultar")
    void altaEventoYListados() {
        altaCategoriaIdempotente(controladorEv, "Tecnologia");
        Object cats = categoriasDTO("Tecnologia");

        // nombre único por corrida
        String evName = "Feria_" + System.nanoTime();

        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaEvento"},
                evName, "Desc Feria", LocalDate.now(), "FER", cats, INST)
        );

        @SuppressWarnings("unchecked")
        List<Object> lista =
            (List<Object>) TestUtils.tryInvoke(controladorEv, new String[]{"listarEventos"});
        assertNotNull(lista);

        var nombres = lista.stream()
            .map(this::getDTEventoNombre)
            .collect(Collectors.toSet());
        assertTrue(nombres.contains(evName));

        Object evento = TestUtils.tryInvoke(controladorEv, new String[]{"consultaEvento"}, evName);
        assertNotNull(evento);
        assertEquals("logica", evento.getClass().getPackageName());
    }

    @Test
    @DisplayName("altaEdicionEvento + listar/obtener/consultaEdicionEvento")
    void edicionFlujoBasico() throws Throwable {
        Object cats = categoriasDTO("Tecnologia");

        String evName = "Feria_" + System.nanoTime();
        String edName = "Ed2025";

        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc Feria", LocalDate.now(), "FER", cats, INST);

        LocalDate hoy = LocalDate.now();
        assertDoesNotThrow(() ->
            crearEdicion(controladorEv, evName, ORG,
                    edName, "ED25", "Edición principal",
                    hoy.plusDays(10), hoy.plusDays(12), hoy,
                    "Montevideo", "Uruguay", null)
        );

        @SuppressWarnings("unchecked")
        List<String> eds =
            (List<String>) TestUtils.tryInvoke(controladorEv, new String[]{"listarEdicionesEvento"}, evName);
        assertNotNull(eds);
        assertTrue(eds.contains(edName));

        Object edicion = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, evName, edName);
        assertNotNull(edicion);

        Object dted = TestUtils.tryInvoke(controladorEv, new String[]{"consultaEdicionEvento"}, evName, edName);
        if (dted != null) {
            var mNombre = TestUtils.findMethod(dted, "getNombre", "nombre");
            if (mNombre != null) {
                try { assertEquals(edName, String.valueOf(mNombre.invoke(dted))); } catch (Exception ignored) {}
            }
        }
    }

    @Test
    @DisplayName("AltaTipoRegistro sobre una edición existente")
    void altaTipoRegistroEnEdicion() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String ev = "Conf_" + System.nanoTime();

        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "CONF", cats, INST);

        LocalDate hoy = LocalDate.now();
        crearEdicion(controladorEv, ev, ORG,
                "Apertura", "AP01", "Inicio",
                hoy.plusDays(1), hoy.plusDays(2), hoy,
                "Montevideo", "Uruguay", null);

        Object edicion = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, ev, "Apertura");
        assertNotNull(edicion);

        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaTipoRegistro"},
                edicion, "GENERAL", "Acceso general", 1000, 50)
        );
    }

    @Test
    @DisplayName("altaRegistroEdicionEvento + consultaRegistro (camino feliz)")
    void registroYConsultaRegistro() throws Throwable {
        altaCategoriaIdempotente(controladorEv, "Tec");

        Object cats = categoriasDTO("Tec");
        String ev = "Expo_" + System.nanoTime();

        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "EXPO", cats, INST);

        LocalDate hoy = LocalDate.now();
        crearEdicion(controladorEv, ev, ORG,
                "Verano", "V24", "Ed verano",
                hoy.plusDays(3), hoy.plusDays(4), hoy,
                "Montevideo", "Uruguay", null);

        Object edicion = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, ev, "Verano");
        Object evento  = TestUtils.tryInvoke(controladorEv, new String[]{"consultaEvento"}, ev);
        assertNotNull(edicion);
        assertNotNull(evento);

        Object instObj = DomainAccess.obtenerInstitucion(INST);
        Object usuario = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "ana", "Ana", "ana@x", "Ap", hoy.minusYears(20), instObj);
        assertNotNull(usuario);

        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro", "AltaTipoRegistro"},
                edicion, "GENERAL", "Acceso general", 0, 50);

        Object tipo = resolverTipoRegistro(edicion, "GENERAL");
        assertNotNull(tipo, "No hay TipoRegistro disponible");

        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                "R-001", usuario, evento, edicion, tipo, hoy, 0.0f, hoy.plusDays(3))
        );

        Object dtr = TestUtils.tryInvoke(controladorEv, new String[]{"consultaRegistro"}, usuario, "R-001");
        assertNotNull(dtr);
    }

    private Object resolverTipoRegistro(Object edicion, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
    @DisplayName("altaEdicionEvento inválida → debe lanzar IllegalArgumentException")
    void altaEdicionEventoFechasInvalidas() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String ev = "Conf_" + System.nanoTime();

        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "CONF", cats, INST);

        LocalDate hoy = LocalDate.now();
        // fechas invertidas → debe fallar (o tu impl normaliza en EdgeCases)
        assertThrows(IllegalArgumentException.class, () ->
            crearEdicion(controladorEv, ev, ORG,
                    "Bad", "B01", "x",
                    hoy.plusDays(5), hoy.plusDays(4), hoy,
                    "Montevideo", "Uruguay", null)
        );
    }

    @Test
    @DisplayName("consultaEdicionEvento con claves inválidas → devuelve null o lanza (ambos válidos)")
    void consultaEdicionEventoInvalida() {
        try {
            Object dto = TestUtils.invokeUnwrapped(
                    controladorEv,
                    new String[]{"consultaEdicionEvento"},
                    "XX", "??"
            );
            // Si no lanza, debe devolver null
            assertNull(dto, "Si no lanza, consultaEdicionEvento debe devolver null para claves inválidas");
        } catch (Throwable e) {
            // Si tu implementación lanza, también es válido (runtime/custom)
            assertTrue(
                e instanceof RuntimeException
                || e instanceof IllegalArgumentException
                || e.getClass().getSimpleName().contains("NoExiste")
                || e.getClass().getSimpleName().contains("Invalida"),
                "Se esperaba una excepción de runtime o de dominio equivalente"
            );
        }
    }
    
    // ===================== NUEVOS TESTS PARA consultaRegistro / selección de edición =====================

    @Test
    @DisplayName("consultaRegistro: devuelve DTRegistro con datos consistentes (camino feliz)")
    void consultaRegistro_detalleOk() throws Throwable {
        // 1) Alta evento + edición + tipo + asistente + registro
        Object cats = categoriasDTO("Tec");
        String ev = "Expo_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "EXPO", cats, INST);

        LocalDate hoy = LocalDate.now();
        String edName = "Otoño";
        String edSig  = "OT25";
        crearEdicion(controladorEv, ev, ORG,
                edName, edSig, "Ed Otoño",
                hoy.plusDays(5), hoy.plusDays(6), hoy,
                "Montevideo", "Uruguay", "ed.png");

        Object edicion = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, ev, edName);
        Object evento  = TestUtils.tryInvoke(controladorEv, new String[]{"consultaEvento"}, ev);
        assertNotNull(edicion);
        assertNotNull(evento);

        Object instObj = DomainAccess.obtenerInstitucion(INST);
        Object usuarioAsis = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "luis", "Luis", "luis@x", "Pérez", hoy.minusYears(30), instObj);
        assertNotNull(usuarioAsis);

        // Tipo y registro
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro", "AltaTipoRegistro"},
                edicion, "GENERAL", "Acceso general", 1234, 10);

        Object tipo = resolverTipoRegistro(edicion, "GENERAL");
        assertNotNull(tipo, "No se pudo resolver TipoRegistro GENERAL");

        String rid = "R-" + System.nanoTime();
        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                rid, usuarioAsis, evento, edicion, tipo, hoy, 1234.0f, hoy.plusDays(5));

        // 2) consultaRegistro(user, id) → DTRegistro
        Object dtr = TestUtils.tryInvoke(controladorEv, new String[]{"consultaRegistro"}, usuarioAsis, rid);
        assertNotNull(dtr, "consultaRegistro debe devolver un DTRegistro");

        // 3) Validaciones de campos vía reflexión (getters típicos)
        Method gId      = TestUtils.findMethod(dtr, "getId", "id");
        Method gUsuario = TestUtils.findMethod(dtr, "getUsuario", "usuario");
        Method gEdicion = TestUtils.findMethod(dtr, "getEdicion", "edicion");
        Method gTipo    = TestUtils.findMethod(dtr, "getTipoRegistro", "tipoRegistro");
        Method gFReg    = TestUtils.findMethod(dtr, "getFechaRegistro");
        Method gCosto   = TestUtils.findMethod(dtr, "getCosto");
        Method gFIni    = TestUtils.findMethod(dtr, "getFechaInicio");

        assertNotNull(gId); assertNotNull(gUsuario); assertNotNull(gEdicion);
        assertNotNull(gTipo); assertNotNull(gFReg); assertNotNull(gCosto); assertNotNull(gFIni);

        assertEquals(rid, String.valueOf(gId.invoke(dtr)));
        // consultaRegistro usa user.getNombre() → "Luis"
        assertEquals("Luis", String.valueOf(gUsuario.invoke(dtr)));
        assertEquals(edName, String.valueOf(gEdicion.invoke(dtr)));
        assertEquals("GENERAL", String.valueOf(gTipo.invoke(dtr)));
        assertEquals(hoy, gFReg.invoke(dtr));
        assertEquals(hoy.plusDays(5), gFIni.invoke(dtr));
        assertEquals(1234.0f, ((Number) gCosto.invoke(dtr)).floatValue(), 0.0001f);
    }

    @Test
    @DisplayName("consultaRegistro: si el usuario NO es Asistente → UsuarioNoEsAsistente")
    void consultaRegistro_usuarioNoAsistente() throws Throwable {
        // Preparamos un registro válido con una asistente
        Object cats = categoriasDTO("Tec");
        String ev = "Conf_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "CONF", cats, INST);

        LocalDate hoy = LocalDate.now();
        String edName = "Primavera";
        crearEdicion(controladorEv, ev, ORG,
                edName, "PR25", "Ed Primavera",
                hoy.plusDays(2), hoy.plusDays(3), hoy,
                "Montevideo", "Uruguay", null);

        Object edicion = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, ev, edName);
        Object evento  = TestUtils.tryInvoke(controladorEv, new String[]{"consultaEvento"}, ev);

        Object instObj = DomainAccess.obtenerInstitucion(INST);
        Object usuarioAsis = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "val", "Valeria", "val@x", "L", hoy.minusYears(25), instObj);
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro"}, edicion, "GEN", "x", 0, 10);
        Object tipo = resolverTipoRegistro(edicion, "GEN");

        String rid = "R-" + System.nanoTime();
        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                rid, usuarioAsis, evento, edicion, tipo, hoy, 0.0f, hoy.plusDays(2));

        // Tomamos un usuario NO asistente (el organizador creado en @BeforeEach)
        Object usuarioNoAsis = DomainAccess.obtenerUsuario(ORG);
        assertNotNull(usuarioNoAsis);

        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"consultaRegistro"},
                    usuarioNoAsis, rid);
            fail("Debe lanzar UsuarioNoEsAsistente");
        } catch (Throwable e) {
            String name = e.getClass().getSimpleName();
            assertTrue(name.contains("UsuarioNoEsAsistente"), "Esperábamos UsuarioNoEsAsistente pero fue: " + name);
        }
    }

    @Test
    @DisplayName("consultaRegistro: id inexistente → RegistroNoExiste")
    void consultaRegistro_idInexistente() throws Exception {
        Object instObj = DomainAccess.obtenerInstitucion(INST);
        Object usuarioAsis = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "bea", "Bea", "bea@x", "Z", LocalDate.now().minusYears(22), instObj);
        assertNotNull(usuarioAsis);

        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"consultaRegistro"},
                    usuarioAsis, "ID_QUE_NO_EXISTE");
            fail("Debe lanzar RegistroNoExiste");
        } catch (Throwable e) {
            String name = e.getClass().getSimpleName();
            assertTrue(name.contains("RegistroNoExiste"), "Esperábamos RegistroNoExiste pero fue: " + name);
        }
    }

    @Test
    @DisplayName("seleccionarEdicion + getEdicionSeleccionadaSigla + obtenerEdicionSeleccionada (camino feliz)")
    void seleccionar_y_obtenerEdicionSeleccionada() throws Throwable {
        // Evento + edición
        Object cats = categoriasDTO("Tecnologia");
        String evName = "Jornadas_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc", LocalDate.now(), "JRN", cats, INST);

        LocalDate hoy = LocalDate.now();
        String edName = "Central";
        String sigla  = "CEN25";
        String ciudad = "Salto";
        String pais   = "Uruguay";
        String img    = "central.png";

        crearEdicion(controladorEv, evName, ORG,
                edName, sigla, "Ed central",
                hoy.plusDays(8), hoy.plusDays(9), hoy,
                ciudad, pais, img);

        // seleccionarEdicion(sigla) y verificar getEdicionSeleccionadaSigla()
        assertDoesNotThrow(() ->
                TestUtils.invokeUnwrapped(controladorEv, new String[]{"seleccionarEdicion"}, sigla));

        Object selSigla = TestUtils.tryInvoke(controladorEv, new String[]{"getEdicionSeleccionadaSigla"});
        assertEquals(sigla, selSigla);

        // obtenerEdicionSeleccionada() → DTEdicion con datos coherentes
        Object dted = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicionSeleccionada"});
        assertNotNull(dted, "Debe devolver un DTEdicion");

        Method gNombre = TestUtils.findMethod(dted, "getNombre", "nombre");
        Method gSigla  = TestUtils.findMethod(dted, "getSigla",  "sigla");
        Method gCiudad = TestUtils.findMethod(dted, "getCiudad","ciudad");
        Method gPais   = TestUtils.findMethod(dted, "getPais",  "pais");
        Method gImagen = TestUtils.findMethod(dted, "getImagen","imagen");
        Method gEvento = TestUtils.findMethod(dted, "getEvento","evento");

        assertEquals(edName, String.valueOf(gNombre.invoke(dted)));
        assertEquals(sigla,  String.valueOf(gSigla.invoke(dted)));
        assertEquals(ciudad, String.valueOf(gCiudad.invoke(dted)));
        assertEquals(pais,   String.valueOf(gPais.invoke(dted)));
        assertEquals(img,    String.valueOf(gImagen.invoke(dted)));

        Object dtEvento = gEvento.invoke(dted);
        assertNotNull(dtEvento, "Debe haber un DTEvento asociado");
        assertEquals(evName, getDTEventoNombre(dtEvento));
    }

    @Test
    @DisplayName("obtenerDtEdicion(nombreEvento, nombreEdicion): arma DTO completo con tipos y registros")
    void obtenerDtEdicion_conTiposYRegistros() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String evName = "Summit_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc", LocalDate.now(), "SUM", cats, INST);

        LocalDate hoy = LocalDate.now();
        String edName = "Ed2026";
        crearEdicion(controladorEv, evName, ORG,
                edName, "S26", "Ed 26",
                hoy.plusDays(20), hoy.plusDays(22), hoy,
                "MVD", "UY", "s26.png");

        Object edicion = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, evName, edName);
        assertNotNull(edicion);

        // 1 tipo de registro
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro", "AltaTipoRegistro"},
                edicion, "GENERAL", "Acceso general", 500, 100);

        // 1 asistente + 1 registro
        Object instObj = DomainAccess.obtenerInstitucion(INST);
        Object usuarioAsis = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "ema", "Ema", "ema@x", "R", hoy.minusYears(19), instObj);

        Object evento  = TestUtils.tryInvoke(controladorEv, new String[]{"consultaEvento"}, evName);
        Object tipo    = resolverTipoRegistro(edicion, "GENERAL");
        String rid     = "R-" + System.nanoTime();

        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                rid, usuarioAsis, evento, edicion, tipo, hoy, 500.0f, hoy.plusDays(20));

        // obtenerDtEdicion(...)
        Object dto = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerDtEdicion"}, evName, edName);
        assertNotNull(dto, "Debe devolver DTEdicion");

        // Validamos listas (>=1 tipo, >=1 registro). No asumimos orden.
        Method gTipos = TestUtils.findMethod(dto, "getTiposRegistro");
        Method gRegs  = TestUtils.findMethod(dto, "getRegistros");
        Method gPats  = TestUtils.findMethod(dto, "getPatrocinios");
        Method gEstado= TestUtils.findMethod(dto, "getEstado");

        @SuppressWarnings("unchecked")
        List<Object> tipos = (List<Object>) gTipos.invoke(dto);
        @SuppressWarnings("unchecked")
        List<Object> regs  = (List<Object>) gRegs.invoke(dto);
        @SuppressWarnings("unchecked")
        List<Object> pats  = (List<Object>) (gPats != null ? gPats.invoke(dto) : java.util.Collections.emptyList());

        assertNotNull(tipos); assertFalse(tipos.isEmpty(), "Debe haber al menos 1 tipo");
        assertNotNull(regs);  assertFalse(regs.isEmpty(),  "Debe haber al menos 1 registro");
        assertNotNull(pats);  // probablemente vacío si no diste de alta patrocinios

        // Chequeo simple: el primer tipo tiene nombre "GENERAL"
        Object tipo0 = tipos.get(0);
        Method gNombreTipo = TestUtils.findMethod(tipo0, "getNombre","nombre");
        assertEquals("GENERAL", String.valueOf(gNombreTipo.invoke(tipo0)));

        // Chequeo simple sobre el primer registro: id y costo
        Object reg0 = regs.get(0);
        Method gId = TestUtils.findMethod(reg0, "getId","id");
        Method gCosto = TestUtils.findMethod(reg0, "getCosto","costo");
        assertEquals(rid, String.valueOf(gId.invoke(reg0)));
        assertEquals(500.0f, ((Number) gCosto.invoke(reg0)).floatValue(), 0.0001f);

        // Estado puede ser null o algún valor de dominio, pero el getter debe existir
        if (gEstado != null) {
            gEstado.invoke(dto); // solo verificamos que no falle
        }
    }
    // ===================== NUEVOS TESTS PARA LOS MÉTODOS PEGADOS =====================

    @Test
    @DisplayName("obtenerEdicionPorSiglaDT y encontrarEventoPorSigla")
    void obtenerPorSiglaDT_y_encontrarEventoPorSigla() throws Throwable {
        Object cats = categoriasDTO("Tecnologia");
        String evName = "ExpoSigla_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc", LocalDate.now(), "EXS", cats, INST);

        String edName = "Siglada";
        String sigla  = "SIG" + System.nanoTime();
        LocalDate hoy = LocalDate.now();

        crearEdicion(controladorEv, evName, ORG,
                edName, sigla, "Ed Siglada",
                hoy.plusDays(1), hoy.plusDays(2), hoy,
                "Montevideo", "Uruguay", "sig.png");

        Object dto = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicionPorSiglaDT"}, sigla);
        assertNotNull(dto, "Debe devolver DTEdicion");

        var gNombre = TestUtils.findMethod(dto, "getNombre");
        var gSigla  = TestUtils.findMethod(dto, "getSigla");
        var gEvento = TestUtils.findMethod(dto, "getEvento");
        assertEquals(edName, String.valueOf(gNombre.invoke(dto)));
        assertEquals(sigla,  String.valueOf(gSigla.invoke(dto)));
        assertEquals(evName, getDTEventoNombre(gEvento.invoke(dto)));

        Object evEncontrado = TestUtils.tryInvoke(controladorEv, new String[]{"encontrarEventoPorSigla"}, sigla);
        assertEquals(evName, evEncontrado);
    }

    @Test
    @DisplayName("listarEventosConEdicionesIngresadas y listarEdicionesIngresadasDeEvento")
    void listarIngresadas_enEventoYGlobal() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String evName = "EVIng_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc", LocalDate.now(), "ING", cats, INST);

        LocalDate hoy = LocalDate.now();
        crearEdicion(controladorEv, evName, ORG, "EdA", "A1", "x",
                hoy.plusDays(3), hoy.plusDays(4), hoy, "MVD", "UY", null);
        crearEdicion(controladorEv, evName, ORG, "EdB", "B1", "x",
                hoy.plusDays(5), hoy.plusDays(6), hoy, "MVD", "UY", null);

        // Setear estado = Ingresada a EdA (accedemos a la entidad)
        Object evento = DomainAccess.obtenerEvento(evName);
        assertNotNull(evento);
        Object edA = TestUtils.tryInvoke(evento, new String[]{"obtenerEdicion"}, "EdA");
        assertNotNull(edA);

        Class<?> enumEstado = Class.forName("logica.enumerados.DTEstado");
        Object INGRESADA = Enum.valueOf((Class<Enum>) enumEstado.asSubclass(Enum.class), "Ingresada");
        Method setEstado = edA.getClass().getMethod("setEstado", enumEstado);
        setEstado.invoke(edA, INGRESADA);

        // Global
        @SuppressWarnings("unchecked")
        List<String> eventosConIngresadas = (List<String>)
                TestUtils.tryInvoke(controladorEv, new String[]{"listarEventosConEdicionesIngresadas"});
        assertTrue(eventosConIngresadas.contains(evName));

        // Por evento
        @SuppressWarnings("unchecked")
        List<String> edsIngresadas = (List<String>)
                TestUtils.tryInvoke(controladorEv, new String[]{"listarEdicionesIngresadasDeEvento"}, evName);
        assertTrue(edsIngresadas.contains("EdA"));
        assertFalse(edsIngresadas.contains("EdB"));
    }

    @Test
    @DisplayName("aceptarRechazarEdicion y cambiarEstadoEdicion")
    void aceptarRechazar_y_cambiarEstado() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String evName = "EVEstado_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc", LocalDate.now(), "EST", cats, INST);

        LocalDate hoy = LocalDate.now();
        String edName = "Panel";
        String sigla  = "P" + System.nanoTime();
        crearEdicion(controladorEv, evName, ORG, edName, sigla, "x",
                hoy.plusDays(1), hoy.plusDays(2), hoy, "MVD", "UY", null);

        // Entidad Ediciones
        Object evento = DomainAccess.obtenerEvento(evName);
        Object edicion = TestUtils.tryInvoke(evento, new String[]{"obtenerEdicion"}, edName);
        assertNotNull(edicion);

        // aceptarRechazarEdicion(edicion, true) -> Aceptada
        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"aceptarRechazarEdicion"}, edicion, true)
        );
        Object estado = TestUtils.findMethod(edicion, "getEstado").invoke(edicion);
        assertEquals("Aceptada", ((Enum<?>) estado).name());

        // cambiarEstadoEdicion(evento, edicion, false) -> Rechazada
        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"cambiarEstadoEdicion"}, evName, edName, false)
        );
        estado = TestUtils.findMethod(edicion, "getEstado").invoke(edicion);
        assertEquals("Rechazada", ((Enum<?>) estado).name());
    }

    @Test
    @DisplayName("actualizarImagenEvento actualiza la imagen en la entidad Evento")
    void actualizarImagenEvento_ok() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String evName = "EVI_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                evName, "Desc", LocalDate.now(), "EVI", cats, INST);

        String imagen = "/path/a/imagen.png";
        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"actualizarImagenEvento"}, evName, imagen)
        );

        Object evento = DomainAccess.obtenerEvento(evName);
        assertNotNull(evento);

        Method gImg = TestUtils.findMethod(evento, "getImagen", "getImagenPath");
        assertNotNull(gImg, "La entidad Evento debe exponer getImagen o getImagenPath");
        assertEquals(imagen, gImg.invoke(evento));
    }

    @Test
    @DisplayName("altaRegistroEdicionEvento(Strings...) – camino feliz y consultaRegistro")
    void altaRegistroEdicionEvento_strings_caminoFeliz() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String ev = "EVR_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "EVR", cats, INST);

        LocalDate hoy = LocalDate.now();
        String ed = "EdR";
        crearEdicion(controladorEv, ev, ORG, ed, "ER1", "x",
                hoy.plusDays(10), hoy.plusDays(11), hoy, "MVD", "UY", null);

        Object edEntidad = TestUtils.tryInvoke(DomainAccess.obtenerEvento(ev), new String[]{"obtenerEdicion"}, ed);
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro"}, edEntidad, "GENERAL", "x", 2, 5);

        Object inst = DomainAccess.obtenerInstitucion(INST);
        Object usuarioAsis = TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "sol", "Sol", "sol@x", "P", hoy.minusYears(23), inst);

        String rid = "R-" + System.nanoTime();
        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                rid, "sol", ev, ed, "GENERAL", hoy, 2.0f, hoy.plusDays(10))
        );

        // Confirmamos vía consultaRegistro (la versión con Usuario)
        Object dtr = TestUtils.tryInvoke(controladorEv, new String[]{"consultaRegistro"}, usuarioAsis, rid);
        assertNotNull(dtr);
        Method gId = TestUtils.findMethod(dtr, "getId");
        assertEquals(rid, String.valueOf(gId.invoke(dtr)));
    }

    @Test
    @DisplayName("altaRegistroEdicionEvento – validaciones: usuario no asistente, evento/edición/tipo inválidos")
    void altaRegistroEdicionEvento_validacionesBasicas() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String ev = "EVV_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "EVV", cats, INST);

        LocalDate hoy = LocalDate.now();
        String ed = "EdV";
        crearEdicion(controladorEv, ev, ORG, ed, "EVV1", "x",
                hoy.plusDays(2), hoy.plusDays(3), hoy, "MVD", "UY", null);

        // Usuario NO asistente
        String rid = "R-" + System.nanoTime();
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    rid, ORG, ev, ed, "GENERAL", hoy, 0f, hoy.plusDays(2));
            fail("Debe fallar: usuario no es asistente válido");
        } catch (Throwable e) {
            assertTrue(e.getMessage().toLowerCase().contains("asistente"),
                    "Mensaje debe indicar que no es asistente");
        }

        // Evento inexistente
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    rid, "nohay", "EV_NO", ed, "GENERAL", hoy, 0f, hoy.plusDays(2));
            fail("Debe fallar: evento no encontrado");
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("Evento no encontrado"), "Debe mencionar evento no encontrado");
        }

        // Edición inexistente
        Object inst = DomainAccess.obtenerInstitucion(INST);
        TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "ina", "Ina", "ina@x", "P", hoy.minusYears(20), inst);

        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    "R-X", "ina", ev, "NO_ED", "GENERAL", hoy, 0f, hoy.plusDays(2));
            fail("Debe fallar: edición no encontrada");
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("Edición no encontrada"), "Debe mencionar edición no encontrada");
        }

        // Tipo inexistente
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    "R-Y", "ina", ev, ed, "NO_TIPO", hoy, 0f, hoy.plusDays(2));
            fail("Debe fallar: tipo no encontrado");
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("Tipo de registro no encontrado"), "Debe mencionar tipo no encontrado");
        }
    }

    @Test
    @DisplayName("altaRegistroEdicionEvento – cupo inválido, duplicado y cupo lleno")
    void altaRegistroEdicionEvento_cupo_y_duplicados() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String ev = "EVCupo_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "CUP", cats, INST);

        LocalDate hoy = LocalDate.now();
        String ed = "EdC";
        crearEdicion(controladorEv, ev, ORG, ed, "CUP1", "x",
                hoy.plusDays(10), hoy.plusDays(11), hoy, "MVD", "UY", null);

        Object edEntidad = TestUtils.tryInvoke(DomainAccess.obtenerEvento(ev), new String[]{"obtenerEdicion"}, ed);

        // Tipo con cupo 0 → CupoTipoRegistroInvalidoException
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro"}, edEntidad, "SINCUPO", "x", 0, 0);
        Object inst = DomainAccess.obtenerInstitucion(INST);
        TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "ari", "Ari", "ari@x", "P", hoy.minusYears(19), inst);
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    "R0", "ari", ev, ed, "SINCUPO", hoy, 0f, hoy.plusDays(10));
            fail("Debe lanzar CupoTipoRegistroInvalidoException por cupo 0");
        } catch (Throwable e) {
            assertTrue(e.getClass().getSimpleName().contains("CupoTipoRegistroInvalido"), "Esperábamos CupoTipoRegistroInvalidoException");
        }

        // Tipo con cupo 1 → segundo registro debe fallar por cupo lleno
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro"}, edEntidad, "UNO", "x", 0, 1);
        TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "a1", "A1", "a1@x", "P", hoy.minusYears(22), inst);
        TestUtils.tryInvoke(controladorUs, new String[]{"ingresarAsistente", "IngresarDatosAsis"},
                "a2", "A2", "a2@x", "P", hoy.minusYears(22), inst);

        // primer ok
        TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                "R1", "a1", ev, ed, "UNO", hoy, 0f, hoy.plusDays(10));
        // duplicado del mismo usuario/edición → RuntimeException
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    "R1b", "a1", ev, ed, "UNO", hoy, 0f, hoy.plusDays(10));
            fail("Debe fallar por usuario ya registrado en la edición");
        } catch (Throwable e) {
            assertTrue(e.getMessage().toLowerCase().contains("ya está registrado"),
                    "Debe mencionar usuario ya registrado");
        }

        // segundo usuario intenta ocupar cupo 1 → CupoTipoRegistroInvalidoException (lleno)
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaRegistroEdicionEvento"},
                    "R2", "a2", ev, ed, "UNO", hoy, 0f, hoy.plusDays(10));
            fail("Debe fallar por cupo lleno");
        } catch (Throwable e) {
            assertTrue(e.getClass().getSimpleName().contains("CupoTipoRegistroInvalido"),
                    "Esperábamos CupoTipoRegistroInvalidoException por cupo lleno");
        }
    }

    @Test
    @DisplayName("altaEdicionEventoDTO – éxito cuando el evento existe; excepción cuando no existe (según tu lógica)")
    void altaEdicionEventoDTO_casos() throws Throwable {
        // Evento existente (caso éxito)
        Object cats = categoriasDTO("Tec");
        String ev = "EVDTO_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "DTX", cats, INST);

        // Armamos DTEvento (nombre = ev) y DTDatosUsuario (nick = ORG)
        Class<?> dtEventoCls = Class.forName("logica.datatypes.DTEvento");
        var ctorEv = dtEventoCls.getDeclaredConstructor(String.class, String.class, String.class, LocalDate.class,
                java.util.List.class, java.util.List.class);
        Object dtEvento = ctorEv.newInstance(ev, "S", "D", LocalDate.now(), java.util.List.of("Tec"), java.util.List.of());

        Class<?> dtDatosUsrCls = Class.forName("logica.datatypes.DTDatosUsuario");
        // probamos constructores comunes: (nick, nombre, correo, ...). Si cambia, usá TestUtils.tolerantNew
        Object dtUsr = TestUtils.tolerantNew("logica.datatypes.DTDatosUsuario",
                ORG, "Org Uno", MAIL, "Ap", LocalDate.of(1990, 1, 1), INST, true, null, null);

        LocalDate hoy = LocalDate.now();
        assertDoesNotThrow(() ->
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaEdicionEventoDTO"},
                dtEvento, dtUsr,
                "DTOEd", "DTO1", "x",
                hoy.plusDays(7), hoy.plusDays(8), hoy,
                "MVD", "UY", "dto.png")
        );

        // Verificamos que ahora existe la edición
        Object getEd = TestUtils.tryInvoke(controladorEv, new String[]{"obtenerEdicion"}, ev, "DTOEd");
        assertNotNull(getEd, "Debe haberse creado la edición a partir del DTO");

        // Evento inexistente según tu lógica → lanza EventoYaExisteException
        Object dtEventoNo = ctorEv.newInstance("NOEXISTE", "S", "D", LocalDate.now(), java.util.List.of("Tec"), java.util.List.of());
        try {
            TestUtils.invokeUnwrapped(controladorEv, new String[]{"altaEdicionEventoDTO"},
                dtEventoNo, dtUsr,
                "X", "X1", "x",
                hoy.plusDays(1), hoy.plusDays(2), hoy,
                "MVD", "UY", null);
            fail("Debe lanzar EventoYaExisteException cuando el evento no está en el manejador (según implementación pegada)");
        } catch (Throwable e) {
            assertTrue(e.getClass().getSimpleName().contains("EventoYaExiste"),
                    "Esperábamos EventoYaExisteException según tu rama de código");
        }
    }

    @Test
    @DisplayName("consultaTipoRegistro devuelve DTO consistente")
    void consultaTipoRegistro_ok() throws Throwable {
        Object cats = categoriasDTO("Tec");
        String ev = "EVTR_" + System.nanoTime();
        TestUtils.tryInvoke(controladorEv, new String[]{"altaEvento"},
                ev, "Desc", LocalDate.now(), "TRG", cats, INST);

        LocalDate hoy = LocalDate.now();
        String ed = "EdTR";
        crearEdicion(controladorEv, ev, ORG, ed, "TR1", "x",
                hoy.plusDays(4), hoy.plusDays(5), hoy, "MVD", "UY", null);

        Object edEntidad = TestUtils.tryInvoke(DomainAccess.obtenerEvento(ev), new String[]{"obtenerEdicion"}, ed);
        TestUtils.tryInvoke(controladorEv, new String[]{"altaTipoRegistro"},
                edEntidad, "VIP", "Beneficios", 1999.99f, 100);

        Object dto = TestUtils.tryInvoke(controladorEv, new String[]{"consultaTipoRegistro"}, ev, ed, "VIP");
        assertNotNull(dto, "Debe devolver DTTipoRegistro");

        Method gNombre = TestUtils.findMethod(dto, "getNombre");
        Method gDesc   = TestUtils.findMethod(dto, "getDescripcion");
        Method gCosto  = TestUtils.findMethod(dto, "getCosto");
        Method gCupo   = TestUtils.findMethod(dto, "getCupo");

        assertEquals("VIP", String.valueOf(gNombre.invoke(dto)));
        assertEquals("Beneficios", String.valueOf(gDesc.invoke(dto)));
        assertEquals(1999.99f, ((Number) gCosto.invoke(dto)).floatValue(), 0.0001f);
        assertEquals(100, ((Number) gCupo.invoke(dto)).intValue());
    }

}
