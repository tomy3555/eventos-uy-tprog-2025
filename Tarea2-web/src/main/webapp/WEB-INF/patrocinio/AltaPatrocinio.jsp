<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="logica.datatypes.DTTipoRegistro" %>

<%
  String ctx = request.getContextPath();

  String eventoSel  = (String) request.getAttribute("evento");   // puede venir null
  String edicionSel = (String) request.getAttribute("edicion");  // puede venir null

  @SuppressWarnings("unchecked")
  List<String> eventosOrganizador   = (List<String>) request.getAttribute("eventosOrganizador");
  @SuppressWarnings("unchecked")
  List<String> edicionesOrganizador = (List<String>) request.getAttribute("edicionesOrganizador");
  @SuppressWarnings("unchecked")
  Set<String> instituciones = (Set<String>) request.getAttribute("instituciones");
  @SuppressWarnings("unchecked")
  List<DTTipoRegistro> tipos = (List<DTTipoRegistro>) request.getAttribute("tiposRegistro");

  String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Alta de Patrocinio</title>
  <!-- Estilos globales -->
  <link rel="stylesheet" href="<%= ctx %>/css/style.css">
  <link rel="stylesheet" href="<%= ctx %>/css/layoutMenu.css">
  <!-- Estilos específicos de esta vista -->
  <link rel="stylesheet" href="<%= ctx %>/css/AltaPatrocinio.css">
  <link rel="stylesheet" href="<%= ctx %>/css/AltaEvento.css">
</head>
<body>

  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row" style="margin-top:1rem;">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="container" style="flex:2; min-width:0;">
      <section class="form-card-altaEvento form-card--wide">
        <h2 style="text-align:center;">Alta de Patrocinio</h2>
        <!-- Selectores de evento y edición (dentro del card principal) -->
        <form method="get" action="<%= ctx %>/edicion/patrocinio/alta" class="inline-form">
          <div class="form-row">
            <label class="form-label">Evento</label>
            <select name="evento" required onchange="this.form.submit()" class="form-select">
              <option value="" <%= (eventoSel==null||eventoSel.isEmpty())?"selected":"" %> disabled>Seleccione evento...</option>
              <% if (eventosOrganizador != null) {
                   for (String ev : eventosOrganizador) { %>
                <option value="<%= ev %>" <%= (ev.equals(eventoSel)?"selected":"") %>><%= ev %></option>
              <% } } %>
            </select>
          </div>
          <noscript><button type="submit" class="btn">Continuar</button></noscript>
        </form>

        <form method="get" action="<%= ctx %>/edicion/patrocinio/alta" class="inline-form">
          <input type="hidden" name="evento" value="<%= eventoSel == null ? "" : eventoSel %>"/>
          <div class="form-row">
            <label class="form-label">Edición</label>
            <select name="edicion" <%= (eventoSel==null||eventoSel.isEmpty())?"disabled":"" %> onchange="this.form.submit()" required class="form-select">
              <option value="" <%= (edicionSel==null||edicionSel.isEmpty())?"selected":"" %> disabled>Seleccione edición...</option>
              <% if (edicionesOrganizador != null) {
                   for (String ed : edicionesOrganizador) { %>
                <option value="<%= ed %>" <%= (ed.equals(edicionSel)?"selected":"") %>><%= ed %></option>
              <% } } %>
            </select>
          </div>
          <noscript><button type="submit" class="btn">Continuar</button></noscript>
        </form>

        <% if (error != null) { %>
          <div class="alert alert-error" style="margin-bottom:1rem;"><%= error %></div>
        <% } %>
        <% if (eventoSel != null && !eventoSel.isEmpty() && edicionSel != null && !edicionSel.isEmpty()) { %>
        <form method="post" action="<%= ctx %>/edicion/patrocinio/alta" class="card form-card">
          <!-- Preservo selección -->
          <input type="hidden" name="evento"  value="<%= eventoSel %>"/>
          <input type="hidden" name="edicion" value="<%= edicionSel %>"/>

          <div class="form-grid">
            <div class="form-group-altaEvento">
              <label class="form-label">Institución</label>
              <select name="institucion" required class="form-select">
                <option value="" disabled selected>Seleccione...</option>
                <% if (instituciones != null) for (String inst : instituciones) { %>
                  <option value="<%= inst %>"><%= inst %></option>
                <% } %>
              </select>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Nivel</label>
              <select name="nivel" required class="form-select">
                <option value="" disabled selected>Seleccione...</option>
                <option value="ORO">ORO</option>
                <option value="PLATA">PLATA</option>
                <option value="BRONCE">BRONCE</option>
              </select>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Tipo de registro</label>
              <select name="tipoRegistro" required class="form-select">
                <option value="" disabled selected>Seleccione...</option>
                <% if (tipos != null) for (DTTipoRegistro t : tipos) { %>
                  <option value="<%= t.getNombre() %>">
                    <%= t.getNombre() %> — <%= t.getDescripcion() %> (costo: <%= t.getCosto() %>, cupo: <%= t.getCupo() %>)
                  </option>
                <% } %>
              </select>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Aporte (monto)</label>
              <input type="number" min="0" step="1" name="aporte" required class="form-input"/>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Fecha del patrocinio</label>
              <input type="date" name="fechaPatrocinio" required class="form-input"/>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Cant. registros gratuitos</label>
              <input type="number" min="0" step="1" name="cantidadRegistros" required class="form-input"/>
            </div>

            <div class="form-group-altaEvento">
              <label class="form-label">Código del patrocinio</label>
              <input type="text" name="codigoPatrocinio" maxlength="40" required class="form-input"/>
            </div>
          </div>

          <div class="form-actions-altaEvento actions">
            <button type="submit" class="btn-guardar-altaEvento">Crear patrocinio</button>
          </div>
        </form>
        <% } %>
      </section>
    </main>
  </div>

</body>
</html>