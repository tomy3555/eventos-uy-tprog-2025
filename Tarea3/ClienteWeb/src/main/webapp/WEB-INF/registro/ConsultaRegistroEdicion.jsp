<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*, publicadores.DtRegistro" %>
<%
  String ctx = request.getContextPath();
  String nickSesion = (String) session.getAttribute("nick");
  String rol = (String) session.getAttribute("rol");
  DtRegistro registro = (DtRegistro) request.getAttribute("registro");
  String error = (String) request.getAttribute("error");
  String mensaje = (String) request.getAttribute("mensaje");

%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta Registro Edición — Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaRegistro.css">
  <style>
    .btn-linklike {
      background:#f9fafb;
      border:1px solid #d1d5db;
      padding:.45rem .75rem;
      border-radius:8px;
      cursor:pointer;
    }
    .btn-linklike:hover { background:#f3f4f6; }
  </style>
</head>
<body>
  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row" style="margin-top:1rem; display:flex; align-items:flex-start;">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="container consulta-layout" style="flex:2; min-width:0;">
      <section class="card event-card">
        <div class="event-header">
          <h1 class="event-title">Consulta de Registro en Edición</h1>
        </div>

        <div class="event-info">
          <% if (error != null) { %>
            <p class="error"><%= error %></p>
          <% } else if (registro != null) { %>
            <div class="event-meta"><strong>Usuario:</strong> <%= registro.getUsuario() %></div>
            <div class="event-meta"><strong>Edición:</strong> <%= registro.getEdicion() %></div>
            <div class="event-meta"><strong>Tipo de registro:</strong> <%= registro.getTipoRegistro() %></div>
            <%
              String fechaR = (registro.getFechaRegistro() != null) ? registro.getFechaRegistro() : "";
            %>
            <div class="event-meta"><strong>Fecha de registro:</strong> <%= fechaR %></div>
            <div class="event-meta"><strong>Costo:</strong> $<%= registro.getCosto() %></div>
            <%
              String fechaI = (registro.getFechaInicio() != null) ? registro.getFechaInicio() : "";
            %>
            <div class="event-meta"><strong>Fecha de inicio:</strong> <%= fechaI %></div>

            <% if ("asistente".equalsIgnoreCase(rol)) { %>
              <%
                List<DtRegistro> asistencias = (List<DtRegistro>) request.getAttribute("asistencias");
                boolean yaAsistio = false;
                if (asistencias != null && registro != null) {
                  for (DtRegistro asis : asistencias) {
                    if (asis.getIdentificador() != null && asis.getIdentificador().equals(registro.getIdentificador())) {
                      yaAsistio = true;
                      break;
                    }
                  }
                }
              %>
              <% if (mensaje != null) { %>
                <div class="alert alert-success" role="alert">
                  <%= mensaje %>
                </div>
              <% } %>
              <% if (yaAsistio) { %>
                <div class="alert alert-info" role="alert">
                  Ya has confirmado tu asistencia para este registro.
                </div>
                <form action="<%= ctx %>/registro/ConsultaRegistroEdicion" method="get" style="margin-top:1rem;">
                  <input type="hidden" name="idRegistro" value="<%= registro.getIdentificador() %>">
                  <input type="hidden" name="accion" value="certificado">
                  <button type="submit" class="btn-linklike">Descargar certificado</button>
                </form>
              <% } %>
            <% } %>

          <% } else { %>
            <p>No se encontró información del registro.</p>
          <% } %>
        </div>
      </section>
    </main>
  </div>
</body>
</html>