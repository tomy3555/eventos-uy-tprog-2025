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
import java.util.Arrays;

import publicadores.DtEdicion;
import publicadores.DtEvento;
import publicadores.DtRegistro;
import publicadores.DtDatosUsuario;
import publicadores.StringArray;

@WebServlet("/edicion/*")
@MultipartConfig
public class EdicionServlet extends HttpServlet {

  private static final String JSP_ALTA     = "/WEB-INF/ediciones/AltaEdicion.jsp";
  private static final String JSP_CONSULTA = "/WEB-INF/ediciones/ConsultaEdicion.jsp";
  private static final String JSP_LISTADO  = "/WEB-INF/ediciones/ListarEdiciones.jsp";
  private static final String UPLOAD_PUBLIC_DIR_ED = "/images/ediciones";

  private String ctx(HttpServletRequest req) { return req.getContextPath(); }

  private DtDatosUsuario getUsuario(HttpServletRequest req) {
    HttpSession sAux = req.getSession(false);
    return sAux == null ? null : (DtDatosUsuario) sAux.getAttribute("usuario_logueado");
  }

  private String getRol(HttpServletRequest req) {
    HttpSession sAux = req.getSession(false);
    return sAux == null ? null : (String) sAux.getAttribute("rol");
  }

  private String buildBaseImageUrl(HttpServletRequest req) {
    String context = "/ServidorCentral-0.0.1-SNAPSHOT";
    String scheme = req.getScheme();
    String hostHeader = req.getHeader("Host");
    String baseUrl = null;
    try {
      Path propsPath = Path.of(System.getProperty("user.home"), ".eventosUy", ".properties");
      java.util.Properties props = new java.util.Properties();
      props.load(Files.newInputStream(propsPath));
      String ip = props.getProperty("servidor.ip", "localhost");
      String puerto = props.getProperty("servidor.puerto", "8080");
      String hostPart;
      if ("localhost".equals(ip) || "127.0.0.1".equals(ip)) {
        if (hostHeader != null && !hostHeader.isBlank()) {
          hostPart = hostHeader;
        } else {
          int reqPort = req.getServerPort();
          String portPart = "";
          if (!(("http".equalsIgnoreCase(scheme) && reqPort == 80) || ("https".equalsIgnoreCase(scheme) && reqPort == 443))) {
            portPart = ":" + reqPort;
          }
          hostPart = req.getServerName() + portPart;
        }
      } else {
        hostPart = ip;
        if (puerto != null && !puerto.isBlank()) hostPart += ":" + puerto;
      }
      baseUrl = scheme + "://" + hostPart + context + "/images/";
    } catch (IOException e) {
      String effectiveHost;
      String hostHeaderFallback = req.getHeader("Host");
      if (hostHeaderFallback != null && !hostHeaderFallback.isBlank()) {
        effectiveHost = hostHeaderFallback;
      } else {
        int reqPort = req.getServerPort();
        String portPart = "";
        if (!(("http".equalsIgnoreCase(scheme) && reqPort == 80) || ("https".equalsIgnoreCase(scheme) && reqPort == 443))) {
          portPart = ":" + reqPort;
        }
        effectiveHost = req.getServerName() + portPart;
      }
      baseUrl = scheme + "://" + effectiveHost + context + "/images/";
    }
    return baseUrl;
  }

  private String buildEdicionImageUrl(HttpServletRequest req, String img) {
    String baseUrl = buildBaseImageUrl(req);
    String imgName = (img == null || img.isBlank()) ? null : img.trim();
    if (imgName == null || imgName.isEmpty()) {
      return baseUrl + "ediciones/edicion-default.svg";
    }
    return baseUrl + "ediciones/" + imgName;
  }
  // --- FIN: Métodos para construir URLs de imágenes igual que InicioServlet ---

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");

    publicadores.PublicadorEventoService service = new publicadores.PublicadorEventoService();
    publicadores.PublicadorEvento port = service.getPublicadorEventoPort();

    String path = req.getPathInfo();

    if (path == null || "/".equals(path) || "/ConsultaEdicion".equals(path)) {
      String evento  = trim(req.getParameter("evento"));
      String edicion = trim(req.getParameter("edicion"));

      if (isBlank(evento) || isBlank(edicion)) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros 'evento' y/o 'edicion'");
        return;
      }

      DtEdicion edicionObj = port.obtenerDtEdicion(evento, edicion);
      if (edicionObj == null) {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Edición no encontrada: " + edicion);
        return;
      }

      req.setAttribute("edicion", edicionObj);
      req.setAttribute("organizador", edicionObj.getOrganizador());

      try {
        List<publicadores.DtTipoRegistro> tipos = new ArrayList<>();
        if (edicionObj.getTiposRegistro() != null && edicionObj.getTiposRegistro().getTipoRegistro() != null)
          tipos.addAll(edicionObj.getTiposRegistro().getTipoRegistro());
        req.setAttribute("tiposRegistro", tipos);
      } catch (Exception ignore) { req.setAttribute("tiposRegistro", List.of()); }

      try {
        List<publicadores.DtPatrocinio> pats = new ArrayList<>();
        if (edicionObj.getPatrocinios() != null && edicionObj.getPatrocinios().getPatrocinio() != null)
          pats.addAll(edicionObj.getPatrocinios().getPatrocinio());
        req.setAttribute("patrocinios", pats);
      } catch (Exception ignore) { req.setAttribute("patrocinios", List.of()); }

      req.setAttribute("evNombre", evento);
      req.setAttribute("rol", getRol(req));

      // URL de imagen de edición
      String edImagenUrl = buildEdicionImageUrl(req, edicionObj.getImagen());
      req.setAttribute("edImagenUrl", edImagenUrl);

      HttpSession session = req.getSession(false);
      String nickSesion = session != null ? (String) session.getAttribute("nick") : null;
      boolean esOrganizador = nickSesion != null
          && edicionObj.getOrganizador() != null
          && nickSesion.equals(edicionObj.getOrganizador());

      List<DtRegistro> registrosList = new ArrayList<>();
      DtRegistro registroUsuario = null;

      if (esOrganizador) {
          try {
              if (edicionObj.getRegistros() != null && edicionObj.getRegistros().getRegistro() != null)
                  registrosList.addAll(edicionObj.getRegistros().getRegistro());
          } catch (Exception ignore) {}
      } else if (nickSesion != null) {
          DtDatosUsuario usuarioLogueado = getUsuario(req);
          try {
              if (usuarioLogueado != null && usuarioLogueado.getRegistros() != null && usuarioLogueado.getRegistros().getRegistro() != null) {
                  for (DtRegistro r : usuarioLogueado.getRegistros().getRegistro()) {
                      if (r.getEdicion() != null && r.getEdicion().equals(edicionObj.getNombre())) {
                          registroUsuario = r;
                          break;
                      }
                  }
              }
          } catch (Exception ignore) {}
      }

      req.setAttribute("registros", registrosList);
      req.setAttribute("registroUsuario", registroUsuario);

      req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
      return;
    }

    switch (path) {
      case "/alta": {
        if (!requiereOrganizador(req, resp)) return;
        List<DtEvento> listaEventos = new ArrayList<>();
        try {
          publicadores.DtEventoArray arr = port.listarEventos();
          if (arr != null && arr.getItem() != null) listaEventos.addAll(arr.getItem());
        } catch (Exception ignore) {}
        req.setAttribute("listaEventos", listaEventos);
        if (listaEventos.isEmpty()) req.setAttribute("sinEventos", true);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        return;
      }
      case "/listar": {
        String evento = trim(req.getParameter("evento"));
        if (isBlank(evento)) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro 'evento'");
          return;
        }
        req.setAttribute("listaEdiciones", listarEdicionesEventoCompleto(evento, port));
        req.getRequestDispatcher(JSP_LISTADO).forward(req, resp);
        return;
      }
      default:
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");

    publicadores.PublicadorEventoService service = new publicadores.PublicadorEventoService();
    publicadores.PublicadorEvento port = service.getPublicadorEventoPort();

    String path = req.getPathInfo();

    if ("/alta".equals(path)) {
        if (!requiereOrganizador(req, resp)) return;

        String evento = trim(req.getParameter("evento"));
        String nombre = trim(req.getParameter("nombre"));
        String desc   = trim(req.getParameter("desc"));
        String iniStr = trim(req.getParameter("fechaInicio"));
        String finStr = trim(req.getParameter("fechaFin"));
        String ciudad = trim(req.getParameter("ciudad"));
        String pais   = trim(req.getParameter("pais"));
        String videoUrl = trim(req.getParameter("videoUrl"));

        Part imagen = null;
        String imagenFileName = null;
        try { imagen = req.getPart("imagen"); } catch (Exception ignore) {}

        if (isBlank(evento) || isBlank(nombre) || isBlank(desc) ||
            isBlank(iniStr) || isBlank(finStr) || isBlank(ciudad) || isBlank(pais)) {
            req.setAttribute("error", "Todos los campos obligatorios deben completarse.");
            cargarEventos(req, port);
            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            return;
        }

        try {
            DtDatosUsuario org = getUsuario(req);
            if (org == null || !"ORGANIZADOR".equals(getRol(req))) {
                req.setAttribute("error", "Debés iniciar sesión como organizador.");
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }

            DtEvento evObj = port.consultaDTEvento(evento);

            // ✅ Guardar imagen solo si hay archivo subido
            if (imagen != null && imagen.getSize() > 0) {
                try {
                    imagenFileName = guardarImagen(req, imagen, nombre);
                } catch (IOException ex) {
                    System.err.println("[WARN] No se pudo guardar la imagen: " + ex.getMessage());
                    imagenFileName = null;
                }
            } else {
                imagenFileName = null; // sin imagen
            }

            String hoyStr = LocalDate.now().toString();

            port.altaEdicionEventoDTO(
                evObj,
                org,
                nombre,
                nombre,
                desc,
                iniStr,
                finStr,
                hoyStr,
                ciudad,
                pais,
                (imagenFileName != null ? imagenFileName : ""), 
                videoUrl
            );

            String evEnc = URLEncoder.encode(evento, StandardCharsets.UTF_8);
            String edEnc = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
            resp.sendRedirect(ctx(req) + "/edicion/ConsultaEdicion?evento=" + evEnc + "&edicion=" + edEnc);

        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            cargarEventos(req, port);
            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        }
        return;
    }

    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  private void cargarEventos(HttpServletRequest req, publicadores.PublicadorEvento port) {
    List<DtEvento> listaEventos = new ArrayList<>();
    try {
      publicadores.DtEventoArray arr = port.listarEventos();
      if (arr != null && arr.getItem() != null) listaEventos.addAll(arr.getItem());
    } catch (Exception ignore) {}
    req.setAttribute("listaEventos", listaEventos);
  }

  private String guardarImagen(HttpServletRequest req, Part imagen, String nombre) throws IOException {
	    if (imagen == null || imagen.getSize() == 0) return null;

	    String ctype = imagen.getContentType();
	    if (ctype == null || !ctype.toLowerCase().startsWith("image/"))
	        throw new IOException("El archivo subido no es una imagen válida.");

	    String safeName = nombre.replaceAll("[^a-zA-Z0-9_-]", "_");
	    String ext = getExtension(getSafeFilename(imagen));
	    if (ext == null || ext.isBlank()) ext = guessExtensionFromContentType(ctype);
	    if (ext == null || ext.isBlank()) ext = ".jpg";
	    String finalName = "IMG-" + safeName + ext;

	    String tomcatBase = System.getProperty("catalina.base");
	    String baseImg = tomcatBase + "/webapps/ServidorCentral-0.0.1-SNAPSHOT/images/ediciones";

	    Files.createDirectories(Path.of(baseImg));
	    Path destino = Path.of(baseImg, finalName);
	    imagen.write(destino.toString());

	    System.out.println("[IMG] Guardada en: " + destino);
	    return finalName;
	}

  private List<DtEdicion> listarEdicionesEventoCompleto(String nombreEvento, publicadores.PublicadorEvento port) {
    List<DtEdicion> lista = new ArrayList<>();
    try {
      DtEvento evento = port.consultaDTEvento(nombreEvento);
      if (evento == null) return lista;
      List<String> nombres = new ArrayList<>();
      try {
        StringArray arr = port.listarEdicionesEvento(nombreEvento);
        if (arr != null && arr.getItem() != null) nombres.addAll(arr.getItem());
      } catch (Throwable t) {
        try {
          Object wrapper = port.listarEdicionesEvento(nombreEvento);
          var items = (List<String>) wrapper.getClass().getMethod("getItem").invoke(wrapper);
          if (items != null) nombres.addAll(items);
        } catch (Exception ignore) {}
      }
      for (String edicionName : nombres) {
        DtEdicion ed = port.obtenerDtEdicion(nombreEvento, edicionName);
        if (ed != null) lista.add(ed);
      }
    } catch (Exception e) {
      System.err.println("[listarEdicionesEventoCompleto] Error: " + e.getMessage());
    }
    return lista;
  }

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

  private static String getSafeFilename(Part pAux) {
    String name = pAux.getSubmittedFileName();
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
    return null;
  }
}
