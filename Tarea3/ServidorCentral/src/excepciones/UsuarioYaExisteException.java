package excepciones;



@SuppressWarnings("serial")
public class UsuarioYaExisteException extends Exception{

	public UsuarioYaExisteException(String string) {
        super(string);
    }
}


