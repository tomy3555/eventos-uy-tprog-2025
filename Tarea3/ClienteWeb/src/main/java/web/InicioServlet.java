package web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import publicadores.*;
import util.ConfigLoader;

@WebServlet({"/inicio"})
public class InicioServlet extends HttpServlet {
    private final PublicadorEventoService service = new PublicadorEventoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<DtEvento> eventos = new ArrayList<>();
        try {
            PublicadorEvento port = service.getPublicadorEventoPort();
            DtEventoArray arr = port.listarEventosVigentes();
            if (arr != null && arr.getItem() != null) eventos = arr.getItem();
        } catch (Exception ignored) {}

        String context = "/ServidorCentral-0.0.1-SNAPSHOT";

        // Build a base URL for images that is reachable by the client machine.
        String scheme = req.getScheme(); // http or https
        String hostHeader = req.getHeader("Host");
        String baseUrl = null;

        try {
            Path propsPath = Path.of(System.getProperty("user.home"), ".eventosUy", ".properties");
            Properties props = new Properties();
            props.load(Files.newInputStream(propsPath));
            String ip = props.getProperty("servidor.ip", "localhost");
            String puerto = props.getProperty("servidor.puerto", "8080");

            String hostPart;
            if ("localhost".equals(ip) || "127.0.0.1".equals(ip)) {
                if (hostHeader != null && !hostHeader.isBlank()) {
                    hostPart = hostHeader; // may include port
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

        System.out.println("Resolved base image URL: " + baseUrl);

        // Build per-event image URLs and perform a quick HEAD to check accessibility
        Map<String, String> imgUrls = new HashMap<>();
        Map<String, String> imgChecks = new HashMap<>(); // debug: status or error

        if (eventos != null) {
            for (DtEvento e : eventos) {
                String nombre = e.getNombre();
                String img = e.getImagen();
                String imgUrl = (img != null && !img.isBlank())
                        ? (baseUrl + "eventos/" + img)
                        : (baseUrl + "eventos/evento-default.svg");

                imgUrls.put(nombre, imgUrl);

                // quick HEAD request to see if reachable from server
                String status;
                try {
                    URL u = new URL(imgUrl);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("HEAD");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    int code = conn.getResponseCode();
                    status = String.valueOf(code);
                } catch (Exception ex) {
                    status = "ERR:" + ex.getClass().getSimpleName() + ":" + ex.getMessage();
                }
                imgChecks.put(nombre, status);

                System.out.println("Event='" + nombre + "' img='" + img + "' => " + imgUrl + " [check=" + status + "]");
            }
        }

        req.setAttribute("eventos", eventos);
        req.setAttribute("baseUrl", baseUrl);
        req.setAttribute("imgUrls", imgUrls);
        req.setAttribute("imgChecks", imgChecks);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}