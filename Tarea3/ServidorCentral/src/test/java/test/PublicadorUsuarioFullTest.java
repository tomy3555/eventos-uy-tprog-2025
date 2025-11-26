package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import publicadores.PublicadorUsuario;
import logica.interfaces.IControladorUsuario;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;

import excepciones.UsuarioNoExisteException;
import excepciones.UsuarioTipoIncorrectoException;
import excepciones.UsuarioYaExisteException;
import excepciones.InstitucionYaExisteException;
import excepciones.CategoriaYaExisteException;

/**
 * Cobertura completa de PublicadorUsuario sin usar frameworks de mocking.
 * Inyecta un proxy dinámico de IControladorUsuario y valida:
 * - Conversión de XMLGregorianCalendar -> LocalDate (null y no-null)
 * - Passthrough de métodos y mapeo a arrays/sets
 * - Ramas booleanas de validarLogin, esAsistente, sigueA
 * - Excepciones declaradas (assertDoesNotThrow / assertThrows)
 */
public class PublicadorUsuarioFullTest {

    /* ---------- Proxy handler ---------- */
    static class IcuHandler implements InvocationHandler {
        // capturas para asserts
        LocalDate altaUsuarioFecha;
        LocalDate modificarFecha;

        String seguirSeguidor, seguirSeguido;
        String dejarSeguidor, dejarSeguido;

        boolean validarLoginRet = true;
        boolean esAsistenteRet = false;
        boolean sigueARet = true;

        Set<String> instituciones = new LinkedHashSet<>(Arrays.asList("FING","ORT"));

        boolean marcarAsistenciaCalled;
        boolean modificarContraseniaCalled;

        boolean throwMarcarAsistencia = false;

        Set<logica.datatypes.DTDatosUsuario> obtenerUsuariosDTRet = new LinkedHashSet<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String m = method.getName();

            switch (m) {
                case "altaUsuario":
                    altaUsuarioFecha = (LocalDate) args[6];
                    return null;
                case "obtenerDatosUsuario":
                    return null;
                case "validarLogin":
                    return validarLoginRet;
                case "altaInstitucion":
                case "altaCategoriaSinGUI":
                    return null;
                case "modificarDatosUsuario":
                    modificarFecha = (LocalDate) args[5];
                    return null;
                case "obtenerUsuariosDT":
                    return obtenerUsuariosDTRet;
                case "seguirUsuario":
                    seguirSeguidor = (String) args[0];
                    seguirSeguido  = (String) args[1];
                    return null;
                case "dejarSeguirUsuario":
                    dejarSeguidor = (String) args[0];
                    dejarSeguido  = (String) args[1];
                    return null;
                case "getInstituciones":
                    return instituciones;
                case "esAsistente":
                    return esAsistenteRet;
                case "sigueA":
                    return sigueARet;
                case "obtenerDatosRegistros":
                    return null;
                case "marcarAsistencia":
                    if (throwMarcarAsistencia) throw new UsuarioNoExisteException("x");
                    marcarAsistenciaCalled = true;
                    return null;
                case "modificarContrasenia":
                    modificarContraseniaCalled = true;
                    return null;
            }

            Class<?> rt = method.getReturnType();
            if (rt.equals(void.class)) return null;
            if (Set.class.isAssignableFrom(rt)) return Collections.emptySet();
            return null;
        }
    }

    /* ---------- Utilidades de inyección ---------- */
    static Object proxyFor(IcuHandler h) {
        return Proxy.newProxyInstance(
                IControladorUsuario.class.getClassLoader(),
                new Class<?>[]{IControladorUsuario.class},
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

    static PublicadorUsuario newSvc(IcuHandler h) {
        try {
            PublicadorUsuario svc = (PublicadorUsuario) unsafe().allocateInstance(PublicadorUsuario.class);
            Field f = PublicadorUsuario.class.getDeclaredField("icu");
            f.setAccessible(true);
            f.set(svc, proxyFor(h));
            return svc;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    static XMLGregorianCalendar xgc(int y, int m, int d) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                    y, m, d, DatatypeConstants.FIELD_UNDEFINED
            );
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /* ---------- Tests ---------- */

    @Test
    void altaUsuario_convierteFechaNullYNoNull() {
    	IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        // Organizador: fechaNacimiento = null  -> debe llegar null al handler
        assertDoesNotThrow(() ->
                svc.altaUsuario(
                        "nick", "Nom", "mail@x",
                        "desc", "link",
                        "ape",
                        /* fechaNacimiento */ null,   // <--- ahora String
                        "Inst",
                        true,                         // esOrganizador
                        "123",
                        "img.png")
        );
        assertNull(h.altaUsuarioFecha);

        // Asistente: fechaNacimiento = "2004-09-11" -> debe parsearse a LocalDate(2004,9,11)
        assertDoesNotThrow(() ->
                svc.altaUsuario(
                        "nick", "Nom", "mail@x",
                        "desc", "link",
                        "ape",
                        /* fechaNacimiento */ "2004-09-11", // <--- ahora String
                        "Inst",
                        false,                        // asistente
                        "123",
                        "img.png")
        );
        assertEquals(LocalDate.of(2004, 9, 11), h.altaUsuarioFecha);
    }

    @Test
    void modificarDatosUsuario_convierteFechaNullYNoNull() {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        // fechaNacimiento = null -> handler recibe null
        assertDoesNotThrow(() ->
                svc.modificarDatosUsuario(
                        "nick",
                        "Nom",
                        "desc",
                        "link",
                        "ape",
                        /* fechaNacimiento */ null,   // <--- ahora String
                        "Inst",
                        "img")
        );
        assertNull(h.modificarFecha);

        // fechaNacimiento = "1999-01-02" -> handler recibe LocalDate(1999,1,2)
        assertDoesNotThrow(() ->
                svc.modificarDatosUsuario(
                        "nick",
                        "Nom",
                        "desc",
                        "link",
                        "ape",
                        /* fechaNacimiento */ "1999-01-02", // <--- ahora String
                        "Inst",
                        "img")
        );
        assertEquals(LocalDate.of(1999, 1, 2), h.modificarFecha);
    }

    @Test
    void validarLogin_trueFalse_y_obtenerDatosUsuario_pass() throws UsuarioNoExisteException {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        h.validarLoginRet = true;
        assertTrue(svc.validarLogin("n","p"));
        h.validarLoginRet = false;
        assertFalse(svc.validarLogin("n","p"));

        assertNull(svc.obtenerDatosUsuario("nick"));
    }

    @Test
    void altasVarias_y_listadosAArray() {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        assertDoesNotThrow(() -> svc.altaInstitucion("FING","Ing","https://fing"));
        assertDoesNotThrow(() -> svc.altaCategoriaSinGUI("Tech"));

        String[] arr1 = svc.listarInstituciones();
        String[] arr2 = svc.getInstituciones();
        assertArrayEquals(new String[]{"FING","ORT"}, arr1);
        assertArrayEquals(new String[]{"FING","ORT"}, arr2);

        assertDoesNotThrow(() -> svc.seguirUsuario("a","b"));
        assertEquals("a", h.seguirSeguidor);
        assertEquals("b", h.seguirSeguido);

        assertDoesNotThrow(() -> svc.dejarSeguirUsuario("c","d"));
        assertEquals("c", h.dejarSeguidor);
        assertEquals("d", h.dejarSeguido);
    }

    @Test
    void booleans_esAsistente_y_sigueA() {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        h.esAsistenteRet = false;
        assertFalse(svc.esAsistente("n"));
        h.esAsistenteRet = true;
        assertTrue(svc.esAsistente("n"));

        h.sigueARet = true;
        assertTrue(svc.sigueA("a","b"));
        h.sigueARet = false;
        assertFalse(svc.sigueA("a","b"));
    }

    @Test
    void obtenerDatosRegistros_null_ok() {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);
        assertNull(svc.obtenerDatosRegistros("id"));
    }



    @Test
    void obtenerUsuariosDT_aArray() throws UsuarioNoExisteException {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        assertEquals(0, svc.obtenerUsuariosDT().length);
    }

    @Test
    void modificarContrasenia_passthrough() throws UsuarioNoExisteException {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);

        assertDoesNotThrow(() -> svc.modificarContrasenia("nick","newpass"));
        assertTrue(h.modificarContraseniaCalled);
    }

    @Test
    void getEndpoint_sinPublicar_esNull() {
        IcuHandler h = new IcuHandler();
        PublicadorUsuario svc = newSvc(h);
        assertNull(svc.getEndpoint());
    }
}