package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.datatypes.DTCategorias;
import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import excepciones.EventoYaExisteException;

@WebServlet("/evento/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class EventoServlet extends HttpServlet {

    private static final String JSP_ALTA = "/WEB-INF/evento/alta.jsp";
    private static final String JSP_CONSULTA = "/WEB-INF/evento/ConsultaEvento.jsp";
    private static final String JSP_REGISTRO = "/WEB-INF/evento/RegistrarseEvento.jsp";
    private static final String JSP_LISTAR = "/WEB-INF/evento/listado.jsp";

    // carpeta pública donde se guardan subidas
    private static final String UPLOAD_PUBLIC_DIR = "/img/eventos";

    private final IControladorEvento controladorEv = fabrica.getInstance().getIControladorEvento();
    private String ctx(HttpServletRequest req) { return req.getContextPath(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();

        if (path == null || "/".equals(path) || "/ConsultaEvento".equals(path)) {
            String nombre = trim(req.getParameter("nombre"));
            if (isBlank(nombre)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro 'nombre'");
                return;
            }

            DTEvento ev = controladorEv.consultaDTEvento(nombre);
            if (ev == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento no encontrado: " + nombre);
                return;
            }

            req.setAttribute("evento", ev);
            req.setAttribute("evNombre", ev.getNombre());
            req.setAttribute("evSigla", ev.getSigla());
            req.setAttribute("evDesc", ev.getDescripcion());
            req.setAttribute("evFecha", formatFecha(ev.getFecha()));
            req.setAttribute("evCategorias", ev.getCategorias());
            req.setAttribute("rol", getRol(req));
            System.out.println("Entra a EventoServelt");
   

            // URL de imagen 
            String evImagenUrl = resolveImagenUrl(req, ev);
            req.setAttribute("evImagenUrl", evImagenUrl);

            // EDICIONES 
            List<String> claves = controladorEv.listarEdicionesEvento(nombre); // puede ser nombres o siglas
            List<DTEdicion> ediciones = new ArrayList<>();
            String siglaEvento = ev.getSigla();

            for (String clave : claves) {
                DTEdicion dtEd = null;

                // Intento por SIGLA 
                try {
                    dtEd = controladorEv.consultaEdicionEvento(siglaEvento, clave);
                } catch (Exception ignore) {}

                //  si no vino nada, pruebo por NOMBRE de edición 
                if (dtEd == null) {
                    try {
                        dtEd = controladorEv.obtenerDtEdicion(nombre, clave);
                    } catch (Exception ignore) {}
                }

                // filtrado por estado aceptado 
                if (dtEd != null && esAceptada(dtEd.getEstado())) {
                    ediciones.add(dtEd);
                }
            }
            req.setAttribute("evEdiciones", ediciones);

            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
            return;
        }

        switch (path) {
        case "/alta":
            if (!requiereOrganizador(req, resp)) return;
            List<DTCategorias> dtCategorias = controladorEv.listarDTCategorias();
            req.setAttribute("dtCategorias", dtCategorias);
            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            return;
            case "/RegistrarseEvento":
                if (!requiereLogin(req, resp)) return;
                req.getRequestDispatcher(JSP_REGISTRO).forward(req, resp);
                return;
            case "/listado":
                List<DTEvento> lista;
                String cat = trim(req.getParameter("categoria"));
                if (!isBlank(cat)) {
                    lista = controladorEv.listarEventosPorCategoria(cat);
                    req.setAttribute("categoriaSeleccionada", cat);
                } else {
                    lista = controladorEv.listarEventos();
                }
                req.setAttribute("lista", lista);
                req.setAttribute("categorias", controladorEv.listarCategoriasConEventos());
                req.getRequestDispatcher(JSP_LISTAR).forward(req, resp);
                return;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                

        }
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
            String sigla = trim(req.getParameter("sigla"));
            String cats = trim(req.getParameter("categorias"));

            List<String> categoriasList = new ArrayList<>();
            if (!isBlank(cats)) {
                for (String cAux : cats.split(",")) {
                    String tAux = cAux.trim();
                    if (!tAux.isEmpty()) categoriasList.add(tAux);
                }
            }
            if (categoriasList.isEmpty()) {
                req.setAttribute("error", "Debe asociar al menos una categoría al evento");
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }

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
                        if (root != null) baseImg = Path.of(root, "img", "eventos").toString();
                    }
                    if (baseImg != null) {
                        Files.createDirectories(Path.of(baseImg));
                        String original = getSafeFilename(imgPart);
                        String ext = getExtension(original);
                        if (isBlank(ext)) ext = guessExtensionFromContentType(ctype);
                        if (isBlank(ext)) ext = ".jpg";
                        String finalName = (isBlank(sigla) ? "evento" : sigla) + ext;
                        Path destino = Path.of(baseImg, finalName);
                        imgPart.write(destino.toAbsolutePath().toString());
                        imagenFileName = finalName;
                        System.out.println("[IMG] Guardada en: " + destino + " | URL: " + ctx(req) + UPLOAD_PUBLIC_DIR + "/" + finalName);
                    } else {
                        System.err.println("WARN: No se pudo resolver ruta física para " + UPLOAD_PUBLIC_DIR);
                    }
                }
            } catch (Exception fileEx) {
                req.setAttribute("error", "Error al procesar la imagen: " + fileEx.getMessage());
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }

            DTCategorias dtCategorias = new DTCategorias(categoriasList);
            try {
                controladorEv.altaEvento(nombre, desc, LocalDate.now(), sigla, dtCategorias, sigla);

                if (imagenFileName != null) {
                    try { controladorEv.actualizarImagenEvento(nombre, imagenFileName); }
                    catch (IllegalArgumentException ex) { System.err.println("No se pudo asociar imagen al evento: " + ex.getMessage()); }
                }

                String nombreEnc = URLEncoder.encode(nombre, StandardCharsets.UTF_8.name());
                resp.sendRedirect(ctx(req) + "/evento/ConsultaEvento?nombre=" + nombreEnc);
            } catch (EventoYaExisteException e) {
                req.setAttribute("error", "duplicado");
                req.setAttribute("nombreEventoDuplicado", nombre);
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            }
            return;
        }

        if ("/RegistrarseEvento".equals(path)) {
            if (!requiereLogin(req, resp)) return;
            String nombre = trim(req.getParameter("nombre"));
            if (isBlank(nombre)) {
                req.setAttribute("error", "Falta parámetro 'nombre' para registrarse.");
                req.getRequestDispatcher(JSP_REGISTRO).forward(req, resp);
                return;
            }
            String nombreEnc = URLEncoder.encode(nombre, StandardCharsets.UTF_8.name());
            resp.sendRedirect(ctx(req) + "/evento/ConsultaEvento?nombre=" + nombreEnc);
            return;
        }
        
        if ("/FinalizarEvento".equals(path)) {
        	String nombreEvento = trim(req.getParameter("nombreEvento"));
        	System.out.println("Finalizando evento en EventoServlet: " + nombreEvento);
        	controladorEv.finalizarEvento(nombreEvento);

			resp.sendRedirect(ctx(req) + "/inicio");
			return;
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

    private boolean requiereLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sAux = req.getSession(false);
        if (sAux == null || sAux.getAttribute("nick") == null) {
            resp.sendRedirect(ctx(req) + "/auth/login");
            return false;
        }
        return true;
    }

    private static String trim(String sAux){ return sAux == null ? null : sAux.trim(); }
    private static boolean isBlank(String sAux){ return sAux == null || sAux.trim().isEmpty(); }

    private String formatFecha(Object fAux) {
        if (fAux == null) return null;
        try {
            if (fAux instanceof java.time.LocalDate ident) return ident.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (fAux instanceof java.time.LocalDateTime ident2) return ident2.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            if (fAux instanceof java.util.Date dateAux) return new java.text.SimpleDateFormat("dd/MM/yyyy").format(dateAux);
        } catch (Exception ignore) {}
        return fAux.toString();
    }

    private String resolveImagenUrl(HttpServletRequest req, DTEvento ev) {
        String ctx = ctx(req);
        String raw = null;
        try { raw = ev.getImagen(); } catch (Exception ignore) {}

        String url = null;
        if (raw != null && !raw.isBlank()) {
            String lower = raw.toLowerCase();
            if (lower.startsWith("http://") || lower.startsWith("https://")) {
                url = raw; 
            } else if (raw.startsWith("/")) {
                if (raw.startsWith(ctx + "/")) {
                    url = raw;
                } else {
                    url = ctx + raw; 
                }
            } else {
                String[] candidates = new String[] {
                    "/img/" + raw,
                    "/img/eventos/" + raw,
                    "/eventos/" + raw
                };
                for (String rel : candidates) {
                    String abs = getServletContext().getRealPath(rel);
                    boolean exists;
                    if (abs != null) {
                        exists = Files.exists(Path.of(abs));
                    } else {
                        exists = true; 
                    }
                    if (exists) {
                        url = ctx + rel;
                        break;
                    }
                }
            }
        }
        if (url == null) url = ctx + "/img/evento-default.jpg";
        return url;
    }

    private static boolean esAceptada(Object estado) {
        if (estado == null) return false;
        // Soporta enum.name() = "ACEPTADA" o toString() = "Aceptada", etc.
        String s = String.valueOf(estado);
        return "ACEPTADA".equalsIgnoreCase(s);
    }

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
    private String getRol(HttpServletRequest req) {
        HttpSession sAux = req.getSession(false);
        if (sAux == null) return null;
        Object rol = sAux.getAttribute("rol");
        return (rol != null) ? rol.toString() : null;
    }

}
