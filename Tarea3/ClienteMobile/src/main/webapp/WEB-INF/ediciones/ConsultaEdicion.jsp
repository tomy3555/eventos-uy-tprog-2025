<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="publicadores.DtEdicion, publicadores.DtRegistro, publicadores.DtTipoRegistro, publicadores.DtPatrocinio" %>
<%
  String ctx  = request.getContextPath();
  String nick = (String) session.getAttribute("nick");
  String rol  = (String) session.getAttribute("rol");

  DtEdicion edicion = (DtEdicion) request.getAttribute("edicion");
  String organizador = (String) request.getAttribute("organizador");
  @SuppressWarnings("unchecked")
  List<DtRegistro> registros = (List<DtRegistro>) request.getAttribute("registros");
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
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEdicionBase.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEdicion.css">
  <style>
    /* Wider layout for edition view */
    .page-consulta-edicion .ed-center{ max-width: 1400px !important; margin: 0 auto !important; padding: 0 1rem; }
    .page-consulta-edicion .event-card{ border-radius: 12px; overflow: hidden; }
    /* Larger media frame for images and embedded video */
    .page-consulta-edicion .img-frame{ width:100% !important; max-width:1200px !important; aspect-ratio:16/9; background:#f3f4f6; border-radius:14px; overflow:hidden; margin:.5rem auto 1rem; }
    .page-consulta-edicion .img-frame img{ width:100%; height:100%; object-fit:cover; display:block; }
    /* Make the info column wider to match the larger media */
    .page-consulta-edicion .event-info.event-text{ max-width:1100px !important; margin:0 auto !important; }
    .page-consulta-edicion .event-header{ text-align:center; }
    .page-consulta-edicion.no-img .event-info.event-text{ margin-top:.5rem !important; }
    /* Larger iframe behavior: allow taller height while keeping responsive width */
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
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />

<div class="container row page-consulta-edicion <%= hasAnyImg ? "" : "no-img" %>" style="margin-top:1rem; display:flex; align-items:flex-start;">

  <main class="container consulta-layout" style="flex:2; min-width:0;">
    <div class="ed-center">
      <section class="event-card">
        <div class="event-header">
          <h1 class="event-title"><%= (edicion != null ? edicion.getNombre() : "Edición") %></h1>
        </div>

	<% String imagen = (edicion != null && edicion.getImagen() != null) ? edicion.getImagen() : ""; %>
	<% String ip = (String) request.getAttribute("ipServidor"); %>
	<% String puerto = (String) request.getAttribute("puertoServidor"); %>
	<% String urlCompleta = "http://" + ip + ":8080/ServidorCentral-0.0.1-SNAPSHOT/images/ediciones/" + imagen; %>
	<% if (hasAnyImg && imagen != null && !imagen.isBlank()) { %>
	  <div class="img-frame">
	    <img src="<%= urlCompleta %>"
	         alt="Imagen de la edición <%= (edicion != null ? edicion.getNombre() : "") %>"
	         onerror="this.onerror=null;this.src='<%=ctx%>/img/evento-default.jpg';">
	    <div style="margin-top:8px;font-size:0.95em;color:#555;text-align:center;">
	      URL: <span style="word-break:break-all;"><%= urlCompleta %></span>
	    </div>
	  </div>
	<% } %>

        <% if (hasVideo) { %>
          <div class="img-frame video-frame" style="margin: 0.5rem auto 1rem;">
            <iframe width="100%" src="<%= edVideoEmbed %>" title="Video de la edición" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
            <p style="text-align:center; font-size:0.9rem; margin-top:0.5rem;"><a href="<%= edVideoRaw %>" target="_blank">Ver en nueva pestaña</a></p>
          </div>
        <% } %>

        <div class="event-info event-text" style="padding: 15px; line-height: 2">
          <h3>Datos de la Edición</h3>
          <% if (edicion != null) { %>
            <div class="event-meta"><strong>Evento:</strong> <%= (evNombre != null ? evNombre : "—") %></div>
            <div class="event-meta"><strong>Sigla:</strong> <%= edicion.getSigla() %></div>
            <div class="event-meta"><strong>Fecha inicio:</strong> <%= edicion.getFechaInicio() %></div>
            <div class="event-meta"><strong>Fecha fin:</strong> <%= edicion.getFechaFin() %></div>
            <div class="event-meta"><strong>Fecha alta:</strong> <%= edicion.getFechaAlta() %></div>
            <div class="event-meta"><strong>Ciudad:</strong> <%= edicion.getCiudad() %></div>
            <div class="event-meta"><strong>País:</strong> <%= edicion.getPais() %></div>
            <div class="event-meta"><strong>Estado:</strong> <%= (edicion.getEstado() != null ? edicion.getEstado() : "—") %></div>
            <div class="event-meta"><strong>Organizador:</strong> <%= (organizador != null && !organizador.isBlank()) ? organizador : "No disponible" %></div>
            <div class="event-meta"><strong>Tipos de Registro:</strong>
            
              <% if (tiposRegistro != null && !tiposRegistro.isEmpty()) { %>
                <ul>
                  <% for (DtTipoRegistro tr : tiposRegistro) { %>
                    <li>
                      <strong><%= tr.getNombre() %></strong>
                    </li>
                  <% } %>
                </ul>
              <% } else { %>
                No hay tipos de registro asociados.
              <% } %>
            </div>
            <div class="event-meta"><strong>Patrocinios:</strong>
              <% if (patrocinios != null && !patrocinios.isEmpty()) { %>
                <ul>
                  <% for (DtPatrocinio p : patrocinios) { %>
                    <li>
                      <strong><%= p.getInstitucion() %></strong>
                    </li>
                  <% } %>
                </ul>
              <% } else { %>
                No hay patrocinios asociados.
              <% } %>
            </div>
          <% } %>

          <% if (registros != null && !registros.isEmpty() && "ASISTENTE".equals(rol) && registros.size() == 1) {
               DtRegistro registro = registros.get(0);
          %>
            <form action="<%= ctx %>/registro/ConsultaRegistroEdicion" method="get" class="mt-3">
              <input type="hidden" name="usuario" value="<%= nick %>" />
              <input type="hidden" name="edicion" value="<%= edicion != null ? edicion.getNombre() : "" %>" />
              <button type="submit" class="btn btn-primary w-100">Ver detalles de mi registro</button>
            </form>
          <% } else if (registros != null && !registros.isEmpty() && "ORGANIZADOR".equals(rol) && edicion != null && organizador != null && organizador.equals(nick)) { %>
            <h3>Asistentes registrados</h3>
            <ul class="lista-asistentes">
              <% int i = 0;
                 for (DtRegistro registro : registros) {
                   String id = "detalle-" + i++;
              %>
                <li class="asistente-item">
                  <button class="asistente-btn" type="button" onclick="toggleDetalles('<%=id%>')">
                    👤 <%= registro.getUsuario() %>
                  </button>
                  <div id="<%=id%>" class="asistente-detalle oculto">
                    <p><strong>Tipo:</strong> <%= registro.getTipoRegistro() %></p>
                    <p><strong>Fecha registro:</strong> <%= registro.getFechaRegistro() %></p>
                    <p><strong>Costo:</strong> $<%= registro.getCosto() %></p>
                  </div>
                </li>
              <% } %>
            </ul>
          <% } else if (registros != null && !registros.isEmpty()) { %>
            <h3>Registros de la edición</h3>
            <table class="tabla-registros" style="width:100%; border-collapse:collapse; margin-bottom:1rem;">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Tipo</th>
                  <th>Fecha registro</th>
                  <th>Costo</th>
                </tr>
              </thead>
              <tbody>
                <% for (DtRegistro r : registros) { %>
                  <tr>
                    <td><%= r.getUsuario() %></td>
                    <td><%= r.getTipoRegistro() %></td>
                    <td><%= r.getFechaRegistro() %></td>
                    <td>$<%= r.getCosto() %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          <% } else { %>
            <p>No estás registrado a esta edición.</p>
          <% } %>
        </div>
      </section>
    </div>
  </main>
</div>

<script>
  function toggleDetalles(id) {
    const detalle = document.getElementById(id);
    if (detalle) detalle.classList.toggle('oculto');
  }
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>