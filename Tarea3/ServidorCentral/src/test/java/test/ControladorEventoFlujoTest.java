package test;

import logica.controladores.ControladorEvento;
import logica.datatypes.DTCategorias;
import logica.manejadores.ManejadorAuxiliar;
import logica.manejadores.ManejadorEvento;
import logica.manejadores.ManejadorUsuario;
import excepciones.EventoYaExisteException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ControladorEventoFlujoTest {

    @Test
    void testNormalizarYListarCategorias() {
        // Simplemente ejercita los métodos estáticos de utilidad
        List<String> res = ControladorEvento.listarCategorias();
        assertNotNull(res);

        String norm = invokeNormalize(" Café ");
        assertEquals("cafe", norm);
    }

    private String invokeNormalize(String text) {
        try {
            var method = ControladorEvento.class.getDeclaredMethod("normalizar", String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFinalizarEventoNoLanzaExcepcion() {
        ControladorEvento ctrl = new ControladorEvento();
        assertDoesNotThrow(() -> ctrl.finalizarEvento("EventoInexistente"));
    }
}