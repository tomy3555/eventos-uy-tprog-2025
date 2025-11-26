package excepciones;

public class PatrocinioYaExisteException extends RuntimeException {
    public PatrocinioYaExisteException(String institucion, String edicion) {
        super("Ya existe un patrocinio de la institución '" + institucion + "' para la edición '" + edicion + "'.");
    }
}
