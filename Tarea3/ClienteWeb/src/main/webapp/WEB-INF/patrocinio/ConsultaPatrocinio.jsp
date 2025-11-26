<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="publicadores.DtPatrocinio" %>
<%
  String ctx = request.getContextPath();
  String nick = (String) session.getAttribute("nick");
  DtPatrocinio patrocinio = (DtPatrocinio) request.getAttribute("patrocinio");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta Patrocinio — <%= (patrocinio != null ? patrocinio.getCodigo() : "Patrocinio") %></title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaPatrocinio.css">
</head>
<body>
<jsp:include page="/WEB-INF/templates/header.jsp" />
<div class="container row layout-inicio" style="margin-top:1rem; display:flex; align-items:flex-start;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />
  <main class="container consulta-layout" style="flex:2; min-width:0;">
    <section class="card event-card" style="max-width:600px; margin:0 auto;">
      <div class="event-header" style="text-align:center; margin-bottom:1.5rem;">
        <h1 class="event-title" style="font-size:2rem; font-weight:700; margin:0;">Patrocinio <%= (patrocinio != null ? (": " + patrocinio.getCodigo()) : "") %></h1>
      </div>
      <div class="event-info" style="padding: 18px 24px; font-size:1.13rem;">
        <% if (patrocinio != null) { %>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Institución:</strong> <span style="font-weight:500;"><%= patrocinio.getInstitucion() %></span></div>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Nivel:</strong> <span style="font-weight:500;"><%= String.valueOf(patrocinio.getNivel()) %></span></div>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Tipo de Registro:</strong> <span style="font-weight:500;"><%= patrocinio.getTipoRegistro() %></span></div>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Aporte:</strong> <span style="font-weight:500;"><%= patrocinio.getMonto() %></span></div>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Fecha de Patrocinio:</strong> <span style="font-weight:500;"><%= (patrocinio.getFecha() != null ? patrocinio.getFecha().toString() : "—") %></span></div>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Cantidad de Registros Gratuitos:</strong> <span style="font-weight:500;"><%= patrocinio.getCantRegistrosGratuitos() %></span></div>
          <div class="event-meta" style="margin-bottom:.7em;"><strong>Código de Patrocinio:</strong> <span style="font-weight:500;"><%= patrocinio.getCodigo() %></span></div>
          <% if (patrocinio.getSiglaEdicion() != null) { %>
            <div class="event-meta" style="margin-bottom:.7em;"><strong>Edición asociada:</strong> <span style="font-weight:500;"><%= patrocinio.getSiglaEdicion() %></span></div>
          <% } %>
        <% } else { %>
          <p style="color:#c00; font-weight:600;">No se encontró información del patrocinio.</p>
        <% } %>
      </div>
    </section>
  </main>
</div>
</body>
</html>
