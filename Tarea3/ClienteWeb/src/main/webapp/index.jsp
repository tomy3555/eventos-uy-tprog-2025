<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="publicadores.DtEvento, java.util.*" %>
<%
  String ctx = request.getContextPath();
  List<DtEvento> eventos = (List<DtEvento>) request.getAttribute("eventos");
  String baseUrl = (String) request.getAttribute("baseUrl");
  Map<String,String> imgUrls = (Map<String,String>) request.getAttribute("imgUrls");
  Map<String,String> imgChecks = (Map<String,String>) request.getAttribute("imgChecks");
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
    .card{border:1px solid #e5e7eb;border-radius:12px;background:#fff;padding:12px}
    .card__media{aspect-ratio:16/9;background:#f3f4f6;border-radius:10px;overflow:hidden;margin-bottom:8px}
    .card__media img{width:100%;height:100%;object-fit:cover;display:block}
    .btn{background:#f9fafb;border:1px solid #d1d5db;padding:.45rem .75rem;border-radius:8px;cursor:pointer}
    .btn:hover{background:#f3f4f6}
    .debug { font-size: 0.8rem; color: #666; margin-top:4px }
  </style>
</head>
<body>
  <jsp:include page="/WEB-INF/templates/header.jsp" />
  <div class="container row layout-inicio">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />
    <main class="main-inicio">
      <h1>Próximos eventos</h1>
      <div class="cards">
        <%
          if (eventos != null && !eventos.isEmpty()) {
            for (DtEvento e : eventos) {
              String img = e.getImagen();
              String imgUrl = null;
              if (imgUrls != null) imgUrl = imgUrls.get(e.getNombre());
              if (imgUrl == null || imgUrl.isBlank()) {
                imgUrl = (img != null && !img.isBlank()) ? (baseUrl + "eventos/" + img) : (baseUrl + "eventos/evento-default.svg");
              }
              String check = (imgChecks != null) ? imgChecks.get(e.getNombre()) : null;
        %>
          <article class="card">
            <div class="card__media">
              <img src="<%= imgUrl %>" alt="<%= e.getNombre() %>"
                   onerror="this.onerror=null;this.src='<%=baseUrl%>eventos/evento-default.svg';">
            </div>
            <h2><%= e.getNombre() %></h2>
            <p><%= e.getDescripcion()==null ? "" : e.getDescripcion() %></p>
            <form action="<%=ctx%>/evento/ConsultaEvento" method="get">
              <input type="hidden" name="nombre" value="<%= e.getNombre() %>">
              <button type="submit" class="btn">Ver más</button>
            </form>
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