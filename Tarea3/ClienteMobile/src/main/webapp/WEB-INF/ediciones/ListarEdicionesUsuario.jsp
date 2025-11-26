<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, publicadores.DtEdicion" %>
<%
String ctx = request.getContextPath();
String nick = (String) session.getAttribute("nick");
List<DtEdicion> edicionesRegistradas = (List<DtEdicion>) request.getAttribute("edicionesRegistradas");
String baseImgUrl = (String) request.getAttribute("edicionesBaseImgUrl");
%>
<link rel="stylesheet" href="<%=ctx%>/css/style.css">
<link rel="stylesheet" href="<%=ctx%>/css/listado.css">
<jsp:include page="/WEB-INF/templates/header.jsp"/>

<div class="container">
  <div class="page-list">
    <main class="content">
      <h1 class="list-title">Ediciones en las que estás registrado</h1>
      <p class="list-sub">
        <%
          int n = (edicionesRegistradas == null) ? 0 : edicionesRegistradas.size();
          out.print(n + (n==1 ? " resultado" : " resultados"));
        %>
      </p>
      <div class="cards-grid">
        <% if (edicionesRegistradas != null) for (DtEdicion ed : edicionesRegistradas) {
             String nombre = ed.getNombre();
             String sigla  = ed.getSigla();
             String ciudad = ed.getCiudad();
             String pais   = ed.getPais();
             String img = ed.getImagen();
             String ip = (String) request.getAttribute("ipServidor");
             String puerto = (String) request.getAttribute("puertoServidor");
             String imgUrl = ("http://" + ip + ":8080/ServidorCentral-0.0.1-SNAPSHOT/images/ediciones/" + img);
        %>
          <article class="card event-card list">
            <img class="event-cover"
                 src="<%= imgUrl %>"
                 alt="Imagen de <%=nombre%>"
                 onerror="this.onerror=null;this.src='<%=ctx%>/img/ediciones/edicion-default.svg';">
            <h3 class="event-title"><%= nombre %></h3>
            <p class="event-sub"><%= (sigla==null||sigla.isBlank()) ? "—" : sigla %></p>
            <p class="event-desc">
              <strong>Ciudad:</strong> <%= ciudad %> | <strong>País:</strong> <%= pais %>
            </p>
            <div class="event-footer">
              <form action="<%= ctx %>/registro/ConsultaRegistroEdicion" method="get" style="display:inline;">
                <input type="hidden" name="usuario" value="<%= nick %>" />
                <input type="hidden" name="edicion" value="<%= nombre %>" />
                <button type="submit" class="btn btn-primary">Ver detalles de mi registro</button>
              </form>
            </div>
          </article>
        <% } %>
      </div>
    </main>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>