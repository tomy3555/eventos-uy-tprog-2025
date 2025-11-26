package publicadores;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.Endpoint;

import logica.datatypes.DTTopEvento;
import util.ConfigLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class PublicadorEstadisticas {

    private Endpoint endpoint = null;

    @WebMethod(exclude = true)
    public void publicar() {
        String ip = ConfigLoader.get("ipServidor");
        String puerto = ConfigLoader.get("puerto");
        String address = "http://" + ip + ":" + puerto + "/publicadorEstadisticas";

        endpoint = Endpoint.publish(address, this);
        System.out.println("Servicio PublicadorEstadisticas publicado en: " + address);
        System.out.println("WSDL disponible en: " + address + "?wsdl");
    }


    /* =============================
       LÓGICA DEL SERVICIO
       ============================= */

    // Contador en memoria por nombre de evento
    private static final Map<String, Integer> VISITAS = new ConcurrentHashMap<>();

    @WebMethod
    public void registrarVisita(@WebParam(name = "eventoNombre") String eventoNombre) {
        if (eventoNombre == null || eventoNombre.isBlank()) return;
        String key = eventoNombre.trim();
        int nuevo = VISITAS.merge(key, 1, Integer::sum);;
    }

    @WebMethod
    public DTTopEvento[] topEventos(@WebParam(name = "n") int n) {
        if (n <= 0 || VISITAS.isEmpty()) return new DTTopEvento[0];

        List<DTTopEvento> lista = new ArrayList<>(VISITAS.size());
        for (Map.Entry<String, Integer> e : VISITAS.entrySet()) {
            int count = (e.getValue() == null ? 0 : e.getValue());
            lista.add(new DTTopEvento(e.getKey(), count));
        }

        lista.sort(Comparator.comparingInt(DTTopEvento::getVisitas).reversed());
        if (lista.size() > n) lista = lista.subList(0, n);

        return lista.toArray(new DTTopEvento[0]);
    }
    
    @WebMethod
    public void setVisitasEvento(@WebParam(name = "eventoNombre") String eventoNombre,
                                 @WebParam(name = "cantidad") int cantidad) {
        if (eventoNombre == null || eventoNombre.isBlank() || cantidad < 0) return;
        VISITAS.put(eventoNombre, cantidad);
    }

    @WebMethod
    public void resetVisitas() {
        VISITAS.clear();
    }
    
    @WebMethod(exclude = true)
    public void seedVisitasLocal(String eventoNombre, int cantidad) {
        if (eventoNombre == null || eventoNombre.isBlank() || cantidad < 0) return;
        VISITAS.put(eventoNombre, cantidad);
    }

    @WebMethod(exclude = true)
    public Endpoint getEndpoint() {
        return endpoint;
    }
}