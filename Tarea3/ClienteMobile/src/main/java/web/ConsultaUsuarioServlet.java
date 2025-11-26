package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

import publicadores.DtDatosUsuario;
import publicadores.DtDatosUsuarioArray;
import publicadores.PublicadorUsuario;
import publicadores.PublicadorUsuarioService;
import publicadores.UsuarioNoExisteException_Exception;

@WebServlet(urlPatterns = {
        "/usuario/ConsultaUsuario",
        "/usuario/modificar",
        "/usuario/seguir",
        "/usuario/dejarSeguir"
})
public class ConsultaUsuarioServlet extends HttpServlet {
    private static final String JSP_CONSULTA = "/WEB-INF/usuario/ConsultaUsuario.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        boolean forzarListado = isTrue(request.getParameter("listar")) ||
                "list".equalsIgnoreCase(trim(request.getParameter("view")));

        String nick = trim(request.getParameter("nick"));
        if (!forzarListado && isBlank(nick)) {
            HttpSession sAux = request.getSession(false);
            if (sAux != null) {
                Object obj = sAux.getAttribute("nick");
                if (obj instanceof String) nick = (String) obj;
            }
        }

        PublicadorUsuarioService service = new PublicadorUsuarioService();
        PublicadorUsuario port = service.getPublicadorUsuarioPort();

        if (forzarListado || isBlank(nick)) {
            List<DtDatosUsuario> usuarios = new ArrayList<>();
            try {
                DtDatosUsuarioArray arr = port.obtenerUsuariosDT();
                usuarios = asList(arr);
            } catch (Exception e) {
                request.setAttribute("error", "No se pudo obtener la lista de usuarios.");
            }
            request.setAttribute("usuarios", usuarios);
        } else {
            try {
                DtDatosUsuario usuario = port.obtenerDatosUsuario(nick);
                request.setAttribute("usuario", usuario);
                String nickSesion = nickEnSesion(request);
                boolean esSuPropioPerfil = nickSesion != null && nickSesion.equals(usuario.getNickname());
                request.setAttribute("esSuPropioPerfil", esSuPropioPerfil);
            } catch (UsuarioNoExisteException_Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "El usuario '" + nick + "' no existe.");
            }
        }
        request.getRequestDispatcher(JSP_CONSULTA).forward(request, response);
    }

    private String nickEnSesion(HttpServletRequest req) {
        HttpSession sAux = req.getSession(false);
        return sAux == null ? null : (String) sAux.getAttribute("nick");
    }

    private static String trim(String s) { return (s == null) ? null : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static boolean isTrue(String v) {
        if (v == null) return false;
        String s = v.trim().toLowerCase(Locale.ROOT);
        return "1".equals(s) || "true".equals(s) || "on".equals(s) || "yes".equals(s) || "y".equals(s);
    }
    private static java.util.List<DtDatosUsuario> asList(DtDatosUsuarioArray arr) {
        if (arr == null) return java.util.List.of();
        try {
            java.util.List<DtDatosUsuario> l = arr.getItem();
            return (l == null) ? java.util.List.of() : l;
        } catch (NoSuchMethodError e) {
            try {
                java.util.List<DtDatosUsuario> l = (java.util.List<DtDatosUsuario>) arr.getClass()
                        .getMethod("getItems").invoke(arr);
                return (l == null) ? java.util.List.of() : l;
            } catch (Exception ignore) {
                return java.util.List.of();
            }
        }
    }
}