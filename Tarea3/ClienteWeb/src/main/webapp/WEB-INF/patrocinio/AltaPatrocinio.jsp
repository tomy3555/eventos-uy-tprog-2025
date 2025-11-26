<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="publicadores.DtTipoRegistro" %>

<%
  String ctx = request.getContextPath();
  String eventoSel  = (String) request.getAttribute("evento");
  String edicionSel = (String) request.getAttribute("edicion");
  @SuppressWarnings("unchecked")
  List<String> eventosOrganizador   = (List<String>) request.getAttribute("eventosOrganizador");
  @SuppressWarnings("unchecked")
  List<String> edicionesOrganizador = (List<String>) request.getAttribute("edicionesOrganizador");
  @SuppressWarnings("unchecked")
  List<String> instituciones = (List<String>) request.getAttribute("instituciones");
  @SuppressWarnings("unchecked")
  List<DtTipoRegistro> tipos = (List<DtTipoRegistro>) request.getAttribute("tiposRegistro");
  String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Alta de Patrocinio — Eventos.uy</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- CSS base -->
  <link rel="stylesheet" href="<%= ctx %>/css/style.css">
  <link rel="stylesheet" href="<%= ctx %>/css/layoutMenu.css">
  <link rel="stylesheet" href="<%= ctx %>/css/AltaEvento.css">
  <link rel="stylesheet" href="<%= ctx %>/css/custom.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

  <!-- Header -->
  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <!-- Layout principal -->
  <div class="container layout-inicio">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="main-inicio">
      <section class="card event-card glass-panel">
        <header class="form-header">
          <h1 class="form-title">Alta de Patrocinio</h1>
          <p class="form-subtitle">
            Completá los datos del patrocinio. Primero seleccioná el evento y la edición correspondiente.
          </p>
        </header>

        <!-- Selección de evento -->
        <form method="get" action="<%= ctx %>/edicion/patrocinio/alta" class="inline-form form-group-altaEvento">
          <label class="form-label" for="eventoSel">Evento <span class="req">*</span></label>
          <select id="eventoSel" name="evento" required onchange="this.form.submit()" class="form-select">
            <option value="" <%= (eventoSel==null||eventoSel.isEmpty())?"selected":"" %> disabled>Seleccione evento...</option>
            <% if (eventosOrganizador != null) {
                 for (String ev : eventosOrganizador) { %>
              <option value="<%= ev %>" <%= (ev.equals(eventoSel)?"selected":"") %>><%= ev %></option>
            <% } } %>
          </select>
        </form>

        <!-- Selección de edición -->
        <form method="get" action="<%= ctx %>/edicion/patrocinio/alta" class="inline-form form-group-altaEvento">
          <input type="hidden" name="evento" value="<%= eventoSel == null ? "" : eventoSel %>"/>
          <label class="form-label" for="edicionSel">Edición <span class="req">*</span></label>
          <select id="edicionSel" name="edicion" <%= (eventoSel==null||eventoSel.isEmpty())?"disabled":"" %> onchange="this.form.submit()" required class="form-select">
            <option value="" <%= (edicionSel==null||edicionSel.isEmpty())?"selected":"" %> disabled>Seleccione edición...</option>
            <% if (edicionesOrganizador != null) {
                 for (String ed : edicionesOrganizador) { %>
              <option value="<%= ed %>" <%= (ed.equals(edicionSel)?"selected":"") %>><%= ed %></option>
            <% } } %>
          </select>
        </form>

        <% if (error != null) { %>
          <div class="alert alert-error" style="margin-top:1rem;">
            <i class='bx bxs-error-circle'></i> <%= error %>
          </div>
        <% } %>

        <% if (eventoSel != null && !eventoSel.isEmpty() && edicionSel != null && !edicionSel.isEmpty()) { %>
        <!-- Formulario principal -->
        <form method="post" action="<%= ctx %>/edicion/patrocinio/alta" class="form-card" style="margin-top:1.5rem;">
          <input type="hidden" name="evento"  value="<%= eventoSel %>"/>
          <input type="hidden" name="edicion" value="<%= edicionSel %>"/>

          <div class="form-grid">
            <div class="form-group-altaEvento">
              <label class="form-label">Institución <span class="req">*</span></label>
              <select name="institucion" required class="form-select">
                <option value="" disabled selected>Seleccione...</option>
                <% if (instituciones != null) for (String inst : instituciones) { %>
                  <option value="<%= inst %>"><%= inst %></option>
                <% } %>
              </select>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Nivel <span class="req">*</span></label>
              <select name="nivel" required class="form-select">
                <option value="" disabled selected>Seleccione...</option>
                <option value="ORO">Oro</option>
                <option value="PLATA">Plata</option>
                <option value="BRONCE">Bronce</option>
              </select>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Tipo de registro <span class="req">*</span></label>
              <select name="tipoRegistro" required class="form-select">
                <option value="" disabled selected>Seleccione...</option>
                <% if (tipos != null) for (DtTipoRegistro t : tipos) { %>
                  <option value="<%= t.getNombre() %>">
                    <%= t.getNombre() %> — <%= t.getDescripcion() %> 
                    (Costo: <%= t.getCosto() %>, Cupo: <%= t.getCupo() %>)
                  </option>
                <% } %>
              </select>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Aporte (monto) <span class="req">*</span></label>
              <input type="number" min="0" step="1" name="aporte" required class="form-input"/>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Fecha del patrocinio <span class="req">*</span></label>
              <input type="date" name="fechaPatrocinio" required class="form-input"/>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Cantidad de registros gratuitos <span class="req">*</span></label>
              <input type="number" min="0" step="1" name="cantidadRegistros" required class="form-input"/>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Código del patrocinio <span class="req">*</span></label>
              <input type="text" name="codigoPatrocinio" maxlength="40" required class="form-input"/>
            </div>
          </div>

          <p class="form-hint-altaEvento">Los campos marcados con <span class="req">*</span> son obligatorios.</p>

          <div class="form-actions-altaEvento actions">
            <button type="submit" class="btn-guardar-altaEvento">
              <i class='bx bx-save'></i> Crear patrocinio
            </button>
            <button type="reset" class="btn btn-cancelar-altaEvento" style="margin-left:1rem;">
              <i class='bx bx-x-circle'></i> Limpiar
            </button>
          </div>
        </form>
        <% } %>
      </section>
    </main>
  </div>
</body>
</html>
