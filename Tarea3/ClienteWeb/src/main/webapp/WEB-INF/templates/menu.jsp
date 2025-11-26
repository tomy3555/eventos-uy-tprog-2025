<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="publicadores.PublicadorEventoService, publicadores.PublicadorEvento, publicadores.DtEventoArray, publicadores.DtEvento,
                 java.util.List, java.util.ArrayList, java.util.HashSet, java.util.Set" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/layoutMenu.css">

<%
  String ctx = request.getContextPath();
  String rol = (String) session.getAttribute("rol");

  // 1) Intentar usar lo que dejó el filtro: List<String>
  @SuppressWarnings("unchecked")
  List<Object> dtCategoriasRaw = (List<Object>) request.getAttribute("dtCategorias");
  Set<String> categoriasSet = new HashSet<>();

  if (dtCategoriasRaw != null) {
      for (Object o : dtCategoriasRaw) {
          if (o instanceof String) {
              String s = (String) o;
              if (s != null && !s.isBlank()) categoriasSet.add(s);
          }
      }
  }

  // 2) Si quedó vacío, traer categorías desde el servicio (listarEventos → DtEventoArray → DtEvento.Categorias.getCategoria())
  if (categoriasSet.isEmpty()) {
      try {
          PublicadorEventoService svc = new PublicadorEventoService();
          PublicadorEvento port = svc.getPublicadorEventoPort();

          DtEventoArray arr = null;
          try { arr = port.listarEventos(); } catch (Exception ignore) { }

          if (arr != null && arr.getItem() != null) {
              for (DtEvento ev : arr.getItem()) {
                  if (ev == null) continue;
                  DtEvento.Categorias c = ev.getCategorias();
                  if (c == null) continue;
                  List<String> cats = c.getCategoria(); // list “viva” del stub
                  if (cats != null) {
                      for (String s : cats) if (s != null && !s.isBlank()) categoriasSet.add(s);
                  }
              }
          }
      } catch (Exception ignore) {
          // si falla, dejamos vacío y mostramos "(Sin categorías)"
      }
  }

  // 3) Pasar a lista para iterar
  List<String> categorias = new ArrayList<>(categoriasSet);
%>
<!-- === BLOQUE DE ESTILOS PARA EL FONDO === -->
<style>
  html, body {
    height: 100%;
    background: url('<%= ctx %>/img/fondo-eventos.png') center/cover no-repeat fixed;
    background-color: #1a1a1a;
  }
  body::before {
    content: "";
    position: fixed;
    inset: 0;
    background: rgba(255,255,255,0.75);
    backdrop-filter: blur(3px);
    z-index: -1;
  }

  /* Evita que las tarjetas y contenedores tapen el fondo */
  main, .container, .card, aside {
    background-color: transparent !important;
  }
</style>
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
