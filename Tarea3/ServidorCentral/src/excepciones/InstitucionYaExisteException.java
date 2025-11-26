package excepciones;



@SuppressWarnings("serial")
public class InstitucionYaExisteException extends Exception{

	public InstitucionYaExisteException(String nombre) {
        super("Ya existe una institución con el nombre '" + nombre + "'.");
    }
}