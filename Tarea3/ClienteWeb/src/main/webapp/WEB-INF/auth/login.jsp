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
  <!-- Include responsive login-specific CSS -->
  <link rel="stylesheet" href="<%=ctx%>/css/InicioSesion.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
  <link rel="stylesheet" href="<%=ctx%>/css/custom.css">
</head>
<body>
  
  <!-- Header  -->

  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <!-- Main -->
  <main class="wrapper-iniciar-sesion">
    <h1 class="titulo-iniciar-sesion">Iniciar sesión</h1>

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
        <button type="button" class="btn ghost btn-cancelar-iniciar-sesion" style="margin-left:1rem;" onclick="window.location='<%=ctx%>/inicio'">Cancelar</button>
      </div>

      <p id="msg" style="display:<%= (error==null)? "none" : "block" %>; color:red; font-weight:bold; margin-top:.5rem;">
        <%= (error==null)? "" : error %>
      </p>
    </form>
  </main>
</body>
</html>