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
import java.util.Properties;
import util.ConfigLoader;

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

  //carpeta pública bajo /img
  private static final String UPLOAD_PUBLIC_DIR_ED = "/img/ediciones";

  private String ctx(HttpServletRequest req) { return req.getContextPath(); }

  private DtDatosUsuario getUsuario(HttpServletRequest req) {
    HttpSession sAux = req.getSession(false);
    return sAux == null ? null : (DtDatosUsuario) sAux.getAttribute("usuario_logueado");
  }
  private String getRol(HttpServletRequest req) {
    HttpSession sAux = req.getSession(false);
    return sAux == null ? null : (String) sAux.getAttribute("rol");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");

    // Create remote service port (PublicadorEvento)
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

      
	    String ip = ConfigLoader.get("ipServidor");
   	    String puerto = ConfigLoader.get("puerto");
        
   	    
   	    req.setAttribute("ipServidor", ip);
   	    req.setAttribute("puertoServidor", puerto);
      
      req.setAttribute("edicion", edicionObj);
      req.setAttribute("organizador", edicionObj.getOrganizador());
      // extract tiposRegistro list
      try {
        List<publicadores.DtTipoRegistro> tipos = new ArrayList<>();
        if (edicionObj.getTiposRegistro() != null && edicionObj.getTiposRegistro().getTipoRegistro() != null) {
          tipos.addAll(edicionObj.getTiposRegistro().getTipoRegistro());
        }
        req.setAttribute("tiposRegistro", tipos);
      } catch (Exception ignore) { req.setAttribute("tiposRegistro", List.of()); }
      // extract patrocinios list
      try {
        List<publicadores.DtPatrocinio> pats = new ArrayList<>();
        if (edicionObj.getPatrocinios() != null && edicionObj.getPatrocinios().getPatrocinio() != null) {
          pats.addAll(edicionObj.getPatrocinios().getPatrocinio());
        }
        req.setAttribute("patrocinios", pats);
      } catch (Exception ignore) { req.setAttribute("patrocinios", List.of()); }
      req.setAttribute("evNombre", evento);
      req.setAttribute("rol", getRol(req));
      System.out.println("Rol del usuario en sesión: " + getRol(req));
      System.out.println("Entra a EdicionServelt");


      // URL de imagen de edición 
      String edImagenUrl = resolveImagenUrlEdicion(req, edicionObj.getImagen());
      if (edImagenUrl != null) {
        req.setAttribute("edImagenUrl", edImagenUrl);
      }

      // registros visibles (organizador ve todos, asistente ve los suyos)
      HttpSession session = req.getSession(false);
      String nickSesion = session != null ? (String) session.getAttribute("nick") : null;
      boolean esOrganizador = nickSesion != null
          && edicionObj.getOrganizador() != null
          && nickSesion.equals(edicionObj.getOrganizador());

      java.util.List<DtRegistro> registrosList = new java.util.ArrayList<>();
      if (esOrganizador) {
        // edicionObj.getRegistros() is a wrapper; extract list
        try {
          if (edicionObj.getRegistros() != null && edicionObj.getRegistros().getRegistro() != null) {
            registrosList.addAll(edicionObj.getRegistros().getRegistro());
          }
        } catch (Exception ignore) {}
      } else if (nickSesion != null) {
        DtDatosUsuario usuarioLogueado = getUsuario(req);
        try {
          if (usuarioLogueado != null && usuarioLogueado.getRegistros() != null && usuarioLogueado.getRegistros().getRegistro() != null) {
            for (DtRegistro r : usuarioLogueado.getRegistros().getRegistro()) {
              if (r.getEdicion() != null && r.getEdicion().equals(edicionObj.getNombre())) {
                registrosList.add(r);
              }
            }
          }
        } catch (Exception ignore) {}
      }
      req.setAttribute("registros", registrosList);

      req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
      return;
    }

    switch (path) {
      case "/alta": {
        if (!requiereOrganizador(req, resp)) return;
        //        var listaEventos = ce().listarEventos();
        // obtener lista de eventos desde el publicador (usar DtEventoArray)
        java.util.List<DtEvento> listaEventos = new ArrayList<>();
        try {
          try {
            publicadores.DtEventoArray arr = port.listarEventos();
            if (arr != null && arr.getItem() != null) listaEventos.addAll(arr.getItem());
          } catch (Throwable t) {
            // fallback reflectively (por compatibilidad con versiones antiguas)
            try {
              Object wrapper = port.getClass().getMethod("listarEventos").invoke(port);
              if (wrapper instanceof DtEvento[]) {
                DtEvento[] darr = (DtEvento[]) wrapper;
                if (darr != null) listaEventos.addAll(Arrays.asList(darr));
              } else if (wrapper != null) {
                @SuppressWarnings("unchecked")
                List<DtEvento> items = (List<DtEvento>) wrapper.getClass().getMethod("getItem").invoke(wrapper);
                if (items != null) listaEventos.addAll(items);
              }
            } catch (Exception ignore) {}
          }
        } catch (Exception ignore) {}

        req.setAttribute("listaEventos", listaEventos);
        if (listaEventos == null || listaEventos.isEmpty()) {
          req.setAttribute("sinEventos", true);
        }
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        return;
      }
      case "/listar": {
        String evento = trim(req.getParameter("evento"));
        if (isBlank(evento)) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro 'evento'");
          return;
        }
        List<DtEdicion> listaEdiciones = listarEdicionesEventoCompleto(evento, port);
        String baseImgUrl = buildBaseImageUrl(req);
        req.setAttribute("listaEdiciones", listaEdiciones);
        req.setAttribute("evento", evento);
        req.setAttribute("edicionesBaseImgUrl", baseImgUrl);
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

    // Create remote service port (PublicadorEvento)
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
      try { imagen = req.getPart("imagen"); } catch (Exception ignore) {}

      if (isBlank(evento) || isBlank(nombre) || isBlank(desc) ||
          isBlank(iniStr) || isBlank(finStr) || isBlank(ciudad) || isBlank(pais)) {
        req.setAttribute("error", "Todos los campos obligatorios deben completarse.");
        // cargar lista de eventos desde publicador
        List<DtEvento> listaEventos = new ArrayList<>();
        try { publicadores.DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) listaEventos = arr.getItem(); } catch (Throwable t) { }
        req.setAttribute("listaEventos", listaEventos);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        return;
      }

      LocalDate ini, fin;
      try {
        ini = LocalDate.parse(iniStr);
        fin = LocalDate.parse(finStr);
      } catch (Exception e) {
        req.setAttribute("error", "Formato de fecha inválido.");
        List<DtEvento> listaEventos = new ArrayList<>();
        try { publicadores.DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) listaEventos = arr.getItem(); } catch (Throwable t) { }
        req.setAttribute("listaEventos", listaEventos);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
        return;
      }

      String imagenFileName = null;
      try {
          if (imagen != null && imagen.getSize() > 0) {
              String ctype = imagen.getContentType();
              if (ctype == null || !ctype.toLowerCase().startsWith("image/")) {
                  req.setAttribute("error", "El archivo subido no es una imagen válida.");
                  List<DtEvento> listaEventos = new ArrayList<>();
                  try { publicadores.DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) listaEventos = arr.getItem(); } catch (Throwable t) { }
                  req.setAttribute("listaEventos", listaEventos);
                  req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                  return;
              }

              String tomcatBase = System.getProperty("catalina.base");
              Path basePath = Path.of(tomcatBase, "webapps", "ServidorCentral-0.0.1-SNAPSHOT", "images", "ediciones");
              Files.createDirectories(basePath);

              String safeNombre = nombre.replaceAll("[^a-zA-Z0-9_-]", "_");
              String ext = getExtension(Path.of(imagen.getSubmittedFileName()).getFileName().toString());
              if (ext == null || ext.isBlank()) ext = guessExtensionFromContentType(ctype);
              if (ext == null || ext.isBlank()) ext = ".jpg";

              String finalName = safeNombre + ext;
              Path destino = basePath.resolve(finalName);
              Files.copy(imagen.getInputStream(), destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
              imagenFileName = finalName;
          }
      } catch (Exception ex) {
          req.setAttribute("error", "Error al procesar la imagen.");
          List<DtEvento> listaEventos = new ArrayList<>();
          try { publicadores.DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) listaEventos = arr.getItem(); } catch (Throwable t) { }
          req.setAttribute("listaEventos", listaEventos);
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

        // Call remote publicador API with video URL
        try {
          // The generated stub expects publicadores.LocalDate instances, construct placeholders
          publicadores.LocalDate pIni = new publicadores.LocalDate();
          publicadores.LocalDate pFin = new publicadores.LocalDate();
          publicadores.LocalDate pHoy = new publicadores.LocalDate();
          
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

        } catch (publicadores.EdicionYaExisteException_Exception | publicadores.EventoYaExisteException_Exception | publicadores.FechasCruzadasException_Exception ex) {
          throw ex;
        }

         String evEnc = URLEncoder.encode(evento, StandardCharsets.UTF_8);
         String edEnc = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
         resp.sendRedirect(ctx(req) + "/edicion/ConsultaEdicion?evento=" + evEnc + "&edicion=" + edEnc);

      } catch (publicadores.EdicionYaExisteException_Exception | publicadores.EventoYaExisteException_Exception | publicadores.FechasCruzadasException_Exception ex) {
        req.setAttribute("error", ex.getMessage());
        List<DtEvento> listaEventos = new ArrayList<>();
        try { publicadores.DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) listaEventos = arr.getItem(); } catch (Throwable t) { }
        req.setAttribute("listaEventos", listaEventos);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
      } catch (Exception ex) {
        req.setAttribute("error", ex.getMessage());
        List<DtEvento> listaEventos = new ArrayList<>();
        try { publicadores.DtEventoArray arr = port.listarEventos(); if (arr != null && arr.getItem() != null) listaEventos = arr.getItem(); } catch (Throwable t) { }
        req.setAttribute("listaEventos", listaEventos);
        req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
      }
      return;
    }

    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  private java.util.List<DtEdicion> listarEdicionesEventoCompleto(String nombreEvento, publicadores.PublicadorEvento port) {
    java.util.List<DtEdicion> lista = new ArrayList<>();
    try {
      DtEvento evento = port.consultaDTEvento(nombreEvento);
      if (evento == null) return lista;
      java.util.List<String> nombres = new ArrayList<>();
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

      if (nombres != null && !nombres.isEmpty()) {
        for (String edicionName : nombres) {
          DtEdicion ed = port.obtenerDtEdicion(nombreEvento, edicionName);
          if (ed != null) lista.add(ed);
        }
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

  //  Resolución unificada de URL para imagen de edición
  private String resolveImagenUrlEdicion(HttpServletRequest req, String raw) {
	    if (raw == null || raw.isBlank()) return null;
	    return "http://localhost:8080/ServidorCentral-0.0.1-SNAPSHOT/images/ediciones/" + raw;
	}

  // Construye la base URL dinámica para imágenes de ediciones
  private String buildBaseImageUrl(HttpServletRequest req) {
    String context = "/ServidorCentral-0.0.1-SNAPSHOT";
    String scheme = req.getScheme();
    String hostHeader = req.getHeader("Host");
    String baseUrl;
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
          if (!(scheme.equalsIgnoreCase("http") && reqPort == 80) && !(scheme.equalsIgnoreCase("https") && reqPort == 443)) {
            portPart = ":" + reqPort;
          }
          hostPart = req.getServerName() + portPart;
        }
      } else {
        hostPart = ip;
        if (puerto != null && !puerto.isBlank()) hostPart += ":" + puerto;
      }
      baseUrl = scheme + "://" + hostPart + context + "/images/ediciones/";
    } catch (IOException e) {
      String effectiveHost;
      String hostHeaderFallback = req.getHeader("Host");
      if (hostHeaderFallback != null && !hostHeaderFallback.isBlank()) {
        effectiveHost = hostHeaderFallback;
      } else {
        int reqPort = req.getServerPort();
        String portPart = "";
        if (!(scheme.equalsIgnoreCase("http") && reqPort == 80) && !(scheme.equalsIgnoreCase("https") && reqPort == 443)) {
          portPart = ":" + reqPort;
        }
        effectiveHost = req.getServerName() + portPart;
      }
      baseUrl = scheme + "://" + effectiveHost + context + "/images/ediciones/";
    }
    return baseUrl;
  }
}
