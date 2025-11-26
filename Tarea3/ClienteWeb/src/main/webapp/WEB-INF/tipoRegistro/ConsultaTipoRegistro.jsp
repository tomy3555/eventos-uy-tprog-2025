<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="publicadores.DtTipoRegistro" %>
<%
  String ctx = request.getContextPath();
  String nick = (String) session.getAttribute("nick");
  publicadores.DtTipoRegistro tipoRegistro = (publicadores.DtTipoRegistro) request.getAttribute("tipoRegistro");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta Tipo de Registro — <%= (tipoRegistro != null ? tipoRegistro.getNombre() : "Tipo de Registro") %></title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaTipoRegistro.css">
  <style>
    /* Ajustes visuales coherentes con ConsultaEvento/Edicion */
    .page-consulta-tipo main {
      flex: 2;
      min-width: 0;
    }

    .page-consulta-tipo .event-card {
      max-width: 750px;
      margin: 0 auto;
      padding: 1.5rem;
      background: #fff;
      border: 1px solid var(--border);
      border-radius: 14px;
      box-shadow: 0 2px 8px rgba(0,0,0,.04);
    }

    .page-consulta-tipo .event-header {
      text-align: center;
      margin-bottom: 1rem;
    }

    .page-consulta-tipo .event-title {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--text);
    }

    .page-consulta-tipo .event-info {
      display: grid;
      gap: .75rem;
      font-size: 1rem;
      line-height: 1.6;
    }

    .page-consulta-tipo .event-meta strong {
      color: var(--text-strong);
      margin-right: .4rem;
    }

    @media (max-width: 900px) {
      .page-consulta-tipo .event-card {
        max-width: 95%;
        padding: 1rem;
      }
    }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />

<div class="container row page-consulta-tipo" style="margin-top:1rem; display:flex; align-items:flex-start;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />

  <main class="container consulta-layout">
    <section class="card event-card">
      <div class="event-header">
        <h1 class="event-title">
          Tipo de Registro: <%= (tipoRegistro != null ? tipoRegistro.getNombre() : "Tipo de Registro") %>
        </h1>
      </div>

      <div class="event-info">
        <% if (tipoRegistro != null) { %>
          <div class="event-meta"><strong>Descripción:</strong> <%= tipoRegistro.getDescripcion() %></div>
          <div class="event-meta"><strong>Costo:</strong> $<%= tipoRegistro.getCosto() %></div>
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
