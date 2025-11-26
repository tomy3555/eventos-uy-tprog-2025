package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import publicadores.PublicadorEventoService;
import publicadores.PublicadorEvento;
import publicadores.DtEvento;
import publicadores.DtEventoArray;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@WebServlet("/buscar")
public class BuscarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String q = req.getParameter("q");
        PublicadorEventoService svc = new PublicadorEventoService();
        PublicadorEvento port = svc.getPublicadorEventoPort();
        List<DtEvento> eventos = new ArrayList<>();
        List<publicadores.DtEdicion> ediciones = new ArrayList<>();
        try {
            DtEventoArray eventoArr = port.listarEventos();
            if (eventoArr != null && eventoArr.getItem() != null) {
                for (DtEvento ev : eventoArr.getItem()) {
                    boolean agregarEvento = false;
                    boolean buscarEdiciones = false;
                    if (q == null || q.trim().isEmpty()) {
                        agregarEvento = true;
                        buscarEdiciones = true;
                    } else {
                        String query = q.trim().toLowerCase();
                        if (ev.getNombre().toLowerCase().contains(query)
                            || (ev.getDescripcion() != null && ev.getDescripcion().toLowerCase().contains(query))) {
                            agregarEvento = true;
                        }
                        buscarEdiciones = true;
                    }
                    if (agregarEvento) {
                        eventos.add(ev);
                    }
                    if (buscarEdiciones) {
                        String nombreEvento = ev.getNombre();
                        publicadores.StringArray clavesArrObj = port.listarEdicionesEvento(nombreEvento);
                        List<String> claves = (clavesArrObj == null || clavesArrObj.getItem() == null) ? List.of() : clavesArrObj.getItem();
                        for (String clave : claves) {
                            if (q == null || q.trim().isEmpty() || (clave != null && clave.toLowerCase().contains(q.trim().toLowerCase()))) {
                                try {
                                    publicadores.DtEdicion ed = port.obtenerDtEdicion(nombreEvento, clave);
                                    if (ed != null) ediciones.add(ed);
                                } catch (Exception ex) {
                                }
                            }
                        }
                    }
                }
                String orden = req.getParameter("orden") != null ? req.getParameter("orden") : "fecha";
                // Ordenar eventos y ediciones según el parámetro 'orden'
                switch (orden) {
                    case "alfabetico_asc":
                        eventos.sort(Comparator.comparing(DtEvento::getNombre, String.CASE_INSENSITIVE_ORDER));
                        ediciones.sort(Comparator.comparing(publicadores.DtEdicion::getNombre, String.CASE_INSENSITIVE_ORDER));
                        break;
                    case "alfabetico_desc":
                        eventos.sort(Comparator.comparing(DtEvento::getNombre, String.CASE_INSENSITIVE_ORDER).reversed());
                        ediciones.sort(Comparator.comparing(publicadores.DtEdicion::getNombre, String.CASE_INSENSITIVE_ORDER).reversed());
                        break;
                    default:
                        eventos.sort(Comparator.comparing(DtEvento::getFecha).reversed());
                        ediciones.sort(Comparator.comparing(publicadores.DtEdicion::getFechaAlta).reversed());
                        break;
                }
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error al buscar: " + e.getMessage());
        }
        req.setAttribute("resultEventos", eventos);
        req.setAttribute("resultEdiciones", ediciones);
        req.setAttribute("query", q);
        req.getRequestDispatcher("/WEB-INF/templates/resultadosBusqueda.jsp").forward(req, resp);
    }
}