package web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.BindingProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@WebFilter(urlPatterns = {
    "/evento/*",                 // ej: /evento/ConsultaEvento  o  /evento/<nombre>
    "/edicion/ConsultaEdicion"   // recibe ?evento=...
})
public class ContadorVisitasFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String evento = null;

        String servletPath = req.getServletPath();   // p.ej. "/evento"
        String pathInfo    = req.getPathInfo();      // p.ej. "/ConsultaEvento" o "/<nombre>" o null
        String queryEvento = trim(req.getParameter("evento"));

        // ====== EXCLUSIÓN TEMPRANA: listado por categoría ======
        boolean esListado =
            "/evento/listado".equals(servletPath) ||
            ("/evento".equals(servletPath) && "/listado".equals(pathInfo));

        if (esListado) {
            chain.doFilter(request, response);
            return;
        }

        boolean esConsultaEvento =
            "/evento/ConsultaEvento".equals(servletPath) ||
            ("/evento".equals(servletPath) && "/ConsultaEvento".equals(pathInfo));
        if (esConsultaEvento) {
            evento = queryEvento;
        }

        boolean esConsultaEdicion =
            "/edicion/ConsultaEdicion".equals(servletPath) ||
            ("/edicion".equals(servletPath) && "/ConsultaEdicion".equals(pathInfo));
        if (isBlank(evento) && esConsultaEdicion) {
            evento = queryEvento;
        }

        if (isBlank(evento) && "/evento".equals(servletPath) && pathInfo != null && pathInfo.length() > 1) {
            String candidato = URLDecoder.decode(pathInfo.substring(1), StandardCharsets.UTF_8);
            if (!"listado".equalsIgnoreCase(candidato)) {
                evento = candidato;
            }
        }

        // Defensa: no contar claves genéricas
        if ("ConsultaEvento".equalsIgnoreCase(evento) || "listado".equalsIgnoreCase(evento)) {
            evento = null;
        }

        // Guardar contexto para log
        String reqUri  = req.getRequestURI();
        String nick    = (req.getSession(false) != null)
                ? String.valueOf(req.getSession(false).getAttribute("nick"))
                : "<anon>";

        chain.doFilter(request, response);

        if (isBlank(evento)) {
            Object v;

            v = req.getAttribute("evento");
            if (v instanceof String s && !isBlank(s)) {
                evento = s.trim();
            }

            if (isBlank(evento)) {
                v = req.getAttribute("nombreEvento");
                if (v instanceof String s && !isBlank(s)) {
                    evento = s.trim();
                }
            }

            if (isBlank(evento)) {
                v = req.getAttribute("evNombre");
                if (v instanceof String s && !isBlank(s)) {
                    evento = s.trim();
                }
            }

            if (isBlank(evento)) {
                String[] posiblesDTO = { "dtEvento", "eventoDTO", "eventoObj" };
                for (String key : posiblesDTO) {
                    v = req.getAttribute(key);
                    if (v != null) {
                        try {
                            Method m = v.getClass().getMethod("getNombre");
                            Object nombre = m.invoke(v);
                            if (nombre != null && !isBlank(nombre.toString())) {
                                String candidato = nombre.toString().trim();
                                if (!"listado".equalsIgnoreCase(candidato)) {
                                    evento = candidato;
                                }
                                break;
                            }
                        } catch (Exception ignore) {
                            // sin getNombre() o fallo — seguimos probando
                        }
                    }
                }
            }
        }

        if ("ConsultaEvento".equalsIgnoreCase(evento) || "listado".equalsIgnoreCase(evento)) {
            evento = null;
        }

        if (!isBlank(evento)) {
            try {
                publicadores.PublicadorEstadisticasService service = new publicadores.PublicadorEstadisticasService();
                publicadores.PublicadorEstadisticas port = service.getPublicadorEstadisticasPort();

                String overrideEndpoint = System.getProperty("estadisticas.endpoint", null);
                if (overrideEndpoint != null && !overrideEndpoint.isBlank()) {
                    ((BindingProvider) port).getRequestContext().put(
                            BindingProvider.ENDPOINT_ADDRESS_PROPERTY, overrideEndpoint);
                }

                ((BindingProvider) port).getRequestContext().put("com.sun.xml.ws.connect.timeout", 3000);
                ((BindingProvider) port).getRequestContext().put("com.sun.xml.ws.request.timeout", 3000);

                port.registrarVisita(evento);

            } catch (jakarta.xml.ws.WebServiceException wse) {
                System.out.println("[VISITAS][ERROR] WebService: " + wse.getClass().getName()
                        + " | msg=" + String.valueOf(wse.getMessage()));
                Throwable c = wse.getCause();
                int hops = 0;
                while (c != null && hops++ < 5) {
                    System.out.println("   causa -> " + c.getClass().getName() + " | " + String.valueOf(c.getMessage()));
                    c = c.getCause();
                }
            } catch (Exception ex) {
                System.out.println("[VISITAS][ERROR] Genérico: " + ex.getClass().getName()
                        + " | msg=" + String.valueOf(ex.getMessage()));
            }
        } else {
            System.out.println("[VISITAS] No se pudo identificar el evento | uri='" + reqUri + "' | nick='" + nick + "'");
        }
    }

    @Override public void init(FilterConfig filterConfig) throws ServletException {}
    @Override public void destroy() {}

    private static String trim(String s){ return s == null ? null : s.trim(); }
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
