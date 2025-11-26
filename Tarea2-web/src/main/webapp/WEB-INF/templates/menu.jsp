<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/layoutMenu.css">

<%
  String ctx = request.getContextPath();
  String rol = (String) session.getAttribute("rol");

  @SuppressWarnings("unchecked")
  java.util.List<logica.datatypes.DTCategorias> dtCategorias =
      (java.util.List<logica.datatypes.DTCategorias>) request.getAttribute("dtCategorias");
  java.util.List<String> categorias = new java.util.ArrayList<>();
  if (dtCategorias != null) {
      for (logica.datatypes.DTCategorias dtCat : dtCategorias) {
          categorias.addAll(dtCat.getCategorias());
      }
  }
%>

<aside class="card aside-inicio">
  <h3>Menú</h3>

  <% if ("ORGANIZADOR".equals(rol)) { %>
    <h4>Acciones</h4>
    <ul>
      <li>
        <form action="<%=ctx%>/evento/alta" method="get" style="display:inline">
          <button type="submit" class="linklike">Crear Evento</button>
        </form>
      </li>
      <li>
        <form action="<%=ctx%>/edicion/alta" method="get" style="display:inline">
          <button type="submit" class="linklike">Crear Edición de Evento</button>
        </form>
      </li>
      <li>
        <form action="<%=ctx%>/registro/alta" method="get" style="display:inline">
          <button type="submit" class="linklike">Crear Registro</button>
        </form>
      </li>
      <li>
        <form action="<%=ctx%>/institucion/alta" method="get" style="display:inline">
          <button type="submit" class="linklike">Crear Institución</button>
        </form>
      </li>
      <li>
        <!-- NUEVO: un botón simple que abre la pantalla de Alta Patrocinio -->
        <form action="<%=ctx%>/edicion/patrocinio/alta" method="get" style="display:inline">
          <button type="submit" class="linklike">Crear Patrocinio</button>
        </form>
      </li>
    </ul>
  <% } else if ("ASISTENTE".equals(rol)) { %>
    <h4>Acciones</h4>
    <ul>
      <li>
        <form action="<%=ctx%>/registro/inscripcion" method="get" style="display:inline">
          <button type="submit" class="linklike">Registrarse a Edición de un Evento</button>
        </form>
      </li>
    </ul>
  <% } %>

  <h4>Categorías</h4>
  <ul class="menu-categorias">
    <% if (categorias != null && !categorias.isEmpty()) {
         for (String cat : categorias) { %>
      <li>
        <form action="<%=ctx%>/evento/listado" method="get">
          <input type="hidden" name="categoria" value="<%=cat%>">
          <button type="submit" class="linklike"><%=cat%></button>
        </form>
      </li>
    <% } } else { %>
      <li><span>(Sin categorías)</span></li>
    <% } %>
  </ul>

  <h4>
    <form action="<%=ctx%>/usuario/ConsultaUsuario" method="get" style="display:inline">
      <input type="hidden" name="listar" value="1">
      <button type="submit" class="linklike">Listar Usuarios</button>
    </form>
  </h4>
</aside>