package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;

@WebServlet("/registro/*")
public class AltaRegistroServlet extends HttpServlet {

    private static final String JSP_ALTA = "/WEB-INF/registro/AltaRegistro.jsp";
    private static final String JSP_OK   = "/WEB-INF/registro/AltaRegistroOK.jsp";

    private IControladorEvento ce() { return fabrica.getInstance().getIControladorEvento(); }
    private String ctx(HttpServletRequest req) { return req.getContextPath(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        System.out.println("Entra al doGet de AltaRegistroServlet con path: " + path);
        if (path == null || "/".equals(path) || "/alta".equals(path)) {
            if (!requiereOrganizador(req, resp)) return;

            // Cargar ediciones del organizador 
            recargarDatosDT(req);

            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        System.out.println("Entra al doPost de AltaRegistroServlet con path: " + path);
        String accion = req.getParameter("accion");
        if ("cancelar".equalsIgnoreCase(accion)) {
            resp.sendRedirect(ctx(req) + "/inicio");
            return;
        }

        if ("/alta".equals(path)) {
            if (!requiereOrganizador(req, resp)) return;

            String siglaEdicion = trim(req.getParameter("edicion"));   // SIGLA de la edición
            String nombre       = trim(req.getParameter("nombre"));    // nombre del TipoRegistro
            String descripcion  = trim(req.getParameter("descripcion"));
            String costoStr     = trim(req.getParameter("costo"));
            String cupoStr      = trim(req.getParameter("cupo"));

            if (isBlank(siglaEdicion) || isBlank(nombre) || isBlank(descripcion)
                    || isBlank(costoStr) || isBlank(cupoStr)) {
                req.setAttribute("error", "Todos los campos son obligatorios.");
                recargarDatosDT(req);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }

            try {
                float costo = Float.parseFloat(costoStr);
                int cupo    = Integer.parseInt(cupoStr);

                DTEdicion dtSel = ce().obtenerEdicionPorSiglaDT(siglaEdicion);
                
                if (dtSel == null) {
                    req.setAttribute("error", "No se encontró la edición seleccionada.");
                    recargarDatosDT(req);
                    req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                    return;
                }
                
                if (dtSel.getFechaFin().isBefore(LocalDate.now())) {
					req.setAttribute("error", "No se pueden agregar tipos de registro a una edición finalizada.");
					recargarDatosDT(req);
					
					req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
					return;
				}
                

                //  Alta
                
                ce().altaTipoRegistroDTO(dtSel, nombre, descripcion, costo, cupo);

                // Redirigir a la consulta del TipoRegistro 
                String eventoNombre = null;
                try { eventoNombre = ce().encontrarEventoPorSigla(siglaEdicion); } catch (Exception ignore) {}
                if (isBlank(eventoNombre)) {
                    try { eventoNombre = dtSel.getEvento().getNombre(); } catch (Exception ignore) {}
                }

                String eventoEnc  = URLEncoder.encode(eventoNombre != null ? eventoNombre : "", StandardCharsets.UTF_8.name());
                String edicionEnc = URLEncoder.encode(dtSel.getNombre(), StandardCharsets.UTF_8.name());
                String tipoEnc    = URLEncoder.encode(nombre, StandardCharsets.UTF_8.name());

                resp.sendRedirect(ctx(req) + "/registro/ConsultaTipoRegistro?evento=" + eventoEnc
                        + "&edicion=" + edicionEnc
                        + "&tipoRegistro=" + tipoEnc);
                return;

            } catch (NumberFormatException nfe) {
                req.setAttribute("error", "Costo y cupo deben ser numéricos.");
                recargarDatosDT(req);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;

            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
                recargarDatosDT(req);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // Helpers  

    private void recargarDatosDT(HttpServletRequest req) {
        HttpSession sAux = req.getSession(false);
        String nick = sAux == null ? null : (String) sAux.getAttribute("nick");

        List<DTEdicion> ediciones = new ArrayList<>();

        if (nick != null) {
            // Recorremos todos los eventos 
//            List<DTEvento> eventos = ce().listarEventos();
        	List<DTEvento> eventos = ce().listarEventosVigentes();
            for (DTEvento ev : eventos) {
                String nombreEvento = ev.getNombre();
                List<String> nombresEd = ce().listarEdicionesEvento(nombreEvento);

                for (String nomEd : nombresEd) {
                    DTEdicion dt = ce().obtenerDtEdicion(nombreEvento, nomEd);
                    if (dt != null) {
                        // Filtrar por organizador 
                        String org = null;
                        try { org = dt.getOrganizador(); } catch (Exception ignore) {}
                        if (org == null || org.equals(nick)) {
                            ediciones.add(dt);
                        }
                    }
                }
            }
        }

        req.setAttribute("ediciones", ediciones);
    }

    
    private boolean requiereOrganizador(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sAux = req.getSession(false);
        String rol = sAux == null ? null : (String) sAux.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            resp.sendRedirect(ctx(req) + "/auth/login");
            return false;
        }
        return true;
    }

    private static String trim(String sAux) { return sAux == null ? null : sAux.trim(); }
    private static boolean isBlank(String sAux) { return sAux == null || sAux.trim().isEmpty(); }
}
