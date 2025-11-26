<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String rol = (String) session.getAttribute("rol");
  String nick = (String) session.getAttribute("nick");
  boolean precargado = Boolean.TRUE.equals(application.getAttribute("datosPrecargados"));

  java.util.List<logica.datatypes.DTEvento> eventos =
    (java.util.List<logica.datatypes.DTEvento>) request.getAttribute("eventos");

  java.util.Map<String,String> imgUrls =
    (java.util.Map<String,String>) request.getAttribute("imgUrls");
  //quisiera hacer responsive el index.jsp siguiendo los lineamientos de rwd
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
  <style>
    .cards{display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:16px}
    .card{border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;background:#fff;padding:12px}
    .card__media{position:relative; aspect-ratio:16/9; background:#f3f4f6; overflow:hidden; border-radius:10px; margin-bottom:8px}
    .card__media img{width:100%; height:100%; object-fit:cover; display:block}
    .card h2{margin:.25rem 0 .5rem}
    .card .actions{margin-top:.5rem}
    .btn-linklike{background:#f9fafb; border:1px solid #d1d5db; padding:.45rem .75rem; border-radius:8px; cursor:pointer}
    .btn-linklike:hover{background:#f3f4f6}

    /* ajuste cuando NO hay imagen: quitamos el espacio que dejaba la media */
    .card.no-media h2{ margin-top: 0; }
  </style>
</head>
<body>
  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row layout-inicio">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="main-inicio">
      <h1>Próximos eventos</h1>

      <div id="eventList" class="cards">
        <%
          if (eventos != null && !eventos.isEmpty()) {
            for (logica.datatypes.DTEvento e : eventos) {
              String imgUrl = (imgUrls == null) ? null : imgUrls.get(e.getNombre());
              boolean hasImg = (imgUrl != null && !imgUrl.isBlank());
        %>
          <article class="card <%= hasImg ? "" : "no-media" %>">
            <% if (hasImg) { %>
              <div class="card__media">
                <img src="<%= imgUrl %>" alt="Imagen de <%= e.getNombre() %>">
              </div>
            <% } %>

            <h2><%= e.getNombre() %></h2>
            <p><strong>Descripción:</strong> <%= e.getDescripcion() == null ? "" : e.getDescripcion() %></p>
            <p><strong>Fecha:</strong> <%= e.getFecha() == null ? "" : e.getFecha() %></p>

            <div class="actions">
              <form action="<%=ctx%>/evento/ConsultaEvento" method="get" style="display:inline">
                <input type="hidden" name="nombre" value="<%= e.getNombre() %>">
                <button type="submit" class="btn-linklike">Ver más</button>
              </form>
            </div>
          </article>
        <%
            }
          } else {
        %>
          <p>No hay eventos disponibles.</p>
        <%
          }
        %>
      </div>
    </main>
  </div>
</body>
</html>
