package excepciones;

public class UsuarioNoEsAsistente extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UsuarioNoEsAsistente(String nickname) {
        super("El usuario con nickname '" + nickname + "' no es un Asistente.");
    }
}