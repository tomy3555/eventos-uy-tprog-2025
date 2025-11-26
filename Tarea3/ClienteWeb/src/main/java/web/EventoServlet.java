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
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Method;
import publicadores.DTEstado;

import publicadores.PublicadorEventoService;
import publicadores.PublicadorEvento;
import publicadores.DtCategorias;
import publicadores.DtEdicion;
import publicadores.DtEvento;
import publicadores.DtEventoArray;
import publicadores.StringArray;
import publicadores.EventoYaExisteException_Exception;

@WebServlet("/evento/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class EventoServlet extends HttpServlet {

    private static final String JSP_ALTA = "/WEB-INF/evento/alta.jsp";
    private static final String JSP_CONSULTA = "/WEB-INF/evento/ConsultaEvento.jsp";
    private static final String JSP_REGISTRO = "/WEB-INF/evento/RegistrarseEvento.jsp";
    private static final String JSP_LISTAR = "/WEB-INF/evento/listado.jsp";

    private static final String UPLOAD_PUBLIC_DIR = "/images/eventos";

    private String ctx(HttpServletRequest req) { return req.getContextPath(); }
    
    private PublicadorEvento obtenerPort() {
        PublicadorEventoService service = new PublicadorEventoService();
        return service.getPublicadorEventoPort();
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
                    if (!(scheme.equalsIgnoreCase("http") && reqPort == 80) || (scheme.equalsIgnoreCase("https") && reqPort == 443)) {
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
                if (!(scheme.equalsIgnoreCase("http") && reqPort == 80) || (scheme.equalsIgnoreCase("https") && reqPort == 443)) {
                    portPart = ":" + reqPort;
                }
                effectiveHost = req.getServerName() + portPart;
            }
            baseUrl = scheme + "://" + effectiveHost + context + "/images/";
        }
        return baseUrl;
    }

    private String buildEventoImageUrl(HttpServletRequest req, DtEvento ev) {
        String img = (ev == null || ev.getImagen() == null) ? null : ev.getImagen().trim();
        String baseUrl = buildBaseImageUrl(req);
        if (img == null || img.isEmpty()) {
            return baseUrl + "eventos/evento-default.svg";
        }
        return baseUrl + "eventos/" + img;
    }
    
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

            PublicadorEvento port = obtenerPort();
            DtEvento ev = port.consultaDTEvento(nombre);
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
            
   

            // URL de imagen 
            String evImagenUrl = buildEventoImageUrl(req, ev);
            req.setAttribute("evImagenUrl", evImagenUrl);

            // EDICIONES 
            StringArray clavesArrObj = port.listarEdicionesEvento(nombre);
            List<String> claves = (clavesArrObj == null || clavesArrObj.getItem() == null) ? List.of() : clavesArrObj.getItem();
            List<DtEdicion> ediciones = new ArrayList<>();
            String siglaEvento = ev.getSigla();

            for (String clave : claves) {
                DtEdicion dtEd = null;

                try {
                    if (clave != null) dtEd = port.obtenerEdicionPorSiglaDT(clave);
                } catch (Exception ignore) {}

                //  si no vino nada, pruebo por NOMBRE de edición 
                if (dtEd == null) {
                    try {
                        dtEd = port.obtenerDtEdicion(nombre, clave);
                    } catch (Exception ignore) {}
                }

                // filtrado por estado aceptado 
                if (dtEd != null && esAceptada(dtEd.getEstado())) {
                	
                	System.out.println("edicion agregada  "+ dtEd.getNombre() + "y su estado es " + dtEd.getEstado().toString());
                	if (dtEd.getEstado().toString() != "ACEPTADA" || dtEd.getEstado() == DTEstado.ACEPTADA )  {
                    ediciones.add(dtEd);}
                }
            }
            req.setAttribute("evEdiciones", ediciones);

            req.getRequestDispatcher(JSP_CONSULTA).forward(req, resp);
            return;
        }

        switch (path) {
        case "/alta":
            if (!requiereOrganizador(req, resp)) return;
            // dtCategorias normalmente lo provee CategoriasFilter; no requerimos llamar al publicador desde aquí
            req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
            return;
            case "/RegistrarseEvento":
                if (!requiereLogin(req, resp)) return;
                req.getRequestDispatcher(JSP_REGISTRO).forward(req, resp);
                return;
            case "/listado":
                PublicadorEvento portList = obtenerPort();
                DtEventoArray listaArrObj = null;
                try {
                    listaArrObj = portList.listarEventos();
                } catch (Exception ignore) {
                    listaArrObj = null;
                }
                List<DtEvento> lista = (listaArrObj == null || listaArrObj.getItem() == null) ? List.of() : listaArrObj.getItem();

                String cat = trim(req.getParameter("categoria"));
                if (!isBlank(cat)) {
                    List<DtEvento> filtered = new ArrayList<>();
                    for (DtEvento e : lista) {
                        try {
                            if (e != null && e.getCategorias() != null && e.getCategorias().getCategoria() != null) {
                                if (e.getCategorias().getCategoria().contains(cat)) filtered.add(e);
                            }
                        } catch (Exception ignore) {}
                    }
                    lista = filtered;
                    req.setAttribute("categoriaSeleccionada", cat);
                }

                req.setAttribute("lista", lista);
                java.util.Map<String,String> fotos = new java.util.HashMap<>();
                String ctx = ctx(req);
                for (DtEvento e : lista) {
                    if (e == null) continue;
                    try {
                        DtEvento source = e;
                        boolean hasImg = false;
                        try {
                            String imgCandidate = (e.getImagen() == null) ? null : e.getImagen().trim();
                            if (imgCandidate != null && !imgCandidate.isEmpty()) hasImg = true;
                        } catch (Exception ignore) { hasImg = false; }

                        if (!hasImg) {
                            try {
                              
                                DtEvento full = portList.consultaDTEvento(e.getNombre());
                                if (full != null) source = full;
                            } catch (Exception ignore) { /* ignore and keep original */ }
                        }

                        String url = resolveImagenUrl(req, source); 
                        fotos.put(e.getNombre(), url);
                        System.out.println("[FOTOS MAP] Evento: " + e.getNombre() + " -> " + url);
                    } catch (Exception ignore) { /* ignore and continue */ }
                }
                req.setAttribute("fotos", fotos);

                 // construir listado de categorías a partir de eventos
                Set<String> cats = new HashSet<>();
                for (DtEvento e : lista) {
                    if (e == null) continue;
                    try {
                        if (e.getCategorias() != null && e.getCategorias().getCategoria() != null) {
                            for (String s : e.getCategorias().getCategoria()) if (s != null) cats.add(s);
                        }
                    } catch (Exception ignore) {}
                }
                req.setAttribute("categorias", cats.isEmpty() ? List.of() : new ArrayList<>(cats));

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
            
            System.out.println("Intentando crear evento:");
            System.out.println("Nombre: " + nombre);
            System.out.println("Sigla: " + sigla);
            System.out.println("Descripción: " + desc);
            System.out.println("Categorías: " + cats);

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
                    String safeName = nombre.replaceAll("[^a-zA-Z0-9_-]", "_");
                    String ctype = imgPart.getContentType();
                    String ext = getExtension(getSafeFilename(imgPart));
                    if (isBlank(ext)) ext = guessExtensionFromContentType(ctype);
                    if (isBlank(ext)) ext = ".jpg";
                    String finalName = "IMG-" + safeName + ext;

                   
                    String tomcatBase = System.getProperty("catalina.base");
                    String baseImg = tomcatBase + "/webapps/ServidorCentral-0.0.1-SNAPSHOT/images/eventos";

                    Files.createDirectories(Path.of(baseImg));
                    Path destino = Path.of(baseImg, finalName);
                    imgPart.write(destino.toString());

                    imagenFileName = finalName;
                    System.out.println("[IMG] Guardada en: " + destino);
                }
            } catch (Exception fileEx) {
                req.setAttribute("error", "Error al procesar la imagen: " + fileEx.getMessage());
                req.getRequestDispatcher(JSP_ALTA).forward(req, resp);
                return;
            }

            // construir DtCategorias para el publicador
            DtCategorias dtCategorias = new DtCategorias();
            DtCategorias.Categorias inner = new DtCategorias.Categorias();
            inner.getCategoria().addAll(categoriasList);
            dtCategorias.setCategorias(inner);
            try {
            	String fechaAlta = LocalDate.now().toString(); // o cualquier fecha válida

                
                
                PublicadorEvento port = obtenerPort();
                port.altaEvento(nombre, desc, fechaAlta, sigla, dtCategorias, sigla);

                if (imagenFileName != null) {
                    // actualizarImagenEvento puede no existir en el stub; intentar reflectivamente
                    try {
                        Method m = port.getClass().getMethod("actualizarImagenEvento", String.class, String.class);
                        m.invoke(port, nombre, imagenFileName);
                    } catch (NoSuchMethodException nsme) {
                        // método no expuesto en el stub -> ignorar
                    } catch (Exception ex) {
                        System.err.println("No se pudo asociar imagen al evento: " + ex.getMessage());
                    }
                }

                String nombreEnc = URLEncoder.encode(nombre, StandardCharsets.UTF_8.name());
                resp.sendRedirect(ctx(req) + "/evento/ConsultaEvento?nombre=" + nombreEnc);
            } catch (EventoYaExisteException_Exception e) {
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
  
        	PublicadorEvento portFin = obtenerPort();
        	try {
        		Method m = portFin.getClass().getMethod("finalizarEvento", String.class);
        		m.invoke(portFin, nombreEvento);
        		
        	} catch (NoSuchMethodException nsme) {
        		
        	} catch (Exception ex) {
        		ex.printStackTrace();
        		
        	}
        	

		resp.sendRedirect(ctx(req) + "/inicio");
		return;
	}

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
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

    private String resolveImagenUrl(HttpServletRequest req, DtEvento ev) {
        String ctx = ctx(req);
        String raw = null;
        try { raw = (ev == null) ? null : ev.getImagen(); } catch (Exception ignore) { raw = null; }

        if (raw == null) raw = "";
        raw = raw.replace('\\', '/').trim();

        String lowRaw = raw.toLowerCase();
        if (lowRaw.startsWith("img/")) {
            raw = raw.substring(4); 
            lowRaw = raw.toLowerCase();
        } else if (lowRaw.startsWith("eventos/")) {
            raw = raw.substring("eventos/".length());
            lowRaw = raw.toLowerCase();
        }

        if (raw.isEmpty()) return ctx + "/img/eventos/evento-default.svg";

        if (lowRaw.startsWith("http://") || lowRaw.startsWith("https://")) return raw;

        if (raw.startsWith(ctx + "/")) return raw;

        if (raw.startsWith("/")) return ctx + raw;

        String[] candidates = new String[] {
            "/img/eventos/" + raw,
            "/img/" + raw,
            "/img/ediciones/" + raw,
            "/img/usuarios/" + raw,
            "/" + raw,            // raw might already include 'img/..'
            "/eventos/" + raw
        };

        java.util.List<String> unknown = new java.util.ArrayList<>();
        boolean anyReal = false;
        for (String rel : candidates) {
            try {
                String abs = getServletContext().getRealPath(rel);
                if (abs == null) {
                    unknown.add(rel);
                    continue;
                }
                anyReal = true;
                if (Files.exists(Path.of(abs))) return ctx + rel;
            } catch (Exception ignore) { }
        }

        if (!anyReal && !unknown.isEmpty()) {
            return ctx + unknown.get(0);
        }

        return ctx + "/img/eventos/evento-default.svg";
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