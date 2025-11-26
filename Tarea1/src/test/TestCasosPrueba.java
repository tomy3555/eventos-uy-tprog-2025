package test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import excepciones.UsuarioYaExisteException;
import logica.fabrica;
import logica.CargaDatosPrueba;
import logica.interfaces.IControladorUsuario;
import logica.manejadores.ManejadorEvento;
import logica.manejadores.ManejadorAuxiliar;
import logica.manejadores.ManejadorUsuario;



public class TestCasosPrueba {

    static private fabrica fabrica = new fabrica();
    
    static private IControladorUsuario cUsuario = fabrica.getIControladorUsuario();
    static private ManejadorAuxiliar mAux = ManejadorAuxiliar.getInstancia();
    static private ManejadorUsuario mUsr = ManejadorUsuario.getInstancia();
    static private ManejadorEvento mEv = ManejadorEvento.getInstancia();

    @BeforeAll
    static void cargarDatosPrueba() throws Exception {
    	CargaDatosPrueba.cargarInstitucionesEjemplo();
    	CargaDatosPrueba.cargarCategorias();
    	CargaDatosPrueba.cargarEventosEjemplo();
    	CargaDatosPrueba.cargarUsuariosEjemplo();
    	CargaDatosPrueba.cargarEdicionesEjemplo();
    	CargaDatosPrueba.cargarTipoRegistroEjemplo();
    	CargaDatosPrueba.cargarPatrociniosEjemplo();
    	CargaDatosPrueba.cargarRegistrosEjemplo();
    	CargaDatosPrueba.logResumenDatos();
    }
     
    @Test
    void testCategoriaYaExiste() {
        assertTrue(mAux.existeCategoria("Tecnología"));
    }
    
    @Test
   	void testUsuarioYaExisteNickname() { 
          Assertions.assertThrows(
       		   UsuarioYaExisteException.class, () -> {cUsuario.altaUsuario(
       				   "atorres",
       				   "Ana",
       				   "atorres@gmail.com",
       				   null,
       				   null,
       				   "Torres",
       				   java.time.LocalDate.of(1990, 5, 12),
       				   "Facultad de Ingeniería",
       				   false,
       				   "contrasena123",
       				   "imagen.jpg"
       			  ); }
       		);
   	}
    
    @Test
   	void testUsuarioYaExisteEmail() { 
    	UsuarioYaExisteException existe = Assertions.assertThrows(
       		   UsuarioYaExisteException.class, () -> {cUsuario.altaUsuario(
       				   "paniTorres",
       				   "pani",
       				   "atorres@gmail.com",
       				   null,
       				   null,
       				   "Torres",
       				   java.time.LocalDate.of(1990, 5, 12),
       				   "Facultad de Ingeniería",
       				   false,
       				 "contrasena123",
     				   "imagen.jpg"
       			  ); }
       		);
          Assertions.assertEquals("El usuario con correo atorres@gmail.com ya esta registrado", existe.getMessage());
   	}
    
    @Test 
    void testInstitucionYaExistente(){
    	assertTrue(mUsr.existeInstitucion("ORT Uruguay"));
    }

    @Test
    void testEventoYaExiste() {
    	assertTrue(mEv.existeEvento("Montevideo Comics"));
    }
    
    @Test
    void testEventoNoExiste() {
    	assertFalse(mEv.existeEvento("eventoPrueba"));
    }
    
    @Test
    void testConsultaUsuarioNoExistente() {
    	
    }

}
