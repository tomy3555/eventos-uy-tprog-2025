package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import publicadores.PublicadorUsuarioService;
import publicadores.PublicadorUsuario;
import publicadores.DtDatosUsuario;
import publicadores.DtDatosUsuarioArray;

@WebServlet(urlPatterns = {"/auth/login", "/auth/logout"})
public class LoginServlet extends HttpServlet {

    private static final String JSP_LOGIN = "/WEB-INF/auth/login.jsp";

    private String ctx(HttpServletRequest req) {
        return req.getContextPath();
    }

    private void cargarInstituciones(HttpServletRequest req) {
        try {
            PublicadorUsuarioService service = new PublicadorUsuarioService();
            PublicadorUsuario port = service.getPublicadorUsuarioPort();
            publicadores.StringArray arr = null;
            try { arr = port.listarInstituciones(); } catch (Exception ignore) { }
            java.util.List<String> lista = (arr == null || arr.getItem() == null) ? java.util.List.of() : arr.getItem();
            req.setAttribute("instituciones", lista);
        } catch (Exception e) {
            req.setAttribute("instituciones", java.util.List.of());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/auth/login".equals(path)) {
            cargarInstituciones(req);
            req.getRequestDispatcher(JSP_LOGIN).forward(req, resp);
            return;
        }

        if ("/auth/logout".equals(path)) {
            HttpSession sAux = req.getSession(false);
            if (sAux != null) sAux.invalidate();
            resp.sendRedirect(ctx(req) + "/inicio");
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!"/auth/login".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nickOrEmail = req.getParameter("email");
        String pass = req.getParameter("pass");

        if (nickOrEmail == null || nickOrEmail.isBlank() || pass == null || pass.isBlank()) {
            req.setAttribute("error", "Ingresá usuario y contraseña.");
            req.getRequestDispatcher(JSP_LOGIN).forward(req, resp);
            return;
        }

        String nick = nickOrEmail.trim();

        // Use publicador
        PublicadorUsuarioService service = new PublicadorUsuarioService();
        PublicadorUsuario port = service.getPublicadorUsuarioPort();

        boolean valido = false;
        try {
            valido = port.validarLogin(nick, pass);
        } catch (Exception e) {
            valido = false;
        }

        if (!valido) {
            req.setAttribute("estado_sesion", "LOGIN_INCORRECTO");
            req.setAttribute("error", "Usuario o contraseña incorrectos.");
            req.getRequestDispatcher(JSP_LOGIN).forward(req, resp);
            return;
        }

        DtDatosUsuario dto = null;
        try {
            // Try to locate by nickname or email: if nickOrEmail contains '@' we pass to obtenerDatosUsuario only if it's nickname
            // First, if input looks like nickname try obtenerDatosUsuario; else fetch list and match
            if (!nickOrEmail.contains("@")) {
                try { dto = port.obtenerDatosUsuario(nick); } catch (Exception ignore) { dto = null; }
            }
            if (dto == null) {
                DtDatosUsuarioArray arr = null;
                try { arr = port.obtenerUsuariosDT(); } catch (Exception ignore) { arr = null; }
                if (arr != null && arr.getItem() != null) {
                    for (DtDatosUsuario u : arr.getItem()) {
                        if (u == null) continue;
                        if (nickOrEmail.equalsIgnoreCase(u.getEmail()) || nickOrEmail.equalsIgnoreCase(u.getNickname())) {
                            dto = u; nick = u.getNickname(); break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore and treat as not found
        }

        if (dto == null) {
            req.setAttribute("error", "Usuario no encontrado.");
            req.getRequestDispatcher(JSP_LOGIN).forward(req, resp);
            return;
        }

        String rol = "ASISTENTE";
        try {
            // try reflective call to esAsistente on the port
            try {
                java.lang.reflect.Method m = port.getClass().getMethod("esAsistente", String.class);
                Object res = m.invoke(port, nick);
                if (res instanceof Boolean && ((Boolean) res)) rol = "ASISTENTE"; else rol = "ORGANIZADOR";
            } catch (NoSuchMethodException ns) {
                // fallback: infer from dto content: if dto.getEdiciones non-empty -> ORGANIZADOR
                boolean tieneEdiciones = false;
                try {
                    if (dto.getEdiciones() != null) {
                        java.util.List<?> list = (java.util.List<?>) dto.getEdiciones().getEdicion();
                        if (list != null && !list.isEmpty()) tieneEdiciones = true;
                    }
                } catch (Exception ignore) {}
                rol = tieneEdiciones ? "ORGANIZADOR" : "ASISTENTE";
            }
        } catch (Exception e) {
            rol = "ASISTENTE";
        }

        HttpSession sAux = req.getSession(true);
        sAux.setAttribute("usuario_logueado", dto);
        sAux.setAttribute("nick", nick);
        sAux.setAttribute("rol", rol);
        sAux.setAttribute("estado_sesion", "LOGIN_CORRECTO");

        resp.sendRedirect(ctx(req) + "/inicio");
    }
   }