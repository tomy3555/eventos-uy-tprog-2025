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
  /* si la tarjeta no tiene cover, ajusto paddings/márgenes */
  .event-card.list.no-cover .event-title { margin-top: .25rem; }
</style>

<div class="container">
  <div class="page-list">
    <jsp:include page="/WEB-INF/templates/menu.jsp"/>

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
             java.util.List<String> evEds  = (ev.getEdiciones() != null) ? ev.getEdiciones().getEdicion() : null;

             String img = ev.getImagen();
             String imgUrl = null;

             java.util.Map<String,String> fotos = (java.util.Map<String,String>) request.getAttribute("fotos");
             if (fotos != null && fotos.get(nombre) != null) {
                 imgUrl = fotos.get(nombre);
             } else {
                 boolean hasImg = (img != null && !img.isBlank());
                 if (hasImg) {
                     if (img.startsWith("http://") || img.startsWith("https://")) {
                       imgUrl = img;
                     } else if (img.startsWith("/")) {
                       imgUrl = ctx + img;
                     } else {
                       imgUrl = ctx + "/img/eventos/" + img;
                     }
                 }
             }
             boolean hasImg = (imgUrl != null && !imgUrl.isBlank());
        %>
          <article class="card event-card list <%= hasImg ? "" : "no-cover" %>">
             <% if (hasImg) { %>
    <img class="event-cover" 
         src="<%= imgUrl %>" 
         alt="Imagen de <%= nombre %>"
         onerror="this.style.display='none';">
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

            <% if (evEds != null && !evEds.isEmpty()) { %>
              <div class="chips" aria-label="Ediciones">
                <% for (String ed : evEds) { %>
                  <span class="chip"><%= ed %></span>
                <% } %>
              </div>
            <% } %>

            <div class="event-footer">
              <span class="event-meta"></span>
              <form action="<%=ctx%>/evento/ConsultaEvento" method="get" style="display:inline;">
                <input type="hidden" name="nombre" value="<%= nombre %>" />
                <button type="submit" class="btn">Ver detalle</button>
              </form>
            </div>
          </article>
        <% } %>
      </div>
    </main>
  </div>
</div>