package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import publicadores.DtEdicion;
import publicadores.DtEvento;
import publicadores.DTNivel;
import publicadores.DtPatrocinio;
import publicadores.DtTipoRegistro;
// Excepciones generadas por el stub
import publicadores.ValorPatrocinioExcedidoException_Exception;

@WebServlet({
    "/edicion/ConsultaPatrocinio",   // legacy (consulta)
    "/edicion/patrocinio/*"          // /consulta y /alta
})
public class ConsultaPatrocinioServlet extends HttpServlet {

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
        try { return URLEncoder.encode(s, StandardCharsets.UTF_8.name()); }
        catch (Exception e) { return s; }
    }
    private String ctx(HttpServletRequest req) { return req.getContextPath(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // === PUERTOS (dos líneas por servicio, como pediste) ===
        publicadores.PublicadorEventoService svcEv = new publicadores.PublicadorEventoService();
        publicadores.PublicadorEvento portEv = svcEv.getPublicadorEventoPort();
        publicadores.PublicadorUsuarioService svcUs = new publicadores.PublicadorUsuarioService();
        publicadores.PublicadorUsuario portUs = svcUs.getPublicadorUsuarioPort();

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

                DtPatrocinio dto = portEv.obtenerDTPatrocinio(codigoPatrocinio);
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

                String eventoSel  = req.getParameter("evento");
                String edicionSel = req.getParameter("edicion");

                // 1) Instituciones
                List<String> instituciones = new ArrayList<>();
                try {
                    Object inst = portUs.listarInstituciones();
                    if (inst instanceof String[]) {
                        instituciones = Arrays.asList((String[]) inst);
                    } else if (inst != null) {
                        @SuppressWarnings("unchecked")
                        List<String> items = (List<String>) inst.getClass().getMethod("getItem").invoke(inst);
                        if (items != null) instituciones.addAll(items);
                    }
                } catch (Exception ignore) {}
                req.setAttribute("instituciones", instituciones);

                // 2) Eventos/ediciones del organizador
                HttpSession s = req.getSession(false);
                String nick = (s == null) ? null : (String) s.getAttribute("nick");

                List<DtEvento> todos = new ArrayList<>();
                try {
                    try {
                        publicadores.DtEventoArray arr = portEv.listarEventos();
                        if (arr != null && arr.getItem() != null) todos.addAll(arr.getItem());
                    } catch (Throwable t) {
                        Object evRes = portEv.getClass().getMethod("listarEventos").invoke(portEv);
                        if (evRes instanceof DtEvento[]) {
                            DtEvento[] darr = (DtEvento[]) evRes;
                            if (darr != null) todos.addAll(Arrays.asList(darr));
                        } else if (evRes != null) {
                            try {
                                @SuppressWarnings("unchecked")
                                List<DtEvento> items = (List<DtEvento>) evRes.getClass().getMethod("getItem").invoke(evRes);
                                if (items != null) todos.addAll(items);
                            } catch (Exception ignore) {}
                        }
                    }
                 } catch (Exception ignore) {}

                List<String> eventosOrganizador   = new ArrayList<>();
                List<String> edicionesOrganizador = new ArrayList<>();

                if (!todos.isEmpty() && nick != null) {
                    for (DtEvento ev : todos) {
                        if (ev == null) continue;
                        String nombreEv = ev.getNombre();

                        List<String> eds = new ArrayList<>();
                        try {
                            Object res = portEv.listarEdicionesEvento(nombreEv);
                            if (res instanceof String[]) {
                                eds = Arrays.asList((String[]) res);
                            } else if (res != null) {
                                @SuppressWarnings("unchecked")
                                List<String> items = (List<String>) res.getClass().getMethod("getItem").invoke(res);
                                if (items != null) eds.addAll(items);
                            }
                        } catch (Exception ignore) {}

                        boolean agregaEvento = false;
                        for (String ed : eds) {
                            DtEdicion edDTO = portEv.obtenerDtEdicion(nombreEv, ed);
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
                        if (agregaEvento) eventosOrganizador.add(nombreEv);
                    }
                }

                if (edicionSel != null && !edicionSel.isEmpty()
                        && (edicionesOrganizador.isEmpty() || !edicionesOrganizador.contains(edicionSel))) {
                    edicionSel = null;
                }

                // 3) Tipos de registro
                List<DtTipoRegistro> tipos = new ArrayList<>();
                if (eventoSel != null && !eventoSel.isEmpty() && edicionSel != null && !edicionSel.isEmpty()) {
                    try {
                        Object trRes = portEv.listarTiposRegistroDeEdicion(eventoSel, edicionSel);
                        if (trRes instanceof DtTipoRegistro[]) {
                            DtTipoRegistro[] arr = (DtTipoRegistro[]) trRes;
                            tipos = (arr == null) ? List.of() : Arrays.asList(arr);
                        } else if (trRes != null) {
                            @SuppressWarnings("unchecked")
                            List<DtTipoRegistro> items = (List<DtTipoRegistro>) trRes.getClass().getMethod("getItem").invoke(trRes);
                            if (items != null) tipos.addAll(items);
                        }
                    } catch (Exception ignore) {}
                }

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

        // === PUERTO eventos ===
        publicadores.PublicadorEventoService svcEv = new publicadores.PublicadorEventoService();
        publicadores.PublicadorEvento portEv = svcEv.getPublicadorEventoPort();

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

        // Validación organizador
        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");

        DtEdicion edDTO = portEv.obtenerDtEdicion(evento, edicion);
        if (edDTO == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Edición inválida.");
            return;
        }
        String orgNick = edDTO.getOrganizador();
        if (nick == null || orgNick == null || !nick.equals(orgNick)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No es organizador de la edición seleccionada.");
            return;
        }

        String siglaEd = edDTO.getSigla();

        try {
            DTNivel nivel   = DTNivel.valueOf(nivelStr);
            int aporte      = Integer.parseInt(aporteStr);
            int cantidad    = Integer.parseInt(cantStr);

            // Fecha como java.util.Date (00:00 en zona local)
            LocalDate fecha = LocalDate.parse(fechaStr); // yyyy-MM-dd
            Date fechaDate = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
            try {
                java.lang.reflect.Method meth = null;
                for (java.lang.reflect.Method m : portEv.getClass().getMethods()) {
                    if (m.getName().equals("altaPatrocinioDT") && m.getParameterCount() == 8) { meth = m; break; }
                }
                if (meth == null) throw new NoSuchMethodException("altaPatrocinioDT not found on publicador port");

                // Adaptación para manejar la fecha correctamente
                Object fechaArg = null;
                Class<?> dateType = meth.getParameterTypes()[5];

                if (dateType.isAssignableFrom(java.util.Date.class)) {
                    fechaArg = fechaDate;
                } else if (dateType.getName().equals("javax.xml.datatype.XMLGregorianCalendar")) {
                    try {
                        javax.xml.datatype.DatatypeFactory df = javax.xml.datatype.DatatypeFactory.newInstance();
                        javax.xml.datatype.XMLGregorianCalendar xgc = df.newXMLGregorianCalendarDate(
                                fecha.getYear(), fecha.getMonthValue(), fecha.getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                        fechaArg = xgc;
                    } catch (Exception ex) {
                        fechaArg = null;
                    }
                } else if (dateType.isAssignableFrom(String.class)) {
                    fechaArg = fechaStr;
                } else {
                    throw new IllegalArgumentException("Tipo de fecha no soportado: " + dateType.getName());
                }

                Object[] args = new Object[] { siglaEd, instit, nivel, tipo, aporte, fechaArg, cantidad, codigo };
                meth.invoke(portEv, args);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                Throwable t = ite.getTargetException();
                if (t instanceof ValorPatrocinioExcedidoException_Exception) throw (ValorPatrocinioExcedidoException_Exception) t;
                throw new RuntimeException(t);
            }
            
             String url = ctx(req) + "/edicion/patrocinio/consulta"
                     + "?evento=" + encode(evento)
                     + "&edicion=" + encode(edicion)
                     + "&codigoPatrocinio=" + encode(codigo);
             resp.sendRedirect(url);

        } catch (NumberFormatException e) {
             req.setAttribute("error", "Costo/cantidad deben ser numéricos.");
             recargarFormAlta(req, resp);
        } catch (IllegalArgumentException e) {
             req.setAttribute("error", "Valores inválidos: " + e.getMessage());
             recargarFormAlta(req, resp);
        } catch (ValorPatrocinioExcedidoException_Exception e) {
             req.setAttribute("error", "La cantidad de registros gratuitos excede el 20% del aporte.");
             recargarFormAlta(req, resp);
        } catch (Exception e) {
             req.setAttribute("error", "Error inesperado: " + e.getClass().getSimpleName());
             recargarFormAlta(req, resp);
        }
    }

    private void recargarFormAlta(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        publicadores.PublicadorUsuarioService svcUs = new publicadores.PublicadorUsuarioService();
        publicadores.PublicadorUsuario portUs = svcUs.getPublicadorUsuarioPort();
        publicadores.PublicadorEventoService svcEv = new publicadores.PublicadorEventoService();
        publicadores.PublicadorEvento portEv = svcEv.getPublicadorEventoPort();

        String eventoSel  = req.getParameter("evento");
        String edicionSel = req.getParameter("edicion");

        // Instituciones
        List<String> instituciones = new ArrayList<>();
        try {
            Object inst = portUs.listarInstituciones();
            if (inst instanceof String[]) {
                instituciones = Arrays.asList((String[]) inst);
            } else if (inst != null) {
                @SuppressWarnings("unchecked")
                List<String> items = (List<String>) inst.getClass().getMethod("getItem").invoke(inst);
                if (items != null) instituciones.addAll(items);
            }
        } catch (Exception ignore) {}
        req.setAttribute("instituciones", instituciones);

        // Eventos/ediciones del organizador
        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nick");

        List<DtEvento> todos = new ArrayList<>();
        try {
            Object evRes = portEv.listarEventos();
            if (evRes instanceof DtEvento[]) {
                todos = (evRes == null) ? List.of() : Arrays.asList((DtEvento[]) evRes);
            } else if (evRes != null) {
                @SuppressWarnings("unchecked")
                List<DtEvento> items = (List<DtEvento>) evRes.getClass().getMethod("getItem").invoke(evRes);
                if (items != null) todos.addAll(items);
            }
        } catch (Exception ignore) {}

        List<String> eventosOrganizador   = new ArrayList<>();
        List<String> edicionesOrganizador = new ArrayList<>();

        if (!todos.isEmpty() && nick != null) {
            for (DtEvento ev : todos) {
                if (ev == null) continue;
                String nombreEv = ev.getNombre();

                List<String> eds = new ArrayList<>();
                try {
                    Object res = portEv.listarEdicionesEvento(nombreEv);
                    if (res instanceof String[]) {
                        eds = Arrays.asList((String[]) res);
                    } else if (res != null) {
                        @SuppressWarnings("unchecked")
                        List<String> items = (List<String>) res.getClass().getMethod("getItem").invoke(res);
                        if (items != null) eds.addAll(items);
                    }
                } catch (Exception ignore) {}

                boolean agregaEvento = false;
                for (String ed : eds) {
                    DtEdicion edDTO = portEv.obtenerDtEdicion(nombreEv, ed);
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
                if (agregaEvento) eventosOrganizador.add(nombreEv);
            }
        }

        if (edicionSel != null && !edicionSel.isEmpty()
                && (edicionesOrganizador.isEmpty() || !edicionesOrganizador.contains(edicionSel))) {
            edicionSel = null;
        }

        List<DtTipoRegistro> tipos = new ArrayList<>();
        if (eventoSel != null && !eventoSel.isEmpty() && edicionSel != null && !edicionSel.isEmpty()) {
            try {
                Object trRes = portEv.listarTiposRegistroDeEdicion(eventoSel, edicionSel);
                if (trRes instanceof DtTipoRegistro[]) {
                    DtTipoRegistro[] arr = (DtTipoRegistro[]) trRes;
                    tipos = (arr == null) ? List.of() : Arrays.asList(arr);
                } else if (trRes != null) {
                    @SuppressWarnings("unchecked")
                    List<DtTipoRegistro> items = (List<DtTipoRegistro>) trRes.getClass().getMethod("getItem").invoke(trRes);
                    if (items != null) tipos.addAll(items);
                }
            } catch (Exception ignore) {}
        }

        req.setAttribute("evento", eventoSel);
        req.setAttribute("edicion", edicionSel);
        req.setAttribute("eventosOrganizador", eventosOrganizador);
        req.setAttribute("edicionesOrganizador", edicionesOrganizador);
        req.setAttribute("tiposRegistro", tipos);

        req.getRequestDispatcher("/WEB-INF/patrocinio/AltaPatrocinio.jsp").forward(req, resp);
    }
}
