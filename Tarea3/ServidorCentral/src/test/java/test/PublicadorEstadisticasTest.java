package test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import publicadores.PublicadorEstadisticas;
import logica.datatypes.DTTopEvento;

class PublicadorEstadisticasTest {

    private PublicadorEstadisticas pub;

    @BeforeEach
    void setUp() {
        pub = new PublicadorEstadisticas();
        pub.resetVisitas();
    }

    @Test
    void testRegistrarYTopEventos() {
        pub.registrarVisita("RockFest");
        pub.registrarVisita("RockFest");
        pub.registrarVisita("JazzFest");

        DTTopEvento[] top = pub.topEventos(2);
        assertEquals(2, top.length);
        assertEquals("RockFest", top[0].getNombreEvento());
        assertTrue(top[0].getVisitas() >= top[1].getVisitas());
    }

    @Test
    void testSetYResetVisitas() {
        pub.setVisitasEvento("TechExpo", 5);
        DTTopEvento[] top = pub.topEventos(1);
        assertEquals("TechExpo", top[0].getNombreEvento());
        assertEquals(5, top[0].getVisitas());

        pub.resetVisitas();
        assertEquals(0, pub.topEventos(5).length);
    }

    @Test
    void testSeedVisitasLocalYFiltradoInvalido() {
        pub.seedVisitasLocal("GamingDay", 3);
        pub.registrarVisita("  "); // ignorado
        DTTopEvento[] top = pub.topEventos(1);
        assertEquals("GamingDay", top[0].getNombreEvento());
        assertEquals(3, top[0].getVisitas());
    }


}
