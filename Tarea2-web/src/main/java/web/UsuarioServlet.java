package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.Set;

import logica.fabrica;
import logica.interfaces.IControladorUsuario;
import logica.datatypes.DTDatosUsuario;
import excepciones.UsuarioYaExisteException;
import excepciones.UsuarioNoExisteException;

@WebServlet(urlPatterns = {"/usuario/AltaUsuario", "/usuario/validar"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // 1 MB en memoria
    maxFileSize = 10 * 1024 * 1024,    // 10 MB por archivo
    maxRequestSize = 20 * 1024 * 1024  // 20 MB total
)
public class UsuarioServlet extends HttpServlet {

  private static final String JSP_LOGIN = "/WEB-INF/auth/login.jsp";
  private static final String JSP_ALTA  = "/WEB-INF/usuario/AltaUsuario.jsp";

  // carpeta relativa (servida por el app) para imágenes de usuarios
  private static final String IMG_REL_BASE_USR = "/img/usuarios";

  private final IControladorUsuario controladorUs = fabrica.getInstance().getIControladorUsuario();

  private String ctx(HttpServletRequest req) { return req.getContextPath(); }

  private void cargarInstituciones(HttpServletRequest req) {
    java.util.Collection<String> instituciones = controladorUs.getInstituciones();
    req.setAttribute("instituciones", instituciones);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String path = req.getServletPath();
    if (path == null) {
      resp.sendRedirect(ctx(req) + "/");
      return;
    }

    // NUEVO: endpoint de validación en vivo
    if ("/usuario/validar".equals(path)) {
      handleValidar(req, resp);
      return;
    }

    if ("/usuario/AltaUsuario".equals(path)) {
      cargarInstituciones(req);
      req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
      return;
    }

    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String path = req.getServletPath();
    if (!"/usuario/AltaUsuario".equals(path)) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // === manejo de imagen (opcional) ===
    Part imagenPart = null;
    try { imagenPart = req.getPart("imagen"); } catch (Exception ignore) {}
    String nombreArchivo = null;

    if (imagenPart != null && imagenPart.getSize() > 0) {
      String ctype = imagenPart.getContentType();
      if (ctype == null || !ctype.toLowerCase().startsWith("image/")) {
        req.setAttribute("error", "El archivo subido no es una imagen válida.");
        cargarInstituciones(req);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        return;
      }

      // Ruta física dentro del webapp (requiere deploy “exploded”)
      String baseImg = getServletContext().getRealPath(IMG_REL_BASE_USR);
      if (baseImg == null) {
        String root = getServletContext().getRealPath("/");
        if (root != null) baseImg = Path.of(root, "img", "usuarios").toString();
      }
      if (baseImg == null) {
        throw new ServletException("No se pudo resolver la ruta física de /img/usuarios.");
      }

      Files.createDirectories(Path.of(baseImg));

      String nickParam = req.getParameter("nick");
      String original = getSafeFilename(imagenPart);
      String ext = getExtension(original);
      if (ext == null || ext.isBlank()) ext = guessExtensionFromContentType(ctype);
      if (ext == null || ext.isBlank()) ext = ".jpg";

      String finalName = (nickParam == null || nickParam.isBlank() ? "avatar" : nickParam) + ext;
      Path destino = Path.of(baseImg, finalName);

      try (var in = imagenPart.getInputStream()) {
        Files.copy(in, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      }

      nombreArchivo = finalName; // se guarda en BD/modelo (tu controlador lo recibe)
      System.out.println("✅ Imagen guardada: " + destino.toAbsolutePath()
          + " | URL: " + ctx(req) + IMG_REL_BASE_USR + "/" + finalName);
    }

    // === leer params ===
    String rol          = req.getParameter("rol");
    String nick         = req.getParameter("nick");
    String nombre       = req.getParameter("nombreA");
    String pass1        = req.getParameter("pass");
    String pass2        = req.getParameter("pass2");
    String organizacion = req.getParameter("nombreO");
    String apellido     = req.getParameter("apellidoA");
    String correo       = req.getParameter("email");
    String descripcion  = req.getParameter("descO");
    String link         = req.getParameter("webO");
    String institucion  = req.getParameter("instIdA");
    String nacStr       = req.getParameter("nacA");

    // validaciones básicas
    if (pass1 == null || pass2 == null || pass1.isBlank() || pass2.isBlank() || !pass1.equals(pass2)) {
      req.setAttribute("error", "Las contraseñas no coinciden o están vacías.");
      cargarInstituciones(req);
      forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
      return;
    }
    if (nick == null || correo == null || rol == null || nick.isBlank() || correo.isBlank()) {
      req.setAttribute("error", "Faltan datos obligatorios.");
      cargarInstituciones(req);
      forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
      return;
    }

    // validación específica por rol
    LocalDate fechaNac = null;
    boolean esOrganizador = "ORGANIZADOR".equalsIgnoreCase(rol);

    if (!esOrganizador) {
      if (nacStr == null || nacStr.isBlank()) {
        req.setAttribute("error", "Debe ingresar una fecha de nacimiento.");
        cargarInstituciones(req);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
      try {
        fechaNac = LocalDate.parse(nacStr);
        if (fechaNac.isAfter(LocalDate.now())) throw new IllegalArgumentException();
      } catch (Exception e) {
        req.setAttribute("error", "Formato de fecha inválido o futura.");
        cargarInstituciones(req);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
    } else {
      if (organizacion == null || organizacion.isBlank() || descripcion == null || descripcion.isBlank()) {
        req.setAttribute("error", "Debe completar los campos obligatorios del organizador.");
        cargarInstituciones(req);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
    }

    // nombre final para alta (org usa 'organizacion', asistente usa 'nombre')
    String nombreFinal = esOrganizador ? organizacion : nombre;

    try {
      // Alta (la firma incluye imagen como último parámetro)
    	
    	if (existeNickDT(nick)) { req.setAttribute("error","El nick ya existe."); cargarInstituciones(req); forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr); return; }
    	if (existeEmailDT(correo)) { req.setAttribute("error","El email ya existe."); cargarInstituciones(req); forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr); return; }
      controladorUs.altaUsuario(
          nick,
          nombreFinal,
          correo,
          descripcion,
          link,
          apellido,
          fechaNac,
          institucion,
          esOrganizador,
          pass1,
          nombreArchivo
      );

      // login directo post-alta
      HttpSession sAux = req.getSession(true);
      DTDatosUsuario usuarioLogueado;
      try {
        usuarioLogueado = controladorUs.obtenerDatosUsuario(nick);
      } catch (UsuarioNoExisteException e) {
        req.setAttribute("error", "No se pudo encontrar el usuario recién creado.");
        cargarInstituciones(req);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
      sAux.setAttribute("usuario_logueado", usuarioLogueado);
      sAux.setAttribute("nick", nick);
      sAux.setAttribute("rol", esOrganizador ? "ORGANIZADOR" : "ASISTENTE");
      sAux.setAttribute("estado_sesion", "LOGIN_CORRECTO");

      // debug (opcional)
      Enumeration<String> names = sAux.getAttributeNames();
      while (names.hasMoreElements()) {
        String nAux = names.nextElement();
        Object vAux = sAux.getAttribute(nAux);
        System.out.println("   * " + nAux + " = " + vAux);
      }

      resp.sendRedirect(ctx(req) + "/inicio");

    } catch (UsuarioYaExisteException e) {
      req.setAttribute("error", e.getMessage());
      cargarInstituciones(req);
      forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
    }
  }

  private void forwardConDatos(HttpServletRequest req, HttpServletResponse resp,
                               String rol, String nick, String nombre, String apellido,
                               String correo, String descripcion, String link,
                               String institucion, String nacStr)
      throws ServletException, IOException {
    req.setAttribute("rol", rol);
    req.setAttribute("nick", nick);
    req.setAttribute("nombreA", nombre);
    req.setAttribute("apellidoA", apellido);
    req.setAttribute("email", correo);
    req.setAttribute("descripcion", descripcion);
    req.setAttribute("link", link);
    req.setAttribute("instIdA", institucion);
    req.setAttribute("nacA", nacStr);
    req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
  }

  // ==== helpers de archivo ====
  private static String getSafeFilename(Part p) {
    String name = p.getSubmittedFileName();
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
    if (ctype.contains("jpeg")) return ".jpg";
    if (ctype.contains("jpg"))  return ".jpg";
    if (ctype.contains("png"))  return ".png";
    if (ctype.contains("gif"))  return ".gif";
    if (ctype.contains("webp")) return ".webp";
    return ".jpg";
  }
  
  private boolean existeNickDT(String nick) {
	  try {
	    Set<DTDatosUsuario> usuarios = controladorUs.obtenerUsuariosDT();
	    if (usuarios == null) return false;
	    for (logica.datatypes.DTDatosUsuario u : usuarios) {
	      if (u != null && u.getNickname() != null && u.getNickname().equals(nick)) return true;
	    }
	  } catch (excepciones.UsuarioNoExisteException ignore) { /* según tu impl puede lanzar o no */ }
	  return false;
	}

	private boolean existeEmailDT(String email) {
	  try {
	    Set<DTDatosUsuario> usuarios = controladorUs.obtenerUsuariosDT();
	    if (usuarios == null) return false;
	    for (DTDatosUsuario u : usuarios) {
	      if (u != null && u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) return true;
	    }
	  } catch (excepciones.UsuarioNoExisteException ignore) { }
	  return false;
	}

	private void handleValidar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	  resp.setCharacterEncoding("UTF-8");
	  resp.setContentType("text/plain; charset=UTF-8");
	  resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

	  String nick  = safeTrim(req.getParameter("nick"));
	  String email = safeTrim(req.getParameter("email"));

	  if ((nick == null || nick.isEmpty()) && (email == null || email.isEmpty())) {
	    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    resp.getWriter().write("ERROR");
	    return;
	  }

	  try {
	    if (nick != null && !nick.isEmpty()) {
	      boolean exists = existeNickDT(nick);
	      resp.getWriter().write(exists ? "NO" : "OK");
	      return;
	    }
	    if (email != null && !email.isEmpty()) {
	      boolean exists = existeEmailDT(email);
	      resp.getWriter().write(exists ? "NO" : "OK");
	      return;
	    }
	    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    resp.getWriter().write("ERROR");
	  } catch (Exception e) {
	    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    resp.getWriter().write("ERROR");
	  }
	}

	private static String safeTrim(String s){ return s == null ? null : s.trim(); }
}
