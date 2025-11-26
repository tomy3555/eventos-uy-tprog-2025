package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import excepciones.UsuarioNoExisteException;
import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;
import logica.datatypes.*;

@WebServlet("/registro/inscripcion")
public class RegistroEdicionEventoServlet extends HttpServlet {

    private static final String JSP_INSCRIPCION = "/WEB-INF/registro/RegistroEdicionEvento.jsp";

    private IControladorEvento ce() { return fabrica.getInstance().getIControladorEvento(); }
    private IControladorUsuario cu() { return fabrica.getInstance().getIControladorUsuario(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requiereAsistente(req, resp)) return;

//        List<DTEvento> eventos = ce().listarEventos();
        List<DTEvento> eventos = ce().listarEventosVigentes();
        req.setAttribute("eventos", eventos);

        // ediciones ACEPTADAS y NO finalizadas
        Map<String, List<DTEdicion>> edicionesPorEvento = new LinkedHashMap<>();
        for (DTEvento ev : eventos) {
            List<String> claves = ce().listarEdicionesEvento(ev.getNombre()); // nombres o siglas
            if (claves == null) continue;

            List<DTEdicion> visibles = new ArrayList<>();
            for (String clave : claves) {
                DTEdicion ed = null;

                try { ed = ce().consultaEdicionEvento(ev.getSigla(), clave); } catch (Exception ignore) {}

                if (ed == null) {
                    try { ed = ce().obtenerDtEdicion(ev.getNombre(), clave); } catch (Exception ignore) {}
                }

                if (ed != null && esAceptada(ed.getEstado())) {
                    LocalDate fin = ed.getFechaFin();
                    if (fin == null || !LocalDate.now().isAfter(fin)) {
                        visibles.add(ed);
                    }
                }
            }
            if (!visibles.isEmpty()) {
                edicionesPorEvento.put(ev.getNombre(), visibles);
            }
        }
        req.setAttribute("edicionesPorEvento", edicionesPorEvento);

        String eventoParam = trim(req.getParameter("evento"));
        String edicionParam = trim(req.getParameter("edicion"));

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");
        boolean yaRegistrado = false;

        if (!isBlank(eventoParam) && !isBlank(edicionParam) && !isBlank(nick)) {
            ResEvento res = resolverEvento(eventos, eventoParam);
            DTEdicion edSel = resolverEdicion(res, edicionParam);

            if (edSel != null && esAceptada(edSel.getEstado())
                    && (edSel.getFechaFin() == null || !LocalDate.now().isAfter(edSel.getFechaFin()))) {

                req.setAttribute("edicionSeleccionada", edSel);

                List<DTTipoRegistro> tipos = (edSel.getTiposRegistro() != null)
                        ? edSel.getTiposRegistro()
                        : new ArrayList<>();
                req.setAttribute("tiposRegistro", tipos);

                // Calcular cupos disponibles y si el usuario ya está registrado
                Map<String, Integer> cuposDisponibles = new HashMap<>();
                if (tipos != null) {
                    for (DTTipoRegistro tipo : tipos) {
                        int cupo = (tipo == null) ? 0 : tipo.getCupo();
                        int registrados = 0;
                        if (edSel.getRegistros() != null) {
                            for (DTRegistro reg : edSel.getRegistros()) {
                                if (reg == null) continue;
                                if (reg.getTipoRegistro() != null && tipo != null
                                        && reg.getTipoRegistro().equalsIgnoreCase(tipo.getNombre())) {
                                    registrados++;
                                }
                                // control de ya registrado
                                if (reg.getUsuario() != null && reg.getUsuario().equals(nick)) {
                                    yaRegistrado = true;
                                }
                            }
                        }
                        if (tipo != null) {
                            cuposDisponibles.put(tipo.getNombre(), cupo - registrados);
                        }
                    }
                }
                req.setAttribute("cuposDisponibles", cuposDisponibles);
                req.setAttribute("yaRegistrado", yaRegistrado);
            } else {
                req.setAttribute("error", "La edición seleccionada no existe, no está aceptada o ya finalizó.");
            }
        }

        req.getRequestDispatcher(JSP_INSCRIPCION).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requiereAsistente(req, resp)) return;
        req.setCharacterEncoding("UTF-8");

        String eventoParam = trim(req.getParameter("evento"));
        String edicionParam = trim(req.getParameter("edicion"));
        String tipoNom = trim(req.getParameter("tipo"));
        String codigoPatrocinio = trim(req.getParameter("codigoPatrocinio"));

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");

        if (isBlank(eventoParam) || isBlank(edicionParam) || isBlank(tipoNom) || isBlank(nick)) {
            req.setAttribute("error", "Debe seleccionar evento, edición y tipo de registro.");
            doGet(req, resp);
            return;
        }

        DTDatosUsuario dtUsuario;
        try {
            dtUsuario = cu().obtenerDatosUsuario(nick);
        } catch (UsuarioNoExisteException e) {
            req.setAttribute("error", "El usuario no existe o la sesión ha expirado.");
            doGet(req, resp);
            return;
        }
        if (dtUsuario == null) {
            req.setAttribute("error", "El usuario no existe o la sesión ha expirado.");
            doGet(req, resp);
            return;
        }

        List<DTEvento> eventos = ce().listarEventos();
        ResEvento res = resolverEvento(eventos, eventoParam);
        DTEdicion edSel = resolverEdicion(res, edicionParam);

        if (edSel == null || !esAceptada(edSel.getEstado())) {
            req.setAttribute("error", "La edición seleccionada no está aceptada o no existe.");
            doGet(req, resp);
            return;
        }
        LocalDate fin = edSel.getFechaFin();
        if (fin != null && LocalDate.now().isAfter(fin)) {
            req.setAttribute("error", "La edición seleccionada ya finalizó.");
            doGet(req, resp);
            return;
        }

        // Tipo de registro
        DTTipoRegistro tipoSel = null;
        List<DTTipoRegistro> tipos = edSel.getTiposRegistro();
        if (tipos != null) {
            for (DTTipoRegistro t : tipos) {
                if (t != null && t.getNombre() != null && t.getNombre().equalsIgnoreCase(tipoNom)) {
                    tipoSel = t;
                    break;
                }
            }
        }
        if (tipoSel == null) {
            req.setAttribute("error", "El tipo de registro seleccionado no es válido.");
            doGet(req, resp);
            return;
        }

        // calcular costo antes del try (patrocinio = costo 0 si válido)
        float costo = tipoSel.getCosto();
        if (!isBlank(codigoPatrocinio)) {
            List<DTPatrocinio> patrocinios = edSel.getPatrocinios();
            boolean valido = false;
            if (patrocinios != null) {
                for (DTPatrocinio p : patrocinios) {
                    if (p != null
                        && p.getCodigo() != null
                        && p.getCodigo().equalsIgnoreCase(codigoPatrocinio)
                        && p.getTipoRegistro() != null
                        && p.getTipoRegistro().equalsIgnoreCase(tipoNom)) {
                        valido = true;
                        break;
                    }
                }
            }
            if (valido) {
                costo = 0f;
            } else {
                req.setAttribute("error", "El código de patrocinio no es válido.");
                doGet(req, resp);
                return;
            }
        }

        try {
            String idRegistro = UUID.randomUUID().toString();
            LocalDate fechaRegistro = LocalDate.now();

            ce().altaRegistroEdicionEvento(
                idRegistro,
                nick,
                res.nombreEvento,
                edicionParam,
                tipoNom,
                fechaRegistro,
                costo,
                edSel.getFechaInicio()
            );

            resp.sendRedirect(req.getContextPath() + "/inicio");
        } catch (excepciones.CupoTipoRegistroInvalidoException e) {
            req.setAttribute("error", "No hay cupos disponibles para el tipo de registro seleccionado.");
            req.setAttribute("nombreTipoRegistroSinCupo", tipoNom);
            doGet(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }

    // Helpers

    private boolean requiereAsistente(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        String rol = s == null ? null : (String) s.getAttribute("rol");
        if (!"ASISTENTE".equals(rol)) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return false;
        }
        return true;
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static boolean esAceptada(Object estado) {
        if (estado == null) return false;
        String s = String.valueOf(estado);
        return "ACEPTADA".equalsIgnoreCase(s);
    }

    private static class ResEvento {
        final String nombreEvento;
        final String siglaEvento;
        ResEvento(String nombre, String sigla) { this.nombreEvento = nombre; this.siglaEvento = sigla; }
        boolean valido() { return nombreEvento != null && siglaEvento != null; }
    }

    private ResEvento resolverEvento(List<DTEvento> eventos, String eventoParam) {
        if (isBlank(eventoParam)) return new ResEvento(null, null);

        DTEvento evByNombre = null;
        try { evByNombre = ce().consultaDTEvento(eventoParam); } catch (Exception ignore) {}
        if (evByNombre != null) {
            return new ResEvento(evByNombre.getNombre(), safe(evByNombre.getSigla()));
        }

        if (eventos != null) {
            for (DTEvento e : eventos) {
                if (e != null && e.getSigla() != null && e.getSigla().equalsIgnoreCase(eventoParam)) {
                    return new ResEvento(e.getNombre(), e.getSigla());
                }
            }
        }
        return new ResEvento(null, null);
    }

    private DTEdicion resolverEdicion(ResEvento res, String edicionParam) {
        if (res == null || !res.valido() || isBlank(edicionParam)) return null;

        DTEdicion ed = null;
        try { ed = ce().consultaEdicionEvento(res.siglaEvento, edicionParam); } catch (Exception ignore) {}

        if (ed == null) {
            try { ed = ce().obtenerDtEdicion(res.nombreEvento, edicionParam); } catch (Exception ignore) {}
        }
        return ed;
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
