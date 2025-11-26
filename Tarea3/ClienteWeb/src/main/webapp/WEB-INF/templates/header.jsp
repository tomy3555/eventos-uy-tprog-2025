<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String error = (String) request.getAttribute("error");
  //obtenemos el usuario logueado 
  String nick = (String) session.getAttribute("nick");
%>
<%
  // Control del fondo global
  boolean fondoActivo = true; // <-- Cambiá a false si querés desactivarlo
%>

<%-- Load Inter font from Google Fonts for a serious, modern sans-serif --%>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">

<% if (fondoActivo) { %>
<style>
  body {
    background: url('<%= request.getContextPath() %>/img/fondo-eventos.png')
                center/cover no-repeat fixed;
    background-color: #1a1a1a;
  }
  body::before {
    content: "";
    position: fixed;
    inset: 0;
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(3px);
    z-index: -1;
  }
</style>
<% } %>
<%
  String path = request.getServletPath();           // mejor que getRequestURI() porque ignora el contextPath
  String q    = request.getParameter("q");

  boolean esRutaBuscar   = "/buscar".equals(path);  // solo la ruta /buscar
  boolean tieneQuery     = q != null && !q.trim().isEmpty();

  // Solo es “búsqueda” si hay texto en q; otros parámetros (orden, etc.) no influyen
  boolean esBusquedaConQ = esRutaBuscar && tieneQuery;

  boolean esLogin = (path != null && path.contains("login")) || esBusquedaConQ;
%>
<% if (!esLogin) { %>
  <!-- Solo cargar estos si NO estamos en login.jsp -->
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/custom.css">
<% } %>

<header class="site-header">
  <div class="container">

    <a class="brand" href="<%=ctx%>/inicio">Eventos.uy</a>

    <nav class="main-nav">
      <form class="search" action="<%=ctx%>/buscar" method="get" role="search" aria-label="Buscar">
        <input class="search-input" type="search" name="q" placeholder="Eventos, Ediciones">
        <button class="btn" type="submit">Buscar</button>
      </form>
    </nav>

    <% if (nick != null) { %>
      <!-- Usuario logueado -->
      <nav class="user-nav" id="userNav">
        <span class="user-name">Hola, <strong><%= nick %></strong></span>
        <form action="<%=ctx%>/usuario/ConsultaUsuario" method="get">
          <input type="hidden" name="nick" value="<%= nick %>">
          <button type="submit" class="btn">Ver Perfil</button>
        </form>
        <form action="<%=ctx%>/auth/logout" method="get">
          <button type="submit" class="btn">
            Cerrar sesión
          </button>
        </form>
      </nav>
    <% } else { %>
      <!-- Visitante -->
      <nav class="user-nav" id="userNav">
         <form action="<%=ctx%>/auth/login" method="get">
			<button type="submit" class="btn">
				 Iniciar Sesión
			</button>
		</form>
        <form action="<%=ctx%>/usuario/AltaUsuario" method="get">
			<button type="submit" class="btn">
				 Registrarse
			</button>
		</form>
      </nav>
    <% } %>
  </div>
</header>