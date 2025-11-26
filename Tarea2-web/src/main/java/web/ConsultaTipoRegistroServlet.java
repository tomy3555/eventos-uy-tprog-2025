package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.datatypes.DTTipoRegistro;

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
    IControladorEvento controladorEv = fabrica.getInstance().getIControladorEvento();
    DTTipoRegistro tipoReg = controladorEv.consultaTipoRegistro(evento, edicion, tipoRegistro);
    if (tipoReg == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tipo de registro no encontrado");
      return;
    }
    req.setAttribute("tipoRegistro", tipoReg);
    req.getRequestDispatcher("/WEB-INF/tipoRegistro/ConsultaTipoRegistro.jsp").forward(req, resp);
  }
}