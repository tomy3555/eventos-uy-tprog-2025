<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String error = (String) request.getAttribute("error");
  boolean precargado = Boolean.TRUE.equals(application.getAttribute("datosPrecargados"));

  //obtenemos el usuario logueado 
  String nick = (String) session.getAttribute("nick");
%>
<%
  Object precargadoAttr = application.getAttribute("datosPrecargados");
  System.out.println("DEBUG datosPrecargados: " + precargadoAttr);
%>

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
        <%-- <a class="btn" href="<%=ctx%>/usuario/perfil"><i class='bx bxs-user'></i> Ver Perfil</a> --%>
<form action="<%=ctx%>/auth/logout" method="get">
  <button type="submit" class="btn">
    Cerrar sesión
  </button>
</form>
<%--         <a class="btn" href="<%=ctx%>/auth/logout"><i class='bx bxs-log-out'></i> Cerrar sesión</a> --%>
        <% if (!precargado) { %>
        	<form action="<%=ctx%>/precargar" method="post" style="display:inline;">
            	<button class="btn" type="submit">Precargar datos</button>
            </form>
          <% } else { %>
            <span>Datos precargados correctamente</span>
          <% } %>
      </nav>
    <% } else { %>
      <!-- Visitante -->
      <nav class="user-nav" id="userNav">
      	 <form action="<%=ctx%>/auth/login" method="get">
  			<button type="submit" class="btn">
    			 Iniciar Sesión
  			</button>
		</form>
        <%-- <a class="btn ghost" href="<%=ctx%>/auth/login">Iniciar Sesión</a> --%>
        <form action="<%=ctx%>/usuario/AltaUsuario" method="get">
  			<button type="submit" class="btn">
    			 Registrarse
  			</button>
		</form>
        <%-- <a class="btn" href="<%=ctx%>/usuario/AltaUsuario">Registrarse</a> --%>
       	<% if (!precargado) { %>
      		<form action="<%=ctx%>/precargar" method="post" style="display:inline;">
            	<button class="btn" type="submit">Precargar datos</button>
            </form>
          <% } else { %>
            <span>Datos precargados correctamente</span>
          <% } %>
      </nav>
    <% } %>
  </div>
</header>
