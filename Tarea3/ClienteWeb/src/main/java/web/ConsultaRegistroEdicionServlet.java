package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import publicadores.DtDatosUsuario;
import publicadores.DtEdicion;
import publicadores.DtRegistro;
import publicadores.DtEvento;
import publicadores.PublicadorUsuario;
import publicadores.PublicadorUsuarioService;
import publicadores.PublicadorEvento;
import publicadores.PublicadorEventoService;
import publicadores.UsuarioNoExisteException_Exception;

@WebServlet("/registro/ConsultaRegistroEdicion")
public class ConsultaRegistroEdicionServlet extends HttpServlet {

    private static final String JSP_CONSULTA = "/WEB-INF/registro/ConsultaRegistroEdicion.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String nickSesion = (session != null) ? (String) session.getAttribute("nick") : null;
        String rol = (session != null) ? (String) session.getAttribute("rol") : null;

        String idRegistro = req.getParameter("idRegistro");
        String accion = req.getParameter("accion");
        String nombreEvento = req.getParameter("evento");
        String nombreEdicion = req.getParameter("edicion");

        if (idRegistro == null || idRegistro.isBlank() || nickSesion == null) {
            req.setAttribute("error", "Registro no especificado o sesión no iniciada.");
            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
            return;
        }

        PublicadorUsuarioService usSvc = new PublicadorUsuarioService();
        PublicadorUsuario usPort = usSvc.getPublicadorUsuarioPort();

        PublicadorEventoService evSvc = new PublicadorEventoService();
        PublicadorEvento evPort = evSvc.getPublicadorEventoPort();

        try {
            DtDatosUsuario dtoUsuario = (DtDatosUsuario) session.getAttribute("usuario_logueado");
            if (dtoUsuario == null) {
                dtoUsuario = usPort.obtenerDatosUsuario(nickSesion);
            }

            DtRegistro dtRegistro = null;

            if ("organizador".equalsIgnoreCase(rol) && nombreEvento != null && nombreEdicion != null) {
                DtEdicion ed = evPort.obtenerDtEdicion(nombreEvento, nombreEdicion);
                if (ed != null && ed.getRegistros() != null && ed.getRegistros().getRegistro() != null) {
                    for (DtRegistro r : ed.getRegistros().getRegistro()) {
                        if (idRegistro.equals(r.getIdentificador())) {
                            dtRegistro = r;
                            break;
                        }
                    }
                }
            } else {
                dtRegistro = evPort.consultaRegistro(nickSesion, idRegistro);
            }

            if (dtRegistro == null) {
                req.setAttribute("error", "No se encontró el registro solicitado.");
                req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
                return;
            }

            // PASAR ASISTENCIAS AL JSP (igual que en mobile)
            java.util.List<DtRegistro> asistencias = dtoUsuario.getAsistencias() != null ? dtoUsuario.getAsistencias().getAsistencia() : java.util.List.of();
            req.setAttribute("asistencias", asistencias);

            boolean asistio = false;
            try {
                asistio = dtRegistro.isAsistio();
            } catch (Exception ignore) {}

            if ("true".equals(req.getParameter("marcoAsistencia"))) {
                DtRegistro refreshed = evPort.consultaRegistro(nickSesion, idRegistro);
                if (refreshed != null) {
                    dtRegistro = refreshed;
                    try {
                        asistio = refreshed.isAsistio();
                    } catch (Exception ignore) {}
                }
            }

            if ("certificado".equalsIgnoreCase(accion)) {
                DtEvento ev = evPort.consultaDTEvento(dtRegistro.getEvento());
                DtEdicion ed = evPort.obtenerDtEdicion(ev.getNombre(), dtRegistro.getEdicion());
                generarCertificadoPDF(resp, dtoUsuario, dtRegistro, ev, ed);
                return;
            }

            req.setAttribute("usuario", dtoUsuario);
            req.setAttribute("registro", dtRegistro);
            req.setAttribute("asistio", asistio);
            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);

        } catch (UsuarioNoExisteException_Exception e) {
            req.setAttribute("error", "No se pudo encontrar el usuario logueado.");
            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error al consultar el registro: " + e.getMessage());
            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
        }
    }


    private void generarCertificadoPDF(HttpServletResponse resp, DtDatosUsuario usr,
                                       DtRegistro reg, DtEvento ev, DtEdicion ed)
            throws IOException, DocumentException {

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition",
                "attachment; filename=Certificado_" + usr.getNickname() + ".pdf");

        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, resp.getOutputStream());
        doc.open();

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font textoFont = new Font(Font.FontFamily.HELVETICA, 12);

        Paragraph titulo = new Paragraph("Certificado de Asistencia", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(25);
        doc.add(titulo);

        doc.add(new Paragraph("Se certifica que:", textoFont));
        doc.add(new Paragraph(" ", textoFont));
        doc.add(new Paragraph("    " + usr.getNombre() + " " + usr.getApellido(),
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        doc.add(new Paragraph(" ", textoFont));
        doc.add(new Paragraph("Ha asistido al evento: " + ev.getNombre(), textoFont));
        doc.add(new Paragraph("Edición: " + reg.getEdicion(), textoFont));
        doc.add(new Paragraph("Ciudad: " + ed.getCiudad(), textoFont));
        doc.add(new Paragraph("Fecha: " + reg.getFechaInicio(), textoFont));
        doc.add(new Paragraph(" ", textoFont));
        doc.add(new Paragraph("______________________________", textoFont));
        doc.add(new Paragraph("Firma del Organizador", textoFont));

        doc.close();
    }
}