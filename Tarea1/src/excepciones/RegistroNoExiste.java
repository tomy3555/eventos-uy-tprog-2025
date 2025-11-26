package excepciones;

public class RegistroNoExiste extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RegistroNoExiste(String idRegistro) {
        super("No existe un registro con ID: " + idRegistro);
    }
}