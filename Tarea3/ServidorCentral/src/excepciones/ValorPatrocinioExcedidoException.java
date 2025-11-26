package excepciones;



@SuppressWarnings("serial")
public class ValorPatrocinioExcedidoException extends Exception{

    public ValorPatrocinioExcedidoException() {
        super("El valor de los registros gratuitos supera el 20% del aporte económico del patrocinio.");
    }
    
	public ValorPatrocinioExcedidoException(String string) {
        super(string);
    }
}