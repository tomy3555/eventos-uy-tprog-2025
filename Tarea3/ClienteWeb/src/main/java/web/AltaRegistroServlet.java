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
import java.util.Arrays;

import javax.xml.datatype.XMLGregorianCalendar;

import publicadores.DtEdicion;
import publicadores.DtEvento;
import publicadores.DtEventoArray;
import publicadores.StringArray;

@WebServlet("/registro/*")
public class AltaRegistroServlet extends HttpServlet {

    private static final String JSP_ALTA = "/WEB-INF/registro/AltaRegistro.jsp";
    private static final String JSP_OK   = "/WEB-INF/registro/AltaRegistroOK.jsp";

    private String ctx(HttpServletRequest req) { return req.getContextPath(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // === EXACTAS estas dos líneas para crear el port (versión Evento) ===
        publicadores.PublicadorEventoService service = new publicadores.PublicadorEventoService();
        publicadores.PublicadorEvento port = service.getPublicadorEventoPort();

        String path = req.getPathInfo();
        System.out.println("Entra al doGet de AltaRegistroServlet con path: " + path);
        if (path == null || "/".equals(path) || "/alta".equals(path)) {
            if (!requiereOrganizador(req, resp)) return;

            // Cargar ediciones del organizador desde el servicio remoto
            recargarDatosDT(req, port);

            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // === EXACTAS estas dos líneas para crear el port (versión Evento) ===
        publicadores.PublicadorEventoService service = new publicadores.PublicadorEventoService();
        publicadores.PublicadorEvento port = service.getPublicadorEventoPort();

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
                recargarDatosDT(req, port);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }

            try {
                float costo = Float.parseFloat(costoStr);
                int cupo    = Integer.parseInt(cupoStr);

                DtEdicion dtSel = port.obtenerEdicionPorSiglaDT(siglaEdicion);
                if (dtSel == null) {
                    req.setAttribute("error", "No se encontró la edición seleccionada.");
                    recargarDatosDT(req, port);
                    req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                    return;
                }

                // === Validación: no permitir agregar tipos a ediciones finalizadas ===
                LocalDate fin = null;
                Object f = dtSel.getFechaFin();
                try {
                	  if (f == null) {
                	    fin = null;

                	  } else if (f instanceof publicadores.LocalDate) {
                	    // Caso: wrapper LocalDate del stub
                	    publicadores.LocalDate ld = (publicadores.LocalDate) f;

                	    // 1) Intentá leer campos year/month/day directamente (si existen)
                	    try {
                	      int y = (int) ld.getClass().getMethod("getYear").invoke(ld);
                	      int m = (int) ld.getClass().getMethod("getMonth").invoke(ld);
                	      int d = (int) ld.getClass().getMethod("getDay").invoke(ld);
                	      fin = LocalDate.of(y, m, d);
                	    } catch (NoSuchMethodException ignore) {
                	      // 2) Si no tiene getters de campos, probá obtener un 'value' con esos getters
                	      Object val = ld.getClass().getMethod("getValue").invoke(ld); // p.ej. XML-ish
                	      if (val != null) {
                	        int y = (int) val.getClass().getMethod("getYear").invoke(val);
                	        int m = (int) val.getClass().getMethod("getMonth").invoke(val);
                	        int d = (int) val.getClass().getMethod("getDay").invoke(val);
                	        fin = LocalDate.of(y, m, d);
                	      }
                	    }

                	  } else if (f instanceof java.util.Date) {
                	    // Caso: xs:date mapeado a java.util.Date
                	    fin = ((java.util.Date) f).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                	  } else if (f instanceof CharSequence) {
                	    // Caso: la fecha llega como "yyyy-MM-dd"
                	    fin = LocalDate.parse(f.toString());

                	  } else {
                	    // Último recurso: intentar leer métodos getYear/getMonth/getDay() por reflexión del propio 'f'
                	    int y = (int) f.getClass().getMethod("getYear").invoke(f);
                	    int m = (int) f.getClass().getMethod("getMonth").invoke(f);
                	    int d = (int) f.getClass().getMethod("getDay").invoke(f);
                	    fin = LocalDate.of(y, m, d);
                	  }
                } catch (Exception ignore) { /* si no podemos convertir, no bloqueamos */ }

                if (fin != null && fin.isBefore(LocalDate.now())) {
                    req.setAttribute("error", "No se pueden agregar tipos de registro a una edición finalizada.");
                    recargarDatosDT(req, port);
                    req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                    return;
                }

                // Alta remota del TipoRegistro (usar el método generado)
                port.altaTipoRegistro(dtSel, nombre, descripcion, costo, cupo);

                // Redirigir a la consulta del TipoRegistro
                String eventoNombre = null;
                try { eventoNombre = (dtSel.getEvento() != null ? dtSel.getEvento().getNombre() : null); } catch (Exception ignore) {}

                String eventoEnc  = URLEncoder.encode(eventoNombre != null ? eventoNombre : "", StandardCharsets.UTF_8.name());
                String edicionEnc = URLEncoder.encode(dtSel.getNombre(), StandardCharsets.UTF_8.name());
                String tipoEnc    = URLEncoder.encode(nombre, StandardCharsets.UTF_8.name());

                resp.sendRedirect(ctx(req) + "/registro/ConsultaTipoRegistro?evento=" + eventoEnc
                        + "&edicion=" + edicionEnc
                        + "&tipoRegistro=" + tipoEnc);
                return;

            } catch (NumberFormatException nfe) {
                req.setAttribute("error", "Costo y cupo deben ser numéricos.");
                recargarDatosDT(req, port);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;

            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
                recargarDatosDT(req, port);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // ===================== Helpers =====================

    private void recargarDatosDT(HttpServletRequest req, publicadores.PublicadorEvento port) {
        HttpSession sAux = req.getSession(false);
        String nick = sAux == null ? null : (String) sAux.getAttribute("nick");

        List<DtEdicion> ediciones = new ArrayList<>();

        if (nick != null) {
            List<DtEvento> eventos = new ArrayList<>();
            try {
                try {
                    DtEventoArray arr = port.listarEventos();
                    if (arr != null && arr.getItem() != null) {
                        eventos.addAll(arr.getItem());
                    }
                } catch (NoSuchMethodError | NoClassDefFoundError nsmd) {
                    
                    try {
                        Object raw = port.getClass().getMethod("listarEventos").invoke(port);
                        if (raw instanceof DtEvento[]) {
                            DtEvento[] darr = (DtEvento[]) raw;
                            eventos.addAll(Arrays.asList(darr));
                        } else {
                            try {
                                var items = (List<DtEvento>) raw.getClass().getMethod("getItem").invoke(raw);
                                if (items != null) eventos.addAll(items);
                            } catch (Exception ignore) {}
                        }
                    } catch (Exception ignore) {
                    }
                }
            } catch (Throwable t) {
                try {
                    Object wrapper = port.getClass().getMethod("listarEventos").invoke(port);
                    if (wrapper instanceof DtEventoArray) {
                        DtEventoArray arr = (DtEventoArray) wrapper;
                        if (arr.getItem() != null) eventos.addAll(arr.getItem());
                    } else if (wrapper instanceof DtEvento[]) {
                        eventos.addAll(Arrays.asList((DtEvento[]) wrapper));
                    } else {
                        try {
                            var items = (List<DtEvento>) wrapper.getClass().getMethod("getItem").invoke(wrapper);
                            if (items != null) eventos.addAll(items);
                        } catch (Exception ignore) {}
                    }
                } catch (Exception ignore) {}
            }

             for (DtEvento ev : eventos) {
                 if (ev == null) continue;
                 String nombreEvento = ev.getNombre();

                List<String> nombresEd;
                try {
                	publicadores.StringArray arr = port.listarEdicionesEvento(nombreEvento);
                	nombresEd = (arr == null || arr.getItem() == null) ? java.util.List.of()
                	                                                   : new java.util.ArrayList<>(arr.getItem());
                } catch (Throwable t) {
                    nombresEd = new ArrayList<>();
                    try {
                        Object wrapper = port.listarEdicionesEvento(nombreEvento);
                        var items = (List<String>) wrapper.getClass().getMethod("getItem").invoke(wrapper);
                        if (items != null) nombresEd.addAll(items);
                    } catch (Exception ignore) {}
                }

                for (String nomEd : nombresEd) {
                    DtEdicion dt = port.obtenerDtEdicion(nombreEvento, nomEd);
                    if (dt != null) {
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