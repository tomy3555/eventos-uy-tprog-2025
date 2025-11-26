package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;


@WebServlet("/precargar")
public class PrecargaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("✅ Entrando al PrecargaServlet...");

        // First try to invoke a remote publicador method to trigger precarga (if server exposes it)
        boolean done = false;
        try {
            // Try PublicadorEvento
            try {
                publicadores.PublicadorEventoService evSvc = new publicadores.PublicadorEventoService();
                publicadores.PublicadorEvento evPort = evSvc.getPublicadorEventoPort();
                try {
                    java.lang.reflect.Method m = evPort.getClass().getMethod("cargar");
                    m.invoke(evPort);
                    done = true;
                } catch (NoSuchMethodException ns) {
                    // ignore
                }
            } catch (Throwable ignore) { }

            // Try PublicadorUsuario if not done
            if (!done) {
                try {
                    publicadores.PublicadorUsuarioService usSvc = new publicadores.PublicadorUsuarioService();
                    publicadores.PublicadorUsuario usPort = usSvc.getPublicadorUsuarioPort();
                    try {
                        java.lang.reflect.Method m2 = usPort.getClass().getMethod("cargar");
                        m2.invoke(usPort);
                        done = true;
                    } catch (NoSuchMethodException ns) {
                        // ignore
                    }
                } catch (Throwable ignore) { }
            }

            // If remote not available, try to call local CargaDatosPrueba.cargar() reflectively (if present on classpath)
            if (!done) {
                try {
                    Class<?> cdp = Class.forName("logica.CargaDatosPrueba");
                    java.lang.reflect.Method m = cdp.getMethod("cargar");
                    m.invoke(null);
                    done = true;
                } catch (ClassNotFoundException cnf) {
                    // not available locally
                }
            }

            if (done) {
                getServletContext().setAttribute("datosPrecargados", Boolean.TRUE);
                resp.sendRedirect(req.getContextPath() + "/inicio");
            } else {
                getServletContext().setAttribute("datosPrecargados", Boolean.FALSE);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No hay método de precarga disponible en cliente ni en publicadores.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            getServletContext().setAttribute("datosPrecargados", Boolean.FALSE);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar datos de prueba.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("🔍 Entró al doGet del PrecargaServlet (por GET)");
        doPost(req, resp);
    }

}