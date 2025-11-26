package test;

import logica.controladores.ControladorEvento;
import logica.clases.*;
import logica.datatypes.*;
import logica.enumerados.*;
import logica.manejadores.*;

import excepciones.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Test funcional sin Mockito para los métodos clave de ControladorEvento.
 */
public class ControladorEventoNuevoTest {

    private ControladorEvento ctrl;
    private ManejadorEvento mEvento;
    private ManejadorUsuario mUsuario;
    private ManejadorAuxiliar mAux;

    private Organizador org;
    private Asistente asist;
    private Eventos evento;
    private Ediciones edicion;
    private TipoRegistro tipo;

    @BeforeEach
    void setUp() {
        ctrl = new ControladorEvento();
        ManejadorUsuario.reset();
        ManejadorEvento.reset();
        ManejadorAuxiliar.reset();
        mEvento = ManejadorEvento.getInstancia();
        mUsuario = ManejadorUsuario.getInstancia();
        mAux = ManejadorAuxiliar.getInstancia();


        // crear organizador y asistente
        org = new Organizador("org1", "Org Uno", "org@eventos.uy", "123", null, "desc", "link");
        asist = new Asistente("asis1", "Asis Uno", "asis@eventos.uy", "abc", null, "Apellido", LocalDate.now().minusYears(20), null);
        mUsuario.addUsuario(org);
        mUsuario.addUsuario(asist);

        // categoría
        Categoria cat = new Categoria("Música");
        mAux.agregarCategoria("Música", cat);

        // evento
        Map<String, Categoria> cats = Map.of("Música", cat);
        evento = new Eventos("RockFest", "RF2025", "desc", LocalDate.now(), cats, "img.png");
        mEvento.agregarEvento(evento);

        // edición
        edicion = new Ediciones(evento, "Ed1", "E1", LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5), LocalDate.now().minusDays(3),
                org, "Montevideo", "Uruguay", "imgEd.png", "desc");
        evento.agregarEdicion(edicion);
        mEvento.agregarEdicion(edicion);

        // tipo registro
        tipo = new TipoRegistro(edicion, "General", "desc", 100, 5);
        edicion.agregarTipoRegistro("General", tipo);
        mEvento.agregarTipoRegistro(tipo);
    }


    @Test
    void testConsultaEdicionEvento() {
        edicion.setEstado(DTEstado.Aceptada);
        DTEdicion dto = ctrl.consultaEdicionEvento(evento.getNombre(), edicion.getNombre());
        assertNotNull(dto);
        assertEquals(edicion.getNombre(), dto.getNombre());
    }

    @Test
    void testExtraerNombresEdiciones() throws Exception {
        var map = new HashMap<String, Ediciones>();
        map.put(edicion.getNombre(), edicion);
        var method = ControladorEvento.class.getDeclaredMethod("extraerNombresEdiciones", Map.class);
        method.setAccessible(true);
        List<String> res = (List<String>) method.invoke(null, map);
        assertTrue(res.contains(edicion.getNombre()));
    }

    @Test
    void testEsYFinalizarEvento() {
        assertTrue(ctrl.esEventoVigente(evento.getNombre()));
        ctrl.finalizarEvento(evento.getNombre());
        assertFalse(ctrl.esEventoVigente(evento.getNombre()));
    }



    @Test
    void testListarEventosVigentes() {
        evento.setVigente(true);
        List<DTEvento> lista = ctrl.listarEventosVigentes();
        assertEquals(1, lista.size());
        assertEquals(evento.getNombre(), lista.get(0).getNombre());
    }

    @Test
    void testConsultaRegistroYMarcarAsistencia() {
        Registro reg = new Registro("R1", asist, edicion, tipo,
                LocalDate.now(), tipo.getCosto(), LocalDate.now(), evento);
        mEvento.agregarRegistro(reg);
        edicion.agregarRegistro("R1", reg);
        asist.addRegistro("R1", reg);

        DTRegistro dto = ctrl.consultaRegistro(asist, "R1");
        assertEquals("R1", dto.getId());

        ctrl.marcarAsistencia(asist.getNickname(), "R1");
        assertTrue(reg.getAsistencia());
    }

    @Test
    void testListarTiposRegistroDeEdicion() {
        var lista = ctrl.listarTiposRegistroDeEdicion(evento.getNombre(), edicion.getNombre());
        assertEquals(1, lista.size());
        assertEquals("General", lista.get(0).getNombre());
    }

    @Test
    void testAltaPatrocinioDT() throws Exception {
        Institucion inst = new Institucion("Uni", "desc", "url");
        mUsuario.addInstitucion(inst);

        DTPatrocinio dto = ctrl.altaPatrocinioDT(
                edicion.getSigla(),
                inst.getNombre(),
                DTNivel.PLATINO,
                tipo.getNombre(),
                10000,
                LocalDate.now(),
                10,
                "PAT1"
        );

        assertEquals("PAT1", dto.getCodigo());
    }

}