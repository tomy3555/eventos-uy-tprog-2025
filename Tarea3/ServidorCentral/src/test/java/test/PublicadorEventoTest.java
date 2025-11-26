package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.ws.WebServiceException;
import logica.interfaces.IControladorEvento;
import publicadores.PublicadorEvento;
import excepciones.EventoYaExisteException;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;

public class PublicadorEventoTest {

    static class IceHandler implements InvocationHandler {
        LocalDate altaEventoFecha;
        LocalDate regFechaRegistro;
        LocalDate regFechaInicio;
        List<String> listarEdicionesEventoReturn = Arrays.asList("E1","E2");
        List<String> listarEdicionesArchivablesReturn = Arrays.asList("A1","A2");
        boolean throwInListarArchivables = false;
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String m = method.getName();
            if ("altaEvento".equals(m)) {
                altaEventoFecha = (LocalDate) args[2];
                return null;
            }
            if ("listarEdicionesEvento".equals(m)) {
                return listarEdicionesEventoReturn;
            }
            if ("listarEventos".equals(m)) {
                return Collections.emptyList();
            }
            if ("altaRegistroEdicionEventoDT".equals(m)) {
                regFechaRegistro = (LocalDate) args[5];
                regFechaInicio   = (LocalDate) args[7];
                return null;
            }
            if ("listarEdicionesArchivables".equals(m)) {
                if (throwInListarArchivables) throw new RuntimeException("boom");
                return listarEdicionesArchivablesReturn;
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
void altaEvento_parsingFecha_nullUsaHoy_yConStringParsea() throws EventoYaExisteException {
    IceHandler h = new IceHandler();
    PublicadorEvento svc = newSvc(h);

    LocalDate hoy = LocalDate.now();
    svc.altaEvento("N","D",null,"S",null,"img.png");
    assertEquals(hoy, h.altaEventoFecha);

    h.altaEventoFecha = null;
    svc.altaEvento("N","D","2025-01-02","S",null,"img.png");
    assertEquals(LocalDate.of(2025,1,2), h.altaEventoFecha);
}

    @Test
    void listarEdicionesEvento_convierteAArray() {
        IceHandler h = new IceHandler();
        h.listarEdicionesEventoReturn = Arrays.asList("A","B","C");
        PublicadorEvento svc = newSvc(h);
        String[] arr = svc.listarEdicionesEvento("EventoX");
        assertArrayEquals(new String[]{"A","B","C"}, arr);
    }

    @Test
    void listarEventos_listaVacia_daArrayVacio() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);
        assertEquals(0, svc.listarEventos().length);
    }

    @Test
    void altaRegistroEdicionEventoDT_fechasNullHaceInicioIgualARegistro() {
        IceHandler h = new IceHandler();
        PublicadorEvento svc = newSvc(h);
        svc.altaRegistroEdicionEventoDT(
                "ID","nick","Ev","Ed","TR",
                "2025-07-01", 100f, null
        );
        assertEquals(LocalDate.of(2025,7,1), h.regFechaRegistro);
        assertEquals(LocalDate.of(2025,7,1), h.regFechaInicio);
    }

    @Test
    void listarEdicionesArchivables_ok_y_manejaExcepcion() {
        IceHandler h = new IceHandler();
        h.listarEdicionesArchivablesReturn = Arrays.asList("ed1","ed2");
        PublicadorEvento svc = newSvc(h);
        assertArrayEquals(new String[]{"ed1","ed2"}, svc.listarEdicionesArchivables("org"));

        h.throwInListarArchivables = true;
        WebServiceException ex = assertThrows(WebServiceException.class, () -> svc.listarEdicionesArchivables("org"));
        assertTrue(ex.getMessage().contains("No se pudo listar ediciones archivables"));
        assertNotNull(ex.getCause());
    }
}