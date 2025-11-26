<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, logica.datatypes.DTEvento, logica.datatypes.DTEdicion" %>
<%
  String ctx = request.getContextPath();
  List<DTEvento> eventos = (List<DTEvento>) request.getAttribute("eventos");
  List<DTEdicion> ediciones = (List<DTEdicion>) request.getAttribute("ediciones");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Alta de Tipo de Registro — Eventos.uy</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/altaTipoRegistro.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />

<div class="container row" style="margin-top:1rem;">
 <!-- menu -->
 <jsp:include page="/WEB-INF/templates/menu.jsp" />

  <!-- Contenido principal -->
  <main class="container" style="flex:2; min-width:0;">
    <section class="form-card-altaEvento form-card--wide">
      <h2>Alta de Tipo de Registro</h2>

      <% if (request.getAttribute("error") != null) { %>
        <p style="color:#c00"><%= request.getAttribute("error") %></p>
      <% } %>

      <form action="<%=ctx%>/registro/alta" method="post" id="form-alta-tipo">
        <!-- EDICIÓN filtrada -->
        <div class="form-group-altaTipoRegistro">
          <label>Edición del evento <span style="color:red">*</span></label>
          <select id="selectEdicion" name="edicion" required>
            <option value="">-- Seleccione una edición --</option>
            <% if (ediciones != null) {
                 for (DTEdicion ed : ediciones) { %>
                   <option value="<%= ed.getSigla() %>">
                     <%= ed.getNombre() %> (<%= ed.getEvento().getNombre() %>)
                   </option>
            <%   }
               } %>
          </select>
        </div>

        <div class="form-group-altaTipoRegistro">
          <label>Nombre <span style="color:red">*</span></label>
          <input name="nombre" required>
        </div>

        <div class="form-group-altaTipoRegistro">
          <label>Descripción <span style="color:red">*</span></label>
          <textarea name="descripcion" rows="4" required></textarea>
        </div>

        <div class="form-group-altaTipoRegistro">
          <label>Costo <span style="color:red">*</span></label>
          <input type="number" name="costo" min="0" step="0.01" required>
        </div>

        <div class="form-group-altaTipoRegistro">
          <label>Cupo <span style="color:red">*</span></label>
          <input type="number" name="cupo" min="1" required>
        </div>

        <div class="form-actions-altaEvento">
          <button type="submit" class="btn-guardar-altaEvento">Guardar</button>
          <button type="submit" class="btn-cancelar-altaEvento" name="accion" value="cancelar">Cancelar</button>
        </div>
      </form>

      <script>
        document.querySelector('.btn-cancelar-altaEvento').addEventListener('click', function(e) {
          var form = document.getElementById('form-alta-tipo');
          Array.from(form.querySelectorAll('[required]')).forEach(function(input) {
            input.removeAttribute('required');
          });
        });
      </script>
    </section>
  </main>
</div>

</body>
</html>