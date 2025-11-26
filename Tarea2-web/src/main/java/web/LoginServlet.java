package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import logica.fabrica;
import logica.interfaces.IControladorUsuario;
import logica.datatypes.DTDatosUsuario;
import excepciones.UsuarioNoExisteException;

@WebServlet(urlPatterns = {"/auth/login", "/auth/logout"})
public class LoginServlet extends HttpServlet {

    private static final String JSP_LOGIN = "/WEB-INF/auth/login.jsp";
    private final IControladorUsuario controladorUser = fabrica.getInstance().getIControladorUsuario();

    private String ctx(HttpServletRequest req) {
        return req.getContextPath();
    }

    private void cargarInstituciones(HttpServletRequest req) {
        IControladorUsuario controladorUs = fabrica.getInstance().getIControladorUsuario();
        Collection<String> instituciones = controladorUs.getInstituciones();
        req.setAttribute("instituciones", instituciones);
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

        boolean valido = controladorUser.validarLogin(nick, pass);

        if (!valido) {
            req.setAttribute("estado_sesion", "LOGIN_INCORRECTO");
            req.setAttribute("error", "Usuario o contraseña incorrectos.");
            req.getRequestDispatcher(JSP_LOGIN).forward(req, resp);
            return;
        }

        Set<DTDatosUsuario> usuarios = new HashSet<>();
        try {
            usuarios = controladorUser.obtenerUsuariosDT();
        } catch (UsuarioNoExisteException e) {
            e.printStackTrace();
        }

        DTDatosUsuario dto = null;
        
        

        for (DTDatosUsuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(nickOrEmail)
                    || u.getNickname().equalsIgnoreCase(nickOrEmail)) {
                dto = u;
                nick = u.getNickname();
                break;
            }
        }


        if (dto == null) {
            req.setAttribute("error", "Usuario no encontrado.");
            req.getRequestDispatcher(JSP_LOGIN).forward(req, resp);
            return;
        }

        String rol = controladorUser.esAsistente(nick) ? "ASISTENTE" : "ORGANIZADOR";

        HttpSession sAux = req.getSession(true);
        sAux.setAttribute("usuario_logueado", dto);
        sAux.setAttribute("nick", nick);
        sAux.setAttribute("rol", rol);
        sAux.setAttribute("estado_sesion", "LOGIN_CORRECTO");

        resp.sendRedirect(ctx(req) + "/inicio");
    }
   }
