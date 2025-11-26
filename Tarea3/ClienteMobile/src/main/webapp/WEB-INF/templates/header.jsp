<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String error = (String) request.getAttribute("error");
  String nick = (String) session.getAttribute("nick");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
</head>
<body>
<header class="site-header" style="position:relative; z-index:2000;">
  <div class="container d-flex justify-content-between align-items-center">
    <span class="brand">Eventos.uy</span>

    <div class="position-relative" style="flex:1;">
      <button class="btn btn-outline-secondary" type="button" id="menuHamburguesa" aria-label="Menú" style="width:56px;height:56px;">
        <i class="bx bx-menu" style="font-size:2.5rem;"></i>
      </button>
      <div id="menuOpciones" class="bg-white border rounded shadow-lg mt-2" style="display:none; position:absolute; top:60px; left:0; right:0; z-index:2001; min-width:220px; width:90vw; max-width:400px; background: rgba(255,255,255,0.95);">
        <ul class="list-unstyled mb-0 py-3">
		<li>
		  <form action="<%=ctx%>/evento/listado" method="get">
		    <button type="submit" class="dropdown-item fs-4 py-3 btn w-100 text-start">Consulta Edición</button>
		  </form>
		</li>
		<li>
		  <form action="<%=ctx%>/usuario/edicionesRegistradas" method="get">
		    <button type="submit" class="dropdown-item fs-4 py-3 btn w-100 text-start">Consulta Registro</button>
		  </form>
		</li>
		<li>
		  <form action="<%=ctx%>/usuario/listarRegistros" method="get">
		    <button type="submit" class="dropdown-item fs-4 py-3 btn w-100 text-start">Registro a Asistencia</button>
		  </form>
		</li>
        </ul>
      </div>
      <script>
        const btn = document.getElementById('menuHamburguesa');
        const menu = document.getElementById('menuOpciones');
        btn.addEventListener('click', function(e) {
          e.stopPropagation();
          menu.style.display = (menu.style.display === 'none' || menu.style.display === '') ? 'block' : 'none';
        });
        document.addEventListener('click', function(e) {
          if (!btn.contains(e.target) && !menu.contains(e.target)) {
            menu.style.display = 'none';
          }
        });
      </script>
    </div>

    <% if (nick != null) { %>
      <nav class="user-nav d-flex" id="userNav">
        <form action="<%=ctx%>/auth/logout" method="get">
          <button type="submit" class="btn">Cerrar sesión</button>
        </form>
      </nav>
    <% } else { %>
      <nav class="user-nav d-flex" id="userNav">
         <form action="<%=ctx%>/auth/login" method="get">
            <button type="submit" class="btn">Iniciar Sesión</button>
         </form>
         <form action="<%=ctx%>/usuario/AltaUsuario" method="get">
            <button type="submit" class="btn">Registrarse</button>
         </form>
      </nav>
    <% } %>
  </div>
</header>
