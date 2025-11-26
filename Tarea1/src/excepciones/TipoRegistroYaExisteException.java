package excepciones;

public class TipoRegistroYaExisteException extends Exception {
    public TipoRegistroYaExisteException(String nombre) {
        super("Ya existe un tipo de registro con el nombre '" + nombre + "' para la edición seleccionada.");
    }
}
