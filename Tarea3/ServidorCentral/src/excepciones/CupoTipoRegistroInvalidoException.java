package excepciones;

public class CupoTipoRegistroInvalidoException extends RuntimeException {
    public CupoTipoRegistroInvalidoException(int cupo) {
        super("No quedan cupos disponibles para este tipo de registro (cupo: " + cupo + ").");
    }
}