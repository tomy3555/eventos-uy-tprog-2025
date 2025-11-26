package test;

import logica.utils.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

public class UtilsNuevoTest {

    @Test
    void testLocalDateAdapterMarshalUnmarshal() throws Exception {
        LocalDateAdapter ad = new LocalDateAdapter();

        LocalDate d = LocalDate.of(2025, 11, 10);
        String s = ad.marshal(d);
        assertEquals("2025-11-10", s);

        LocalDate parsed = ad.unmarshal("2025-11-10");
        assertEquals(d, parsed);

        assertNull(ad.marshal(null));
        assertNull(ad.unmarshal(null));
        assertNull(ad.unmarshal(""));
    }

    @Test
    void testEntityManagerUtilSingletonAndShutdown() {
        var emf1 = EntityManagerUtil.getEMF();
        var emf2 = EntityManagerUtil.getEMF();
        assertSame(emf1, emf2);

        var em = EntityManagerUtil.em();
        assertNotNull(em);
        em.close();

        EntityManagerUtil.shutdown();
    }

    @Test
    void testEntityManagerTxAndVoid() {
        AtomicBoolean called = new AtomicBoolean(false);
        // No transacción real, pero debe ejecutar sin excepción
        EntityManagerUtil.txVoid(em -> called.set(true));
        assertTrue(called.get());
    }

    @Test
    void testConfigSCDefaults() {
        assertNotNull(ConfigSC.host());
        assertTrue(ConfigSC.port() > 0);
        assertTrue(ConfigSC.epUsuario().contains("publicadorUsuario"));
        assertTrue(ConfigSC.epEvento().contains("publicadorEvento"));
        assertTrue(ConfigSC.epEstad().contains("publicadorEstadisticas") 
                || ConfigSC.epEstad().contains("publicadorEstad"));
    }
}
