<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Collection" %>
<%
  String ctx   = request.getContextPath();
  String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Iniciar Sesión — Eventos.uy</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <link rel="stylesheet" href="<%=ctx%>/css/InicioSesion.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

  <!-- Main -->
  <main class="wrapper-iniciar-sesion">
    <h1 class="titulo-iniciar-sesion">EventosUy</h1>

    <form id="loginForm" class="card form-iniciar-sesion" method="post" action="<%=ctx%>/auth/login">
      <div class="row-iniciar-sesion">
        <div class="campo-iniciar-sesion">
          <label for="email">Correo electrónico o Nickname</label>
          <input id="email" name="email" type="text" required placeholder="Correo o Nick">
        </div>
      </div>

      <div class="row-iniciar-sesion">
        <div class="campo-iniciar-sesion">
          <label for="pass">Contraseña</label>
          <input id="pass" name="pass" type="password" required>
        </div>
      </div>

      <div class="acciones-iniciar-sesion">
        <button type="submit" class="btn btn-iniciar-sesion">Ingresar</button>
      </div>

      <p id="msg" style="display:<%= (error==null)? "none" : "block" %>; color:red; font-weight:bold; margin-top:.5rem;">
        <%= (error==null)? "" : error %>
      </p>
    </form>
    <script>
      document.querySelector('.btn-cancelar-iniciar-sesion').addEventListener('click', function(e) {
        var form = document.getElementById('loginForm');
        Array.from(form.querySelectorAll('[required]')).forEach(function(input) {
          input.removeAttribute('required');
        });
      });
    </script>
  </main>
</body>
</html>