<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="publicadores.DtEdicion, publicadores.DtRegistro, publicadores.DtTipoRegistro, publicadores.DtPatrocinio, publicadores.PublicadorEvento, publicadores.PublicadorEventoService" %>
<%
  String ctx  = request.getContextPath();
  String nick = (String) session.getAttribute("nick");
  String rol  = (String) session.getAttribute("rol");

  DtEdicion edicion = (DtEdicion) request.getAttribute("edicion");
  String organizador = (String) request.getAttribute("organizador");
  @SuppressWarnings("unchecked")
  List<DtTipoRegistro> tiposRegistro = (List<DtTipoRegistro>) request.getAttribute("tiposRegistro");
  @SuppressWarnings("unchecked")
  List<DtPatrocinio> patrocinios = (List<DtPatrocinio>) request.getAttribute("patrocinios");
  String evNombre = (String) request.getAttribute("evNombre");

  String edImagenUrl = (String) request.getAttribute("edImagenUrl");
  boolean hasAnyImg = (edImagenUrl != null && !edImagenUrl.isBlank());
  String edVideoRaw = (edicion != null) ? edicion.getVideo() : null;
  String edVideoEmbed = null;
  boolean hasVideo = false;

  if (edVideoRaw != null && !edVideoRaw.isBlank()) {
    String lower = edVideoRaw.toLowerCase();
    try {
      if (lower.contains("youtube.com/watch") || lower.contains("youtube.com/watch?")) {
        int idx = edVideoRaw.indexOf("v=");
        if (idx >= 0) {
          String id = edVideoRaw.substring(idx + 2);
          int amp = id.indexOf('&');
          if (amp > 0) id = id.substring(0, amp);
          edVideoEmbed = "https://www.youtube.com/embed/" + id;
        }
      } else if (lower.contains("youtu.be/")) {
        int slash = edVideoRaw.lastIndexOf('/');
        if (slash >= 0) {
          String id = edVideoRaw.substring(slash + 1);
          int q = id.indexOf('?'); if (q > 0) id = id.substring(0, q);
          edVideoEmbed = "https://www.youtube.com/embed/" + id;
        }
      } else if (lower.contains("vimeo.com/")) {
        int slash = edVideoRaw.lastIndexOf('/');
        if (slash >= 0) {
          String id = edVideoRaw.substring(slash + 1);
          int q = id.indexOf('?'); if (q > 0) id = id.substring(0, q);
          edVideoEmbed = "https://player.vimeo.com/video/" + id;
        }
      } else if (lower.startsWith("http://") || lower.startsWith("https://")) {
        edVideoEmbed = edVideoRaw;
      }
    } catch (Exception ignore) { edVideoEmbed = edVideoRaw; }
    hasVideo = (edVideoEmbed != null && !edVideoEmbed.isBlank());
  }
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta de Edición — <%=(edicion != null ? edicion.getNombre() : "Edición")%></title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEdicionBase.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEdicion.css">
  <style>
    .page-consulta-edicion .ed-center{ max-width: 1400px !important; margin: 0 auto !important; padding: 0 1rem; }
    .page-consulta-edicion .event-card{ border-radius: 12px; overflow: hidden; }
    .page-consulta-edicion .img-frame{ width:100% !important; max-width:1200px !important; aspect-ratio:16/9; background:#f3f4f6; border-radius:14px; overflow:hidden; margin:.5rem auto 1rem; }
    .page-consulta-edicion .img-frame img{ width:100%; height:100%; object-fit:cover; display:block; }
    .page-consulta-edicion .event-info.event-text{ max-width:1100px !important; margin:0 auto !important; }
    .page-consulta-edicion .event-header{ text-align:center; }
    .page-consulta-edicion.no-img .event-info.event-text{ margin-top:.5rem !important; }
    .page-consulta-edicion .video-frame iframe { height: 640px; max-height: 80vh; }
    @media (max-width: 1200px){
      .page-consulta-edicion .img-frame{ max-width:100% !important; }
      .page-consulta-edicion .event-info.event-text{ max-width:100% !important; }
      .page-consulta-edicion .video-frame iframe { height: 420px; }
    }
    @media (max-width: 900px){
      .page-consulta-edicion .img-frame{ max-width:100% !important; }
      .page-consulta-edicion .event-info.event-text{ max-width:100% !important; }
    }
    @media (max-width: 600px){
      .page-consulta-edicion .video-frame iframe { height: 260px; }
    }
    .registro-detalle {
      border: 1px solid #e5e7eb;
      background: #fafafa;
      border-radius: 10px;
      padding: 1rem;
      margin-top: 1rem;
    }
    .registro-detalle h3 { margin-top: 0; font-size: 1.1rem; }
    .link-button {
      background:none;
      border:none;
      color:#2563eb;
      text-decoration:underline;
      cursor:pointer;
      font:inherit;
      padding:0;
    }
    .link-button:hover { color:#1e40af; }
  </style>
</head>

<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />

<div class="container row page-consulta-edicion <%= hasAnyImg ? "" : "no-img" %>" style="margin-top:1rem; display:flex; align-items:flex-start;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />

  <main class="container page-consulta-edicion" style="margin-top:1.5rem;">
    <section class="card event-card"
      style="display:grid; grid-template-columns: 1fr 1.2fr; gap:2rem;
             max-width:1100px; margin:0 auto; padding:1.5rem; align-items:start;">

      <% if (hasAnyImg) { %>
        <div class="img-frame">
          <img src="<%= edImagenUrl %>" alt="Imagen de la edición <%= edicion.getNombre() %>">
        </div>
      <% } %>

      <div class="event-info" style="display:flex; flex-direction:column; gap:1.5rem;">

        <% if (hasVideo) { %>
          <div class="video-frame">
            <iframe src="<%= edVideoEmbed %>" allowfullscreen style="width:100%; border:none; border-radius:10px;"></iframe>
          </div>
        <% } %>

        <div>
          <h1 style="font-size:1.6rem; font-weight:700; margin-bottom:.5rem;">
            <strong>Edición de Evento <%= edicion.getNombre() %></strong>
          </h1>
          <p style="font-size:1rem; color:#444;">
            <strong>Evento:</strong>
            <a href="<%=ctx%>/evento/ConsultaEvento?nombre=<%= java.net.URLEncoder.encode(evNombre, java.nio.charset.StandardCharsets.UTF_8) %>"
               style="color:inherit; text-decoration:none;"><%= evNombre %></a>
            —
            <strong>Organizador:</strong>
            <a href="<%=ctx%>/usuario/ConsultaUsuario?nick=<%=organizador%>"
               style="color:inherit; text-decoration:none;"><%= organizador %></a>
          </p>

          <div class="event-meta" style="display:grid; grid-template-columns:repeat(auto-fit,minmax(140px,1fr)); gap:.25rem .75rem; font-size:.95rem; color:#555;">
            <div><strong>Sigla:</strong> <%= edicion.getSigla() %></div>
            <div><strong>Ciudad:</strong> <%= edicion.getCiudad() %></div>
            <div><strong>País:</strong> <%= edicion.getPais() %></div>
            <div><strong>Inicio:</strong> <%= edicion.getFechaInicio() %></div>
            <div><strong>Fin:</strong> <%= edicion.getFechaFin() %></div>
            <div><strong>Alta:</strong> <%= edicion.getFechaAlta() %></div>
            <div><strong>Estado:</strong> <%= edicion.getEstado() %></div>
          </div>
        </div>

        <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
          <div>
            <h3>Tipos de Registro</h3>
            <% if (tiposRegistro != null && !tiposRegistro.isEmpty()) { %>
              <ul style="list-style:none; padding:0;">
                <% for (DtTipoRegistro tr : tiposRegistro) { %>
                  <li style="margin:.3rem 0;">
                    <strong><%= tr.getNombre() %></strong>
                    <form action="<%=ctx%>/registro/ConsultaTipoRegistro" method="get" style="display:inline;">
                      <input type="hidden" name="evento" value="<%= evNombre %>">
                      <input type="hidden" name="edicion" value="<%= edicion.getNombre() %>">
                      <input type="hidden" name="tipoRegistro" value="<%= tr.getNombre() %>">
                      <button type="submit" class="btn btn-ver-detalles" style="margin-left:0.5rem;">Ver</button>
                    </form>
                  </li>
                <% } %>
              </ul>
            <% } else { %>
              <p style="color:#666;">Ninguno.</p>
            <% } %>
          </div>

          <div>
            <h3>Patrocinios</h3>
            <% if (patrocinios != null && !patrocinios.isEmpty()) { %>
              <ul style="list-style:none; padding:0;">
                <% for (DtPatrocinio p : patrocinios) { %>
                  <li style="margin:.3rem 0;">
                    <strong><%= p.getInstitucion() %></strong>
                    <form action="<%=ctx%>/edicion/ConsultaPatrocinio" method="get" style="display:inline;">
                      <input type="hidden" name="evento" value="<%= evNombre %>">
                      <input type="hidden" name="edicion" value="<%= edicion.getNombre() %>">
                      <input type="hidden" name="codigoPatrocinio" value="<%= p.getCodigo() %>">
                      <button type="submit" class="btn btn-ver-detalles" style="margin-left:0.5rem;">Ver</button>
                    </form>
                  </li>
                <% } %>
              </ul>
            <% } else { %>
              <p style="color:#666;">Ninguno.</p>
            <% } %>
          </div>
        </div>

        <% 
          if (rol != null && rol.equalsIgnoreCase("organizador") && organizador != null && organizador.equalsIgnoreCase(nick)) {
            PublicadorEventoService svc = new PublicadorEventoService();
            PublicadorEvento port = svc.getPublicadorEventoPort();
            DtEdicion edAux = port.obtenerDtEdicion(evNombre, edicion.getNombre());
            if (edAux != null && edAux.getRegistros() != null && edAux.getRegistros().getRegistro() != null) {
              List<DtRegistro> regs = edAux.getRegistros().getRegistro();
              if (!regs.isEmpty()) {
        %>
<div class="registro-detalle">
  <h3>Usuarios registrados en esta edición</h3>
  <table style="width:100%; border-collapse:collapse; font-size:0.95rem;">
    <thead>
      <tr style="background:#e5e7eb;">
        <th style="text-align:left; padding:0.5rem;">Usuario</th>
        <th style="text-align:left; padding:0.5rem;">Asistió</th>
      </tr>
    </thead>
    <tbody>
      <% for (DtRegistro r : regs) { 
           Boolean asistencia = r.isAsistio();
           boolean asistio = (asistencia != null && asistencia);
      %>
        <tr style="border-bottom:1px solid #ddd;">
          <td style="padding:0.5rem;">
            <form action="<%=ctx%>/registro/ConsultaRegistroEdicion" method="get" style="display:inline;">
              <input type="hidden" name="idRegistro" value="<%= r.getIdentificador() %>">
              <input type="hidden" name="evento" value="<%= evNombre %>">
              <input type="hidden" name="edicion" value="<%= edicion.getNombre() %>">
              <button type="submit" class="link-button"><%= r.getUsuario() %></button>
            </form>
          </td>
          <td style="padding:0.5rem; color:<%= asistio ? "green" : "red" %>;">
            <%= asistio ? "Sí" : "No" %>
          </td>
        </tr>
      <% } %>
    </tbody>
  </table>
</div>
        <% 
              }
            }
          } 
        %>

        <%-- Mostrar datos del registro si el usuario es asistente y está registrado --%>
        <% if (rol != null && rol.equalsIgnoreCase("asistente") && edicion != null && edicion.getRegistros() != null && edicion.getRegistros().getRegistro() != null && nick != null) {
             DtRegistro miRegistro = null;
             for (DtRegistro r : edicion.getRegistros().getRegistro()) {
               if (nick.equals(r.getUsuario())) {
                 miRegistro = r;
                 break;
               }
             }
             if (miRegistro != null) {
        %>
        <div class="registro-detalle" style="border: 1px solid #e5e7eb; background: #fafafa; border-radius: 10px; padding: 1rem; margin-top: 1rem;">
          <h3>Tu registro en esta edición</h3>
          <p><strong>Tipo:</strong> <%= miRegistro.getTipoRegistro() %></p>
          <p><strong>Fecha registro:</strong> <%= miRegistro.getFechaRegistro() %></p>
          <p><strong>Costo:</strong> $<%= miRegistro.getCosto() %></p>
          <form action="<%=ctx%>/registro/ConsultaRegistroEdicion" method="get" style="margin-top:1rem;">
            <input type="hidden" name="idRegistro" value="<%= miRegistro.getIdentificador() %>" />
            <input type="hidden" name="evento" value="<%= evNombre %>" />
            <input type="hidden" name="edicion" value="<%= edicion.getNombre() %>" />
            <button type="submit" class="btn btn-ver-detalles">Ver detalles de mi registro</button>
          </form>
        </div>
        <%   }
           }
        %>
      </div>
    </section>
  </main>
</div>
</body>
</html>