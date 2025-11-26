<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="publicadores.PublicadorEventoService, publicadores.DtEvento, publicadores.DtEdicion, publicadores.StringArray, publicadores.LocalDate, java.util.List, java.util.ArrayList, java.util.Collections" %>
<%
  String ctx  = request.getContextPath();
  String nick = (String) session.getAttribute("nick");

  DtEvento ev = null;
  Object reqEv = request.getAttribute("evento");
  String paramNombre = request.getParameter("nombre");
  try {
    if (reqEv instanceof DtEvento) {
      ev = (DtEvento) reqEv;
    } else if (paramNombre != null && !paramNombre.isBlank()) {
      PublicadorEventoService svc = new PublicadorEventoService();
      publicadores.PublicadorEvento port = svc.getPublicadorEventoPort();
      ev = port.consultaDTEvento(paramNombre);
    }
  } catch (Exception ex) {
    ex.printStackTrace();
  }

  String evNombre = (ev != null ? ev.getNombre() : null);
  String evSigla  = (ev != null ? ev.getSigla() : null);
  String evDesc   = (ev != null ? ev.getDescripcion() : null);
  String evFecha  = (ev != null ? String.valueOf(ev.getFecha()) : "");
  List<String> evCategorias = (ev != null && ev.getCategorias() != null)
      ? ev.getCategorias().getCategoria() : Collections.emptyList();

  String evImagenUrl = (String) request.getAttribute("evImagenUrl");
  boolean hasImgCandidate = (evImagenUrl != null && !evImagenUrl.isBlank());

  List<DtEdicion> ediciones = (List<DtEdicion>) request.getAttribute("evEdiciones");
  try {
    if ((ediciones == null || ediciones.isEmpty()) && ev != null) {
      PublicadorEventoService svc2 = new PublicadorEventoService();
      publicadores.PublicadorEvento port2 = svc2.getPublicadorEventoPort();
      StringArray arr = port2.listarEdicionesEvento(ev.getNombre());
      ediciones = new ArrayList<>();
      if (arr != null && arr.getItem() != null) {
        for (String nombreEd : arr.getItem()) {
          DtEdicion dt = port2.obtenerDtEdicion(ev.getNombre(), nombreEd);
          if (dt != null) ediciones.add(dt);
        }
      }
    }
  } catch (Exception ex) {
    ex.printStackTrace();
    if (ediciones == null) ediciones = Collections.emptyList();
  }

  String rolUsuario = (String) request.getAttribute("rol");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta de Evento — <%= (evNombre != null ? evNombre : "Evento") %></title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEventoBase.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
  <style>
    .event-hero { display:flex; gap:1rem; align-items:flex-start; margin-bottom:1rem; }
    .event-hero.no-img { display:block; }
    .event-hero__img { display:none; width:360px; max-width:40vw; aspect-ratio:16/9; background:#f3f4f6; border-radius:12px; overflow:hidden; flex-shrink:0; }
    .event-hero.has-img .event-hero__img { display:block; }
    .event-hero__img img { width:100%; height:100%; object-fit:cover; display:block; }
    .chips { display:flex; flex-wrap:wrap; gap:.4rem; }
    .chip { background:#eef2ff; color:#3730a3; padding:.2rem .5rem; border-radius:999px; font-size:.9rem; }
    .ediciones-list { list-style:none; padding:0; margin:0; display:grid; gap:.6rem; }
    .ediciones-list li { display:flex; align-items:center; gap:.5rem; justify-content:space-between; border:1px solid var(--line); border-radius:10px; padding:.5rem .75rem; background:#fff; }
    .btn { border:none; padding:.35rem .7rem; border-radius:8px; cursor:pointer; }
    .btn-ver-detalles { background:#111827; color:#fff; }
  </style>
  <link rel="stylesheet" href="<%=ctx%>/css/custom.css">
</head>
<body>

  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row layout-inicio" style="display:flex; align-items:flex-start;">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="container consulta-evento-main" style="flex:2; min-width:0; padding:15px; line-height:2;">
      <section class="card event-card">
        <div class="event-hero <%= hasImgCandidate ? "" : "no-img" %>">
          <% if (hasImgCandidate) { %>
            <div class="event-hero__img">
              <img
                src="<%= evImagenUrl %>"
                alt="Imagen de <%= (evNombre != null ? evNombre : "Evento") %>"
                onload="this.closest('.event-hero')?.classList.add('has-img');"
                onerror="this.onerror=null;this.src='<%=ctx%>/img/eventos/evento-default.svg';"
              >
            </div>
          <% } %>

          <div class="event-hero__meta">
            <h1 class="event-title"><strong>Evento <%= (evNombre != null ? evNombre : "Evento") %></strong></h1>

            <div class="event-info">
              <h3>Descripción</h3>
              <p><%= (evDesc != null ? evDesc : "—") %></p>

              <div class="event-meta"><strong>Sigla:</strong> <%= (evSigla != null ? evSigla : "—") %></div>

              <div class="chips" style="margin:.5rem 0 0;">
                <span class="categorias-label" style="margin-right:.3rem;"><strong>Categorías:</strong></span>
                <% if (evCategorias == null || evCategorias.isEmpty()) { %>
                  <span class="chip">—</span>
                <% } else { for (String c : evCategorias) { %>
                  <span class="chip"><%= c %></span>
                <% } } %>
              </div>

              <div class="event-meta" style="margin-top:.5rem;">
                <p><strong>Fecha:</strong> <%= evFecha %></p>
              </div>

              <% if ("ORGANIZADOR".equalsIgnoreCase(rolUsuario)) { %>
                <form action="<%= ctx %>/evento/FinalizarEvento" method="post" style="margin-top:1rem;">
                  <input type="hidden" name="nombreEvento" value="<%= evNombre %>" />
                  <button type="submit" class="btn btn-finalizar-evento">Finalizar evento</button>
                </form>
              <% } %>
            </div>
          </div>
        </div>
      </section>
    </main>

    <aside class="card event-card" style="min-width:300px; flex:1; margin-left:2rem; align-self:flex-start;">
      <h3>Ediciones</h3>
      <% if (ediciones == null || ediciones.isEmpty()) { %>
        <p>No hay ediciones asociadas a este evento.</p>
      <% } else { %>
        <ul class="ediciones-list li">
        <% for (DtEdicion ed : ediciones) { 
        	if (ed.getEstado().toString() == "ACEPTADA"){%>
          <li>
            <div>
              <strong><%= ed.getNombre() %></strong>
              <span>(<%= ed.getFechaInicio() %> - <%= ed.getFechaFin() %>)</span>
            </div>
            <form action="<%= ctx %>/edicion/ConsultaEdicion" method="get" style="display:inline;">
              <input type="hidden" name="evento" value="<%= evNombre %>" />
              <input type="hidden" name="edicion" value="<%= ed.getNombre() %>" />
              <button type="submit" class="btn btn-ver-detalles">Ver detalles</button>
            </form>
          </li>
        <% }} %>
        </ul>
      <% } %>
    </aside>
  </div>

</body>
</html>