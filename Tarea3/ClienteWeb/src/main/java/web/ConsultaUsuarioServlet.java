package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
import publicadores.DtDatosUsuario;
import publicadores.DtDatosUsuarioArray;
import publicadores.PublicadorUsuario;
import publicadores.PublicadorUsuarioService;
import publicadores.UsuarioNoExisteException_Exception;
import publicadores.StringArray;

@WebServlet(urlPatterns = {
        "/usuario/ConsultaUsuario",
        "/usuario/seguir",
        "/usuario/dejarSeguir",
        "/usuario/archivarEdicion"
})
@jakarta.servlet.annotation.MultipartConfig
public class ConsultaUsuarioServlet extends HttpServlet {
    private static final String JSP_CONSULTA = "/WEB-INF/usuario/ConsultaUsuario.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        boolean forzarListado = isTrue(request.getParameter("listar")) ||
                "list".equalsIgnoreCase(trim(request.getParameter("view")));

        String nick = trim(request.getParameter("nick"));
        if (!forzarListado && isBlank(nick)) {
            HttpSession sAux = request.getSession(false);
            if (sAux != null) {
                Object obj = sAux.getAttribute("nick");
                if (obj instanceof String) nick = (String) obj;
            }
        }

        PublicadorUsuarioService service = new PublicadorUsuarioService();
        PublicadorUsuario port = service.getPublicadorUsuarioPort();

        String checkSeguidos = request.getParameter("checkSeguidos");
        String nicksParam = request.getParameter("nicks");
        if ("1".equals(checkSeguidos) && nicksParam != null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            String[] parts = nicksParam.split(",");
            String sessionNick = nickEnSesion(request);
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            boolean first = true;
            for (String p : parts) {
                String target = p == null ? null : p.trim();
                if (target == null || target.isEmpty()) continue;
                boolean sigue = false;
                try {
                    if (sessionNick != null && !sessionNick.isBlank()) {
                        sigue = port.sigueA(sessionNick, target);
                    }
                } catch (Exception ignore) { }
                if (!first) sb.append(',');
                first = false;
                sb.append('"').append(escapeJson(target)).append('"').append(':').append(sigue);
            }
            sb.append('}');
            response.getWriter().write(sb.toString());
            return;
        }

        if (forzarListado || isBlank(nick)) {
            List<DtDatosUsuario> usuarios = new ArrayList<>();
            Map<String, String> usuariosImagenUrlMap = new HashMap<>();
            try {
                DtDatosUsuarioArray arr = port.obtenerUsuariosDT();
                usuarios = asList(arr);
                String nickSesion = nickEnSesion(request);
                Map<String, Boolean> yaLoSigoMap = new HashMap<>();
                if (nickSesion != null && !nickSesion.isBlank()) {
                    for (DtDatosUsuario u : usuarios) {
                        if (u == null || u.getNickname() == null) continue;
                        String objetivo = u.getNickname();
                        if (nickSesion.equalsIgnoreCase(objetivo)) {
                            yaLoSigoMap.put(objetivo, false);
                        } else {
                            try {
                                boolean sigue = port.sigueA(nickSesion, objetivo);
                                yaLoSigoMap.put(objetivo, sigue);
                            } catch (Exception ignore) {
                                yaLoSigoMap.put(objetivo, false);
                            }
                        }
                        // Construir la URL de imagen para cada usuario
                        String imgUrl = buildUsuarioImageUrl(request, u.getImagen());
                        usuariosImagenUrlMap.put(objetivo, imgUrl);
                    }
                } else {
                    for (DtDatosUsuario u : usuarios) {
                        if (u == null || u.getNickname() == null) continue;
                        String objetivo = u.getNickname();
                        String imgUrl = buildUsuarioImageUrl(request, u.getImagen());
                        usuariosImagenUrlMap.put(objetivo, imgUrl);
                    }
                }
                request.setAttribute("yaLoSigoMap", yaLoSigoMap);
            } catch (Exception e) {
                request.setAttribute("error", "No se pudo obtener la lista de usuarios.");
            }
            request.setAttribute("usuarios", usuarios);
            request.setAttribute("usuariosImagenUrlMap", usuariosImagenUrlMap);

        } else {
            try {
                DtDatosUsuario usuario = port.obtenerDatosUsuario(nick);
                String ctx = request.getContextPath();
                String imagenUrl = buildUsuarioImageUrl(request, usuario.getImagen());
                request.setAttribute("usrImagenUrl", imagenUrl);
                request.setAttribute("usuario", usuario);

                List<String> seguidores = new ArrayList<>();
                List<String> seguidos = new ArrayList<>();
                if (usuario.getSeguidores() != null && usuario.getSeguidores().getSeguidor() != null) {
                    seguidores.addAll(usuario.getSeguidores().getSeguidor());
                }
                if (usuario.getSeguidos() != null && usuario.getSeguidos().getSeguido() != null) {
                    seguidos.addAll(usuario.getSeguidos().getSeguido());
                }
                request.setAttribute("seguidores", seguidores);
                request.setAttribute("seguidos", seguidos);

                List<String> instituciones = Collections.emptyList();
                try {
                    StringArray arr = port.listarInstituciones();
                    if (arr != null && arr.getItem() != null) instituciones = arr.getItem();
                } catch (Exception ignore) {}
                request.setAttribute("instituciones", instituciones);

                Map<String, String> edicionToEvento = new HashMap<>();
                try {
                    if (usuario.getEdiciones() != null && usuario.getEdiciones().getEdicion() != null) {
                        for (publicadores.DtEdicion ed : usuario.getEdiciones().getEdicion()) {
                            if (ed.getEvento() != null && ed.getEvento().getNombre() != null) {
                                edicionToEvento.put(ed.getNombre(), ed.getEvento().getNombre());
                            }
                        }
                    }
                    if (usuario.getRegistros() != null && usuario.getRegistros().getRegistro() != null) {
                        for (publicadores.DtRegistro reg : usuario.getRegistros().getRegistro()) {
                            if (reg.getEdicion() != null && reg.getEvento() != null) {
                                edicionToEvento.put(reg.getEdicion(), reg.getEvento());
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("[WARN] No se pudo construir edicionToEvento: " + ex.getMessage());
                }
                request.setAttribute("edicionToEvento", edicionToEvento);

                Map<String,String> instFotos = new HashMap<>();
                String[] exts = new String[]{".png",".jpg",".jpeg",".webp",".gif"};
                ServletContext sc = getServletContext();
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
                        String abs = sc.getRealPath(candRel);
                        if (abs != null && Files.exists(Path.of(abs))) {
                            instFotos.put(inst, ctx + candRel);
                            break;
                        }
                    }
                }
                request.setAttribute("instFotos", instFotos);

                boolean esPerfilOrganizador = (usuario.getDesc() != null) || (usuario.getLink() != null);
                request.setAttribute("esPerfilOrganizador", esPerfilOrganizador);

                try {
                    String nickPerfil = (usuario != null ? usuario.getNickname() : null);
                    if (esPerfilOrganizador && nickPerfil != null && !nickPerfil.isBlank()) {
                        publicadores.PublicadorEventoService evSrv = new publicadores.PublicadorEventoService();
                        publicadores.PublicadorEvento evPort = evSrv.getPublicadorEventoPort();
                        java.util.Set<String> archivables = new java.util.HashSet<>();
                        try {
                            publicadores.StringArray arrArch = evPort.listarEdicionesArchivables(nickPerfil);
                            if (arrArch != null && arrArch.getItem() != null) {
                                archivables.addAll(arrArch.getItem());
                            }
                        } catch (Exception ex) {
                            System.out.println("[ARCHIV][ERROR] listarEdicionesArchivables: " + ex.getMessage());
                        }
                        request.setAttribute("archivablesSet", archivables);
                    } else {
                        request.setAttribute("archivablesSet", java.util.Collections.emptySet());
                    }
                } catch (Exception e) {
                    request.setAttribute("archivablesSet", java.util.Collections.emptySet());
                }

                String nickSesion = nickEnSesion(request);
                boolean esSuPropioPerfil = nickSesion != null && nickSesion.equals(usuario.getNickname());
                request.setAttribute("esSuPropioPerfil", esSuPropioPerfil);
                boolean yaLoSigo = false;
                if (!esSuPropioPerfil && nickSesion != null) {
                    try { yaLoSigo = port.sigueA(nickSesion, usuario.getNickname()); } catch (Exception ignore) {}
                }
                request.setAttribute("yaLoSigo", yaLoSigo);

                try {
                    publicadores.PublicadorEventoService evSrv = new publicadores.PublicadorEventoService();
                    publicadores.PublicadorEvento evPort = evSrv.getPublicadorEventoPort();
                    publicadores.DTArchEdicionArray archOrgWrap = null;
                    // Only fetch archived editions from the DB when the profile belongs to the organizer
                    // and the viewer is the organizer themselves. The requirement is that the DB is used
                    // exclusively to show archived editions when an organizer views their own profile.
                    if (esPerfilOrganizador && esSuPropioPerfil) {
                        archOrgWrap = evPort.edicionesArchivadasOrganizadas(usuario.getNickname());
                    }
                    java.util.List<publicadores.DTArchEdicion> archOrgList =
                            (archOrgWrap != null && archOrgWrap.getItem() != null)
                                    ? archOrgWrap.getItem()
                                    : java.util.Collections.emptyList();
                    request.setAttribute("archivadasOrganizadas", archOrgWrap);
                    request.setAttribute("archivadasOrganizadasList", archOrgList);
                    request.setAttribute("archivadasOrganizadasCount", archOrgList.size());
                } catch (Exception ex) {
                    request.setAttribute("archivadasOrganizadas", null);
                    request.setAttribute("archivadasOrganizadasList", java.util.Collections.emptyList());
                    request.setAttribute("archivadasOrganizadasCount", 0);
                }
            } catch (UsuarioNoExisteException_Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "El usuario '" + nick + "' no existe.");
            }
        }
        request.getRequestDispatcher(JSP_CONSULTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        PublicadorUsuarioService service = new PublicadorUsuarioService();
        PublicadorUsuario port = service.getPublicadorUsuarioPort();
        if ("/usuario/seguir".equals(path) || "/usuario/dejarSeguir".equals(path)) {
            HttpSession sAux = request.getSession(false);
            String nickSesion = (sAux != null) ? (String) sAux.getAttribute("nick") : null;
            if (nickSesion == null || nickSesion.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            String objetivo = trim(request.getParameter("a"));
            if (isBlank(objetivo)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro 'a'.");
                return;
            }
            if (!nickSesion.equalsIgnoreCase(objetivo)) {
                try {
                    if ("/usuario/seguir".equals(path)) { port.seguirUsuario(nickSesion, objetivo); }
                    else { port.dejarSeguirUsuario(nickSesion, objetivo); }
                } catch (Exception ignore) {}
            }
            String xrw = request.getHeader("X-Requested-With");
            boolean isAjax = xrw != null && "XMLHttpRequest".equalsIgnoreCase(xrw);
            if (isAjax) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain; charset=UTF-8");
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.getWriter().write("OK");
                return;
            }
            String listarParam = trim(request.getParameter("listar"));
            if (!isBlank(listarParam)) {
                response.sendRedirect(request.getContextPath() + "/usuario/ConsultaUsuario?listar=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/usuario/ConsultaUsuario?nick=" +
                        java.net.URLEncoder.encode(objetivo, java.nio.charset.StandardCharsets.UTF_8));
            }
            return;
        }

        if ("/usuario/modificar".equals(path)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if ("/usuario/archivarEdicion".equals(path)) {
            HttpSession sAux = request.getSession(false);
            String nickSesion = (sAux != null) ? (String) sAux.getAttribute("nick") : null;
            if (nickSesion == null || nickSesion.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            String edicionNombre = trim(request.getParameter("edicion"));
            String owner = trim(request.getParameter("owner"));
            if (isBlank(edicionNombre) || isBlank(owner)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros.");
                return;
            }
            if (!nickSesion.equalsIgnoreCase(owner)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "No podés archivar ediciones de otro usuario.");
                return;
            }
            try {
                publicadores.PublicadorEventoService evSrv = new publicadores.PublicadorEventoService();
                publicadores.PublicadorEvento evPort = evSrv.getPublicadorEventoPort();
                evPort.archivarEdicion(edicionNombre);
                response.sendRedirect(request.getContextPath() + "/");
                return;
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/?error=" +
                        java.net.URLEncoder.encode("No se pudo archivar: " + ex.getMessage(),
                                java.nio.charset.StandardCharsets.UTF_8));
                return;
            }
        }
        doGet(request, response);
    }

    private String nickEnSesion(HttpServletRequest req) {
        HttpSession sAux = req.getSession(false);
        return sAux == null ? null : (String) sAux.getAttribute("nick");
    }

    private static String trim(String s) { return (s == null) ? null : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static boolean isTrue(String v) {
        if (v == null) return false;
        String s = v.trim().toLowerCase(Locale.ROOT);
        return "1".equals(s) || "true".equals(s) || "on".equals(s) || "yes".equals(s) || "y".equals(s);
    }
    private static java.util.List<DtDatosUsuario> asList(DtDatosUsuarioArray arr) {
        if (arr == null) return java.util.List.of();
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
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
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
                    if (!("http".equalsIgnoreCase(scheme) && reqPort == 80) || ("https".equalsIgnoreCase(scheme) && reqPort == 443)) {
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
                if (!("http".equalsIgnoreCase(scheme) && reqPort == 80) || ("https".equalsIgnoreCase(scheme) && reqPort == 443)) {
                    portPart = ":" + reqPort;
                }
                effectiveHost = req.getServerName() + portPart;
            }
            baseUrl = scheme + "://" + effectiveHost + context + "/images/";
        }
        return baseUrl;
    }

    private String buildUsuarioImageUrl(HttpServletRequest req, String img) {
        String baseUrl = buildBaseImageUrl(req);
        String imgName = (img == null || img.isBlank()) ? null : img.trim();
        if (imgName == null || imgName.isEmpty()) {
            return baseUrl + "usuarios/usuario-default.svg";
        }
        return baseUrl + "usuarios/" + imgName;
    }
}
