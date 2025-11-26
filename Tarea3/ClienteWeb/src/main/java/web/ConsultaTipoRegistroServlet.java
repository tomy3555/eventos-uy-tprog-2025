package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import publicadores.DtTipoRegistro;
import publicadores.PublicadorEvento;
import publicadores.PublicadorEventoService;

@WebServlet("/registro/ConsultaTipoRegistro")
public class ConsultaTipoRegistroServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String evento = req.getParameter("evento");
        String edicion = req.getParameter("edicion");
        String tipoRegistro = req.getParameter("tipoRegistro");
        if (evento == null || edicion == null || tipoRegistro == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros");
            return;
        }

        PublicadorEventoService service = new PublicadorEventoService();
        PublicadorEvento port = service.getPublicadorEventoPort();

        try {
            DtTipoRegistro tipoReg = port.consultaTipoRegistro(evento, edicion, tipoRegistro);
            if (tipoReg == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tipo de registro no encontrado");
                return;
            }
            req.setAttribute("tipoRegistro", tipoReg);
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo obtener el tipo de registro: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/tipoRegistro/ConsultaTipoRegistro.jsp").forward(req, resp);
    }
}