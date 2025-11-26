package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import logica.clases.Organizador;
import logica.clases.TipoRegistro;
import logica.clases.Usuario;

public class DominioUnicoTest {

    // Subclase mínima para poder instanciar Usuario sin tocar producción
    static class DummyUsuario extends Usuario {
        public DummyUsuario(String nickname, String nombre, String email, String contrasena) {
            super(nickname, nombre, email, contrasena);
        }
        @Override public String getTipoUsuario() { return "Dummy"; }
    }

    @Test
    void dominio_basico_todo_en_uno_sin_mockito() {
        // ===== TipoRegistro =====
        // Constructor admite edicion null, así evitamos dependencias externas
        TipoRegistro tr = new TipoRegistro(null, "General", "Desc gen", 100.0f, 10);
        assertEquals("General", tr.getNombre());
        assertEquals("Desc gen", tr.getDescripcion());
        assertEquals(100.0f, tr.getCosto());
        assertEquals(10, tr.getCupo());
        assertNull(tr.getEdicion());

        // Setters
        tr.setNombre("Premium");
        tr.setDescripcion("Nueva desc");
        tr.setEdicion(null); // por diseño actual se permite null
        assertEquals("Premium", tr.getNombre());
        assertEquals("Nueva desc", tr.getDescripcion());
        assertNull(tr.getEdicion());

        // ===== Usuario (dummy) =====
        DummyUsuario u = new DummyUsuario("nico", "Nicolás", "n@e.com", "1234");
        assertEquals("nico", u.getNickname());
        assertEquals("Nicolás", u.getNombre());
        assertEquals("n@e.com", u.getEmail());
        assertEquals("1234", u.getContrasena());
        assertNull(u.getImagen());
        assertNull(u.getInstitucion());

        // Cambios básicos
        u.setNombre("Nico R");
        u.setContrasena("abcd");
        u.setImagen("img.png");
        assertEquals("Nico R", u.getNombre());
        assertEquals("abcd", u.getContrasena());
        assertEquals("img.png", u.getImagen());

        // Seguimiento: sets sin duplicados y sin nulos
        assertEquals(0, u.contarSeguidores());
        assertEquals(0, u.contarSeguidos());
        u.addSeguidor("a");
        u.addSeguidor("a");      // no duplica
        u.addSeguidor(null);     // ignora null
        u.addSeguido("b");
        u.addSeguido(null);
        assertEquals(1, u.contarSeguidores());
        assertEquals(1, u.contarSeguidos());
        assertTrue(u.getSeguidores().contains("a"));
        assertTrue(u.sigueA("b"));
        assertFalse(u.sigueA("x"));
        assertFalse(u.sigueA(null));

        // Remover
        u.removeSeguidor("a");
        u.removeSeguidor(null);
        assertEquals(0, u.contarSeguidores());
        u.removeSeguido("b");
        u.removeSeguido("noexiste");
        assertEquals(0, u.contarSeguidos());

        // esAsistente: solo cubrimos la rama false sin depender de Asistente real
        assertFalse(u.esAsistente(null));
        assertEquals("Dummy", u.getTipoUsuario());

        // ===== Organizador =====
        Organizador org = new Organizador(
                "org1", "Org", "o@e.com", "pass", "img.png", "mi desc", "https://link"
        );
        assertEquals("mi desc", org.getDesc());
        assertEquals("https://link", org.getLink());
        assertEquals("Organizador", org.getTipoUsuario());
        assertNotNull(org.getEdiciones());
        assertTrue(org.getEdiciones().isEmpty());

        org.setDesc("otra");
        org.setLink("http://otro");
        assertEquals("otra", org.getDesc());
        assertEquals("http://otro", org.getLink());

        // Listado vacío: debe devolver lista vacía sin excepciones
        List<logica.datatypes.DTEdicion> lista = org.listarEdicionesAPartirDeOrganizador();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());

        // NOTA: para probar agregarEdicion() y el listado con elementos sin Mockito,
        // necesitás construir una Ediciones real de tu proyecto (y su Evento asociado).
        // Si me pasás el constructor de Ediciones/Eventos, te agrego acá mismo ese caso.
        
        
    }
    
    
    
    
}