package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import publicadores.PublicadorUsuarioService;
import publicadores.PublicadorUsuario;
import publicadores.InstitucionYaExisteException_Exception;

@WebServlet("/institucion/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class InstitucionServlet extends HttpServlet {
    private static final String JSP_ALTA = "/WEB-INF/institucion/altaInstitucion.jsp";
    private static final String UPLOAD_PUBLIC_DIR = "/img/instituciones";

    private String ctx(HttpServletRequest req) { return req.getContextPath(); }

    private PublicadorUsuario obtenerPort() {
        PublicadorUsuarioService svc = new PublicadorUsuarioService();
        return svc.getPublicadorUsuarioPort();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if ("/alta".equals(path)) {
            if (!requiereOrganizador(req, resp)) return;
            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        String accion = req.getParameter("accion");
        if ("cancelar".equalsIgnoreCase(accion)) {
            resp.sendRedirect(ctx(req) + "/inicio");
            return;
        }
        if ("/alta".equals(path)) {
            if (!requiereOrganizador(req, resp)) return;
            String nombre = trim(req.getParameter("nombre"));
            String desc = trim(req.getParameter("desc"));
            String web = trim(req.getParameter("web"));
            String imagenFileName = null;
            try {
                Part imgPart = null;
                try { imgPart = req.getPart("imagen"); } catch (IllegalStateException ise) { imgPart = null; }
                if (imgPart != null && imgPart.getSize() > 0) {
                    String ctype = imgPart.getContentType();
                    if (ctype == null || !ctype.toLowerCase().startsWith("image/")) {
                        req.setAttribute("error", "El archivo subido no es una imagen válida.");
                        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                        return;
                    }
                    String baseImg = getServletContext().getRealPath(UPLOAD_PUBLIC_DIR);
                    if (baseImg == null) {
                        String root = getServletContext().getRealPath("/");
                        if (root != null) baseImg = Path.of(root, "img", "instituciones").toString();
                    }
                    if (baseImg != null) {
                        Files.createDirectories(Path.of(baseImg));
                        String original = getSafeFilename(imgPart);
                        String ext = getExtension(original);
                        if (isBlank(ext)) ext = guessExtensionFromContentType(ctype);
                        if (isBlank(ext)) ext = ".jpg";
                        String finalName = (isBlank(nombre) ? "institucion" : nombre.replaceAll("[^a-zA-Z0-9]", "_")) + ext;
                        Path destino = Path.of(baseImg, finalName);
                        imgPart.write(destino.toAbsolutePath().toString());
                        imagenFileName = finalName;
                    }
                }
            } catch (Exception fileEx) {
                req.setAttribute("error", "Error al procesar la imagen: " + fileEx.getMessage());
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }
            // Call publicador
            PublicadorUsuario port = obtenerPort();
            try {
                port.altaInstitucion(nombre, desc, web);
                // image file saved locally; publicador doesn't expose image attach in client stub
                resp.sendRedirect(ctx(req) + "/inicio");
                return;
            } catch (InstitucionYaExisteException_Exception ex) {
                req.setAttribute("error", "duplicado");
                req.setAttribute("nombreInstitucionDuplicado", nombre);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            } catch (Exception e) {
                req.setAttribute("error", "Error inesperado: " + e.getMessage());
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // Helpers
    private boolean requiereOrganizador(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sAux = req.getSession(false);
        String rol = sAux == null ? null : (String) sAux.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            resp.sendRedirect(ctx(req) + "/auth/login");
            return false;
        }
        return true;
    }
    private static String trim(String sAux){ return sAux == null ? null : sAux.trim(); }
    private static boolean isBlank(String sAux){ return sAux == null || sAux.trim().isEmpty(); }
    private static String getSafeFilename(Part partAux) {
        String name = partAux.getSubmittedFileName();
        if (name == null) return "archivo";
        name = name.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        return (slash >= 0) ? name.substring(slash + 1) : name;
    }
    private static String getExtension(String filename) {
        if (filename == null) return null;
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return null;
        return filename.substring(dot).toLowerCase();
    }
    private static String guessExtensionFromContentType(String ctype) {
        if (ctype == null) return null;
        ctype = ctype.toLowerCase();
        if (ctype.contains("jpeg") || ctype.contains("jpg")) return ".jpg";
        if (ctype.contains("png")) return ".png";
        if (ctype.contains("gif")) return ".gif";
        if (ctype.contains("webp")) return ".webp";
        return null;
    }
}