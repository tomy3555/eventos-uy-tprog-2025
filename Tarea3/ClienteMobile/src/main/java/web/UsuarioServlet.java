package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import publicadores.DtDatosUsuario;
import publicadores.DtDatosUsuarioArray;
import publicadores.StringArray;
import publicadores.UsuarioNoExisteException_Exception;
import publicadores.UsuarioYaExisteException_Exception;
import util.ConfigLoader;



@WebServlet(urlPatterns = {"/usuario/AltaUsuario", "/usuario/validar", "/usuario/edicionesRegistradas", "/usuario/listarRegistros"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // 1 MB en memoria
    maxFileSize = 10 * 1024 * 1024,    // 10 MB por archivo
    maxRequestSize = 20 * 1024 * 1024  // 20 MB total
)
public class UsuarioServlet extends HttpServlet {

  private static final String JSP_LOGIN = "/WEB-INF/auth/login.jsp";
  private static final String JSP_ALTA  = "/WEB-INF/usuario/AltaUsuario.jsp";
  private static final String IMG_REL_BASE_USR = "/img/usuarios";

  private String ctx(HttpServletRequest req) { return req.getContextPath(); }

  private void cargarInstituciones(HttpServletRequest req, publicadores.PublicadorUsuario port) {
    StringArray arr = null;
    try { arr = port.listarInstituciones(); } catch (Exception ignore) { }
    java.util.List<String> lista;
    if (arr == null || arr.getItem() == null) lista = java.util.List.of(); else lista = arr.getItem();
    req.setAttribute("instituciones", lista);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    // *** Mantenemos EXACTAMENTE estas dos líneas ***
    publicadores.PublicadorUsuarioService service = new publicadores.PublicadorUsuarioService();
    publicadores.PublicadorUsuario port = service.getPublicadorUsuarioPort();

    String path = req.getServletPath();
    if (path == null) {
      resp.sendRedirect(ctx(req) + "/");
      return;
    }

    if ("/usuario/validar".equals(path)) {
      handleValidar(req, resp, port);
      return;
    }

    if ("/usuario/AltaUsuario".equals(path)) {
      cargarInstituciones(req, port);
      req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
      return;
    }

    if ("/usuario/edicionesRegistradas".equals(path)) {
      String nick = (String) req.getSession().getAttribute("nick");
      if (nick == null || nick.isBlank()) {
        resp.sendRedirect(ctx(req) + "/login");
        return;
      }
      DtDatosUsuario usuario = null;
      try {
        usuario = port.obtenerDatosUsuario(nick);
      } catch (Exception e) {
        req.setAttribute("error", "No se pudo obtener el usuario.");
        req.getRequestDispatcher("/WEB-INF/ediciones/ListarEdicionesUsuario.jsp").forward(req, resp);
        return;
      }
      // Obtener todas las ediciones aceptadas (DtEdicion completos)
      java.util.List<publicadores.DtEdicion> todasEdiciones = new java.util.ArrayList<>();
      try {
        publicadores.PublicadorEventoService eventoService = new publicadores.PublicadorEventoService();
        publicadores.PublicadorEvento eventoPort = eventoService.getPublicadorEventoPort();
        // Obtener todos los eventos
        publicadores.DtEventoArray eventosArr = eventoPort.listarEventos();
        java.util.List<String> nombresEventos = new java.util.ArrayList<>();
        if (eventosArr != null && eventosArr.getItem() != null) {
          for (publicadores.DtEvento evento : eventosArr.getItem()) {
            if (evento != null && evento.getNombre() != null) {
              nombresEventos.add(evento.getNombre());
            }
          }
        }
        for (String nombreEvento : nombresEventos) {
          // Obtener nombres de ediciones para cada evento
          StringArray edicionesArr = eventoPort.listarEdicionesEvento(nombreEvento);
          java.util.List<String> nombresEdiciones = (edicionesArr != null && edicionesArr.getItem() != null) ? edicionesArr.getItem() : java.util.List.of();
          for (String nombreEdicion : nombresEdiciones) {
            publicadores.DtEdicion ed = eventoPort.obtenerDtEdicion(nombreEvento, nombreEdicion);
            if (ed != null) todasEdiciones.add(ed);
          }
        }
      } catch (Exception e) {
        System.err.println("[UsuarioServlet] Error obteniendo todas las ediciones: " + e.getMessage());
      }
      // Filtrar solo las ediciones donde el usuario está registrado
      java.util.List<String> edicionesRegistradasNombres = new java.util.ArrayList<>();
      if (usuario != null && usuario.getRegistros() != null && usuario.getRegistros().getRegistro() != null) {
        for (publicadores.DtRegistro reg : usuario.getRegistros().getRegistro()) {
          if (reg.getEdicion() != null) {
            edicionesRegistradasNombres.add(reg.getEdicion());
          }
        }
      }
      java.util.List<publicadores.DtEdicion> edicionesRegistradas = new java.util.ArrayList<>();
      for (publicadores.DtEdicion ed : todasEdiciones) {
        if (edicionesRegistradasNombres.contains(ed.getNombre())) {
          edicionesRegistradas.add(ed);
        }
      }
      
      String ip = ConfigLoader.get("ipServidor");
      String puerto = ConfigLoader.get("puerto");
    
    
      req.setAttribute("ipServidor", ip);
      req.setAttribute("puertoServidor", puerto);
      req.setAttribute("edicionesRegistradas", edicionesRegistradas);
      req.getRequestDispatcher("/WEB-INF/ediciones/ListarEdicionesUsuario.jsp").forward(req, resp);
      return;
    }

    if ("/usuario/listarRegistros".equals(path)) {
      String nick = (String) req.getSession().getAttribute("nick");
      if (nick == null || nick.isBlank()) {
        resp.sendRedirect(ctx(req) + "/login");
        return;
      }
      DtDatosUsuario usuario = null;
      try {
        usuario = port.obtenerDatosUsuario(nick);
      } catch (Exception e) {
        req.setAttribute("error", "No se pudo obtener el usuario.");
        req.getRequestDispatcher("/WEB-INF/ediciones/ListarRegistros.jsp").forward(req, resp);
        return;
      }
      java.util.List<publicadores.DtRegistro> registrosUsuario = java.util.List.of();
      if (usuario != null && usuario.getRegistros() != null && usuario.getRegistros().getRegistro() != null) {
        registrosUsuario = usuario.getRegistros().getRegistro();
      }
      req.setAttribute("registrosUsuario", registrosUsuario);
      req.getRequestDispatcher("/WEB-INF/ediciones/ListarRegistros.jsp").forward(req, resp);
      return;
    }

    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (!"/usuario/AltaUsuario".equals(req.getServletPath())) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // *** Mantenemos EXACTAMENTE estas dos líneas también acá ***
    publicadores.PublicadorUsuarioService service = new publicadores.PublicadorUsuarioService();
    publicadores.PublicadorUsuario port = service.getPublicadorUsuarioPort();

    // === manejo de imagen (opcional) ===
    Part imagenPart = null;
    try { imagenPart = req.getPart("imagen"); } catch (Exception ignore) {}
    String nombreArchivo = null;

    if (imagenPart != null && imagenPart.getSize() > 0) {
      String ctype = imagenPart.getContentType();
      if (ctype == null || !ctype.toLowerCase().startsWith("image/")) {
        req.setAttribute("error", "El archivo subido no es una imagen válida.");
        cargarInstituciones(req, port);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        return;
      }

      String baseImg = getServletContext().getRealPath(IMG_REL_BASE_USR);
      if (baseImg == null) {
        String root = getServletContext().getRealPath("/");
        if (root != null) baseImg = Path.of(root, "img", "usuarios").toString();
      }
      if (baseImg == null) throw new ServletException("No se pudo resolver la ruta física de /img/usuarios.");
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

      nombreArchivo = finalName;
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
      cargarInstituciones(req, port);
      forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
      return;
    }
    if (nick == null || correo == null || rol == null || nick.isBlank() || correo.isBlank()) {
      req.setAttribute("error", "Faltan datos obligatorios.");
      cargarInstituciones(req, port);
      forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
      return;
    }

    // validación específica por rol
    LocalDate fechaNac = null;
    boolean esOrganizador = "ORGANIZADOR".equalsIgnoreCase(rol);

    if (!esOrganizador) {
      if (nacStr == null || nacStr.isBlank()) {
        req.setAttribute("error", "Debe ingresar una fecha de nacimiento.");
        cargarInstituciones(req, port);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
      try {
        fechaNac = LocalDate.parse(nacStr);
        if (fechaNac.isAfter(LocalDate.now())) throw new IllegalArgumentException();
      } catch (Exception e) {
        req.setAttribute("error", "Formato de fecha inválido o futura.");
        cargarInstituciones(req, port);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
    } else {
      if (organizacion == null || organizacion.isBlank() || descripcion == null || descripcion.isBlank()) {
        req.setAttribute("error", "Debe completar los campos obligatorios del organizador.");
        cargarInstituciones(req, port);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
    }

    String nombreFinal = esOrganizador ? organizacion : nombre;

    try {
      // Chequeos de unicidad vía servicio
      if (existeNickDT(nick, port)) {
        req.setAttribute("error","El nick ya existe.");
        cargarInstituciones(req, port);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }
      if (existeEmailDT(correo, port)) {
        req.setAttribute("error","El email ya existe.");
        cargarInstituciones(req, port);
        forwardConDatos(req, resp, rol, nick, nombre, apellido, correo, descripcion, link, institucion, nacStr);
        return;
      }



      // helper to send non-null trimmed strings
      java.util.function.Function<String,String> sendNonNull = s -> (s == null) ? "" : s.trim();

      String svcNombre = sendNonNull.apply(nombreFinal);
      String svcCorreo = sendNonNull.apply(correo);
      String svcDescripcion = sendNonNull.apply(descripcion);
      String svcLink = sendNonNull.apply(link);
      String svcApellido = sendNonNull.apply(apellido);
      String svcInstitucion = sendNonNull.apply(institucion);
      String svcImagen = (nombreArchivo == null) ? "" : nombreArchivo;

      // For role-specific unused fields, send empty string (do NOT send null)
      if (esOrganizador) {
        // organizador: assistant-specific fields should be empty
        svcApellido = "";
        // fecha not used by organizer but keep a non-null object
      } else {
        // asistente: organizer-specific fields should be empty
        svcDescripcion = "";
        svcLink = "";
      }
      
      
      String fechaStr = (fechaNac != null) ? fechaNac.toString() : null;
      // Debug: print sanitized parameters to confirm none are null

      port.altaUsuario(
              nick,
              svcNombre,
              svcCorreo,
              svcDescripcion,
              svcLink,
              svcApellido,
              fechaStr,
              svcInstitucion,
              esOrganizador,
              pass1,
              svcImagen
          );

      // login directo post-alta
      HttpSession sAux = req.getSession(true);
      DtDatosUsuario usuarioLogueado;
      try {
        usuarioLogueado = port.obtenerDatosUsuario(nick);
      } catch (UsuarioNoExisteException_Exception e) {
    	    req.setAttribute("error", "No se pudo encontrar el usuario recién creado.");
    	    cargarInstituciones(req, port);
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

    } catch (UsuarioYaExisteException_Exception e) {
        req.setAttribute("error", e.getMessage());
        cargarInstituciones(req, port);
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

  // ==== helpers contra el servicio (usan el 'port' recibido) ====
  private boolean existeNickDT(String nick, publicadores.PublicadorUsuario port) {
	  try {
	    DtDatosUsuarioArray arr = port.obtenerUsuariosDT();
	    for (DtDatosUsuario u : asList(arr)) {
	      if (u != null && nick != null && nick.equals(u.getNickname())) return true;
	    }
	  } catch (Exception ignore) { }
	  return false;
	}

	private boolean existeEmailDT(String email, publicadores.PublicadorUsuario port) {
	  try {
	    DtDatosUsuarioArray arr = port.obtenerUsuariosDT();
	    for (DtDatosUsuario u : asList(arr)) {
	      if (u != null && u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) return true;
	    }
	  } catch (Exception ignore) { }
	  return false;
	}

  private void handleValidar(HttpServletRequest req, HttpServletResponse resp,
                             publicadores.PublicadorUsuario port) throws IOException {
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
        boolean exists = existeNickDT(nick, port);
        resp.getWriter().write(exists ? "NO" : "OK");
        return;
      }
      if (email != null && !email.isEmpty()) {
        boolean exists = existeEmailDT(email, port);
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

// Convert empty (or whitespace-only) string to null for SOAP service compatibility
private static String emptyToNull(String s) {
  if (s == null) return null;
  String t = s.trim();
  return t.isEmpty() ? null : t;
}

private static java.util.List<DtDatosUsuario> asList(DtDatosUsuarioArray arr) {
	  if (arr == null) return java.util.List.of();
	  // según cómo generó el stub puede ser getItem() o getItems():
	  try {
	    java.util.List<DtDatosUsuario> l = arr.getItem();
	    return (l == null) ? java.util.List.of() : l;
	  } catch (NoSuchMethodError e) {
	    try {
	      java.util.List<DtDatosUsuario> l = (java.util.List<DtDatosUsuario>) arr.getClass()
	          .getMethod("getItems").invoke(arr);
	      return (l == null) ? java.util.List.of() : l;
	    } catch (Exception ignore) {
	      return java.util.List.of();
	    }
	  }
	}
}