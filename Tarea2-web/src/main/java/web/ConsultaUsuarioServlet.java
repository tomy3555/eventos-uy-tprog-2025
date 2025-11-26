package web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import logica.fabrica;
import logica.datatypes.DTDatosUsuario;
import logica.datatypes.DTEvento;
import logica.interfaces.IControladorUsuario;
import logica.interfaces.IControladorEvento;
import excepciones.UsuarioNoExisteException;
import excepciones.UsuarioTipoIncorrectoException;

@WebServlet(urlPatterns = {
        "/usuario/ConsultaUsuario",
        "/usuario/modificar",
        "/usuario/seguir",
        "/usuario/dejarSeguir"
})
@MultipartConfig(
    fileSizeThreshold = 256 * 1024,
    maxFileSize       = 5L * 1024 * 1024,
    maxRequestSize    = 10L * 1024 * 1024
)
public class ConsultaUsuarioServlet extends HttpServlet {

    private IControladorUsuario cu() { return fabrica.getInstance().getIControladorUsuario(); }
    private IControladorEvento  ce() { return fabrica.getInstance().getIControladorEvento(); }

    // =====================================================
    // GET → Consulta de usuario o listado
    // =====================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        boolean forzarListado = isTrue(request.getParameter("listar")) ||
                "list".equalsIgnoreCase(trim(request.getParameter("view")));

        String nick = trim(request.getParameter("nick"));

        // Si no se fuerza listado, usar el de sesión
        if (!forzarListado && isBlank(nick)) {
            HttpSession sAux = request.getSession(false);
            if (sAux != null) {
                Object obj = sAux.getAttribute("nick");
                if (obj instanceof String) nick = (String) obj;
            }
        }

        IControladorUsuario ctrlUsuario = cu();

        if (forzarListado || isBlank(nick)) {
            // === LISTA DE USUARIOS ===
            Set<DTDatosUsuario> usuariosSet = new HashSet<>();

            try {
                usuariosSet = ctrlUsuario.obtenerUsuariosDT();
            } catch (UsuarioNoExisteException e) {
                e.printStackTrace();
                request.setAttribute("error", "No se pudo obtener la lista de usuarios.");
            }

            List<DTDatosUsuario> usuarios = new ArrayList<>(usuariosSet);
            request.setAttribute("usuarios", usuarios);

            Map<String, String> fotos = new HashMap<>();
            Map<String, String> nombres = new HashMap<>();

            String ctx = request.getContextPath();
            ServletContext sc = getServletContext();

            for (DTDatosUsuario u : usuarios) {
                if (u == null) continue;

                String nombreSeguro = nvl(u.getNombre(), u.getNickname());
                nombres.put(u.getNickname(), nombreSeguro);

                String url = resolveUserImageUrl(u.getImagen(), ctx, sc);
                if (url != null) fotos.put(u.getNickname(), url);
            }

            request.setAttribute("fotos", fotos);
            request.setAttribute("nombres", nombres);

            Map<String,String> instFotos = buildInstitutionImageMap(ctrlUsuario.getInstituciones(), request.getContextPath(), getServletContext());
            request.setAttribute("instFotos", instFotos);

        } else {
            // === PERFIL INDIVIDUAL ===
            try {
                DTDatosUsuario usuario = ctrlUsuario.obtenerDatosUsuario(nick);
                request.setAttribute("usuario", usuario);
                request.setAttribute("usuarioNombreSeguro",
                        nvl(usuario.getNombre(), usuario.getNickname()));

                // foto
                String url = resolveUserImageUrl(
                        usuario.getImagen(),
                        request.getContextPath(),
                        getServletContext()
                );
                if (url != null) request.setAttribute("usrImagenUrl", url);

                // mapa edicion -> evento
                List<DTEvento> eventos = ce().listarEventos();
                Map<String, String> edicionToEvento = new HashMap<>();
                if (eventos != null) {
                    for (DTEvento ev : eventos) {
                        if (ev == null || ev.getEdiciones() == null) continue;
                        for (String edNombre : ev.getEdiciones()) {
                            if (edNombre != null && !edNombre.isBlank()) {
                                edicionToEvento.put(edNombre, ev.getNombre());
                            }
                        }
                    }
                }
                request.setAttribute("edicionToEvento", edicionToEvento);

                // instituciones para el dropdown
                request.setAttribute("instituciones", ctrlUsuario.getInstituciones());

                // imágenes instituciones
                Map<String,String> instFotos = buildInstitutionImageMap(ctrlUsuario.getInstituciones(), request.getContextPath(), getServletContext());
                request.setAttribute("instFotos", instFotos);

                // === Rol real del perfil consultado (organizador/asistente)
                boolean esPerfilOrganizador = (usuario.getDesc() != null) || (usuario.getLink() != null);
                request.setAttribute("esPerfilOrganizador", esPerfilOrganizador);

                // === Follow/unfollow flags
                String nickSesion = nickEnSesion(request);
                boolean esSuPropioPerfil = nickSesion != null && nickSesion.equals(usuario.getNickname());
                request.setAttribute("esSuPropioPerfil", esSuPropioPerfil);

                boolean yaLoSigo = false;
                if (!esSuPropioPerfil && nickSesion != null) {
                    try {
                        yaLoSigo = ctrlUsuario.sigueA(nickSesion, usuario.getNickname()); // implementar en lógica
                    } catch (Exception ignore) {}
                }
                request.setAttribute("yaLoSigo", yaLoSigo);

            } catch (UsuarioNoExisteException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "El usuario \"" + nick + "\" no existe.");
            }
        }

        request.getRequestDispatcher("/WEB-INF/usuario/ConsultaUsuario.jsp")
                .forward(request, response);
    }

    // =====================================================
    // POST → Modificación de usuario / seguir / dejar de seguir
    // =====================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if ("/usuario/modificar".equals(path)) {
            // === Tu lógica de modificar (con imagen) ya implementada previamente ===
            IControladorUsuario ctrlUsuario = cu();

            HttpSession sAux = request.getSession(false);
            String nick = (sAux != null) ? (String) sAux.getAttribute("nick") : null;

            if (nick == null || nick.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            // Texto
            String nombre      = request.getParameter("nombre");
            String apellido    = request.getParameter("apellido");
            String institucion = request.getParameter("institucion");
            String descripcion = request.getParameter("descripcion");
            String password    = request.getParameter("password");
            String link        = request.getParameter("link");
            String nacStr      = request.getParameter("fechaNac");

            LocalDate fechaNac = null;
            if (nacStr != null && !nacStr.isBlank()) {
                try { fechaNac = LocalDate.parse(nacStr); } catch (Exception ignored) {}
            }

            // Imagen opcional
            String imagenRelGuardada = null;
            Part imagenPart = null;
            try { imagenPart = request.getPart("imagen"); } catch (IllegalStateException ise) { /* > max size */ }

            if (imagenPart != null && imagenPart.getSize() > 0) {
                String contentType = imagenPart.getContentType();
                if (contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
                    String ext = guessExt(contentType);
                    if (ext == null) ext = ".png";
                    String base = nick.replaceAll("[^a-zA-Z0-9_-]", "_");
                    String fileName = base + "_" + System.currentTimeMillis() + ext;

                    ServletContext sc = getServletContext();
                    String relDir = "/img/usuarios";
                    String absDir = sc.getRealPath(relDir);
                    if (absDir == null) {
                        relDir = "/img";
                        absDir = sc.getRealPath(relDir);
                    }
                    if (absDir != null) {
                        Path dir = Path.of(absDir);
                        try { Files.createDirectories(dir); } catch (Exception ignored) {}
                        Path destino = dir.resolve(fileName);
                        try (var in = imagenPart.getInputStream()) {
                            Files.copy(in, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                        imagenRelGuardada = relDir + "/" + fileName;
                    }
                }
            }

            try {
                // Nueva sobrecarga con imagen
                ctrlUsuario.modificarDatosUsuario(
                        nick, nombre, descripcion, link, apellido, fechaNac, institucion, imagenRelGuardada
                );
                if (password != null && !password.isBlank()) {
                    ctrlUsuario.modificarContrasenia(nick, password);
                }

                response.sendRedirect(request.getContextPath() + "/usuario/ConsultaUsuario?nick=" +
                        enc(nick));
                return;

            } catch (UsuarioNoExisteException | UsuarioTipoIncorrectoException e) {
                request.setAttribute("error", e.getMessage());
                doGet(request, response);
                return;
            }
        }

        // === Seguir / Dejar de seguir (SIN AJAX) ===
        if ("/usuario/seguir".equals(path) || "/usuario/dejarSeguir".equals(path)) {
            IControladorUsuario ctrlUsuario = cu();

            String nickSesion = nickEnSesion(request);
            if (nickSesion == null || nickSesion.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            String objetivo = trim(request.getParameter("a")); // nick del perfil objetivo
            if (isBlank(objetivo)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro 'a'.");
                return;
            }

            if (!nickSesion.equalsIgnoreCase(objetivo)) {
                try {
                    if ("/usuario/seguir".equals(path)) {
                        ctrlUsuario.seguirUsuario(nickSesion, objetivo);       // implementar
                    } else {
                        ctrlUsuario.dejarSeguirUsuario(nickSesion, objetivo);  // implementar
                    }
                } catch (Exception ignore) {}
            }

            response.sendRedirect(request.getContextPath() + "/usuario/ConsultaUsuario?nick=" + enc(objetivo));
            return;
        }

        // fallback
        doGet(request, response);
    }

    // ==== Helpers ====

    private String nickEnSesion(HttpServletRequest req) {
        HttpSession sAux = req.getSession(false);
        return sAux == null ? null : (String) sAux.getAttribute("nick");
    }

    private Map<String,String> buildInstitutionImageMap(Set<String> instituciones, String ctx, ServletContext sc) {
        Map<String,String> map = new HashMap<>();
        if (instituciones == null || instituciones.isEmpty()) return map;
        String[] exts = new String[]{".png",".jpg",".jpeg",".webp",".gif"};
        for (String inst : instituciones) {
            if (inst == null || inst.isBlank()) continue;
            String safe = inst.replaceAll("[^a-zA-Z0-9]", "_");
            List<String> candidates = new ArrayList<>();
            for (String ext: exts) {
                candidates.add("/img/instituciones/" + safe + ext);
                candidates.add("/img/instituciones/" + inst + ext);
                candidates.add("/img/" + safe + ext);
                candidates.add("/img/" + inst + ext);
            }
            candidates.add("/img/instituciones/" + safe);
            candidates.add("/img/instituciones/" + inst);
            for (String candRel : candidates) {
                Boolean ex = exists(getServletContext(), candRel);
                if (Boolean.TRUE.equals(ex)) {
                    map.put(inst, ctx + candRel);
                    break;
                }
            }
        }
        return map;
    }

    private static String trim(String s) { return (s == null) ? null : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String nvl(String s, String alt) { return (s == null || s.trim().isEmpty()) ? alt : s; }
    private static boolean isTrue(String v) {
        if (v == null) return false;
        String s = v.trim().toLowerCase(Locale.ROOT);
        return "1".equals(s) || "true".equals(s) || "on".equals(s) || "yes".equals(s) || "y".equals(s);
    }

    private static String resolveUserImageUrl(String raw, String ctx, ServletContext sc) {
        if (raw == null || raw.isBlank()) return null;

        String v = raw.trim().replace("\\", "/");
        String low = v.toLowerCase(Locale.ROOT);

        if (low.startsWith("http://") || low.startsWith("https://")) return v;
        if (v.startsWith("/")) return ctx + v;
        if (low.startsWith("img/")) return ctx + "/" + v;
        if (low.startsWith("usuarios/")) return ctx + "/img/" + v;

        String relImg = "/img/" + v;
        String relUsr = "/img/usuarios/" + v;

        Boolean exImg = exists(sc, relImg);
        Boolean exUsr = exists(sc, relUsr);

        if (exImg != null || exUsr != null) {
            if (Boolean.TRUE.equals(exImg)) return ctx + relImg;
            if (Boolean.TRUE.equals(exUsr)) return ctx + relUsr;
            return null;
        }
        return ctx + relImg;
    }

    private static Boolean exists(ServletContext sc, String rel) {
        String abs = sc.getRealPath(rel);
        if (abs == null) return null;
        return Files.exists(Path.of(abs));
    }

    private static String guessExt(String contentType) {
        String ct = (contentType == null) ? "" : contentType.toLowerCase(Locale.ROOT);
        if (ct.contains("png")) return ".png";
        if (ct.contains("jpeg") || ct.contains("jpg")) return ".jpg";
        if (ct.contains("webp")) return ".webp";
        if (ct.contains("gif")) return ".gif";
        return null;
    }

    private static String enc(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
