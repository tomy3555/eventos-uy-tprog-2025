package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.ws.WebServiceException;
import logica.interfaces.IControladorEvento;
import publicadores.PublicadorEvento;

import excepciones.EdicionYaExisteException;
import excepciones.EventoYaExisteException;
import excepciones.FechasCruzadasException;
import excepciones.PatrocinioYaExisteException;
import excepciones.ValorPatrocinioExcedidoException;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;

public class PublicadorEventoExtraTest {

    static class IceHandler implements InvocationHandler {
        LocalDate altaEventoFecha;

        LocalDate altaEd_fi, altaEd_ff, altaEd_fa;

        LocalDate regDT_fr, regDT_fi;
        LocalDate reg_fr, reg_fi;

        LocalDate pat_fp;

        boolean actualizarImagenCalled;
        boolean finalizarEventoCalled;
        boolean marcarAsistenciaCalled;
        int altaTipoRegistroDtoCalls;
        boolean archivarCalled;
        boolean throwInArchivar;

        List<String> listarEdicionesEventoReturn = Arrays.asList("E1", "E2");
        List<String> listarEdicionesArchivablesReturn = Arrays.asList("A1","A2");
        List<logica.datatypes.DTCategorias> listarDTCategoriasReturn = Collections.emptyList();
        List<logica.datatypes.DTEvento> listarEventosPorCategoriaReturn = Collections.emptyList();
        List<String> listarCategoriasConEventosReturn = Arrays.asList("Tech","Sports");
        List<logica.datatypes.DTTipoRegistro> listarTiposRegistroDeEdicionReturn = Collections.emptyList();
        List<logica.datatypes.DTEvento> listarEventosVigentesReturn = Collections.emptyList();
        List<logica.datatypes.DTArchEdicion> edicionesArchivadasReturn = Collections.emptyList();

        String encontrarEventoPorSiglaReturn = "EventoX";

        String lastMethod;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String m = method.getName();
            lastMethod = m;

            switch (m) {
                case "altaEvento":
                    altaEventoFecha = (LocalDate) args[2];
                    return null;
                case "altaEdicionEventoDTO":
                    altaEd_fi = (LocalDate) args[5];
                    altaEd_ff = (LocalDate) args[6];
                    altaEd_fa = (LocalDate) args[7];
                    return null;
                case "listarEdicionesEvento":
                    return listarEdicionesEventoReturn;
                case "listarEventos":
                    return Collections.emptyList();
                case "altaRegistroEdicionEventoDT":
                    regDT_fr = (LocalDate) args[5];
                    regDT_fi = (LocalDate) args[7];
                    return null;
                case "altaRegistroEdicionEvento":
                    reg_fr = (LocalDate) args[5];
                    reg_fi = (LocalDate) args[7];
                    return null;
                case "listarEdicionesArchivables":
                    if (throwInArchivar) throw new RuntimeException("boom-listar");
                    return listarEdicionesArchivablesReturn;
                case "archivarEdicion":
                    if (throwInArchivar) throw new RuntimeException("boom-archivar");
                    archivarCalled = true;
                    return null;
                case "listarDTCategorias":
                    return listarDTCategoriasReturn;
                case "listarEventosPorCategoria":
                    return listarEventosPorCategoriaReturn;
                case "listarCategoriasConEventos":
                    return listarCategoriasConEventosReturn;
                case "listarTiposRegistroDeEdicion":
                    return listarTiposRegistroDeEdicionReturn;
                case "listarEventosVigentes":
                    return listarEventosVigentesReturn;
                case "consultaDTEvento":
                case "obtenerDtEdicion":
                case "obtenerEdicionPorSiglaDT":
                case "consultaEdicionEvento":
                case "obtenerDTPatrocinio":
                    return null;
                case "actualizarImagenEvento":
                    actualizarImagenCalled = true;
                    return null;
                case "finalizarEvento":
                    finalizarEventoCalled = true;
                    return null;
                case "altaTipoRegistroDTO":
                    altaTipoRegistroDtoCalls++;
                    return null;
                case "encontrarEventoPorSigla":
                    return encontrarEventoPorSiglaReturn;
                case "altaPatrocinioDT":
                    pat_fp = (LocalDate) args[5];
                    return null;
            }

            Class<?> rt = method.getReturnType();
            if (rt.equals(void.class)) return null;
            if (List.class.isAssignableFrom(rt)) return Collections.emptyList();
            if (rt.isArray()) return Array.newInstance(rt.getComponentType(), 0);
            return null;
        }
    }

    static Object proxyFor(IceHandler h) {
        return Proxy.newProxyInstance(
                IControladorEvento.class.getClassLoader(),
                new Class<?>[]{IControladorEvento.class},
                h
        );
    }

    static sun.misc.Unsafe unsafe() {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    static PublicadorEvento newSvc(IceHandler h) {
        try {
            PublicadorEvento svc = (PublicadorEvento) unsafe().allocateInstance(PublicadorEvento.class);
            Field iceF = PublicadorEvento.class.getDeclaredField("ice");
            iceF.setAccessible(true);
            iceF.set(svc, proxyFor(h));
            return svc;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void altaEdicionEventoDTO_parseaFechas() throws EdicionYaExisteException, EventoYaExisteException, FechasCruzadasException {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        LocalDate hoy = LocalDate.now();
        svc.altaEdicionEventoDTO(null, null, "N", "S", "D", null, null, null, "C", "P", "img", "vid");
        assertNull(h.altaEd_fi);
        assertNull(h.altaEd_ff);
        assertEquals(hoy, h.altaEd_fa);

        svc.altaEdicionEventoDTO(null, null, "N", "S", "D", "2025-01-02", "2025-01-03", "2024-12-31", "C", "P", "img", "vid");
        assertEquals(LocalDate.of(2025,1,2), h.altaEd_fi);
        assertEquals(LocalDate.of(2025,1,3), h.altaEd_ff);
        assertEquals(LocalDate.of(2024,12,31), h.altaEd_fa);
    }

    @Test
    void altaPatrocinioDT_parseaFecha() throws ValorPatrocinioExcedidoException, PatrocinioYaExisteException {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        LocalDate hoy = LocalDate.now();
        svc.altaPatrocinioDT("SIG","Inst", logica.enumerados.DTNivel.ORO, "TR", 1000, null, 10, "COD");
        assertEquals(hoy, h.pat_fp);

        svc.altaPatrocinioDT("SIG","Inst", logica.enumerados.DTNivel.PLATA, "TR", 200, "2025-02-10", 5, "COD");
        assertEquals(LocalDate.of(2025,2,10), h.pat_fp);
    }


    @Test
    void altaRegistroEdicionEvento_parseoPorDefecto() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        LocalDate hoy = LocalDate.now();
        svc.altaRegistroEdicionEvento("ID", null, null, null, null, null, 100f, null);
        assertEquals(hoy, h.reg_fr);
        assertEquals(hoy, h.reg_fi);

        svc.altaRegistroEdicionEvento("ID", null, null, null, null, "2025-07-01", 100f, null);
        assertEquals(LocalDate.of(2025,7,1), h.reg_fr);
        assertEquals(LocalDate.of(2025,7,1), h.reg_fi);

        svc.altaRegistroEdicionEvento("ID", null, null, null, null, "2025-07-01", 100f, "2025-07-10");
        assertEquals(LocalDate.of(2025,7,1), h.reg_fr);
        assertEquals(LocalDate.of(2025,7,10), h.reg_fi);
    }

    @Test
    void wrappersAltaTipoRegistro_noFallaAmbasVariantes() throws excepciones.TipoRegistroYaExisteException,
            excepciones.CupoTipoRegistroInvalidoException, excepciones.CostoTipoRegistroInvalidoException {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        svc.altaTipoRegistro(null, "VIP", "D", 10f, 5);
        svc.altaTipoRegistroDTO(null, "VIP", "D", 10f, 5);
        assertEquals(2, h.altaTipoRegistroDtoCalls);
    }

    @Test
    void convertidoresAArray_variosMetodos() {
        IceHandler h = new IceHandler();
        h.listarEdicionesEventoReturn = Arrays.asList("A","B");
        h.listarCategoriasConEventosReturn = Arrays.asList("Tech","Sports");

        PublicadorEvento svc = newSvc(h);

        assertArrayEquals(new String[]{"A","B"}, svc.listarEdicionesEvento("Ev"));
        assertArrayEquals(new String[]{"Tech","Sports"}, svc.listarCategoriasConEventos());
        assertEquals(0, svc.listarEventosVigentes().length);
        assertEquals(0, svc.listarDTCategorias().length);
        assertEquals(0, svc.listarTiposRegistroDeEdicion("Ev","Ed").length);
        assertEquals(0, svc.listarEventosPorCategoria("Tech").length);
        assertEquals(0, svc.edicionesArchivadasOrganizadas("org").length);
    }

    @Test
    void passthroughs_sinRetorno_noLanzan() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        assertDoesNotThrow(() -> svc.actualizarImagenEvento("Ev","/p.png"));
        assertDoesNotThrow(() -> svc.finalizarEvento("Ev"));
        assertDoesNotThrow(() -> svc.marcarAsistenciaRegistro("nick","id"));

        assertTrue(h.actualizarImagenCalled);
        assertTrue(h.finalizarEventoCalled);
    }

    @Test
    void obteneres_noFallaAunqueControladorDevuelvaNull() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        assertDoesNotThrow(() -> svc.consultaDTEvento("Ev"));
        assertDoesNotThrow(() -> svc.obtenerDtEdicion("Ev","Ed"));
        assertDoesNotThrow(() -> svc.obtenerEdicionPorSiglaDT("SIG"));
        assertDoesNotThrow(() -> svc.consultaEdicionEvento("SE","SI"));
        assertEquals("EventoX", svc.encontrarEventoPorSigla("SI"));
    }

    @Test
    void archivarEdicion_ok_y_mapeaExcepcion() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        assertDoesNotThrow(() -> svc.archivarEdicion("Ed"));
        assertTrue(h.archivarCalled);

        h.throwInArchivar = true;
        WebServiceException ex = assertThrows(WebServiceException.class, () -> svc.archivarEdicion("Ed"));
        assertTrue(ex.getMessage().contains("No se pudo archivar la edición"));
        assertNotNull(ex.getCause());
    }

    @Test
    void altaEvento_parseaFechas() throws EventoYaExisteException {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        LocalDate hoy = LocalDate.now();
        svc.altaEvento("N","D",null,"S",null,"img");
        assertEquals(hoy, h.altaEventoFecha);

        h.altaEventoFecha = null;
        svc.altaEvento("N","D","2025-01-02","S",null,"img");
        assertEquals(LocalDate.of(2025,1,2), h.altaEventoFecha);
    }

    @Test
    void altaRegistroEdicionEventoDT_parseoNullHaceInicioIgualRegistro() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);

        svc.altaRegistroEdicionEventoDT("ID","nick","Ev","Ed","TR", "2025-07-01", 100f, null);
        assertEquals(LocalDate.of(2025,7,1), h.regDT_fr);
        assertEquals(LocalDate.of(2025,7,1), h.regDT_fi);
    }

    @Test
    void getEndpoint_sinPublicar_esNull() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);
        assertNull(svc.getEndpoint());
    }
}