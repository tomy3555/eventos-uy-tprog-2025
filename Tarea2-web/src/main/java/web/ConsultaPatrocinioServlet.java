package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;
import logica.datatypes.DTPatrocinio;
import logica.datatypes.DTEvento;
import logica.datatypes.DTEdicion;
import logica.datatypes.DTTipoRegistro;
import logica.enumerados.DTNivel;

@WebServlet({
    "/edicion/ConsultaPatrocinio",   // legacy (consulta)
    "/edicion/patrocinio/*"          // /consulta y /alta
})
public class ConsultaPatrocinioServlet extends HttpServlet {

    private IControladorEvento ctl() {
        return fabrica.getInstance().getIControladorEvento();
    }
    private IControladorUsuario ctlUs() {
        return fabrica.getInstance().getIControladorUsuario();
    }

    // --- Helper: requiere rol organizador ---
    private boolean requiereOrganizador(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        String rol = (s == null) ? null : (String) s.getAttribute("rol");
        boolean ok = rol != null && (rol.equalsIgnoreCase("organizador")
                                  || rol.equalsIgnoreCase("ORGANIZADOR")
                                  || rol.equalsIgnoreCase("ORG"));
        if (!ok) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Se requiere rol Organizador para realizar esta acción.");
        }
        return ok;
    }

    private String encode(String s) {
        try { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();
        String pathInfo    = req.getPathInfo();

        boolean esLegacyConsulta = "/edicion/ConsultaPatrocinio".equals(servletPath);
        String path = esLegacyConsulta ? "/consulta" : (pathInfo == null ? "/consulta" : pathInfo);

        switch (path) {
            case "/consulta": {
                String evento = req.getParameter("evento");
                String edicion = req.getParameter("edicion");
                String codigoPatrocinio = req.getParameter("codigoPatrocinio");

                if (evento == null || edicion == null || codigoPatrocinio == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros");
                    return;
                }

                DTPatrocinio dto = ctl().obtenerDTPatrocinio(codigoPatrocinio);
                if (dto == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Patrocinio no encontrado");
                    return;
                }

                req.setAttribute("evento", evento);
                req.setAttribute("edicion", edicion);
                req.setAttribute("patrocinio", dto);
                req.getRequestDispatcher("/WEB-INF/patrocinio/ConsultaPatrocinio.jsp").forward(req, resp);
                return;
            }

            case "/alta": {
                if (!requiereOrganizador(req, resp)) return;

                String eventoSel  = req.getParameter("evento");   // puede venir null
                String edicionSel = req.getParameter("edicion");  // puede venir null

                // 1) Instituciones (Set<String>)
                Set<String> instituciones = ctlUs().getInstituciones();
                req.setAttribute("instituciones", instituciones);

                // 2) Filtrar eventos/ediciones por organizador usando SOLO DTOs
                HttpSession s = req.getSession(false);
                String nick = (s == null) ? null : (String) s.getAttribute("nick");

//                List<DTEvento> todos = ctl().listarEventos();
                List<DTEvento> todos = ctl().listarEventosVigentes();
                List<String> eventosOrganizador   = new ArrayList<>();
                List<String> edicionesOrganizador = new ArrayList<>();

                if (todos != null && nick != null) {
                    for (DTEvento ev : todos) {
                        String nombreEv = ev.getNombre();
                        List<String> eds = ctl().listarEdicionesEvento(nombreEv);
                        boolean agregaEvento = false;
                        if (eds != null) {
                            for (String ed : eds) {
                                DTEdicion edDTO = ctl().obtenerDtEdicion(nombreEv, ed);
                                if (edDTO != null) {
                                    // getOrganizador() devuelve el identificador del organizador (nick)
                                    String orgNick = edDTO.getOrganizador();
                                    if (orgNick != null && orgNick.equals(nick)) {
                                        agregaEvento = true;
                                        if (nombreEv.equals(eventoSel)) {
                                            edicionesOrganizador.add(ed);
                                        }
                                    }
                                }
                            }
                        }
                        if (agregaEvento) {
                            eventosOrganizador.add(nombreEv);
                        }
                    }
                }

                // Si la edición seleccionada no pertenece al evento elegido (o cambió el evento), la descarto
                if (edicionSel != null && !edicionSel.isEmpty()
                        && (edicionesOrganizador == null || !edicionesOrganizador.contains(edicionSel))) {
                    edicionSel = null;
                }

                // 3) Tipos de registro si ya hay selección válida
                List<DTTipoRegistro> tipos =
                    (eventoSel != null && !eventoSel.isEmpty() && edicionSel != null && !edicionSel.isEmpty())
                        ? ctl().listarTiposRegistroDeEdicion(eventoSel, edicionSel)
                        : java.util.Collections.emptyList();

                // 4) Atributos para JSP
                req.setAttribute("evento", eventoSel);
                req.setAttribute("edicion", edicionSel);
                req.setAttribute("eventosOrganizador", eventosOrganizador);
                req.setAttribute("edicionesOrganizador", edicionesOrganizador);
                req.setAttribute("tiposRegistro", tipos);

                req.getRequestDispatcher("/WEB-INF/patrocinio/AltaPatrocinio.jsp").forward(req, resp);
                return;
            }

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();
        String pathInfo    = req.getPathInfo();
        String path = ("/edicion/ConsultaPatrocinio".equals(servletPath)) ? "/consulta" : (pathInfo == null ? "" : pathInfo);

        if (!"/alta".equals(path)) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (!requiereOrganizador(req, resp)) return;

        String evento    = req.getParameter("evento");
        String edicion   = req.getParameter("edicion");
        String instit    = req.getParameter("institucion");
        String nivelStr  = req.getParameter("nivel");
        String tipo      = req.getParameter("tipoRegistro");
        String aporteStr = req.getParameter("aporte");
        String fechaStr  = req.getParameter("fechaPatrocinio");
        String cantStr   = req.getParameter("cantidadRegistros");
        String codigo    = req.getParameter("codigoPatrocinio");

        if (evento == null || edicion == null || instit == null || nivelStr == null ||
            tipo == null || aporteStr == null || fechaStr == null || cantStr == null || codigo == null) {
            req.setAttribute("error", "Faltan campos obligatorios.");
            recargarFormAlta(req, resp);
            return;
        }

        // Seguridad: validar que el usuario actual organiza esa edición (usando solo DTO)
        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");

        DTEdicion edDTO = ctl().obtenerDtEdicion(evento, edicion);
        if (edDTO == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Edición inválida.");
            return;
        }
        String orgNick = edDTO.getOrganizador(); // <- organizador como string (nick)
        if (nick == null || orgNick == null || !nick.equals(orgNick)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No es organizador de la edición seleccionada.");
            return;
        }

        // Para altaPatrocinioDT necesitamos la SIGLA de la edición (del DTO)
        String siglaEd = edDTO.getSigla();

        try {
            DTNivel nivel   = DTNivel.valueOf(nivelStr);
            int aporte      = Integer.parseInt(aporteStr);
            int cantidad    = Integer.parseInt(cantStr);
            LocalDate fecha = LocalDate.parse(fechaStr); // yyyy-MM-dd

            ctl().altaPatrocinioDT(siglaEd, instit, nivel, tipo, aporte, fecha, cantidad, codigo);

            String url = req.getContextPath() + "/edicion/patrocinio/consulta"
                    + "?evento=" + encode(evento)
                    + "&edicion=" + encode(edicion)
                    + "&codigoPatrocinio=" + encode(codigo);
            resp.sendRedirect(url);

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Valores inválidos: " + e.getMessage());
            recargarFormAlta(req, resp);
        } catch (excepciones.PatrocinioYaExisteException e) {
            req.setAttribute("error", "Ya existe un patrocinio para esa institución/edición o el código ya está en uso.");
            recargarFormAlta(req, resp);
        } catch (excepciones.ValorPatrocinioExcedidoException e) {
            req.setAttribute("error", "La cantidad de registros gratuitos excede el 20% del aporte.");
            recargarFormAlta(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error inesperado: " + e.getClass().getSimpleName());
            recargarFormAlta(req, resp);
        }
    }

    private void recargarFormAlta(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String eventoSel  = req.getParameter("evento");
        String edicionSel = req.getParameter("edicion");

        // Instituciones
        Set<String> instituciones = ctlUs().getInstituciones();
        req.setAttribute("instituciones", instituciones);

        // Reconstruir eventos/ediciones del organizador SOLO con DTOs
        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");

        List<DTEvento> todos = ctl().listarEventos();
        List<String> eventosOrganizador   = new ArrayList<>();
        List<String> edicionesOrganizador = new ArrayList<>();

        if (todos != null && nick != null) {
            for (DTEvento ev : todos) {
                String nombreEv = ev.getNombre();
                List<String> eds = ctl().listarEdicionesEvento(nombreEv);
                boolean agregaEvento = false;
                if (eds != null) {
                    for (String ed : eds) {
                        DTEdicion edDTO = ctl().obtenerDtEdicion(nombreEv, ed);
                        if (edDTO != null) {
                            String orgNick = edDTO.getOrganizador();
                            if (orgNick != null && orgNick.equals(nick)) {
                                agregaEvento = true;
                                if (nombreEv.equals(eventoSel)) {
                                    edicionesOrganizador.add(ed);
                                }
                            }
                        }
                    }
                }
                if (agregaEvento) {
                    eventosOrganizador.add(nombreEv);
                }
            }
        }

        // Invalidar edición si no pertenece al evento seleccionado
        if (edicionSel != null && !edicionSel.isEmpty()
                && (edicionesOrganizador == null || !edicionesOrganizador.contains(edicionSel))) {
            edicionSel = null;
        }

        List<DTTipoRegistro> tipos =
            (eventoSel != null && !eventoSel.isEmpty() && edicionSel != null && !edicionSel.isEmpty())
                ? ctl().listarTiposRegistroDeEdicion(eventoSel, edicionSel)
                : java.util.Collections.emptyList();

        req.setAttribute("evento", eventoSel);
        req.setAttribute("edicion", edicionSel);
        req.setAttribute("eventosOrganizador", eventosOrganizador);
        req.setAttribute("edicionesOrganizador", edicionesOrganizador);
        req.setAttribute("tiposRegistro", tipos);

        req.getRequestDispatcher("/WEB-INF/patrocinio/AltaPatrocinio.jsp").forward(req, resp);
    }
}
