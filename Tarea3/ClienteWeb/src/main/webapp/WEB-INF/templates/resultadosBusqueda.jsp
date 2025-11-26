<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="publicadores.DtEvento" %>
<%@ page import="publicadores.DtEdicion" %>
<%
    String ctx = request.getContextPath();
    String query = (String) request.getAttribute("query");
    List<DtEvento> eventos = (List<DtEvento>) request.getAttribute("resultEventos");
    List<DtEdicion> ediciones = (List<DtEdicion>) request.getAttribute("resultEdiciones");
    String orden = request.getParameter("orden") != null ? request.getParameter("orden") : "fecha";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Resultados de búsqueda — Eventos.uy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Estilos globales -->
    <link rel="stylesheet" href="<%=ctx%>/css/style.css">
    <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEdicionBase.css">
    <link rel="stylesheet" href="<%=ctx%>/css/ConsultaEdicion.css">
    <link rel="stylesheet" href="<%=ctx%>/css/Custom.css">
    <link rel="stylesheet" href="<%=ctx%>/css/Busqueda.css">


</head>
<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />

<main class="page-busqueda-wrapper">

    <!-- Sidebar de filtros / orden -->
    <aside class="busqueda-sidebar">
        <h2>Ordenar resultados</h2>
        <form class="orden-form" method="get" action="<%=ctx%>/buscar">
            <input type="hidden" name="q" value="<%= query != null ? query : "" %>">
            <label for="orden">Ordenar por</label>
            <select name="orden" id="orden" onchange="this.form.submit()">
                <option value="fecha" <%= "fecha".equals(orden) ? "selected" : "" %>>
                    Fecha de alta (desc)
                </option>
                <option value="alfabetico_asc" <%= "alfabetico_asc".equals(orden) ? "selected" : "" %>>
                    Alfabético (A-Z)
                </option>
                <option value="alfabetico_desc" <%= "alfabetico_desc".equals(orden) ? "selected" : "" %>>
                    Alfabético (Z-A)
                </option>
            </select>
        </form>
        <%-- Espacio para futuros filtros (categoría, fecha, etc.) --%>
    </aside>

    <!-- Resultados -->
    <section class="busqueda-main">
        <h1>
          Resultados
          <% if (query != null && !query.isBlank()) { %>
            <span class="query-chip"><%= query %></span>
          <% } %>
        </h1>

        <%
          boolean hayEv = (eventos != null && !eventos.isEmpty());
          boolean hayEd = (ediciones != null && !ediciones.isEmpty());
          if (hayEv || hayEd) {
        %>

        <div class="result-grid">
          <% if (hayEv) {
               for (DtEvento ev : eventos) { %>
            <article class="result-card card">
                <div class="result-head">
                    <div>Eventos.uy</div>
                </div>

                <div class="result-body">
                    <h2 class="result-title"><%= ev.getNombre() %></h2>
                    <div class="result-tipo"><span class="chip">Evento</span></div>

                    <div class="result-actions">
                        <form action="<%=ctx%>/evento/ConsultaEvento" method="get" style="display:inline">
                            <input type="hidden" name="nombre" value="<%= ev.getNombre() %>">
                            <button type="submit" class="btn-vermas">Ver más</button>
                        </form>
                    </div>
                </div>
            </article>
          <% }} %>

          <% if (hayEd) {
               for (DtEdicion ed : ediciones) {
                   String eventoNombre = "";
                   if (ed.getEvento() != null && ed.getEvento().getNombre() != null) {
                       eventoNombre = ed.getEvento().getNombre();
                   }
          %>
            <article class="result-card card">
                <div class="result-head">
                    <div>Eventos.uy</div>
                </div>

                <div class="result-body">
                    <h2 class="result-title"><%= ed.getNombre() %></h2>
                    <div class="result-tipo"><span class="chip">Edición</span></div>

                    <div class="result-actions">
                        <form action="<%=ctx%>/edicion/ConsultaEdicion" method="get" style="display:inline">
                            <input type="hidden" name="evento" value="<%= eventoNombre %>">
                            <input type="hidden" name="edicion" value="<%= ed.getNombre() %>">
                            <button type="submit" class="btn-vermas">Ver más</button>
                        </form>
                    </div>
                </div>
            </article>
          <% }} %>
        </div>

        <% } else { %>
          <div class="no-results">No se encontraron resultados.</div>
        <% } %>
    </section>

</main>

</body>
</html>