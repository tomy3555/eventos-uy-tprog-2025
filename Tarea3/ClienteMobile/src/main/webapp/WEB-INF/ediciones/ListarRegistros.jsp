<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, publicadores.DtRegistro" %>
<%
String ctx = request.getContextPath();
String nick = (String) session.getAttribute("nick");
List<DtRegistro> registros = (List<DtRegistro>) request.getAttribute("registrosUsuario");
%>
<link rel="stylesheet" href="<%=ctx%>/css/style.css">
<link rel="stylesheet" href="<%=ctx%>/css/listado.css">
<jsp:include page="/WEB-INF/templates/header.jsp"/>

<div class="container">
  <div class="page-list">
    <main class="content">
      <h1 class="list-title">Registros de asistencia a ediciones</h1>
      <p class="list-sub">
        <%
          int n = (registros == null) ? 0 : registros.size();
          out.print(n + (n==1 ? " registro" : " registros"));
        %>
      </p>
      <div class="cards-grid">
        <% if (registros != null) for (DtRegistro reg : registros) {
             String edicion = reg.getEdicion();
        %>
          <article class="card event-card list">
            <h3 class="event-title"><%= edicion %></h3>
            <div class="event-footer">
              <form action="<%= ctx %>/registro/ConsultaRegistroEdicion" method="get" style="display:inline;">
                <input type="hidden" name="usuario" value="<%= nick %>" />
                <input type="hidden" name="edicion" value="<%= edicion %>" />
                <button type="submit" class="btn btn-primary">Ver detalle del registro</button>
              </form>
            </div>
          </article>
        <% } %>
      </div>
    </main>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
