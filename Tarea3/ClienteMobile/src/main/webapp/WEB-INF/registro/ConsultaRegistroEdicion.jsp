<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*, publicadores.DtRegistro" %>
<%
  String ctx = request.getContextPath();
  String nickSesion = (String) session.getAttribute("nick");
  DtRegistro registro = (DtRegistro) request.getAttribute("registro");
  String error = (String) request.getAttribute("error");
  String mensaje = (String) request.getAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta Registro Edición — Eventos.uy</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaRegistro.css">
</head>
<body>
  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container my-4">
    <main class="row justify-content-center">
      <section class="col-md-8 col-lg-6">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            <h1 class="h4 mb-0">Consulta de Registro en Edición</h1>
          </div>

          <div class="card-body">
            <% if (error != null) { %>
              <div class="alert alert-danger" role="alert">
                <%= error %>
              </div>
            <% } else if (registro != null) { %>
              <ul class="list-group list-group-flush mb-3">
                <li class="list-group-item"><strong>Usuario:</strong> <%= registro.getUsuario() %></li>
                <li class="list-group-item"><strong>Edición:</strong> <%= registro.getEdicion() %></li>
                <li class="list-group-item"><strong>Tipo de registro:</strong> <%= registro.getTipoRegistro() %></li>
                <li class="list-group-item"><strong>Fecha de registro:</strong> <%= registro.getFechaRegistro() %></li>
                <li class="list-group-item"><strong>Costo:</strong> $<%= registro.getCosto() %></li>
                <li class="list-group-item"><strong>Fecha de inicio:</strong> <%= registro.getFechaInicio() %></li>
              </ul>


			<%
			  List<DtRegistro> asistencias = (List<DtRegistro>) request.getAttribute("asistencias");
			%>

			<%
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
			<% } else if (yaAsistio) { %>
			  <div class="alert alert-info" role="alert">
			    Ya has confirmado tu asistencia para este registro.
			  </div>
			
			<% } else { %>
			  <form action="<%= ctx %>/registro/ConsultaRegistroEdicion" method="post">
			    <input type="hidden" name="usuario" value="<%= nickSesion %>" />
			    <input type="hidden" name="edicion" value="<%= registro.getEdicion() %>" />
			    <input type="hidden" name="registroId" value="<%= registro.getIdentificador() %>" />
			    <div class="d-grid">
			      <button type="submit" class="btn btn-success">
			        Confirmar asistencia
			      </button>
			    </div>
			  </form>
			<% } %>

            <% } else { %>
              <div class="alert alert-warning" role="alert">
                No se encontró información del registro.
              </div>
            <% } %>
          </div>
        </div>
      </section>
    </main>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
