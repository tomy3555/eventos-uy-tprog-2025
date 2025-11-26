package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import publicadores.*;

@WebServlet(urlPatterns = { "/usuario/modificar" })
@MultipartConfig
public class ModificarDatosServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession sAux = request.getSession(false);
        String nick = (sAux != null) ? (String) sAux.getAttribute("nick") : null;
        if (isBlank(nick)) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        PublicadorUsuarioService service = new PublicadorUsuarioService();
        PublicadorUsuario port = service.getPublicadorUsuarioPort();

        List<String> alertsOk = new ArrayList<>();
        List<String> alertsWarn = new ArrayList<>();
        List<String> alertsErr = new ArrayList<>();

        DtDatosUsuario antes;
        try {
            antes = port.obtenerDatosUsuario(nick);
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo cargar el perfil actual.");
            request.getRequestDispatcher("/WEB-INF/usuario/ConsultaUsuario.jsp").forward(request, response);
            return;
        }

        String nombre = orEmpty(request.getParameter("nombre"));
        String apellido = orEmpty(request.getParameter("apellido"));
        String institucion = orEmpty(request.getParameter("institucion"));
        String descripcion = orEmpty(request.getParameter("descripcion"));
        String link = orEmpty(request.getParameter("link"));
        String password = orEmpty(request.getParameter("password"));
        String password2 = orEmpty(request.getParameter("password2"));
        String nacStr = orEmpty(request.getParameter("fechaNac"));

        request.setAttribute("form_nombre", nombre);
        request.setAttribute("form_apellido", apellido);
        request.setAttribute("form_institucion", institucion);
        request.setAttribute("form_descripcion", descripcion);
        request.setAttribute("form_link", link);
        request.setAttribute("form_fechaNac", nacStr);

        String fechaStr = "";
        if (!nacStr.isBlank()) {
            LocalDate hoy = LocalDate.now();
            try {
                LocalDate fnac = LocalDate.parse(nacStr);
                if (!fnac.isAfter(hoy) && !fnac.isBefore(hoy.minusYears(120))) fechaStr = nacStr;
                else alertsErr.add("Fecha de nacimiento inválida.");
            } catch (DateTimeParseException e) {
                alertsErr.add("Formato de fecha inválido.");
            }
        }

        boolean quiereCambiarPass = !password.isBlank() || !password2.isBlank();
        if (quiereCambiarPass) {
            if (password.isBlank() || password2.isBlank()) alertsErr.add("Debés completar ambas contraseñas.");
            else if (!password.equals(password2)) alertsErr.add("Las contraseñas no coinciden.");
            else if (password.length() < 6) alertsErr.add("La contraseña debe tener al menos 6 caracteres.");
        }

        String imgFileName = "";
        try {
            Part imgPart = request.getPart("imagen");
            if (imgPart != null && imgPart.getSize() > 0) {
                String ctype = imgPart.getContentType();
                if (ctype != null && ctype.toLowerCase().startsWith("image/")) {
                    String tomcatBase = System.getProperty("catalina.base");
                    Path basePath = Path.of(tomcatBase, "webapps", "ServidorCentral-0.0.1-SNAPSHOT", "images", "usuarios");
                    Files.createDirectories(basePath);

                    String safeNick = nick.replaceAll("[^a-zA-Z0-9_-]", "_");
                    String ext = getExtension(Path.of(imgPart.getSubmittedFileName()).getFileName().toString());
                    if (ext == null || ext.isBlank()) ext = guessExtensionFromContentType(ctype);
                    if (ext == null || ext.isBlank()) ext = ".jpg";

                    // si la imagen anterior no corresponde al nick, eliminarla
                    String actualImg = antes.getImagen();
                    if (actualImg != null && !actualImg.isBlank() && !actualImg.startsWith(safeNick)) {
                        Path anterior = basePath.resolve(actualImg);
                        try { Files.deleteIfExists(anterior); } catch (Exception ignored) {}
                    }

                    // siempre guardar nueva con el nombre del nick
                    String finalName = safeNick + ext;
                    Path destino = basePath.resolve(finalName);
                    Files.copy(imgPart.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                    imgFileName = finalName;
                }
            } else {
                // si no sube nueva, mantener la actual
                String actualImg = antes.getImagen();
                imgFileName = (actualImg != null) ? actualImg : "";
            }
        } catch (Exception e) {
            alertsErr.add("Error al guardar la imagen.");
        }

        if (!alertsErr.isEmpty()) {
            setAlertsAndForward(request, response, alertsOk, alertsWarn, alertsErr, antes);
            return;
        }

        boolean huboCambios;
        try {
            boolean cambioNombre = !eqSafe(antes.getNombre(), nombre);
            boolean cambioApellido = !eqSafe(nvl(antes.getApellido()), apellido);
            boolean cambioInstitucion = !eqSafe(nvl(antes.getNombreInstitucion()), institucion);
            boolean cambioDesc = !eqSafe(nvl(antes.getDesc()), descripcion);
            boolean cambioLink = !eqSafe(nvl(antes.getLink()), link);
            boolean cambioFecha = !eqSafe(dateToStr(antes.getFechaNac()), fechaStr);
            boolean cambioImg = !eqSafe(nvl(antes.getImagen()), imgFileName);

            huboCambios = cambioNombre || cambioApellido || cambioInstitucion ||
                    cambioDesc || cambioLink || cambioFecha || cambioImg;

            if (!huboCambios && !quiereCambiarPass) {
                alertsWarn.add("No realizaste cambios.");
                setAlertsAndForward(request, response, alertsOk, alertsWarn, alertsErr, antes);
                return;
            }

            port.modificarDatosUsuario(nick, nombre, descripcion, link, apellido, fechaStr, institucion, imgFileName);
            if (quiereCambiarPass) tryUpdatePasswordByReflection(port, nick, password);

            DtDatosUsuario despues = port.obtenerDatosUsuario(nick);
            request.setAttribute("usuario", despues);
            putAlerts(request, alertsOk, alertsWarn, alertsErr);
            request.getRequestDispatcher("/WEB-INF/usuario/ConsultaUsuario.jsp").forward(request, response);
        } catch (Exception e) {
            alertsErr.add("No se pudieron actualizar los datos.");
            setAlertsAndForward(request, response, alertsOk, alertsWarn, alertsErr, antes);
        }
    }

    private static void setAlertsAndForward(HttpServletRequest req, HttpServletResponse resp,
                                            List<String> ok, List<String> warn, List<String> err,
                                            DtDatosUsuario usuario)
            throws ServletException, IOException {
        putAlerts(req, ok, warn, err);
        if (req.getAttribute("usuario") == null && usuario != null) req.setAttribute("usuario", usuario);
        req.getRequestDispatcher("/WEB-INF/usuario/ConsultaUsuario.jsp").forward(req, resp);
    }

    private static void putAlerts(HttpServletRequest req, List<String> ok, List<String> warn, List<String> err) {
        if (ok != null && !ok.isEmpty()) req.setAttribute("alerts_ok", ok);
        if (warn != null && !warn.isEmpty()) req.setAttribute("alerts_warn", warn);
        if (err != null && !err.isEmpty()) req.setAttribute("alerts_err", err);
    }

    private static void tryUpdatePasswordByReflection(PublicadorUsuario port, String nick, String newPass) throws Exception {
        String[] nombres = { "modificarContrasenia", "modificarContrasena", "cambiarContrasenia", "cambiarContrasena" };
        for (String m : nombres) {
            try {
                port.getClass().getMethod(m, String.class, String.class).invoke(port, nick, newPass);
                return;
            } catch (NoSuchMethodException ignored) {}
        }
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
        if (ctype.contains("jpeg")) return ".jpg";
        if (ctype.contains("jpg")) return ".jpg";
        if (ctype.contains("png")) return ".png";
        if (ctype.contains("gif")) return ".gif";
        if (ctype.contains("webp")) return ".webp";
        return ".jpg";
    }

    private static String orEmpty(String s) { return (s == null) ? "" : s; }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String nvl(String s) { return (s == null) ? "" : s; }
    private static boolean eqSafe(String a, String b) { return Objects.equals(nvl(a), nvl(b)); }
    private static String dateToStr(Object f) { return (f == null) ? "" : f.toString(); }
}
