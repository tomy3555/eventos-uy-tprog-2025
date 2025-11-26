package web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import logica.interfaces.IControladorEvento;
import logica.datatypes.DTEvento;
import logica.fabrica;

@WebServlet({"/inicio"})
public class InicioServlet extends HttpServlet {

  private IControladorEvento ce() {
    return fabrica.getInstance().getIControladorEvento();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    IControladorEvento controladorEv = ce();
//    List<DTEvento> eventos = controladorEv.listarEventos();
    List<DTEvento> eventos = controladorEv.listarEventosVigentes();

    if (eventos == null || eventos.isEmpty()) {
      getServletContext().setAttribute("datosPrecargados", Boolean.FALSE);
    }

    // nombreEvento -> urlImagen
    Map<String, String> imgUrls = new HashMap<>();

    // build absolute base URL so clients on other machines can reach resources
    String scheme = req.getScheme(); // http / https

    // Prefer Host header (includes host:port) because req.getServerName()/getServerPort()
    // may return localhost when server was started locally. Host header reflects what
    // clients used in the request (IP or hostname).
    String hostHeader = req.getHeader("Host");
    String hostPart;
    if (hostHeader != null && !hostHeader.isBlank()) {
      hostPart = hostHeader; // already contains port if present
    } else {
      String serverName = req.getServerName(); // hostname or IP
      int serverPort = req.getServerPort();
      String portPart = "";
      if (!(("http".equalsIgnoreCase(scheme) && serverPort == 80) || ("https".equalsIgnoreCase(scheme) && serverPort == 443))) {
        portPart = ":" + serverPort;
      }
      hostPart = serverName + portPart;
    }

    String contextPath = req.getContextPath();
    String baseUrl = scheme + "://" + hostPart + contextPath;

    if (eventos != null) {
      for (DTEvento e : eventos) {
        String nombre = e.getNombre();
        System.out.println("Nombre del evento" + nombre);
        String raw = null;

        //  intentar desde el DTO
        try { raw = e.getImagen(); } catch (Exception ignore) {}

        //  si no vino en el DTO, consultar el evento completo
        if (raw == null || raw.isBlank()) {
          try {
            DTEvento eventIter = controladorEv.consultaDTEvento(nombre);
            if (eventIter != null) raw = eventIter.getImagen();
          } catch (Exception ex) { /* noop */ }
        }

        String url = null;
        if (raw != null && !raw.isBlank()) {
          if (raw.startsWith("http://") || raw.startsWith("https://")) {
            // external URL: keep as-is
            url = raw;
          } else if (raw.startsWith("/")) {
            // app-relative path in the WAR: build absolute URL
            url = baseUrl + raw;
          } else {
            // Solo nombre de archivo: try common locations inside the webapp
            String[] candidates = new String[] {
              "/img/" + raw,
              "/img/eventos/" + raw,
              "/eventos/" + raw        // subidos por la app
            };

            for (String rel : candidates) {
              boolean exists = false;

              // Try to resolve as a real file on disk (exploded WAR)
              String abs = getServletContext().getRealPath(rel);
              if (abs != null) {
                exists = Files.exists(Path.of(abs));
              } else {
                // If not exploded, try to find the resource inside the WAR
                URL resource = getServletContext().getResource(rel);
                if (resource != null) {
                  exists = true;
                }
              }

              if (exists) {
                url = baseUrl + rel;
                break;
              }
            }
          }
        }

        // debug output to help troubleshooting from server logs
        System.out.println("Resolved image URL for '" + nombre + "' => " + url);

        imgUrls.put(nombre, url); // puede ser null si realmente no hay imagen
      }
    }

    req.setAttribute("eventos", eventos);
    req.setAttribute("imgUrls", imgUrls);
    req.getRequestDispatcher("/index.jsp").forward(req, resp);
  }
}