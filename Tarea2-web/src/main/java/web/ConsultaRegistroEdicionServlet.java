package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import logica.datatypes.DTRegistro;
import logica.datatypes.DTDatosUsuario;
import logica.interfaces.IControladorUsuario;
import logica.fabrica;

@WebServlet("/registro/ConsultaRegistroEdicion")
public class ConsultaRegistroEdicionServlet extends HttpServlet {
    private static final String JSP_CONSULTA = "/WEB-INF/registro/ConsultaRegistroEdicion.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String nick = (session != null) ? (String) session.getAttribute("nick") : null;
        String idRegistro = req.getParameter("idRegistro");

        if (idRegistro == null || idRegistro.isBlank() || nick == null) {
            req.setAttribute("error", "Registro no especificado o sesión no iniciada.");
            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
            return;
        }

        try {
            IControladorUsuario ctrlUsr = fabrica.getInstance().getIControladorUsuario();

            // Obtener usuario logueado en DT
            DTDatosUsuario dtoUsuario = (DTDatosUsuario) session.getAttribute("usuario");
            if (dtoUsuario == null) {
                dtoUsuario = ctrlUsr.obtenerDatosUsuario(nick);
            }

            // Obtener DTRegistro  desde el controlador
            DTRegistro dtRegistro = ctrlUsr.obtenerDatosRegistros(idRegistro);

            if (dtRegistro == null) {
                req.setAttribute("error", "No se encontró el registro solicitado.");
            } else {
                req.setAttribute("registro", dtRegistro);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error al consultar el registro: " + e.getMessage());
        }

        req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
    }
}
