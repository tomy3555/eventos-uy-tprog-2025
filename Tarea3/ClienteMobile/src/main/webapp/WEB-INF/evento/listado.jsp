<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.net.URLEncoder, java.nio.charset.StandardCharsets, publicadores.DtEvento, java.util.List" %>
<%
String ctx = request.getContextPath();

  java.util.List<publicadores.DtEvento> lista =
      (java.util.List<publicadores.DtEvento>) request.getAttribute("lista");

  java.util.List<String> categorias =
      (java.util.List<String>) request.getAttribute("categorias");

  String catSel = (String) request.getAttribute("categoriaSeleccionada");
%>

<link rel="stylesheet" href="<%=ctx%>/css/style.css">
<link rel="stylesheet" href="<%=ctx%>/css/listado.css">

<jsp:include page="/WEB-INF/templates/header.jsp"/>

<style>
  .event-card.list.no-cover .event-title { margin-top: .25rem; }
</style>

<div class="container">
  <div class="page-list">
    <main class="content">
      <h1 class="list-title"><%= (catSel==null||catSel.isBlank()) ? "Todos los eventos" : "Eventos en " + catSel %></h1>
      <p class="list-sub">
        <%
          int n = (lista == null) ? 0 : lista.size();
          out.print(n + (n==1 ? " resultado" : " resultados"));
        %>
      </p>

      <div class="cards-grid">
        <% if (lista != null) for (publicadores.DtEvento ev : lista) {
             String nombre = ev.getNombre();
             String sigla  = ev.getSigla();
             String desc   = (ev.getDescripcion() == null ? "" : ev.getDescripcion());
             java.util.List<String> evCats = (ev.getCategorias() != null) ? ev.getCategorias().getCategoria() : null;
             String img = ev.getImagen();
             boolean hasImg = (img != null && !img.isBlank());
        %>
          <article class="card event-card list <%= hasImg ? "" : "no-cover" %>">
            <% if (hasImg) { %>
              <img class="event-cover" src="<%=ctx%>/img/<%=img%>" alt="Imagen de <%=nombre%>">
            <% } %>

            <h3 class="event-title"><%= nombre %></h3>
            <p class="event-sub"><%= (sigla==null||sigla.isBlank()) ? "—" : sigla %></p>

            <% if (desc != null && !desc.isBlank()) { %>
              <p class="event-desc"><%= desc %></p>
            <% } %>

            <% if (evCats != null && !evCats.isEmpty()) { %>
              <div class="chips" aria-label="Categorías">
                <% for (String c : evCats) { %>
                  <span class="chip"><%= c %></span>
                <% } %>
              </div>
            <% } %>

            <div class="event-footer">
              <span class="event-meta"></span>
              <form action="<%=ctx%>/evento/ediciones/ListadoEdiciones" method="get" style="display:inline;">
                <input type="hidden" name="evento" value="<%= nombre %>" />
                <button type="submit" class="btn btn-primary">Ver ediciones</button>
              </form>
            </div>
          </article>
        <% } %>
      </div>
    </main>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>