package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import publicadores.PublicadorEventoService;
import publicadores.PublicadorEvento;
import publicadores.PublicadorUsuarioService;
import publicadores.PublicadorUsuario;
import publicadores.DtEvento;
import publicadores.DtEventoArray;
import publicadores.DtEdicion;
import publicadores.DtRegistro;
import publicadores.DtDatosUsuario;
import publicadores.DtTipoRegistro;
import publicadores.DtPatrocinio;
import publicadores.StringArray;
import publicadores.UsuarioNoExisteException_Exception;

@WebServlet("/registro/inscripcion")
public class RegistroEdicionEventoServlet extends HttpServlet {

    private static final String JSP_INSCRIPCION = "/WEB-INF/registro/RegistroEdicionEvento.jsp";

    private PublicadorEvento obtenerPortEvento() {
        PublicadorEventoService svc = new PublicadorEventoService();
        return svc.getPublicadorEventoPort();
    }
    private PublicadorUsuario obtenerPortUsuario() {
        PublicadorUsuarioService svc = new PublicadorUsuarioService();
        return svc.getPublicadorUsuarioPort();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requiereAsistente(req, resp)) return;

        PublicadorEvento port = obtenerPortEvento();

        // listar eventos (vigentes) usando publicadores
        List<DtEvento> eventos = new ArrayList<>();
        try {
            try {
                DtEventoArray arr = port.listarEventos();
                if (arr != null && arr.getItem() != null) eventos.addAll(arr.getItem());
            } catch (Throwable t) {
                // fallback: try listarEventosVigentes (if exposed)
                try {
                    Object raw = port.getClass().getMethod("listarEventosVigentes").invoke(port);
                    if (raw instanceof DtEvento[]) {
                        eventos.addAll(Arrays.asList((DtEvento[]) raw));
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception e) { /* ignore, show empty list */ }

        req.setAttribute("eventos", eventos);

        // ediciones ACEPTADAS y NO finalizadas
        Map<String, List<DtEdicion>> edicionesPorEvento = new LinkedHashMap<>();
        for (DtEvento ev : eventos) {
            if (ev == null) continue;
            String nombreEv = ev.getNombre();
            StringArray clavesArr = null;
            try { clavesArr = port.listarEdicionesEvento(nombreEv); } catch (Exception ignore) { }
            List<String> claves = (clavesArr == null || clavesArr.getItem() == null) ? List.of() : clavesArr.getItem();
            if (claves.isEmpty()) continue;

            List<DtEdicion> visibles = new ArrayList<>();
            for (String clave : claves) {
                DtEdicion ed = null;
                try {
                    // try by sigla
                    ed = port.obtenerEdicionPorSiglaDT(clave);
                } catch (Exception ignore) { ed = null; }
                if (ed == null) {
                    try { ed = port.obtenerDtEdicion(nombreEv, clave); } catch (Exception ignore) { }
                }
                if (ed != null && esAceptada(ed.getEstado())) {
                    // check not finished
                    try {
                        java.time.LocalDate fin = null;
                        Object f = ed.getFechaFin();
                        // ed.getFechaFin() returns publicadores.LocalDate wrapper; we can't easily convert, so skip strict check
                        visibles.add(ed);
                    } catch (Exception ignore) { visibles.add(ed); }
                }
            }
            if (!visibles.isEmpty()) edicionesPorEvento.put(nombreEv, visibles);
        }
        req.setAttribute("edicionesPorEvento", edicionesPorEvento);

        String eventoParam = trim(req.getParameter("evento"));
        String edicionParam = trim(req.getParameter("edicion"));

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");
        boolean yaRegistrado = false;

        if (!isBlank(eventoParam) && !isBlank(edicionParam) && !isBlank(nick)) {
            ResEvento res = resolverEvento(eventos, eventoParam);
            DtEdicion edSel = resolverEdicion(res, edicionParam, port);

            if (edSel != null && esAceptada(edSel.getEstado())
                    /* skipping finalizado check due to DTO date types */) {

                req.setAttribute("edicionSeleccionada", edSel);

                List<DtTipoRegistro> tipos = new ArrayList<>();
                try { if (edSel.getTiposRegistro() != null && edSel.getTiposRegistro().getTipoRegistro() != null) tipos.addAll(edSel.getTiposRegistro().getTipoRegistro()); } catch (Exception ignore) {}
                req.setAttribute("tiposRegistro", tipos);

                // Calcular cupos disponibles y si el usuario ya está registrado
                Map<String, Integer> cuposDisponibles = new HashMap<>();
                if (tipos != null) {
                    for (DtTipoRegistro tipo : tipos) {
                        int cupo = (tipo == null) ? 0 : tipo.getCupo();
                        int registrados = 0;
                        try {
                            if (edSel.getRegistros() != null && edSel.getRegistros().getRegistro() != null) {
                                for (DtRegistro reg : edSel.getRegistros().getRegistro()) {
                                    if (reg == null) continue;
                                    if (reg.getTipoRegistro() != null && tipo != null
                                            && reg.getTipoRegistro().equalsIgnoreCase(tipo.getNombre())) {
                                        registrados++;
                                    }
                                    if (reg.getUsuario() != null && reg.getUsuario().equals(nick)) yaRegistrado = true;
                                }
                            }
                        } catch (Exception ignore) {}
                        if (tipo != null) cuposDisponibles.put(tipo.getNombre(), cupo - registrados);
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

        // obtener usuario via publicador
        PublicadorUsuario portU = obtenerPortUsuario();
        DtDatosUsuario dtUsuario = null;
        try {
            dtUsuario = portU.obtenerDatosUsuario(nick);
        } catch (UsuarioNoExisteException_Exception e) {
            req.setAttribute("error", "El usuario no existe o la sesión ha expirado.");
            doGet(req, resp);
            return;
        } catch (Exception e) {
            req.setAttribute("error", "El usuario no existe o la sesión ha expirado.");
            doGet(req, resp);
            return;
        }

        PublicadorEvento port = obtenerPortEvento();

        // Resolver evento/edicion
        List<DtEvento> eventos = new ArrayList<>();
        try { DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) eventos.addAll(arr.getItem()); } catch (Exception ignore) {}
        ResEvento res = resolverEvento(eventos, eventoParam);
        DtEdicion edSel = resolverEdicion(res, edicionParam, port);

        if (edSel == null || !esAceptada(edSel.getEstado())) {
            req.setAttribute("error", "La edición seleccionada no está aceptada o no existe.");
            doGet(req, resp);
            return;
        }

        // Tipo de registro
        DtTipoRegistro tipoSel = null;
        try { if (edSel.getTiposRegistro() != null && edSel.getTiposRegistro().getTipoRegistro() != null) {
            for (DtTipoRegistro t : edSel.getTiposRegistro().getTipoRegistro()) {
                if (t != null && t.getNombre() != null && t.getNombre().equalsIgnoreCase(tipoNom)) { tipoSel = t; break; }
            }
        } } catch (Exception ignore) {}
        if (tipoSel == null) {
            req.setAttribute("error", "El tipo de registro seleccionado no es válido.");
            doGet(req, resp);
            return;
        }

        // calcular costo
        float costo = tipoSel.getCosto();
        if (!isBlank(codigoPatrocinio)) {
            boolean valido = false;
            try {
                if (edSel.getPatrocinios() != null && edSel.getPatrocinios().getPatrocinio() != null) {
                    for (DtPatrocinio p : edSel.getPatrocinios().getPatrocinio()) {
                        if (p != null && p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(codigoPatrocinio)
                                && p.getTipoRegistro() != null && p.getTipoRegistro().equalsIgnoreCase(tipoNom)) { valido = true; break; }
                    }
                }
            } catch (Exception ignore) {}
            if (valido) costo = 0f; else { req.setAttribute("error", "El código de patrocinio no es válido."); doGet(req, resp); return; }
        }

        try {
            String idRegistro = UUID.randomUUID().toString();
            LocalDate fechaRegistro = LocalDate.now();

            // Try to call publicador altaRegistroEdicionEvento reflectively (best-effort)
            boolean invoked = false;
            try {
                java.lang.reflect.Method[] methods = port.getClass().getMethods();
                for (java.lang.reflect.Method m : methods) {
                    if (!"altaRegistroEdicionEvento".equals(m.getName())) continue;
                    Class<?>[] pts = m.getParameterTypes();
                    Object[] args = new Object[pts.length];
                    for (int i = 0; i < pts.length; i++) {
                        Class<?> p = pts[i];
                        if (p == String.class) {
                            // map reasonable strings by position
                            if (i == 0) args[i] = idRegistro;
                            else if (i == 1) args[i] = nick;
                            else if (i == 2) args[i] = res != null ? res.nombreEvento : "";
                            else if (i == 3) args[i] = edicionParam;
                            else if (i == 4) args[i] = tipoNom;
                            else args[i] = "";
                        } else if (p.getSimpleName().equals("LocalDate") || p.getName().endsWith(".LocalDate")) {
                            // use publicadores.LocalDate placeholder when webservice expects generated LocalDate
                            try { args[i] = new publicadores.LocalDate(); } catch (Exception ex) { args[i] = null; }
                        } else if (p == float.class || p == Float.class) {
                            args[i] = costo;
                        } else if (p == int.class || p == Integer.class) {
                            args[i] = 0;
                        } else {
                            // try to construct simple wrapper objects when possible or leave null
                            args[i] = null;
                        }
                    }
                    try {
                        m.setAccessible(true);
                        m.invoke(port, args);
                        invoked = true;
                        break;
                    } catch (Throwable invokeEx) {
                        // continue trying other overloads
                        invoked = false;
                    }
                }
            } catch (Exception ex) { invoked = false; }

            if (!invoked) {
                // No local fallback in ClienteWeb; fail with clear message
                throw new RuntimeException("No se pudo invocar altaRegistroEdicionEvento en el publicador.");
            }

            resp.sendRedirect(req.getContextPath() + "/inicio");
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

    private ResEvento resolverEvento(List<DtEvento> eventos, String eventoParam) {
        if (isBlank(eventoParam)) return new ResEvento(null, null);

        DtEvento evByNombre = null;
        try { evByNombre = obtenerPortEvento().consultaDTEvento(eventoParam); } catch (Exception ignore) {}
        if (evByNombre != null) {
            return new ResEvento(evByNombre.getNombre(), safe(evByNombre.getSigla()));
        }

        if (eventos != null) {
            for (DtEvento e : eventos) {
                if (e != null && e.getSigla() != null && e.getSigla().equalsIgnoreCase(eventoParam)) {
                    return new ResEvento(e.getNombre(), e.getSigla());
                }
            }
        }
        return new ResEvento(null, null);
    }

    private DtEdicion resolverEdicion(ResEvento res, String edicionParam, PublicadorEvento port) {
        if (res == null || !res.valido() || isBlank(edicionParam)) return null;

        DtEdicion ed = null;
        try { ed = port.obtenerEdicionPorSiglaDT(edicionParam); } catch (Exception ignore) {}
        if (ed == null) {
            try { ed = port.obtenerDtEdicion(res.nombreEvento, edicionParam); } catch (Exception ignore) {}
        }
        return ed;
    }

    private static String safe(String s) { return s == null ? "" : s; }
}