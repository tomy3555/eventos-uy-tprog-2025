<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
String ctx = request.getContextPath();
String nick = (String) session.getAttribute("nick");
logica.datatypes.DTTipoRegistro tipoRegistro = (logica.datatypes.DTTipoRegistro) request.getAttribute("tipoRegistro");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta Tipo de Registro — <%= (tipoRegistro != null ? tipoRegistro.getNombre() : "Tipo de Registro") %></title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaTipoRegistro.css">
</head>
<body>
<jsp:include page="/WEB-INF/templates/header.jsp" />
<div class="container row" style="margin-top:1rem; display: flex; align-items: flex-start;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />
  <main class="container consulta-layout" style="flex:2; min-width:0;display :flow">
    <section class="event-card">
      <div class="event-header">
        <h1 class="event-title">Tipo de Registro: <%= (tipoRegistro != null ? tipoRegistro.getNombre() : "Tipo de Registro") %></h1>
      </div>
      <div class="event-info" style="padding:15px;">
        <% if (tipoRegistro != null) { %>
          <div class="event-meta"><strong>Descripción:</strong> <%= tipoRegistro.getDescripcion() %></div>
          <div class="event-meta"><strong>Costo:</strong> <%= tipoRegistro.getCosto() %></div>
          <div class="event-meta"><strong>Cupo:</strong> <%= tipoRegistro.getCupo() %></div>
        <% } else { %>
          <p>No se encontró información del tipo de registro.</p>
        <% } %>
      </div>
    </section>
  </main>
</div>
</body>
</html>