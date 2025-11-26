package excepciones;



@SuppressWarnings("serial")
public class NombreEdicionEnUsoException extends Exception{

	public NombreEdicionEnUsoException(String string) {
        super(string);
    }
}
